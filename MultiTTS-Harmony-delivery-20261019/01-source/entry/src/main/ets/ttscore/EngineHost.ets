/**
 * 引擎宿主 —— 对应安卓端 TTSService 的合成编排层(简化版):
 *   onSynthesizeText(request, callback) → 选 Speaker → 按引擎类型分发 → 回调音频
 * 鸿蒙无 TextToSpeechService,phase1 提供 await 风格 API 给 UI 直接调用;
 * 后台朗读/AVSession 接入放在 phase3。
 */
import { Engine, Speaker, SynthProgress } from './TtsModels';
import { HttpTtsClient, HttpTtsRequest } from './HttpTtsClient';
import { WavParser, PcmData } from './WavParser';
import { TextSanitizer } from './TextSanitizer';
import { LocalEngineBridge, LocalVoice } from './LocalEngineBridge';

export class SynthResult {
  pcm: ArrayBuffer = new ArrayBuffer(0);
  sampleRate: number = 24000;
  channels: number = 1;
  bitsPerSample: number = 16;
  durationMs: number = 0;
  contentType: string = '';
}

/** MultiTTS 内置变量表(RequestObject 展开时可用) */
export class EngineHost {
  /**
   * 合成一段文本。
   * phase1 支持 httpTts 引擎;local/system 引擎类型抛"暂未实现"(接口已预留)。
   */
  static async synthesize(engine: Engine, speaker: Speaker, text: string,
    onProgress: ((p: SynthProgress) => void) | null = null): Promise<SynthResult> {
    // 文本净化(数字读法/符号/URL 折叠), 对齐安卓端 replace 层职责
    const clean = TextSanitizer.sanitize(text);
    if (engine.type === 'local') {
      // 本地 sherpa-onnx 引擎: param 存 LocalVoice.id
      const voice = new LocalVoice();
      voice.id = speaker.code;
      voice.modelDir = speaker.param.length > 0 ? speaker.param : speaker.code;
      voice.sampleRate = speaker.sampleRate > 0 ? speaker.sampleRate : 24000;
      const r = await LocalEngineBridge.synthesize(voice, clean, speaker.speed > 0 ? speaker.speed : 1);
      const localOut = new SynthResult();
      localOut.pcm = r.pcm;
      localOut.sampleRate = r.sampleRate;
      localOut.channels = 1;
      localOut.bitsPerSample = 16;
      localOut.durationMs = WavParser.pcmDurationMs(r.pcm, r.sampleRate, 1, 16);
      localOut.contentType = 'audio/pcm';
      return localOut;
    }
    if (engine.type !== 'httpTts') {
      throw new Error(`引擎类型 ${engine.type} 暂未实现(规划: system=系统TTS)`);
    }
    const vars = EngineHost.buildTemplateVars(speaker, text);    const req = HttpTtsClient.parseTemplate(engine.url, vars, null);
    HttpTtsClient.mergeHeaderInto(req.headers, engine.header);

    const p1 = new SynthProgress();
    p1.state = 'requesting';
    p1.message = `${speaker.name} 请求合成服务`;
    if (onProgress !== null) {
      onProgress(p1);
    }
    const raw = await HttpTtsClient.fetchAudio(req);
    const p2 = new SynthProgress();
    p2.state = 'decoding';
    p2.progress = 60;
    p2.message = '解析音频';
    if (onProgress !== null) {
      onProgress(p2);
    }
    const out = new SynthResult();
    if (WavParser.isWav(raw)) {
      const wav = WavParser.parse(raw);
      out.pcm = wav.pcm;
      out.sampleRate = wav.sampleRate;
      out.channels = wav.channels;
      out.bitsPerSample = wav.bitsPerSample;
      out.durationMs = wav.durationMs;
      out.contentType = 'audio/wav';
    } else {
      // phase1 约定: 非 WAV 一律按 16bit 单态裸 PCM 处理(与 MultiTTS 音色包 param
      // 里声明 sampleRate 的裸流音色对齐);mp3/ogg 解码在 phase2 引入。
      const rate = speaker.sampleRate > 0 ? speaker.sampleRate : 24000;
      out.pcm = raw;
      out.sampleRate = rate;
      out.channels = 1;
      out.bitsPerSample = 16;
      out.durationMs = WavParser.pcmDurationMs(raw, rate, 1, 16);
      out.contentType = 'audio/pcm';
    }
    const p3 = new SynthProgress();
    p3.state = 'done';
    p3.progress = 100;
    p3.message = `完成, ${out.durationMs}ms`;
    if (onProgress !== null) {
      onProgress(p3);
    }
    return out;
  }

  /** 模板变量表 —— 变量名与 MultiTTS/legado httpTTS 生态对齐 */
  static buildTemplateVars(speaker: Speaker, text: string): Record<string, string> {
    const vars: Record<string, string> = {};
    vars['text'] = encodeURIComponent(text);
    vars['textRaw'] = text;
    const speed = speaker.speed > 0 ? speaker.speed : 1;
    const volume = speaker.volume > 0 ? speaker.volume : 1;
    const pitch = speaker.pitch > 0 ? speaker.pitch : 1;
    // MultiTTS 常见三种语速口径全量提供,音色模板按需取用
    vars['speed'] = String(speed);
    vars['volume'] = String(volume);
    vars['pitch'] = String(pitch);
    vars['percent'] = String(Math.round(speed * 100)); // 100=1x(原版 voice_speed 口径)
    vars['rate'] = String(Math.round(speed * 50)); // 50=1x(系统 TTS 口径)
    vars['speaker'] = speaker.code;
    vars['speakerName'] = speaker.name;
    vars['locale'] = speaker.locale;
    return vars;
  }

  /** 预检: 引擎+音色能否直接出请求(供 UI 校验,不发网络) */
  static dryRun(engine: Engine, speaker: Speaker, sampleText: string): HttpTtsRequest {
    const vars = EngineHost.buildTemplateVars(speaker, sampleText);
    const req = HttpTtsClient.parseTemplate(engine.url, vars, null);
    HttpTtsClient.mergeHeaderInto(req.headers, engine.header);
    return req;
  }
}
