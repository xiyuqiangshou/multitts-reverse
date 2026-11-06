/**
 * 本地合成引擎接口 —— sherpa-onnx 接入层(契约先行, NAPI 桥接 phase3 落地)。
 *
 * 设计: 引擎统一走 SynthEngine 接口, EngineHost 按 engine.type 分发:
 *   httpTts → HttpTtsClient(已实现)
 *   local   → LocalEngineBridge(本文件, sherpa-onnx NAPI)
 *   system  → 系统 TTS(鸿蒙 textToSpeech Kit, phase3)
 *
 * sherpa-onnx 接入步骤(真机环境):
 *   1. ohpm 适配包或自编 libsherpa-onnx-c-api.so 放入 libs/arm64-v8a
 *   2. 本文件 loadLibrary 替换为 dlopen + napi 函数绑定
 *   3. 模型文件(kokoro-82M INT8 / vits-zh)放 rawfile, 首次启动解压到沙箱
 *   4. 实现 synthesize(): 文本→phoneme→onnx→PCM float32→int16
 * 接口契约已由 tests 固定: 输出 16bit 单态 PCM + 采样率自描述。
 */

/** 本地引擎描述 */
export class NativeSynthResult {
  pcm: ArrayBuffer = new ArrayBuffer(0);
  sampleRate: number = 24000;
}

export class LocalVoice {
  id: string = '';
  name: string = '';
  /** 模型目录名(rawfile 下) */
  modelDir: string = '';
  sampleRate: number = 24000;
  /** zh / en / multilingual */
  lang: string = 'zh';
}

/** NAPI 桥接占位 —— 真机接入时替换实现体 */
export class LocalEngineBridge {
  private static loaded: boolean = false;
  private static voices: LocalVoice[] = [];

  /** 已注册本地音色(模型解压完成后调用 registerVoice) */
  static available(): boolean {
    return LocalEngineBridge.loaded;
  }

  static listVoices(): LocalVoice[] {
    return LocalEngineBridge.voices;
  }

  static registerVoice(v: LocalVoice): void {
    const idx = LocalEngineBridge.voices.findIndex((x) => x.id === v.id);
    if (idx >= 0) {
      LocalEngineBridge.voices[idx] = v;
    } else {
      LocalEngineBridge.voices.push(v);
    }
  }

  /**
   * 合成文本 → 16bit 单态 PCM。
   * phase3 实现: 调用 napi 模块 sherpa_onnx_offline_tts_generate。
   * 当前抛出明确错误(UI 可灰显本地音色), 不做静默降级。
   */
  static async synthesize(voice: LocalVoice, text: string, speed: number): Promise<NativeSynthResult> {
    if (!LocalEngineBridge.loaded) {
      throw new Error('本地引擎未就绪: sherpa-onnx NAPI 桥接尚未初始化(需真机构建 phase3)');
    }
    return LocalEngineBridge.nativeGenerate(voice.modelDir, text, speed);
  }

  /** NAPI 占位 —— 由 native 模块注入 */
  private static nativeGenerate(modelDir: string, text: string,
    speed: number): Promise<NativeSynthResult> {
    return Promise.reject(new Error('native bridge 未注入'));
  }
}
