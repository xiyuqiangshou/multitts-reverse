# MultiTTS-Harmony · 移植工程

MultiTTS（安卓离线 TTS 引擎聚合器）→ HarmonyOS NEXT 的原生重写移植。
源参照：[xiyuqiangshou/multitts-reverse](https://github.com/xiyuqiangshou/multitts-reverse)（MultiTTS 1.8.3 逆向可编译工程）。

## 当前状态

**Phase 2 完成** —— 三引擎架构 + 完整周边（纯逻辑层 59 项行为测试全过）：

| 模块 | 移植自 | 状态 |
|---|---|---|
| `ttscore/RuleAnalyzer.ets` | `plugin/httptts/RuleAnalyzer.java` | ✅ `{{...}}` 定位 + `url, {选项}` 拆分，语义对齐原版 innerRule/paramPattern |
| `ttscore/HttpTtsClient.ets` | `plugin/httptts/RequestObject.java` | ✅ 模板展开、选项 JSON、header 双格式、二进制请求 |
| `ttscore/SafeEvaluator.ets` | Rhino `java.*` 桥（RequestObject.Java） | ✅ **白名单 AST 求值器**：字符串/三元/`??`/算术/比较 + `Java.md5Encode32/16, urlEncode/Decode, base64Encode/Decode, timeFormat, parseInt…` + `Math.*` + 字符串方法；拒绝原型逃逸/全局对象/属性访问 |
| `ttscore/Md5.ets` | java.security.MessageDigest | ✅ 纯 TS MD5（含 UTF-8 中文），测试向量校验 |
| `ttscore/TextSanitizer.ets` | `tts/replace` 包职责 | ✅ 数字读法（含组间补零：10001→一万零一）、`50%`→百分之五十、年份、小数、URL 折叠、装饰行移除 |
| `ttscore/SentenceSplitter.ets` | TTSService 分句逻辑 | ✅ 强/弱标点两级切分 + 偏移定位（供高亮）+ 数字单位避让 |
| `ttscore/VoiceStore.ets` | Room 数据库层 | ✅ **MultiTTS 与 legado httpTTS JSON 双格式导入**、去重覆盖、preferences 持久化 |
| `ttscore/ForwardServer.ets` | `service/ForwardService.java` | ✅ 局域网转发：`GET /voices`、`GET /tts?text=&voice=&rate=` → WAV（socket HTTP/1.1 服务端） |
| `ttscore/LocalEngineBridge.ets` | 本地语音包引擎位 | 🔶 契约就绪，sherpa-onnx NAPI 桥接待真机（无 SDK 环境无法编译原生库） |
| `ttscore/TtsPlayer.ets` | AudioTrack 链 | ✅ AudioRenderer PCM 播放（需真机验证） |
| `ttscore/EngineHost.ets` | `service/TTSService.java` 编排 | ✅ httpTts/local 双引擎分发 + 净化管线 + dryRun 预检 |
| `pages/Index.ets` | 主界面 | ✅ 三 Tab：试听 / 音色管理（导入/删除）/ 转发服务开关 |

**测试**：`node --experimental-strip-types ../tools/run-core-tests.js` → **59 pass / 0 fail**

## 与安卓 MultiTTS 的功能对照

| 能力 | 安卓 | 鸿蒙版 | 说明 |
|---|---|---|---|
| HTTP TTS 音色 | ✅ | ✅ | 模板/变量/签名函数全兼容 |
| legado httpTTS JSON 导入 | ✅ | ✅ | `legado://import/httpTTS` 生态直读 |
| 本地语音包（微软/三星/Vocalizer…） | ✅ | ❌→🔶 | Android ELF + 包格式封闭，以 sherpa-onnx 开源模型替代（kokoro/vits） |
| 系统级 TTS 引擎注册 | ✅ | ❌ | 鸿蒙 NEXT 无开放 API；朗读联动改走 legado-Harmony 音色导入或本工程转发服务 |
| 局域网转发 | ✅ | ✅ | 协议对齐（HTTP TTS API） |
| 替换净化/数字读法 | ✅ | ✅ | 核心读法规则已实现 |
| 多音字/拼音修正 | ✅ | ⏳ | 需拼音数据表，phase3 |
| 后台朗读/AVSession | ✅ | ⏳ | phase3（真机调试项） |

## Phase 3（真机环境）

1. **sherpa-onnx NAPI**：`LocalEngineBridge` 契约已定 —— 编译 `libsherpa-onnx-c-api.so`（OHOS 三元组）→ 注入 `nativeGenerate` → 模型放 rawfile
2. **后台朗读**：backgroundModes audio 已声明（module.json5），接 AVSession + 长时任务
3. **系统 TTS 兜底**：鸿蒙 textToSpeech Kit 作为 `system` 引擎类型
4. **多音字表**：`TextSanitizer` 预留管线

## 构建与验证

```
# 逻辑层测试(无需鸿蒙环境, Node ≥ 22):
cd tools && node --experimental-strip-types run-core-tests.js   # 59 pass
# 语法结构检查:
node check-braces.js $(find MultiTTS-Harmony -name "*.ets")

# 真机/模拟器: DevEco Studio 打开 MultiTTS-Harmony → 自动 sync → 运行 entry
#   试听 Tab: 粘贴任意 httpTTS 音色模板 → 试听
#   音色 Tab: 粘贴 legado httpTTS JSON 批量导入
#   服务 Tab: 开启转发 → 局域网 legado 配置 http://<鸿蒙机IP>:18080
```

## 已知约束（如实声明）

- **无真机验证**：TtsPlayer / ForwardServer / VoiceStore 依赖鸿蒙运行时，已在 DevEco 外尽力静态验证（结构配平 + 逻辑层 59 测试），上真机仍可能遇到 SDK API 版本差异 —— 需要一轮真机调试
- **本地引擎未编译**：sherpa-onnx 原生库需 OHOS SDK 交叉编译环境
- **系统级 TTS 不可行**：鸿蒙 NEXT 平台限制（非工程问题）

## 法律说明

移植仅针对逆向仓库中 MultiTTS 本体的可编译源码逻辑，以学习研究为目的；
原应用版权归原作者，语音包（微软/三星/Vocalizer/字节等）不随本工程分发。
