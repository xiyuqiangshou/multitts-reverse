# ============================================================================
# MultiTTS 重建工程 · R8 出包保真规则
# 目的：让重建 APK 走与官方一致的「R8」出包管线（官方 1.8.3 = R8 产物，3 个 dex），
#       同时【绝对不改名、不删类、不优化】，保证与已验收的类名/符号完全一致。
#   -dontobfuscate : 代码本身已是混淆后形态，再混淆会破坏 RefAudit 的全部结论
#   -dontshrink    : 不能删类 —— 大量类只被反射/字符串引用，shrink 会制造 NoClassDefFoundError
#   -dontoptimize  : 不做类合并/内联，保证字节码层面可逐类对照
# ============================================================================
-dontobfuscate
-dontshrink
-dontoptimize
-dontwarn **
-dontnote **
-keepattributes *
-ignorewarnings
