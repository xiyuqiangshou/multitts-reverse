/**
 * 局域网转发服务端 —— 对应安卓 ForwardService:
 *   在鸿蒙端暴露 HTTP TTS API, 供局域网内 legado(安卓)/浏览器/脚本调用。
 *   GET /voices                                   → 音色列表 JSON
 *   GET /tts?text=...&voice=<code>&rate=<0.5~2>   → audio/wav
 *   GET /                                         → 状态页
 * 实现: @ohos.net.socket TCPSocketServer, 手写 HTTP/1.1 最小协议。
 * 连接处理模型: server.on('connect') 给出 TCPSocketConnection, 持有引用后
 * 在其 message 事件读请求、close 前写回响应(每个连接一个状态机)。
 */
import socket from '@ohos.net.socket';
import common from '@ohos.app.ability.common';
import { VoiceStore, VoiceEntry } from './VoiceStore';
import { EngineHost, SynthResult } from './EngineHost';
import { TextSanitizer } from './TextSanitizer';

class VoiceItem {
  code: string = '';
  name: string = '';
  source: string = '';
}

class VoicesResponse {
  voices: VoiceItem[] = [];
}

interface ParsedRequest {
  method: string;
  path: string;
  query: Record<string, string>;
}

class ConnState {
  connection: socket.TCPSocketConnection;
  buffer: string = '';
  responded: boolean = false;

  constructor(conn: socket.TCPSocketConnection) {
    this.connection = conn;
  }
}

export class ForwardServer {
  private static tcpServer: socket.TCPSocketServer | null = null;
  private static port: number = 18080;
  private static running: boolean = false;
  private static ctx: common.Context | null = null;

  static isRunning(): boolean {
    return ForwardServer.running;
  }

  static getPort(): number {
    return ForwardServer.port;
  }

  static async start(context: common.Context, port: number = 18080): Promise<void> {
    if (ForwardServer.running) {
      return;
    }
    ForwardServer.ctx = context;
    ForwardServer.port = port;
    const server = socket.constructTCPSocketServerInstance();
    ForwardServer.tcpServer = server;
    const addr: socket.NetAddress = {
      address: '0.0.0.0',
      port: port,
      family: 1
    };
    await server.listen(addr);
    server.on('connect', (client: socket.TCPSocketConnection): void => {
      ForwardServer.attach(new ConnState(client));
    });
    ForwardServer.running = true;
  }

  static async stop(): Promise<void> {
    const s = ForwardServer.tcpServer;
    ForwardServer.tcpServer = null;
    ForwardServer.running = false;
    if (s !== null) {
      try {
        s.off('connect');
      } catch (e) {
        // 忽略关闭异常
      }
    }
  }

  private static attach(state: ConnState): void {
    state.connection.on('message', (value: socket.SocketMessageInfo): void => {
      state.buffer += ForwardServer.bufToStr(value.message);
      // 请求头完整(以空行结束)即处理, 不等待 body(/tts 为 GET)
      if (state.buffer.indexOf('\r\n\r\n') >= 0 && !state.responded) {
        state.responded = true;
        const req = ForwardServer.parseRequest(state.buffer);
        ForwardServer.dispatch(state, req).catch(async (e: Object) => {
          const msg = e instanceof Error ? e.message : String(e);
          await ForwardServer.reply(state, 500, 'text/plain', 'error: ' + msg);
        });
      }
    });
    state.connection.on('close', (): void => {
      // 连接生命周期结束, 无需清理(GC 回收 state)
    });
  }

  private static bufToStr(buf: ArrayBuffer): string {
    const arr = new Uint8Array(buf);
    let s = '';
    for (let i = 0; i < arr.length; i++) {
      s += String.fromCharCode(arr[i]);
    }
    return s;
  }

  private static parseRequest(raw: string): ParsedRequest {
    const lineEnd = raw.indexOf('\r\n');
    const line = lineEnd > 0 ? raw.substring(0, lineEnd) : raw;
    const parts = line.split(' ');
    const method = parts.length > 0 ? parts[0] : 'GET';
    const target = parts.length > 1 ? parts[1] : '/';
    const qIdx = target.indexOf('?');
    const path = qIdx >= 0 ? target.substring(0, qIdx) : target;
    const query: Record<string, string> = {};
    if (qIdx >= 0) {
      for (const kv of target.substring(qIdx + 1).split('&')) {
        const eq = kv.indexOf('=');
        if (eq > 0) {
          const k = decodeURIComponent(kv.substring(0, eq));
          const vRaw = kv.substring(eq + 1).replace(/\+/g, ' ');
          let v = vRaw;
          try {
            v = decodeURIComponent(vRaw);
          } catch (e) {
            // 保留原始串
          }
          query[k] = v;
        }
      }
    }
    const r: ParsedRequest = { method: method, path: path, query: query };
    return r;
  }

  private static async dispatch(state: ConnState, req: ParsedRequest): Promise<void> {
    if (req.path === '/voices') {
      const list = await VoiceStore.all(ForwardServer.ctx as common.Context);
      const items: VoiceItem[] = [];
      for (const v of list) {
        const it = new VoiceItem();
        it.code = v.speaker.code;
        it.name = v.speaker.name;
        it.source = v.source;
        items.push(it);
      }
      const wrap = new VoicesResponse();
      wrap.voices = items;
      await ForwardServer.reply(state, 200, 'application/json', JSON.stringify(wrap));
      return;
    }
    if (req.path === '/tts') {
      const text = req.query['text'];
      if (text === undefined || text.length === 0) {
        await ForwardServer.reply(state, 400, 'text/plain', 'missing text');
        return;
      }
      const list = await VoiceStore.all(ForwardServer.ctx as common.Context);
      let entry: VoiceEntry | null = null;
      const voiceCode = req.query['voice'];
      if (voiceCode !== undefined) {
        for (const v of list) {
          if (v.speaker.code === voiceCode) {
            entry = v;
            break;
          }
        }
      } else if (list.length > 0) {
        entry = list[0];
      }
      if (entry === null) {
        await ForwardServer.reply(state, 404, 'text/plain', 'voice not found');
        return;
      }
      const speaker = entry.speaker;
      const rateRaw = req.query['rate'];
      if (rateRaw !== undefined) {
        const r = Number(rateRaw);
        if (Number.isFinite(r) && r >= 0.5 && r <= 2) {
          speaker.speed = r;
        }
      }
      const clean = TextSanitizer.sanitize(text);
      const result: SynthResult = await EngineHost.synthesize(entry.engine, speaker, clean, null);
      const wav = ForwardServer.wrapWav(result);
      await ForwardServer.replyBytes(state, 200, 'audio/wav', wav);
      return;
    }
    await ForwardServer.reply(state, 200, 'text/plain',
      'MultiTTS-Harmony forward server\nport: ' + ForwardServer.port +
      '\nGET /voices -> voice list\nGET /tts?text=...&voice=...&rate=... -> wav audio');
  }

  /** PCM → WAV RIFF 封装(16bit), 便于通用客户端直接播放 */
  private static wrapWav(r: SynthResult): Uint8Array {
    if (r.bitsPerSample !== 16 || r.channels > 2) {
      return new Uint8Array(0);
    }
    const dataLen = r.pcm.byteLength;
    const buf = new ArrayBuffer(44 + dataLen);
    const dv = new DataView(buf);
    const wStr = (off: number, s: string): void => {
      for (let i = 0; i < s.length; i++) {
        dv.setUint8(off + i, s.charCodeAt(i));
      }
    };
    wStr(0, 'RIFF');
    dv.setUint32(4, 36 + dataLen, true);
    wStr(8, 'WAVE');
    wStr(12, 'fmt ');
    dv.setUint32(16, 16, true);
    dv.setUint16(20, 1, true);
    dv.setUint16(22, r.channels, true);
    dv.setUint32(24, r.sampleRate, true);
    dv.setUint32(28, r.sampleRate * r.channels * 2, true);
    dv.setUint16(32, r.channels * 2, true);
    dv.setUint16(34, 16, true);
    wStr(36, 'data');
    dv.setUint32(40, dataLen, true);
    new Uint8Array(buf, 44).set(new Uint8Array(r.pcm));
    return new Uint8Array(buf);
  }

  private static async reply(state: ConnState, code: number, contentType: string,
    text: string): Promise<void> {
    const bytes: Uint8Array = ForwardServer.strToBytes(text);
    await ForwardServer.replyBytes(state, code, contentType, bytes);
  }

  private static async replyBytes(state: ConnState, code: number, contentType: string,
    body: Uint8Array): Promise<void> {
    const head = 'HTTP/1.1 ' + code + ' OK\r\nContent-Type: ' + contentType +
      '\r\nContent-Length: ' + body.byteLength + '\r\nConnection: close\r\n\r\n';
    const headBytes = ForwardServer.strToBytes(head);
    const bodyBytes = new Uint8Array(body);
    const out = new Uint8Array(headBytes.length + bodyBytes.length);
    out.set(headBytes, 0);
    out.set(bodyBytes, headBytes.length);
const sendOpt: socket.TCPSendOptions = {
      data: out.buffer as ArrayBuffer
    };
    await state.connection.send(sendOpt);
    state.connection.close().catch((): void => {
    });
  }

  private static strToBytes(s: string): Uint8Array {
    // UTF-8 编码(手写, 与 Md5.utf8Encode 同源逻辑; 不用 TextEncoder 以控依赖)
    const bytes: number[] = [];
    for (let i = 0; i < s.length; i++) {
      let code = s.charCodeAt(i);
      if (code >= 0xD800 && code <= 0xDBFF && i + 1 < s.length) {
        const lo = s.charCodeAt(i + 1);
        if (lo >= 0xDC00 && lo <= 0xDFFF) {
          code = 0x10000 + ((code - 0xD800) << 10) + (lo - 0xDC00);
          i++;
        }
      }
      if (code < 0x80) {
        bytes.push(code);
      } else if (code < 0x800) {
        bytes.push(0xC0 | (code >> 6), 0x80 | (code & 0x3F));
      } else if (code < 0x10000) {
        bytes.push(0xE0 | (code >> 12), 0x80 | ((code >> 6) & 0x3F), 0x80 | (code & 0x3F));
      } else {
        bytes.push(0xF0 | (code >> 18), 0x80 | ((code >> 12) & 0x3F),
          0x80 | ((code >> 6) & 0x3F), 0x80 | (code & 0x3F));
      }
    }
    return new Uint8Array(bytes);
  }
}
