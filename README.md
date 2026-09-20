# MultiTTS 逆向工程（1.8.3 修复版）

> **逆向目标**：MultiTTS `org.nobody.multitts` v1.8.3 (vc84) —— 作者失联、源码丢失的闭源 Android TTS 引擎聚合器。**本仓库未获得作者明确授权**；原应用为免费公益软件（不收费），作者失联前的公开言行亦不反对逆向研究。如作者或版权方对本仓库内容有异议，将及时下架。
> **验证载体**：本仓库源码工程（重建的可编译工程），仅用于承载静态逆向结论并生成可运行 APK 验证。

本仓库是 MultiTTS 1.8.3 的完整逆向工程产物：**从官方 APK 反编译 → 还原为可编译工程 → 修复缺陷 → 增强功能 → 一键重新打包**，并在真机（HyperOS）上完成系统级 TTS 协议验证。

## 修复与增强

| # | 内容 | 说明 |
|---|---|---|
| 1 | 语速滑条激活 | 原版主界面「语速」滑条只写不读（死 UI）；现已接入系统 TTS 链路（`voice_speed`，50=1x） |
| 2 | 系统语速/音调链路修复 | 排查并修复系统级 `tts_default_rate`/`tts_default_pitch` 异常值导致的飞快/含糊问题 |
| 3 | BGM 无声修复 | Media3 状态机死锁（空 playlist prepare→ENDED→不重置）；ASM 注入 `player.D()` 补丁 |
| 4 | BGM 试听按钮 | 引擎配置页新增音乐试听（独立 MediaPlayer，字节码注入实现） |
| 5 | okio 悬空引用修复 | `-Deprecated` 改名导致的字节级悬空 |
| 6 | new-instance 类型错位 | 573 位点/431 类 + j$ desugar 库 70 位点（官方对账替换） |
| 7 | 资源 ID 错位修复 | 放弃 public.xml 钉 typeId，改为**官方底包换 dex** 方案 |
| 8 | 反编译畸变修复 | MainActivity.onDestroy 双 super 等重编译畸变 |
| 9 | JNI 按名查找保护 | 保留原混淆字段名，避免 `libspeexdsp.so` 等原生层查找不到 |

最终验证：三引擎异构合成零崩溃、legado（阅读）朗读联动正常、系统「播放」出声正常。

## 目录结构

```
├── MultiTTS-Project/     重建的可编译源码工程（gradle, AGP）
│   └── app/src/main/     java 源码（含全部补丁）+ res + assets + jniLibs(arm64-v8a)
├── base/                 （Release 附件）官方 1.8.3 底包 multitts_orig.apk
├── keys/                 签名 keystore（androiddebugkey / android）
├── tools/swap_dex.py     dex-swap 脚本（官方底包为基座，STORED 方式换 classes*.dex）
├── build.sh              一键打包脚本（编译→换dex→对齐→签名→自检→清理）
└── out/                  （Release 附件）成品 mtts_arscfix_signed.apk
```

## 一键复现

```bash
cd mtts_build_kit
bash build.sh
# 产物: out/mtts_arscfix_signed.apk (~153MB, v1+v2 签名, adb install -r 可装)
```

外部依赖（需自行准备）：
- gradle 8.x、Android SDK（build-tools 的 zipalign/apksigner）、Python 3.x
- 官方底包 `base/multitts_orig.apk`：从本仓库 **Releases** 下载，或自备官方 1.8.3 APK（MD5 `b898a404…`）

### ⚠️ Windows 特有坑（build.sh 已内置处理）

1. **大小写互吞对**：`ui/setting/` 下同时存在 `A.java` 与 `a.java` 两个不同的类。任何复制/压缩工具（含 zip）都可能把它们合并——解压后必须校验两文件并存。
2. **AGP dexBuilder 大小写盲区**：per-class dex 归档目录未启用大小写敏感，`a.dex` 会覆盖 `A.dex` 静默丢类。build.sh 用 `fsutil file setCaseSensitiveInfo` + 编译后自检解决（Linux/macOS 无此问题）。

## 免责声明

- 本项目仅供**个人学习与研究** Android 逆向工程技术之用。
- MultiTTS 原应用版权归原作者所有。**本仓库未获得作者明确授权**：原应用为免费公益软件（不收费），作者失联前的公开言行不反对逆向研究；若作者或版权方提出异议，将**及时下架**本仓库相关内容。
- 请勿将本仓库内容用于商业用途。如有侵权请联系删除。
