/**
 * PCM 播放器 —— 对应安卓端 AudioTrack 链路,鸿蒙用 AudioRenderer。
 * 参考 wip3l/legado-harmony 的 TtsPcmAudioPlayer 模式: 先设流参数再逐块写。
 */
import audio from '@ohos.multimedia.audio';

export class TtsPlayer {
  private renderer: audio.AudioRenderer | null = null;
  private stopped: boolean = false;

  async playPcm(pcm: ArrayBuffer, sampleRate: number, channels: number,
    bitsPerSample: number): Promise<void> {
    await this.stopInternal();
    this.stopped = false;

    const streamInfo: audio.AudioStreamInfo = {
      samplingRate: TtsPlayer.mapRate(sampleRate),
      channels: channels >= 2 ? audio.AudioChannel.CHANNEL_2 : audio.AudioChannel.CHANNEL_1,
      sampleFormat: bitsPerSample === 8
        ? audio.AudioSampleFormat.SAMPLE_FORMAT_U8
        : audio.AudioSampleFormat.SAMPLE_FORMAT_S16LE,
      encodingType: audio.AudioEncodingType.ENCODING_TYPE_RAW
    };
    const rendererInfo: audio.AudioRendererInfo = {
      content: audio.ContentType.CONTENT_TYPE_SPEECH,
      usage: audio.StreamUsage.STREAM_USAGE_AUDIOBOOK,
      rendererFlags: 0
    };
    const options: audio.AudioRendererOptions = {
      streamInfo: streamInfo,
      rendererInfo: rendererInfo
    };
    const renderer = await audio.createAudioRenderer(options);
    this.renderer = renderer;
    await renderer.start();
    // 32KB 块写入,写前查状态以便 stop() 及时打断(对应安卓端 stop() 语义)
    const chunk = 32768;
    for (let off = 0; off < pcm.byteLength; off += chunk) {
      if (this.stopped) {
        break;
      }
      const end = Math.min(off + chunk, pcm.byteLength);
      const view = pcm.slice(off, end);
      if (renderer.state === audio.AudioState.STATE_RUNNING) {
        await renderer.write(view);
      } else {
        break;
      }
    }
    if (!this.stopped && renderer.state === audio.AudioState.STATE_RUNNING) {
      await renderer.drain();
    }
    await this.stopInternal();
  }

  async stop(): Promise<void> {
    this.stopped = true;
    await this.stopInternal();
  }

  private async stopInternal(): Promise<void> {
    const r = this.renderer;
    this.renderer = null;
    if (r === null) {
      return;
    }
    try {
      if (r.state === audio.AudioState.STATE_RUNNING) {
        await r.stop();
      }
      await r.release();
    } catch (e) {
      // 释放失败可忽略,下一次 play 会新建 renderer
    }
  }

  private static mapRate(rate: number): audio.AudioSamplingRate {
    const known: number[] = [8000, 11025, 16000, 22050, 24000, 32000, 44100, 48000, 64000, 96000];
    let best = 48000;
    let bestDiff = Number.MAX_VALUE;
    for (const k of known) {
      const d = Math.abs(k - rate);
      if (d < bestDiff) {
        bestDiff = d;
        best = k;
      }
    }
    const table: Record<number, audio.AudioSamplingRate> = {
      8000: audio.AudioSamplingRate.SAMPLE_RATE_8000,
      11025: audio.AudioSamplingRate.SAMPLE_RATE_11025,
      16000: audio.AudioSamplingRate.SAMPLE_RATE_16000,
      22050: audio.AudioSamplingRate.SAMPLE_RATE_22050,
      24000: audio.AudioSamplingRate.SAMPLE_RATE_24000,
      32000: audio.AudioSamplingRate.SAMPLE_RATE_32000,
      44100: audio.AudioSamplingRate.SAMPLE_RATE_44100,
      48000: audio.AudioSamplingRate.SAMPLE_RATE_48000,
      64000: audio.AudioSamplingRate.SAMPLE_RATE_64000,
      96000: audio.AudioSamplingRate.SAMPLE_RATE_96000
    };
    const hit = table[best];
    return hit !== undefined ? hit : audio.AudioSamplingRate.SAMPLE_RATE_48000;
  }
}
