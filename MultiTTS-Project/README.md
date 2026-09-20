# MultiTTS 还原工程（org.nobody.multitts 1.8.3 / versionCode 84）

由 APK 全量逆向还原出的 **可编译 Android Studio 工程**。原包 `minSdk 21 / targetSdk 35`，
共 11,162 个类。

---

## 1. 目录结构

```
MultiTTS-Project/
├─ settings.gradle / build.gradle / gradle.properties / local.properties
└─ app/
   ├─ build.gradle
   ├─ libs/deps.jar                 10,755 个第三方类（预编译，参与打包）
   └─ src/main/
      ├─ AndroidManifest.xml        原包 Manifest，包名 org.nobody.multitts
      ├─ java/                      347 个自有类源码（可维护部分）
      ├─ res/                       1,207 个资源文件
      ├─ assets/                    33 个（含模型、词典、html）
      └─ jniLibs/                   73 个 .so（arm64-v8a / armeabi-v7a）
```

## 2. 关键约束：必须开启 NTFS 区分大小写（**源码目录 + 构建输出目录，两处都要**）

R8 做了 **包名压平**，产生了 `o/g` 与 `o/G`、`R/q` 与 `R/Q`、`ui/setting/A` 与 `ui/setting/a`
这类**仅大小写不同**的类。Windows 默认不区分大小写，二者会互相覆盖。

### 2.1 两处都必须开启

```bat
fsutil.exe file setCaseSensitiveInfo D:\mtts_work\MultiTTS-Project\app\src\main\java enable
```

上面这条只保住了**源码**。**构建输出目录同样需要**，否则 javac 写出的 `A.class` 会被
`a.class` 覆盖，`dexBuilderDebug` 直接失败：

```
Caused by: com.android.tools.r8.internal.tw: Class content provided for type descriptor
  org.nobody.multitts.ui.setting.A actually defines class org.nobody.multitts.ui.setting.a
```

（2026-09-18 实测：一次 `gradle clean` 就把工程弄坏了 —— clean 删掉 `app/build` 后该目录的
CS 属性一起丢失，`BUILD FAILED`。此前增量构建一直是好的，所以这个坑很隐蔽。）

### 2.2 三条必须记住的规则

1. `fsutil.exe file setCaseSensitiveInfo <dir> enable` **只能在空目录上设置** ——
   非空会直接报 `错误: 0x00000091 目录不是空的`；
2. **新建子目录会自动继承**（已实测：在 `app/build` 上开启后，其下三层子目录全部"已启用"）；
3. 一旦目录被 `rm -rf` / `clean` 重建，属性就没了，必须重新执行。

### 2.3 已内置自愈钩子

`app/build.gradle` 的 `JavaCompile` 配置里已加 `doFirst` 钩子：编译前检查 javac 输出目录的 CS 状态，
未开启且目录为空则自动 `fsutil` 开启；**非空且未开启时打印明确的手动修复命令，不静默失败**。
所以正常情况下 **clean 之后直接 `assembleDebug` 即可**，不需要人工干预。

## 3. 源码加工流程（全部脚本在 `D:\mtts_work\mt_deobf\`）

按 1→5 顺序执行，可完整复现当前源码树：

| # | 脚本 | 作用 |
|---|---|---|
| 1 | `ownership_v3.py` | 从 11,162 个类中判定出自有 407 个（三条判据：包名 / 引用自有业务类 / 硬编码 0x7f 资源 ID） |
| 2 | `build_source_tree.py` | 407 个自有类 → jadx 产物映射；还原 jadx 的 `p098o0 → o0` 包名改写（180 条） |
| 3 | `fix_bare_string.py` | 415 处 StringFog 解密调用残桩（`"xxx";` 非法语句）转注释 |
| 4 | `fix_jadx_renames.py` | 还原 jadx 因文件名冲突而改的类名 `[前缀]C####原名 → 原名`（657 条、1,541 处） |
| 5 | `fix_jadx_fields.py` / `fix_jadx_methods.py` | 还原 jadx 改名的字段 `f####名`、方法 `mo#名`（15,290 处） |

辅助：`fix_unknown_type.py`（jadx `??` 类型占位符）、`fix_file_names.py`（文件名对齐类名）、
`fix_pkg_shadow.py`（包名被同名 import 遮蔽）、`decrypt_strings.py`（StringFog XOR 解密）。

## 4. 当前编译状态（如实说明）

验证命令（不依赖 Gradle，纯 javac）：

```bat
cd D:\mtts_work\multitts_src
javac -Xmaxerrs 100000 -proc:none -nowarn -encoding UTF-8 -d _out ^
  -cp "D:\mtts_work\dex\classes.jar;D:\mtts_work\dex\classes2.jar;D:\mtts_work\dex\classes3.jar;%ANDROID_HOME%\platforms\android-35\android.jar" @_files.txt
```

| 阶段 | 错误数 |
|---|---|
| 原始 jadx 产物直接编译 | 2,726 |
| + 包名/类名/字段名还原后 | **1,462** |

- 覆盖 347 个文件、407 个自有类
- 剩余 1,462 个错误集中在 120 个文件，**全部是 jadx 反编译本身的缺陷**，不是改名引入的：
  1. **包名被同名类遮蔽**：`import a7.h;` 之后 `extends h.m` 会被解析成「类 a7.h 的成员 m」；
     同理 `import android.R;` 之后无法再引用压平出的程序包 `R`。Java 无包别名机制，
     只能逐个文件改写成完全限定名。
  2. **匿名内部类构造参数**：jadx 输出 `new View.OnClickListener(this) { ... }`（接口匿名类带参），语法非法。
  3. **泛型信息丢失**：`for (String s : map.keySet())` 被推断成 Object。
  4. **内部类合并**：jadx 把 `X$1` 内联进 `X.java`，另一处仍按独立类引用。

`org.nobody.multitts`（作者手写的业务代码，100 个类）剩余 89 个错误，集中在
`VoicePoolActivity / LoginActivity / MainActivity / TTSService / ForwardService / LogsActivity`
—— 这 7 个文件 jadx 反编译质量最差（见上 1/2/3 类问题）。

## 5. 仍能出包的可靠路径

源码编译尚未跑通时，**改动仍可通过 smali 链路出包**（已验证，见
`D:\GameSourceCollection\multitts_真机共存验证报告.md`）：

```bat
java -jar apktool_3.0.2.jar b D:\multitts-dev\phone_src -o out_unsigned.apk
java -jar apksigner.jar sign --ks <keystore> out_unsigned.apk
```

`phone_src` 是 apktool 解包的干净副本（**保真基线，不要改**）；
需要改包名共存时用副本 `phone_src_rb`。

## 6. Gradle 构建

```bat
cd D:\mtts_work\MultiTTS-Project
gradlew.bat :app:assembleDebug
```

首次构建需要联网下载 AGP 8.7.3。若不想装 Gradle，用第 4 节的 javac 命令即可验证源码。

**关于 `clean`**：现在 clean 之后可以直接 `assembleDebug`（第 2.3 节的自愈钩子会处理 CS 属性）。
2026-09-18 实测：clean 全量构建 `BUILD SUCCESSFUL in 1m 46s / 36 tasks executed`。

**关于 dex 数量**：重建产物是 **14 个 dex**（官方 1.8.3 是 3 个 / 1.8.1 是 2 个）。
全量 clean 构建复现的也是 14 个 —— **不是增量残留，是当前构建配置下的真实布局**，收敛另做。

**完整性与保真度验收**：见同目录 `MultiTTS_源码流传核查与重建完备性验收.md`
（结论：自有类 **100/100** 命中官方包，零缺失）。
