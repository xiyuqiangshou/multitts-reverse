# 鸿蒙如何利用安卓 legado 书源 —— 源码级调研报告

> 调研时间：2026-09-23 · 样本仓库：mgz0227/legado-Harmony、wip3l/legado-harmony（开源轻页）
> 关联任务：MultiTTS (multitts-reverse) 鸿蒙移植可行性评估

---

## 1. legado 鸿蒙生态现状（重要前提）

| 项目 | 状态 | 说明 |
|---|---|---|
| **mgz0227/legado-Harmony** (2.17k★) | ⚠️ **已闭源** | README 原话："由于盗版猖獗……鸿蒙版将不再开源"。公开仓库只剩 HAP 安装包分发 + 图标资源，源码转入私有仓库 `mgz0227/Harmony-Legado`。技术栈 TypeScript/ArkTS，依赖 @ohos/axios、dayjs |
| **wip3l/legado-harmony**（开源轻页，16★） | ✅ **完整开源** | GPL-3.0，活跃维护（4 天前更新）。HarmonyOS NEXT（API 12–26），ArkTS/ArkUI，phone/tablet/2in1。目标就是"**验证用户自行提供的 legado 书源规则**在鸿蒙端的导入、搜索、发现、详情、目录、正文全链路解析" |

**结论：想看"鸿蒙怎么跑 legado 书源"，唯一完整的开源教材是 wip3l/legado-harmony。** 以下全部结论基于其实源码。

---

## 2. 核心机制：鸿蒙是这样"吃下"安卓书源的

安卓 legado 书源 = JSON 规则 + 内嵌 Rhino JS 脚本 + Java 正则 + java.* API 调用。鸿蒙没有 ART/Rhino，开源轻页的做法是**六层重建**：

### 2.1 规则引擎纯 ArkTS 重写（不是移植 Android 代码）
`entry/src/main/ets/core/rule/`（合计 ~7800 行 TS/ETS）：
- `AnalyzeRule.ts`（**2882 行**）：legado 规则主解析器（@选择器、`##` 排除、jsonpath、混合规则、`{{}}`、`@get:{}`），对外暴露 `getString / getStringList / getElements`，与安卓版同名同语义
- `AnalyzeUrl.ts`（678 行）：URL 规则模板（POST body、`{{key}}`、`searchUrl` 编码选项）
- `JsonPathEvaluator.ts`（372 行）+ `JavaRegexCompat.ts`（**484 行**）：**Java 正则方言 → JS 正则**自动转换（命名组、前后断言差异、flag 差异）
- `AjaxRuleCompat.ts`：webview ajax 规则兼容

### 2.2 书源内嵌 JS：QuickJS 原生引擎（NAPI）
ArkTS 禁 eval，所以书源里的 `<js>...</js>` 与 `@js:` 用 **QuickJS C 库**跑：
- 独立模块 `quickjs/`：CMakeLists + napi_init.cpp（Engine/Value 双注册表）+ `libquickjs.a`（arm64-v8a、x86_64 静态库）
- `JsRuntime.ts`（793 行）+ `ScriptEngine.ts`（1433 行）：把 `source`、`baseUrl`、`cookie`、`cache`、`content` 等安卓书源脚本依赖的上下文对象注入 QuickJS 全局

### 2.3 java.* API 兼容层（精髓所在，ScriptCompatibility.ts）
对书源脚本做**文本级改写 → 白名单内建函数**，模拟安卓 legado 暴露给脚本的 Java API：

| 书源脚本写的 | 鸿蒙层改写为 |
|---|---|
| `java.base64Encode/Decode(...)`（含 android.util.Base64 变体） | 内建 base64 实现 |
| `URLEncoder.encode / URLDecoder.decode` | `java.urlEncode/urlDecode` → encodeURIComponent 系列 |
| `System.currentTimeMillis()` | `Date.now()` |
| `Integer.parseInt / Long.parseLong / String.valueOf` | `parseInt / String(...)` |
| `new StringBuilder / HashMap / ArrayList` | 内建 shim 类 |
| `TextUtils.isEmpty(...)` | `isEmpty(...)` |
| `java.md5Encode32 / sha256Encode / timeFormat / put / get` | 内建摘要/时间/变量函数（`JsRuntime.ts` L90–123） |
| `Cipher(AES/DES/3DES) + SecretKeySpec + MessageDigest` | 映射到鸿蒙 cryptoFramework（支持的加密流程白名单） |
| `java.lang.Thread.sleep(...)` | 直接删（鸿蒙请求管线已是异步） |

**显式拒绝（安全白名单，`unsupportedReason()`）**：Runtime/ProcessBuilder、反射、文件 IO、sqlite、Intent、WebView 扩展、Rhino 专有 API → 书源脚本请求这些能力时直接给出"未映射"提示，**宁可失败也不给导入的书源任何越权能力**。

### 2.4 导入与往返保真
- 支持 URL/剪贴板/文件/`legado://import/{path}?src={url}` 唤起导入；path 类型含 `bookSource`、`replaceRule`、`httpTTS`、`theme` 等（与安卓 legado 完全一致）
- 导入时**保留原始 JSON + 未知字段**，导出时合并回写，尽量保持安卓书源"往返无损"
- 替换净化可直接导入 Android legado / YueDu 导出的规则文件，Java 正则自动转换

### 2.5 书源调试与校验
- 单源/批量校验，区分"通过/失败/无结果/需要验证/暂时异常"
- `BookSourceDebug` 页面按搜索→发现→详情→目录→正文分段运行，展示每步请求与规则匹配结果 —— 这是规则兼容性的"试金石"

### 2.6 TTS 链路（对 MultiTTS 移植最有参考价值）
- `SystemTtsReader.ets`（**3013 行**）：系统 TTS，PCM + AudioRenderer 输出、句子焦点高亮、分句算法
- `HttpTtsReader.ets`（1086 行）：**可直接导入原版 HttpTTS JSON**——URL 模板、请求头、Content-Type、并发限流（TtsRateBucket）、重试、语速变量、Cookie jar、登录脚本；合成结果按 PCM/文件双路走 AudioRenderer
- 有声书独立播放器 + AVSession 锁屏控制 + 后台续播

---

## 3. 对 MultiTTS 鸿蒙化的修正结论

之前判断"鸿蒙无系统级第三方 TTS 注册通道"仍成立（API 现状如此），但 wip3l 的代码给出了一条**此前被低估的现实桥**：

### 路线 A（桥接，立即可用）：安卓 MultiTTS 当"合成后端"，鸿蒙 App 当"前端"
1. 安卓手机上 MultiTTS 开启**局域网转发**（ForwardService / 快捷磁贴 QuickHttpTileService，Manifest 已证实存在），暴露 HTTP TTS API —— 社区插件（chrome-local-tts 等）就是这么用它的
2. 把该音色导出为 legado **HttpTTS JSON**（`legado://import/httpTTS?src=...` 或剪贴板）
3. 鸿蒙端开源轻页（或自研 App 复用其 HttpTtsReader）导入后即可朗读 —— **安卓的语音包能力原样桥进鸿蒙**，只是依赖局域网 + 安卓端在线
- ✅ 零开发也能跑通（用现成开源轻页）；⚠️ 离线不可用

### 路线 B（原生重写，1–2 人月级）：复用 wip3l 全套地基
- 合成层：sherpa-onnx HAR（社区已适配 OpenHarmony）+ 开源模型（kokoro/vits/piper），PCM → AudioRenderer（照抄 TtsPcmAudioPlayer 模式）
- 音色管理/分组/替换净化/TTS 设置页：参考 `TtsSettings.ets`、`CustomTtsVoiceManager.ets`
- 如果目标只是"TTS 引擎 App"而非阅读器，从 wip3l 拆 core/rule + utils/Tts* 四个模块即可起步

### 路线 C（等官方）：闭源版 legado-Harmony 已有 2.17k 星用户盘，可通过其 API/社区反馈推动 HttpTTS 音色生态兼容 —— 但源码不可见，只能当分发渠道

---

## 4. 可直接复用的工程资产清单

| 资产 | 来源 | 用途 |
|---|---|---|
| AnalyzeRule / AnalyzeUrl / JavaRegexCompat | wip3l `core/rule/` | 任何要读 legado 书源/替换规则的鸿蒙 App |
| QuickJS NAPI 模块（含预编译 .a） | wip3l `quickjs/` | 书源脚本、动态音色脚本执行 |
| ScriptCompatibility 白名单 | wip3l `core/rule/ScriptCompatibility.ts` | java.* API 兼容的安全范式 |
| HttpTtsReader + 限流 + PCM 播放 | wip3l `utils/HttpTtsReader.ets`、`TtsPcmAudioPlayer` | HTTP TTS 音色消费端 |
| legado://import 唤起协议 | legado README / wip3l 实现 | 跨 App 导入书源/音色 |
| MultiTTS 可编译工程 | xiyuqiangshou/multitts-reverse | 安卓端维持合成后端/导出音色 |
| 官方鸿蒙版 HAP | mgz0227/legado-Harmony Releases | 用户体验基准 + 书源兼容性对照 |

---

## 5. 一句话总结

**鸿蒙"利用安卓 legado 书源"靠的不是移植 Android 代码，而是 ArkTS 重写规则引擎 + QuickJS 跑脚本 + 白名单模拟 java.* API 的"三件套"（开源轻页已全部开源验证）。** 对 MultiTTS 而言，最短路径是先用 HttpTTS JSON 桥（零开发、依赖安卓端局域网），再以 sherpa-onnx + wip3l 地基做原生离线替代。
