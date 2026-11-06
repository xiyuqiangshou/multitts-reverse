# MultiTTS-Harmony · 鸿蒙版 MultiTTS

> **⚠️ 重要声明**
> - 这是一个 **HarmonyOS NEXT（鸿蒙）应用**（ArkTS / Stage 模型），不是安卓应用；
> - **尚未进行真机验证**——代码仅在模拟器环境完成安装测试，核心逻辑层通过 59 项行为测试（59 pass / 0 fail），但朗读、音频播放、局域网转发等运行时功能**未经真机实测**；
> - 本仓库**仅供学习与参考**，不构成可用性承诺，请勿直接投入生产使用。

## 这是什么

MultiTTS（安卓离线 TTS 引擎聚合器）→ HarmonyOS NEXT 的原生重写移植工程。
源参照：[MultiTTS 1.8.3 逆向可编译工程](https://github.com/xiyuqiangshou/multitts-reverse)。

- **语言/框架**：ArkTS（TypeScript 超集）+ ArkUI，Stage 模型，API 12+
- **包名**：`org.nobody.multitts.harmony`
- **体量**：16 个 `.ets` 源文件，2551 行 ArkTS
- **测试**：`node --experimental-strip-types MultiTTS-Harmony-delivery-20261019/04-scripts/run-core-tests.js` → **59 pass / 0 fail**

## 核心能力（代码层面已完成）

| 能力 | 说明 |
|---|---|
| HTTP TTS 音色 | 模板/变量/签名函数（md5/base64/urlEncode 等）全兼容 MultiTTS 与 legado httpTTS |
| legado 书源 JSON 导入 | `legado://import/httpTTS` 生态音色配置直读 |
| 安全表达式求值器 | 白名单 AST 求值（SafeEvaluator），拒绝原型逃逸/全局对象访问 |
| 文本净化 | 数字读法、百分号、年份、小数、装饰行移除 |
| 分句引擎 | 强/弱标点两级切分 + 偏移定位（供朗读高亮） |
| 局域网转发服务 | `GET /tts?text=&voice=&rate=` → WAV，协议对齐安卓版 |
| PCM 播放 | AudioRenderer 播放链（**需真机验证**） |
| 本地语音包 | 🔶 契约就绪，sherpa-onnx NAPI 桥**待真机环境开发** |

**未实现/受限**：系统级 TTS 引擎注册（鸿蒙 NEXT 无开放 API）、多音字拼音修正（phase3）、后台 AVSession 朗读（phase3）。

## 目录结构

```
MultiTTS-Harmony-delivery-20261019/
├── 00-README-交付说明.md      # 交付包总说明（构建/装机/验证全流程）
├── 01-source/                 # ArkTS 工程源码（DevEco Studio 直接 Open 此目录）
│   └── README.md              # 架构细节与安卓功能对照表
├── 02-docs/                   # 装机验收单 + legado 书源/TTS 接口分析
├── 03-signing/                # 自签三级证书链 + keystore（命令行重签用）
├── 04-scripts/                # 逻辑测试 / 图标生成 / 构建脚本
└── 05-hap/                    # 已签名 HAP（267 KB，hdc install 可直接装）
```

## 快速开始

```powershell
# 1. 安装 HAP（需 hdc，鸿蒙 command-line-tools / DevEco Studio 自带）
hdc install 05-hap/entry-default-signed-20261019.hap

# 2. 跑核心逻辑测试（无需鸿蒙环境，纯 Node）
node --experimental-strip-types 04-scripts/run-core-tests.js
```

在 DevEco Studio 中打开 `01-source/` 即可继续开发编译；命令行构建见 `00-README-交付说明.md`。

## 再次提醒

本项目**未经过真机验证**，音频播放、后台运行、转发服务等运行时行为均可能存在未知问题。欢迎参考、 fork 与改进，发现问题欢迎提 issue。
