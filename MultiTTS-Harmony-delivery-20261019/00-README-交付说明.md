# MultiTTS-Harmony 交付包（2026-10-19 · 已含修复后重新编译的 HAP）

一包拿走，直接在 Windows 上继续开发 / 编译 / 装机。

| 目录 | 内容 | 用途 |
|---|---|---|
| `01-source/` | ArkTS 工程源码（23 个源文件，2551 行 ArkTS，252 KB，已剔除 build/.hvigor 缓存） | DevEco Studio 直接 Open 这个目录 |
| `02-docs/` | `pre-install-checklist.md`（装机验收单）、`legado-booksource-analysis.md`（书源/TTS 接口分析）；架构说明见 `01-source/README.md` | 阅读 |
| `03-signing/` | 自签三级证书链（root→subca→app）、debug profile p7b、keystore.p12 | 只在要命令行重签时需要 |
| `04-scripts/` | `run-core-tests.js`（59 项逻辑测试）、`check-braces.js`、`gen-icons.js`、`build-linux.sh` | 见下 |
| `05-hap/` | `entry-default-signed-20261019.hap`（267 KB，**修复后重新编译并签名，可直接装**） | `hdc install` |

包体积：**约 628 KB**（压缩后 180 KB 左右）。

---

## 一、为什么这么小？

这个工程本来就只有这么大。它的真实体量：

| 组成 | 大小 | 说明 |
|---|---|---|
| ArkTS 源码 | **2551 行 / 252 KB** | 16 个 `.ets` 文件，最大的是 `SafeEvaluator.ets`（777 行） |
| 配置文件 | 79 行 | 5 个 `.json5` + 2 个 `.ts` 构建脚本 |
| 已签名 HAP | 267 KB | 编译产物，里面 `ets/modules.abc` 只有 145 KB |
| 文档 | 16 KB | 2 篇分析文档 + README |
| 签名材料 | 48 KB | 证书链 + keystore |

**被排除在包外的大件**（这些本来就不该进交付包）：

| 项目 | 大小 | 为什么不打包 |
|---|---|---|
| `.hvigor/` 构建缓存 | 4.0 MB | DevEco 打开工程会自动重建 |
| `entry/build/` 中间产物 | 2.7 MB | 同上，含 `.ts_checker_cache`、`.tsbuildinfo` 等 |
| `command-line-tools` 工具链 | **9.6 GB** | 鸿蒙 SDK + hvigor + ohpm，Windows 上装 DevEco Studio 自带 |
| `multitts-reverse` 参考工程 | 482 MB | 上游 Android 逆向工程，只是移植参照物 |
| `wip-legado` 工作副本 | 93 MB | 中间实验目录 |

所以"204 KB"不是压缩丢东西，而是：**一个 2551 行的鸿蒙工程 + 一个 267 KB 的 HAP**，压缩后就这个量级。对比参照：一个空的 Hello World 鸿蒙工程打包也就 40–60 KB。

---

## 二、Windows 上怎么拿这个包

### 方式 A：聊天附件（当前唯一实测可用）
本对话附带的 zip，点一下即可下载。

### 方式 B / C：HTTP 直连、scp
云端实测：只有内网 `10.1.23.201`，**无默认路由、无 sshd、未对公网开放端口** → 这两条默认走不通，除非平台侧做端口映射。

### 方式 D：网盘中转
zip 只有 152 KB / 59 KB，随便哪个网盘都行，秒传。

---

## 三、Windows 上跑起来

### 3.1 直接装 HAP（最快）
```powershell
hdc list targets
hdc install .\05-hap\entry-default-signed-20261019.hap
```
> 这个包是 2026-10-19 用修复后的源码**重新编译并签名**的，`hap-sign-tool verify-app` 返回 `Verify success`。
> 若真机报 `9568289 / 签名校验失败`：换成你自己华为账号的自动签名重打（见 3.2），本地自签只能用于自己调试。

### 3.2 DevEco Studio 编译（可控）
1. DevEco Studio 5.0+ → **Open** → 选 `01-source/`（含 `oh-package.json5`）。
2. 等 Sync（首次拉 ohpm 依赖，需联网）。
3. **File → Project Structure → Signing Configs** → 勾 **Automatically generate signature**。
4. 连真机 / 模拟器 → Run。

### 3.3 跑逻辑测试（无需鸿蒙环境，Node ≥ 22）
```powershell
cd 04-scripts
node --experimental-strip-types .\run-core-tests.js
```
预期 `59 pass, 0 fail`。脚本自动在 `../01-source` 找源码，也可用 `$env:MTTS_SRC` 指定。

### 3.4 命令行构建
`build-linux.sh` 是 Linux 版；Windows 走 WSL2 最省事。

---

## 四、本次修复记录

`01-source/entry/src/main/ets/ttscore/SafeEvaluator.ets` 补了 **12 处** AST 节点字段赋值：

| 方法 | 补充字段 |
|---|---|
| `parseTernary` | `cond / yes / no` |
| `parseNullish` / `parseOr` / `parseAnd` | `left / right` |
| `parseCompare` / `parseAdd` / `parseMul` | `op / left / right` |
| `parseUnary` | `op / operand` |
| `parsePostfix` | `args`（call）、`name`（member） |

- 修复前：`Java.md5Encode32('abc')` 返回空串 MD5、`Math.max` 返回 `-Infinity`、`.replace/.substring` 不生效 → **52 pass / 7 fail**
- 修复后：**59 pass / 0 fail**，括号配平检查全过，`hvigorw assembleHap` BUILD SUCCESSFUL

---

## 五、安全提醒

1. `03-signing/keystore.p12` 密码 `123456`，证书本地自签（CN=root / MultiTTS CA），**只能自己调试，不能上架**。
2. 正式分发用你自己的华为开发者账号重签，并删除 `03-signing/`。
3. 包内无账号、无 API Key、无用户数据。
4. 进 Git 前 `.gitignore`：`03-signing/`、`05-hap/`、`entry/build/`、`.hvigor/`。

---

## 六、校验值

```
ArkTS 源码：16 个文件 / 2551 行
逻辑测试：59 pass / 0 fail
HAP SHA-256：07121fc479d9113ae5ad8f3901517de20381d8a93b8585148b9e2d5bc71f8867
签名校验：hap-sign-tool verify-app → Verify success
hvigor 构建：BUILD SUCCESSFUL in 14 s 209 ms
```
