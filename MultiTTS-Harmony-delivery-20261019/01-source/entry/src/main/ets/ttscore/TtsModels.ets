/**
 * 数据模型层 —— 移植自 multitts-reverse:
 *   org.nobody.multitts.tts.speaker.Speaker
 *   org.nobody.multitts.tts.engine.Engine
 * 仅保留合成链路必需字段;extendUI/分组/头像等 UI 字段在后续阶段补齐。
 */

/** 合成进度事件(TTSService 进度回调的鸿蒙对应物) */
export class SynthProgress {
  state: string = 'idle'; // idle|parsing|requesting|decoding|playing|done|error
  progress: number = 0; // 0..100, 合成阶段;播放阶段恒为 -1
  message: string = '';
}

/** 引擎定义 —— 对应 Engine.java 的 JSON 结构(可直接接 MultiTTS 引擎导出) */
export class Engine {
  code: string = '';
  name: string = '';
  /** phase1 固定 httpTts;后续扩展 system/local(sherpa-onnx) */
  type: string = 'httpTts';
  /** 请求模板: <url>[, {json 选项}] 支持 {{var}} */
  url: string = '';
  /** 请求头,支持 "k: v" 多行或 JSON 对象 */
  header: string = '';
  /** 引擎参数(并发/超时/采样率声明等,phase1 解析 timeout 与 sampleRate) */
  param: string = '';
  note: string = '';
}

/** 发音人定义 —— 对应 Speaker.java */
export class Speaker {
  id: string = '';
  code: string = '';
  name: string = '';
  locale: string = '';
  /** 0=female 1=male(Speaker.java 语义) */
  gender: number = 0;
  /** 0=本地包 1=其他(httpTTS 在 MultiTTS 中记为 1) */
  type: number = 1;
  group: string = '';
  desc: string = '';
  note: string = '';
  /** 声明采样率;0 表示由响应自描述(WAV 头)决定 */
  sampleRate: number = 0;
  speed: number = 1.0;
  volume: number = 1.0;
  pitch: number = 1.0;
  param: string = '';
}
