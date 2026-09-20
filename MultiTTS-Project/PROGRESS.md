
---

# 第 N 轮（2026-09-18 15:40）：历史版本考古 + 「最后一公里」去混淆

## 一、背景：把"需人工操作、已封"的路重新走通

此前《源码流传核查与重建完备性验收》把「拿更早版本 APK 反推真实类名」列为**唯一有价值的路径**，
但获取渠道（123云盘/蓝奏云/Telegram）全部标注"需人工操作、已封"。
本轮从**第三方镜像 + 作者本人蓝奏云**打通，拿到 7 个官方签名版本（详见根目录最终报告）。

## 二、关键结论

1. **混淆是作者硬盘损坏（2023-08）之后才引入的**：code ≤ 30 完全未混淆（99.8% 可读），
   code ≥ 33 已混淆（72.8%）。1.3.2.4 的 APK 内时间戳 = 2023-09-17，落在损坏之后。
2. **1.8.3 的"真实命名"无法全量恢复**：它本身就是重建产物，1.3.x 与它同款混淆。
   205 个混淆成员中，只有"1.2.9 时代已存在并延续下来"的 58 个可考证，实际恢复 **28 个**。
3. **520/725（71.7%）成员本来就是可读名**——重建工程的可维护性远好于预期。

## 三、量化

| 指标 | 值 |
|---|---|
| 1.8.3 自有成员 | 725 |
| 其中混淆 | 205 (28.3%) |
| **反推成功** | **28 (13.7% of 混淆；24 个全票)** |
| 不可恢复 | 62（`ui/setting/a–z` 合成类 ~47、`tts/jni` 7、`ui/other` 新增 Activity 14） |

## 四、方法学（三条路径实测对比，教训重要）

| 路径 | 类匹配 | 产出 | 结论 |
|---|--:|--:|---|
| 桥梁跳 LCS | 61/100 | 13 | 过度对齐，**产出错位**（`a`被标成 getBoolean，实为 getApplication） |
| 桥梁跳 + 严格同名 | 61/100 | 12 | 不错位，但**没有锚点**，召回低 |
| **直接跳 1.2.9→1.8.3** | 33/100 | **28** | ✅ 最优：220 个可读名锚点 |

**核心认识：成员对齐质量取决于「可读名锚点」的数量，不是类匹配数量。**

**已证伪（勿再试）**：
- 混淆名按原名字母序分配 → 一致率仅 14.3%
- 同名即同成员 → 仅同名 65.4%，加描述符校验后降到 48.0%（AppContext 10 个同名里只有 2 个描述符也对）

## 五、新增工具（`D:/mtts_work/mt_deobf/`）

`ApkProfile.py`（指纹+混淆画像）、`DexMembers.py`（dexdump→成员表，保序）、
`SeqMap.py`（两级对齐）、`DeobfV3.py`（**最终去混淆器**）、`lanzou_dl.py`（蓝奏云下载器）。

## 六、交付物（本工程根目录）

- `MultiTTS_历史版本考古与去混淆最终报告.md`
- `deobf_v3_映射表.tsv` / `deobf_v3_报告.txt`
�走 dex jar） | **613** |

L1 数字比全树里"自有包 89"高，是因为全树编译时单字母包里的类以源码形式兜住了大量符号；单独编译时这些引用改由 `classes*.jar` 解析，暴露出包名错位问题。
**L1 才是"源码可维护"的真实口径**，已把自动化任务改成分级目标：先 L1 → 0，再 L2 → 0。

### 1462 的实测构成（按可批量性排序）

| 类别 | 数量 | 能否脚本批量 | 备注 |
|---|---:|---|---|
| 找不到符号·类 | 290 | 中 | 只有 39 种简单名；34 种在树里有同名类、包名不同；dex 里每个简单名平均 10+ 候选包 |
| 找不到符号·变量/方法 | 688 | 低 | 需逐个查 dex 真实声明 |
| 无法取消引用 int/boolean/float | 78~86 | 中 | javap 查真实类型后可批量改声明 |
| 不兼容类型 Object→X | ~100 | 中 | 泛型丢失，补强转 |
| 匿名类实现接口不能有参数 | 17~19 | **高** | 纯语法，正则可批量 |
| 方法不会覆盖超类型方法 | 22 | 低 | 对 dex 签名修正 |
| 静态上下文引用非静态变量 a | 121（L1 口径） | **高（若确认是回归）** | 疑似 fix_jadx_fields 过度还原，确认后可一次性抹掉 |

### 工期估算

历史速率：23:20 → 00:43（83 分钟）从 2726 降到 1462（-1264），但该段红利主要来自字段/类名改名的大批量脚本，边际递减。

- **乐观 3 小时**（约 05:00）：C 类回归确认成立 + A 类批量顺利 → L1 先到 0
- **中性 4~6 小时**（05:00~07:00）：按 A→B→C 顺序正常推进，L1 到 0，L2 剩少量
- **悲观**：L2 尾部 100~300 属 jadx 结构性缺陷（内部类合并、类型推断），无法自动修，需人工逐处判断 → 明早看到的是 "L1 100% + L2 95%"

自动化任务每 30 分钟一轮、有效至 12:00，窗口内约 22 轮，够覆盖中性情形。

---

## 01:10-01:25 · 突破：L1 归零（613 → 0），L2 1462 → 697

### 关键发现：那 121 个「静态上下文引用非静态变量 a」全是 jadx 坏代码

javap 实测字节码：

```
getstatic  Field org/nobody/multitts/ui/setting/SettingActivity$a.r0:I
istore_2                       <- 存进局部变量后，该变量在后续代码中从未被使用
```

dex 里**根本不存在 `r0` 这个字段**（`javap -p` 列不出），它是 dex2jar/jadx 对死代码的错误渲染。
jadx 写成 `SettingActivity.a` → javac 解析成「类 SettingActivity 的静态成员 a」→ 报错。

**修法**：只把 `int i12 = Xxx.a.r0;` 这类「赋给一个后续从未使用的局部变量」的行整行注释掉，
原文保留在 `// OBF:` 注释里。语义零风险（值本就没被用过）。

脚本：`mt_deobf\fix_dead_static_ref.py`（第一版误伤，已由 `rollback_and_fix.py` 回滚后严格重做）

### 踩坑记录（务必记住）

第一版脚本按「日志里的 (文件, 行号)」定位，但**同一个 javac 错误类型不只 `r0` 一种形态**：
`h\E.java:296` 报的也是「无法从静态上下文中引用非静态」，但那行是
`if (i >= 33 || (hVar = s.c) == null) {`（`s.c` 才是真凶）。结果误伤 if/赋值语句，
全树瞬间从 1462 冒出 738 个「需要 class、interface、enum 或 record」语法错误。

**教训**：批量改代码前，判据必须是「行内容匹配」（本例：正则 `= Xxx.r0;`），
不能只靠「错误类型 + 行号」。已改为全树扫描 `.r0` 赋值行 + 变量后续 40 行未使用的双重判据。

### 当前数字

| 口径 | 之前 | 现在 |
|---|---:|---:|
| L1 自有包 78 文件 | 613 | **0** ✅ |
| L2 全树 347 文件 | 1462 | **697** |

L2 剩余构成：找不到符号 502 / 无法取消引用 int·boolean·float 41 / 方法不会覆盖 20 / Object→X 不兼容 30 / 其他。

### 并行实验：jadx 升级到 1.5.6

已下载 `D:\mtts_work\tools\jadx156`（官方 v1.5.6，2026-07-10），用新参数重跑反编译：

```
jadx -j 12 -m auto --type-update-limit 30 --no-move-inner-classes
     --no-inline-anonymous --rename-flags "valid,case" --fs-case-sensitive
     --show-bad-code --no-res -ds D:\mtts_work\jadx156_src <3 个 dex>
```

效果：输出 **9781** 个 java（旧版 7669），多出的正是**被 jadx 1.5.0 合并进外部类的内部类**——
`--no-move-inner-classes` 直接治「内部类被合并」这个结构性缺陷。
v2 源码树正在 `D:\mtts_work\multitts_src_v2` 重建（流水线 `mt_deobf\run_v2.py`），结果待对比。

### v2 实验结论（已跑完，重要）

| 阶段 | 全树错误数 |
|---|---:|
| v2 刚生成 | 27 |
| 修掉 `??` 占位符后 | 3 |
| 修掉最后一处 `?? arrayList2;` 后 | **1462** |

**「3 个错误」是假象**：javac 分阶段工作，存在语法错误（parse error）时会跳过语义分析，
所以 1000+ 个「找不到符号」被压着不报；一旦语法全对，语义错误一次性涌出。
→ **以后判断进度，必须先保证 0 语法错误，再看符号错误数。**

v2 的两个新问题（都是 `--no-move-inner-classes` 带来的）：
1. jadx 把嵌套类写成 `Outer$Inner`（`PorterDuff$Mode`、`Map$Entry`、`C0.e` 全部失效）。
   已写 `fix_dollar_ref.py` 按 dex 事实精确改写（dex 里存在 `X$Y` 就保留，否则改成 `X.Y`）。
2. 内部类被输出成独立顶层文件后，代码里仍按 `Outer.Inner` 引用 → 需要反向改成 `Outer$Inner`。

v2 净效果 1462 ≈ 与旧树持平，没有赚到。

### v3 实验（进行中）

改用 jadx 1.5.6 但**去掉 `--no-move-inner-classes`**（保留 jadx 原本"把内部类搬进父类"的行为），
只享受新版本的类型推断改进（`--type-update-limit 30`、`--no-inline-anonymous`）。
产物 `D:\mtts_work\jadx156_src2` → `D:\mtts_work\multitts_src_v3`。

**v3 结果：1043**（347 文件）。仍不如主线 697。

| 源码树 | jadx 版本 / 参数 | 全树错误 |
|---|---|---:|
| **multitts_src（主线）** | 1.5.0 默认 + 8 轮手工修复 | **697** |
| multitts_src_v3 | 1.5.6 + type-update-limit 30 + no-inline-anonymous | 1043 |
| multitts_src_v2 | 1.5.6 + 再加 no-move-inner-classes | 1462 |

结论：**主线保持 `multitts_src`，不再折腾换版本。** jadx 升级唯一确定有价值的产出是
`fix_dollar_ref.py`（按 dex 事实把 `Outer$Inner` 改写为 `Outer.Inner`，并跳过方法名里的 `$`）。

---

---

---

## 2026-09-18 02:30-08:40 · ⚠️ 重大口径纠正：之前的数字是假的

### 一、必须纠正的两件事（诚实优先）

**1. 「L1 = 0 错误」是假的。**
真因是**日志编码陷阱**：`_own.log` 有时 GBK 有时 UTF-8；按错误编码读时「错误:」的字节序列
一个都匹配不到，`count()` 返回 0，于是报出虚假的 0。
（同一条 javac 命令，两次运行的日志编码都可能不同。）
→ 已写 `mt_deobf/mt_verify.py`：**同时用 UTF-8 与 GBK 两种字节序列去 count**，物理上不可能再被骗。

**2. 「全树 697 / 555」也是被压制的假数字。**
javac 分阶段报错：**只要还有语法错误，后面的语义错误就不报**。
修掉若干语法问题后，全树从 555 瞬间变成 **2181**。
这条教训我自己写进过指导文档第 15.6 节，却又被它骗了一次
—— 说明它必须做成**工具强制**，不能靠人记得。

### 二、本轮实测的真实数字（两个独立验证器交叉确认）

| 口径 | 数字 | 说明 |
|---|---:|---|
| **L1 自有包 org/nobody/multitts 78 文件单独编译** | **299** | 语法类错误 0，数字干净 |
| **L2 全树 347 文件** | **2181** | 语法类错误 9，数字干净 |
| L1 本轮起始 | 383 | 经修复降到 299 |

L2 构成：找不到符号 1352 / 无法取消引用 int·boolean 173 / 匿名类带参数 53 / Object→X 约 60 / 其他。

### 三、本轮新建的工具（都可用，已实测）

| 脚本 | 作用 | 关键点 |
|---|---|---|
| `InsnRefDump.java` | **dexlib2 按「类→方法→指令」导出全部类型引用** → `insn_refs.csv`（627,409 条，78MB） | 每条 `new-instance` / `invoke` / `iget` / `check-cast` 都带完整 `Lx/y/Z;` 描述符，是消歧同名类的**终极事实源** |
| `mt_verify.py` | 唯一权威验证器，同时算 L1/L2 | 两种编码字节 count，免疫编码陷阱 |
| `fix_symbols.py` | 用方法级 dex 事实定位缺失符号所属类 | 类→NEW/CAST、构造器→NEW、方法→CALL 的 declaring class、变量→GET/PUT 的 declaring class；命中唯一才改 |
| `classify_errs.py` | 按 符号种类/缺失名/文件 精确分类错误日志 | 替代之前不靠谱的类型统计 |
| `probe_missing.py` | 抽样打印错误的源码行原文（可指定日志） | 归纳错误形态用 |
| `ref_resolve3.py` / `ref_resolve4.py` | 用 smali / dex 事实消歧同名类 | ref4 是方法级，比 ref3 准 |
| `partial_rollback.py` | 按前缀精准回滚到快照 | 用于「只回滚第三方、保留自有」这类操作 |

### 四、本轮确认的根因与修法

1. **`f{序号}{原名}` 字段名残留（`f78a` / `f82e` / `f53b`）**

   `fix_jadx_fields.py` 原正则写的是 `\d{3,6}`，**2 位序号的形态全被漏掉**。
   已用 javap 校验：`A6.i` 在 dex 里字段真名就是 `a`..`i`，源码里是 `f78a`..`f85h`。
   已把正则改为 `\d{1,6}`。
   **铁律：字段改名还原必须全树一致** —— 只改 org 不回滚第三方（或反之）会造成
   「声明改了、引用没改」，错误数暴涨（实测 555→2238 那次就是这种不一致态）。

2. **`h.g` 这类「内部类写成点号」**：dex 里是 `h$g`。
   别名必须同时支持 `Outer$Inner` / `Outer.Inner` / `Inner` 三种写法
   （已做进 `fix_symbols.py` 的 `_aliases()`）。

3. **局部变量遮蔽类名**（`i.r.put(...)` 里的 `i` 是类不是变量）：
   先用 dex 事实确认，再改写为完全限定名 —— 不要去改变量声明。

### 五、下一步建议（按性价比）

1. 继续用 `fix_symbols.py` 的方法级事实清 L1 的 299；剩余多为「无候选」，
   需要引入**字段类型传播**（从 `recv.field` 的 dex 字段类型反推类型名）。
2. L2 的 2181 里第三方库（ExoPlayer/media3、Firebase、Guava 等）占大头 ——
   这些**有公开源码，不该花力气修反编译质量**。建议 L2 正式口径改为
   「自有 407 类 0 错误 + 第三方走 dex jar」。
3. 任何数字变化都必须用 `mt_verify.py` 复测，禁止再用单编码 count。

---

## 2026-09-18 08:00-08:35 · 🔥 三个关键发现（L1: 299 → 174）

### 一、「拉取第三方库源码」这条路，实测不可行（已钉死，不要再试）

用户提议：第三方库有公开源码，直接拉源码替换。
**实测结论：不行。** R8 是**全量混淆**，官方源码与 dex 之间不存在任何可对齐的锚点：

| 可能的锚点 | 实测结果 |
|---|---|
| `mapping.txt` | APK 里没有（`unzip -l` 查过 1317 个条目，无 mapping） |
| `SourceFile` 属性 | **11162 个类全部被改成字面量 `"SourceFile"`**（R8 `-renamesourcefileattribute`） |
| 注解 / `kotlin.Metadata` | **一个都没有**（`-keepattributes` 未保留），d2 原始类名字符串全丢 |
| META-INF 依赖清单 | 只有 69 项，全是 androidx/material/coroutines；而错误大头在**被混淆成单字母包**的类（`g0.J`、`h.E`、`B0.F`、`C5.b`），androidx 官方源码里根本不存在这些名字 |

→ 第三方库的正确用法是**直接用它编译好的字节码**（dex2jar 产物 / deps.jar），不是拉源码。
用户"第三方不该花力气修"的判断是对的，只是载体应该是**编译好的 class**而非源码。

### 二、🔥 交叉反编译器对照实验（首次做，结论推翻了中途的乐观）

用同一批自有类（83 文件）、同一 classpath、同一 javac 参数对比四个反编译器：

| 反编译器 | 自有包错误数 | 说明 |
|---|---:|---|
| **jadx 1.5.0 主线（已后处理）** | **299** | 用改名机制规避了 Java 命名冲突 |
| CFR 0.152 | 25 → **修完语法后暴露 821** | 类型推断更准，但不处理同名类遮蔽（`extends h.m` 直接炸） |
| Vineflower 1.11 | 该方法直接崩（FinallyProcessor 异常） | 无法用于此类 |
| Procyon | 反编译失败（exit=1，0 文件） | 不可用 |

**中途踩的坑**：CFR 的 25 是"语法错误压着语义错误"，修掉 3 处 `** GOTO` 后瞬间变 821。
（这是**第三次**被同一条规律骗：语法错误会压住语义错误 —— 必须做成工具强制。）

→ **jadx 主线仍是最优基线**，CFR 只能当局部补丁源。**换反编译器这条路不划算。**

### 三、🔥🔥 决定性发现：编译基准错了（ACC_SYNTHETIC），L1 一步降 89

现象：`javap` 明明显示 `public C5.b(int);`，javac 却报「找不到符号 构造器 b(int)」。
最小复现脚本 20 行就重现了（`new C5.b(1)`），排查过程：
1. 三个 jar 里只有 classes3.jar 有这个类 → 不是 jar 顺序问题
2. `javap -v` 看构造器详情 → **`flags: (0x1001) ACC_PUBLIC, ACC_SYNTHETIC`**

**根因**：R8 给 lambda / metafactory 合成类（`C5.b`、`B6.c`、`B6.e`…）的构造器打了
`ACC_SYNTHETIC`，dex2jar 忠实保留。而 **javac 在编译 Java 源码时会无视所有 synthetic 成员** ——
于是 `javap` 看得见、`javac` 看不见。**这类「找不到符号 构造器/方法」根本不是源码问题，改源码毫无意义。**

**解法**：`mt_deobf/StripSynthetic.java`（ASM 9.5，dex-tools 自带）清除 class 里的
`ACC_SYNTHETIC`（类/方法/字段三级），生成 `classes_nosyn.jar / classes2_nosyn.jar / classes3_nosyn.jar`。
- 共处理 11162 个类，**清除 3315 个类的标志**
- `mt_verify.py` 与 `verify_compile.py` 的 classpath 已改为 `_nosyn.jar`
- **效果：L1 263 → 174**
- 验证：`probe/TestC5b.java` 用旧 jar 报错、用新 jar 通过

### 四、另一个工具 bug（也已修）

`InsnRefDump.java` 里用 `insn.getOpcode().name` 取指令名，但 dexlib2 的 `Opcode.name`
是 **`"new-instance"` 这种小写带连字符形态**，不是枚举常量名 → NEW/CAST/CLASS/SGET 全部匹配失败，
退化成 TYPE/FIELD。改用 `((Enum<?>) insn.getOpcode()).name()` 后：

| kind | 修复前 | 修复后 |
|---|---:|---:|
| NEW | **0** | 37,863 |
| CAST | **0** | 32,088 |
| SGET/SPUT/IGET/IPUT | 全塌成 FIELD | 正确区分 |
| CLASS | **0** | 2,518 |

（`fix_symbols.py` 一直报「无候选」的直接原因就是这个。）

### 五、批量替换路线：判定失败，已放弃

`fix_symbols.py` 的「唯一候选代入 FQN」多次尝试**全部净亏**：
- 首版（整行 re.sub 无 count）：263 → 272
- 加 caret 精确定位后：263 → 266
- 仅方法级事实（`--only-method`）：263 → 264
- 换成修正后的编译基准再跑：174 → 176

即便定位逻辑已修（含"±3 列容错 + 排除 Java 关键字"，见 `token_span()` 的注释），
**"候选唯一"仍不足以证明"候选正确"** —— 降级到整类粒度时会合并同类所有方法。
→ **结论：不再做批量符号替换，剩余错误逐个精修（javap / dex 事实佐证）。**

### 六、当前真实数字（`mt_verify.py`，两个独立验证器交叉）

| 口径 | 数字 |
|---|---:|
| **L1 自有包 78 文件（新基准）** | **174** |
| L2 全树 347 文件 | 待用新基准重测 |

本轮净进展：**299 → 174（-125）**，其中 ≈89 来自修好编译基准，≈33 来自匿名类修复，其余来自个别修正。

### 七、下一步建议

1. 用新基准重测 L2，确认全树口径。
2. L1 剩余 174 逐个精修；优先「找不到符号」里位置明确、可 javap 佐证的。
3. 把 `_nosyn.jar` 固化进 `MultiTTS-Project/app/libs/`（替换 deps.jar），保证 Gradle 侧同样正确。
4. 恢复每 30 分钟自动化，prompt 里必须带上：**①合成类 ACC_SYNTHETIC 铁律 ②语法压制语义铁律
   ③批量替换净亏、禁止再试 ④L1 必须恒不上升**。


---

## 2026-09-18 09:00-09:40 —— L1：174 → 68（-106，零回归）

本轮首次做「**先按根因聚合、再逐类裁决**」的系统化收敛，而非逐条试错。

### 一、新增工作流与工具（都在 `mt_deobf/`）

| 工具 | 作用 |
|---|---|
| `l1_errors.py` | 按 文件 / 消息类型 / caret token 三视图聚合 L1 错误，可 `-v` 展开源码行 |
| `l1_shadow.py` | 精确提取 (文件, 行, caret 列, 被遮蔽 token)，输出 `_l1_shadow.csv` |
| `fix_shadow.py` | 「原始类型遮蔽类名」的 masked 批量限定化（跳过字符串/注释） |
| `fix_setting_activity.py` | SettingActivity 4 类根因的专项修复 |
| `overlay_src/` + `overlay.jar` | **库 API 补全层**（见下） |
| `append_guide17.py` / `append_guide18.py` | 经验沉淀到指导文档 + skill |

**纪律**：每次改动后必跑 `diff_logs.py` 证明 **新增 = 0**，再报净减。

### 二、消灭的错误（按根因）

| # | 根因 | 文件 | 条数 |
|---|---|---|---:|
| 1 | 原始类型字段/局部变量遮蔽导入的类名（`int i` vs `kotlin.jvm.internal.i`） | RuleAnalyzer / EncoderUtils / StringExtensionsKt / Ner / Ngr / AppContext | 43 |
| 2 | 包名与同包类名同名（`h` 既是包又是 `setting/h.java`）→ `h.g` 解析失败 | SettingActivity / ReplaceActivity（后者待做） | 12 |
| 3 | 限定名在**表达式位置**退化为字段（JLS 6.5.2）→ `SettingActivity.a.W(...)` | SettingActivity | 8 |
| 4 | **static 嵌套类**导致外层实例链断裂 → `SettingActivity.this` 不可求值 | SettingActivity | 5 |
| 5 | 同包单字母顶层类被嵌套类/import 遮蔽（`new a(aVar)` / `new m(this)`） | SettingActivity | 2 |
| 6 | 同包 `a` 遮蔽包 `a` → `a.a` 解析为嵌套类 | SettingActivity / EngineActivity | 2 |
| 7 | 类型整片写错（jadx 把 `g7.l`/`g7.f`/`g7.c` 写成 `l`/`H6.f`/`o0.c`） | VoicePoolActivity | 24 |
| 8 | **R8 构造器去虚拟化 → 库类空壳**（无任何构造器） | GsonExtensionsKt | 4 |
| 9 | 继承字段遮蔽库类（`d` vs `K6.d`） | EngineActivity | 1 |

### 三、🔴 本轮最重要的三条新认知

1. **`smali/h/m.smali` 的 `.class` 行是 `LH/m`（大写）**，真正的小写 `h/m` 在 `smali/h.1/m.smali`。
   NTFS 大小写不敏感导致 apktool 目录改名。**读 smali 事实前必须核对 `.class` 行**，
   否则会读成另一个类、得出完全错误的结论（本轮差点因此误判 `g7.c` 的父类）。

2. **限定名的解析结果依赖位置**（已用最小复现验证）：
   `Outer.a x = ...`（类型位置）→ 嵌套类 ✅；`Outer.a.W()`（表达式位置）→ **字段** ❌。
   所以「找不到符号 类 g」与「无法从静态上下文中引用非静态 变量 a」常是同一个根因。

3. **R8 会把子类构造器去虚拟化成父类构造器调用**：
   `new-instance JsonSyntaxException` + `invoke-direct {p}, Ljava/lang/RuntimeException;-><init>(...)V`。
   → dex 里库类变成**无构造器空壳** → deps jar 缺 API → javac 拒绝。
   **解法：`overlay.jar`（按官方库真实 API 写补全源码）置于 classpath 最前**，
   已在 `mt_verify.py` / `verify_compile.py` 中生效。

### 四、当前真实数字（`mt_verify.py`，2026-09-18 09:40）

| 口径 | 本轮起点 | 现在 |
|---|---:|---:|
| **L1 自有包 78 文件** | 174 | **68** |
| L2 全树 347 文件 | 1942 | 1840 |

**已清零的文件**：SettingActivity(32) / RuleAnalyzer(28) / VoicePoolActivity(27) /
EncoderUtils(6) / GsonExtensionsKt(4) / StringExtensionsKt(3) / EngineActivity(2) / Ner(1) / Ngr(1)。

**头部剩余**：AppContext 15、LoginActivity 14、MainActivity 13、ReplaceActivity 6、
LogsActivity 4、o.java 4、TipsActivity 3、RequestParser 2，其余 8 个文件各 1。

### 五、下一步

1. `SettingActivity` 的 4 类修法可**同型复用**到 `ReplaceActivity`（`h.g`）与 `LoginActivity/MainActivity`。
2. `AppContext` 的 `Object[] objArr = 0;` / `(boolean)(objArr==true?1:0)` 需按 smali 常量归零
   （已核对 `LP2/a;-><init>(IZ)V` 第二参即 v2=0）——属「按寄存器常量还原」类，逐个做。
3. 固化 `_nosyn.jar` + `overlay.jar` 进 `MultiTTS-Project/app/libs/`。
4. 自动化 prompt 补两条：**⑤读 smali 前核对 `.class` 行 ⑥库类「无构造器」= R8 去虚拟化，走 overlay**。

---

## 🎯 L1 归零达成（2026-09-18 11:40）

### 一、最终数字

| 口径 | 本轮起点 | **现在** |
|---|---:|---:|
| **L1 自有包 78 文件（唯一 KPI）** | 16 | **0 ✅** |
| L2 全树 347 文件 | 1840 | 1771（余下全为第三方混淆烂源码，不计进度） |

**正向证据**（排除「假 0」）：`_own_out/` 产出 **127 个 .class**（含
`MainActivity.class` / `MainActivity$1.class`）；`_own.log` 只剩 4 行 javac `注:`
（deprecation / unchecked），`错误:`.count == 0。
**零回归证据**：`diff_logs.py` 对比本轮起点 → **新增 0，消失 7**。

### 二、本轮关键突破：**编译基线三件套**

新的认识——**R8 删掉的是「编译期才需要的信息」，源码本身没错，改源码毫无意义**。
完整方案固化为 `mk_baseline.py`（一键可复现，替代此前手工三步）：

```
classes{N}.jar  --PatchCtors-->  --PatchThrows-->  --StripSynthetic-->  classes{N}_nosyn.jar
```

| 工具 | 修什么 | 本轮实例 |
|---|---|---|
| `PatchCtors`（**已升级**：支持 `类名:描述符`） | 构造器被 R8 去虚拟化抹掉 | `F6/j:()V`、`kotlin/jvm/internal/h:(ILjava/lang/Object;...;I)V` |
| `PatchThrows`（**新增**） | `Exceptions` 属性被 ProGuard/R8 删 | `E6/b:close()V`（Closeable 契约）、`k5/i:f()V`（NanoHTTPD.start 契约） |
| `StripSynthetic` | `ACC_SYNTHETIC` | 已有 |
| ASM 来源 | 免安装 | `D:\gradle-9.6.1\lib\asm-9.9.jar`（本机 Gradle 自带、未重定位） |

> `patch_ctors.txt` / `patch_throws.txt` / `overlay_src/` 应与 `.java` 一同版本管理。

### 三、javac **流分析**级错误：五类根因（新战场）

符号错误清完后剩下的 `可能尚未初始化/可能已分配/无法访问的语句`，
根因都是「R8 寄存器复用 + jadx 把寄存器物化成变量 → 读在写前」：

| 根因 | 实例 | 修法 |
|---|---|---|
| ① 常量寄存器被当变量 | LoginActivity `i9`（smali `const/4 v15, 0x1` 复用） | 删变量，改 `i10++` |
| ② R8 字段 final 化过激 | Ner / Ngr `final long a` 但构造函数 `iput-wide` 两次 | 去 `final`，保两次赋值 |
| ③ 空 catch 体吞赋值 | TTSService `catch (Exception unused) {}`（smali `:catch_e9` 是裸 `nop`，落入共用 `:cond_ea`） | 把共用块逻辑补进 catch |
| ④ `while(true)` 缺 `else break` | MainActivity `i50`/`i51` 循环 | 按 smali 反向比较补 break |
| ⑤ 同一基本块被 jadx 复制多份且降级 | MainActivity 正向中层蛇（3 份，2 份降级） | 照**反向中层蛇**（jadx 写对了）镜像重写 |

**⑥ 用 label break 还原 `goto` 跳过整层循环**：`found: for (...) { while(true){...; break found;} }`。

### 四、重要反直觉认识

**计数会「越修越多」**：javac 在文件有 attribution 错误时会**跳过该文件的流分析**，
所以修好一条符号错误后，同文件的流错误才第一次浮现（8→2→1→1→1→1→0 中后段全是级联揭示）。
→ 发现「修完 A 冒出 B」时先看 B 是否**同一文件**；是则正常，别回滚。

### 五、下一步

1. 把 `_nosyn.jar` + `overlay.jar` 固化进 `MultiTTS-Project/app/libs/`。
2. `multitts_src` 同步到 `MultiTTS-Project/app/src/main/java`（注意 NTFS 大小写目录）。
3. 挑战真实构建：`gradlew assembleDebug`（L2 的 1771 条第三方烂源码需要为它们做
   「只编自有包 + 依赖 jar」的 Gradle 源集裁剪，而不是硬修第三方）。
4. 重打包多 dex APK → 真机/模拟器回归「文本替换 + 语音合成」链路。


---

## 源集裁剪 + Gradle 构建启动（2026-09-18 12:30）

1. **源集裁剪**：`app/src/main/java` 只保留 `org/`（78 个自有 .java）；
   109 个压平包目录 + 7 个构建残留移至 `D:\mtts_work\mt_archive\proj_flat_src_20260918\`
   （注意：该归档目录**未**开大小写敏感，15 组大小写对以 `b__lc` 后缀平级存放）。
   理由：压平包 class 已全在 deps.jar；且刚同步的压平包源码 mtime 新于 deps.jar，
   会被 javac `-Xprefer:newer` 拖进编译而炸出 L2 的 1771 条旧账。
2. **javac 陷阱对策落地**：`app/build.gradle` 给 JavaCompile 加
   `-sourcepath src/main/java`（第 20.5 节：显式文件列表会静默丢弃
   `setting/a.java` 这个大小写兄弟文件，sourcepath 兜底找回）。
3. deps.jar 重建口径更新：11,062 类（= 11,162 − dex 内 100 个 org/nobody 类，
   后者由源码重编，留在 jar 会 D8 重复定义）。
4. `gradle assembleDebug --offline`（Gradle 8.10.2 + AGP 8.7.3 本地缓存）已启动。

---

## ✅ assembleDebug 打通（2026-09-18 12:54，新会话接手后）

### 结果

```
BUILD SUCCESSFUL in 46s
app/build/outputs/apk/debug/app-debug.apk = 152,412,895 B
14 个 dex / 73 个 lib/*.so / 33 个 assets
（参照件 multitts_50417.apk = 151,736,991 B → 100.45%）
```

构建命令（本机无 gradlew，用 wrapper dists 里的 8.14.3）：

```bash
cd /d/mtts_work/MultiTTS-Project
"/c/Users/Administrator/.gradle/wrapper/dists/gradle-8.14.3-all/10utluxaxniiv4wxiphsi49nj/gradle-8.14.3/bin/gradle.bat" \
    assembleDebug --offline
```

### 卡点 A：javac 输出目录把 `setting/{a,A}.class` 坍缩

- 源码树与工程源集都开了大小写敏感、`a.java`/`A.java` 都在，
  但 **javac 的 `-d` 输出目录不在大小写敏感目录下** → D8 报
  `Class content provided for descriptor ...A actually defines class ...a`。
- 修法（`fsutil` 不允许对非空目录开标志，错误 `0x00000091`）：
  `rm -rf app/build` → `mkdir app/build` → 对**空**的 `app/build` 开 case-sensitive → Gradle 子目录继承。
  ⚠️ **每次 clean 后必须重做**。
- 验证：`intermediates/javac/.../setting/` 下 `a.class` 与 `A.class` **双双存在**。

### 卡点 B：D8 的 `j$` 前缀硬规则（换掉了原定方案）

- **两条死路（实测，别再试）**：
  1. `coreLibraryDesugaring` + `desugar_jdk_libs:2.1.5`：该 artifact 里是 **`java/*` 形态、无 `j$.*`**；
     且 deps.jar 整体已是脱糖产物 → D8 报 `Code has already been library desugared`。
  2. 把 `j$`(+`java/`) 拆成独立 jar 期待单独成 dex：无效，规则禁止的**就是合并**本身，
     报错任务变成 `:app:mergeExtDexDebug`，同一条规则。
- **正解：抹掉 `j$` 前缀**（`j$` → `jq`，先确认 `jq` 未被占用）。
  新脚本 `mt_deobf/rename_jdollar.py`（默认只校验，`--apply` 落盘）：
  **只重写常量池 `CONSTANT_Utf8` payload + 等长替换**（2→2 字节，长度字段/偏移全不变）。
  ⚠️ 绝不能整文件 `bytes.replace`：class 里 `b'j$'` 有 **153 处是字节码指令操作数**（`0x6a 0x24`）。
- 命中：`deps.jar` 704 类 / `desugar_lib.jar` 550 类 + 549 条路径；残留 0。
- 同步改动：3 个源码文件 `import j$.util.Objects;` → `jq.util.Objects`（`multitts_src` 与工程源集两处都改）。
- 备份：`libs/deps.jar.bak_before_jrename`、`libs/desugar_lib.jar.bak_before_jrename`、`libs/deps.jar.bak_before_jstrip`。

### 当前 libs 口径

| 文件 | 内容 |
|---|---|
| `deps.jar` | 9,964 类（原 11,162 − dex 内 100 个 org/nobody − 549 j$ − 37 java/*） |
| `desugar_lib.jar` | 549 个 `jq/`（原 j$）+ 37 个 `java/util/function/*` 等回移类 |

`compileOptions.coreLibraryDesugaringEnabled` 必须保持 **false**（代码已脱糖）。

---

## 2026-09-18 13:20-13:35 · 🔴 验证器自身的两处缺口（修完才敢说数字）

用户问「确定 100% 逆向完成了吗」，于是把三个验证器全部**重跑**，结果发现 **KPI 验证器本身有两个缺陷**，
以及我自己在 13:0x 的 j$ 改名引入了一处回归。

### 一、我自己引入的回归（已修）

j$ → jq 改名时改了 `multitts_src` 的 3 个 `import jq.util.Objects;`，
但 `mt_verify.py` 的 classpath 里**没有** jq 这批类（它们被拆进了 `desugar_lib.jar`）
→ L2 从 1771 冒到 **1778**（6 条「程序包jq.util不存在」+ 级联）。
**修法**：把 `app/libs/desugar_lib.jar` 加进 L1/L2/L3 三个验证器的 classpath ——
工程 `app/build.gradle` 的 dependencies 本来就把这两个 jar 一起 implementation，验证必须与之一致。
**铁律：改动构建输入（jar / 源码 / 前缀）后，三个验证器必须一起重跑，数字变了先怀疑自己。**

### 二、🔴 L1 的「0」里藏着一个根本没编的文件（本次最重要的发现）

`_own_out` 产物恒为 **127** 个 .class，而 L3 是 **128** 个 —— 差的是 `setting/a.class`。

**根因不是 sourcepath**（补了也没用），而是：
**dex 基线 jar 里本来就带 100 个 `org/nobody/**` 的 .class**（全在 `classes3_nosyn.jar`）。
javac 从 **classpath** 就能解析到 `org.nobody.multitts.ui.setting.a`，
于是判定「已有该类型」→ **直接跳过编 `setting/a.java`**（既不报错、也不出 class）。
L3 用 deps.jar（已剔掉自有类）所以出 128 个。

**危害**：L1 报「78 文件 → 0 错误」，听上去是「78 个都编过了」，实际是 **77 个真编 + 1 个静默跳过**。

**修法**：新增 `dex/_noown/*.jar`（Python zipfile 过滤 `org/nobody/` 前缀，其余逐条原样搬运，
不是改写 dex 事实），L1 改用它 + **不再挂 `_verify_out`**（L2 产物同样含自有类，同样会掩盖）。
`mt_verify.py` 同时补两处：
1. L1 加 `-sourcepath`（junction 到 `multitts_src/org`）—— 对付「显式列表静默丢弃大小写兄弟源文件」；
2. **打印正向证据**「_own_out 产出 N 个 .class」—— 0 错误必须拿产物说话，不能只信日志。

### 三、修完后的真实数字（2026-09-18 13:35，全部重跑）

| 口径 | 结果 |
|---|---|
| **L1 自有包 78 文件** | **0 错误 ✅ / 产出 128 个 .class**（`setting/a.class` 与 `A.class` 双双在） |
| **L3 Gradle 等价条件** | **0 错误 ✅ / 产出 128 个 .class** |
| L2 全树 347 文件 | 1771（与修前一致 → 零回归；余下全为第三方 R8 烂源码，不计进度） |
| assembleDebug | BUILD SUCCESSFUL，app-debug.apk = 152,412,895 B |

**新增/改动文件**：`dex/_noown/{classes,classes2,classes3}_nosyn.jar`（新）、
`mt_verify.py`（DEX_NOOWN + sourcepath + 产物计数）、`mt_verify_gradle.py`（+desugar_lib.jar）。

### 四、教训（值得进 skill）

**验证器的 classpath 本身就是被测对象的一部分。**
「自有包单独编译」这类 KPI，若 classpath 里恰好含有被测包的 class，
javac 会静默跳过同名/同包源文件的编译 —— **报出的 0 是「没编」的 0，不是「编对了」的 0**。
判据永远只能看**产物**（.class 个数、关键大小写兄弟类是否双产出），不能看日志有没有「错误:」。
