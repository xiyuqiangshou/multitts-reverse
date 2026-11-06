/**
 * WAV 解析器 —— TTS 服务最常见返回是 16bit PCM WAV。
 * MultiTTS 安卓端在 AudioTrack 层喂流;鸿蒙 AudioRenderer 需要显式采样率/声道参数,故先解 RIFF。
 */

export class PcmData {
  pcm: ArrayBuffer = new ArrayBuffer(0);
  sampleRate: number = 24000;
  channels: number = 1;
  bitsPerSample: number = 16;
  durationMs: number = 0;
}

export class WavParser {
  static isWav(buf: ArrayBuffer): boolean {
    if (buf.byteLength < 12) {
      return false;
    }
    const dv = new DataView(buf);
    const riff = WavParser.fourCC(dv, 0);
    const wave = WavParser.fourCC(dv, 8);
    return riff === 'RIFF' && wave === 'WAVE';
  }

 /** 遍历 RIFF chunk 提取 fmt 与 data;不合法结构抛错 */
  static parse(buf: ArrayBuffer): PcmData {
    if (!WavParser.isWav(buf)) {
      throw new Error('不是合法的 WAV(RIFF) 数据');
    }
    const dv = new DataView(buf);
    let off = 12;
    let fmtFound = false;
    const out = new PcmData();
    while (off + 8 <= buf.byteLength) {
      const id = WavParser.fourCC(dv, off);
      const size = dv.getUint32(off + 4, true);
      const body = off + 8;
      if (id === 'fmt ') {
        out.channels = dv.getUint16(body + 2, true);
        out.sampleRate = dv.getUint32(body + 4, true);
        out.bitsPerSample = dv.getUint16(body + 14, true);
        fmtFound = true;
      } else if (id === 'data') {
        const len = Math.min(size, buf.byteLength - body);
        out.pcm = buf.slice(body, body + len);
        break;
      }
      off = body + size + (size % 2); // chunk 按 2 字节对齐
      if (size === 0 && id === 'data') {
        break;
      }
    }
    if (!fmtFound || out.pcm.byteLength === 0) {
      throw new Error('WAV 缺少 fmt 或 data 块');
    }
    out.durationMs = WavParser.pcmDurationMs(out.pcm, out.sampleRate, out.channels, out.bitsPerSample);
    return out;
  }

  static pcmDurationMs(pcm: ArrayBuffer, sampleRate: number, channels: number,
    bitsPerSample: number): number {
    if (sampleRate <= 0 || channels <= 0 || bitsPerSample <= 0) {
      return 0;
    }
    const bytesPerFrame = channels * (bitsPerSample / 8);
    const frames = pcm.byteLength / bytesPerFrame;
    return Math.round(frames * 1000 / sampleRate);
  }

  private static fourCC(dv: DataView, off: number): string {
    let s = '';
    for (let i = 0; i < 4; i++) {
      s += String.fromCharCode(dv.getUint8(off + i));
    }
    return s;
  }
}
