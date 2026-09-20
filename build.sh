
# ==== [收尾] 清理中间产物，保持 kit 精简 ====
rm -rf MultiTTS-Project/app/build out/mtts_unsigned.apk out/mtts_aligned.apk out/mtts_arscfix_signed.apk.idsig
echo "==== 已清理中间产物 ===="

# 用法：在 kit 根目录执行  bash build.sh
set -e
cd "$(dirname "$0")"

GRADLE_BIN="/d/Android_SDK/gradle-8.14.4/gradle-8.14.4/bin/gradle"
PY="C:/Users/Administrator/.workbuddy/binaries/python/versions/3.13.12/python.exe"
BT_ROOT="C:/Users/Administrator/AppData/Local/Android/Sdk/build-tools"
BT="$BT_ROOT/$(ls -v "$BT_ROOT" | tail -1)"
export GRADLE_USER_HOME=/d/gradle_home

APP=MultiTTS-Project/app
ARCHIVE="$APP/build/intermediates/project_dex_archive"
ARCHIVE_WIN='D:\mtts_work\mtts_build_kit\MultiTTS-Project\app\build\intermediates\project_dex_archive'

echo "==== [1/6] gradle 编译（javac）===="
(cd MultiTTS-Project && "$GRADLE_BIN" :app:compileReleaseJavaWithJavac -q) || { echo "编译失败"; exit 1; }

echo "==== [2/6] dex 归档目录防大小写互吞 ===="
# 源码里 A.java 与 a.java 同包并存；AGP 只对 javac 输出目录启用了 NTFS
# 大小写敏感，dexBuilder 的 per-class 归档目录没启用 -> a.dex 覆盖 A.dex。
# 处理：删旧档，重建空目录并启用敏感标志（子目录创建时自动继承）。
rm -rf "$ARCHIVE"
mkdir -p "$ARCHIVE"
fsutil file setCaseSensitiveInfo "$ARCHIVE_WIN" enable >/dev/null && echo "  project_dex_archive 已启用大小写敏感"

echo "==== [3/6] gradle assembleRelease ===="
(cd MultiTTS-Project && "$GRADLE_BIN" :app:assembleRelease -q) || { echo "打包失败"; exit 1; }
ls -la $APP/build/outputs/apk/release/app-release.apk

echo "==== [4/6] dex-swap（官方底包换 dex）===="
"$PY" tools/swap_dex.py

echo "==== [5/6] zipalign + apksigner ===="
"$BT/zipalign.exe" -f -p 4 out/mtts_unsigned.apk out/mtts_aligned.apk
"$BT/apksigner.bat" sign \
  --ks keys/multitts_debug.keystore \
  --ks-key-alias androiddebugkey \
  --ks-pass pass:android --key-pass pass:android \
  --v1-signing-enabled true --v2-signing-enabled true \
  --out out/mtts_arscfix_signed.apk out/mtts_aligned.apk

echo "==== [6/6] 校验 ===="
"$BT/apksigner.bat" verify --print-certs out/mtts_arscfix_signed.apk | head -3
# 互吞对自检：最终 APK 必须同时含 setting/A 与 setting/a 两个类
"$PY" - << 'EOF'
import zipfile
z = zipfile.ZipFile("out/mtts_arscfix_signed.apk")
blob = b"".join(z.read(n) for n in z.namelist() if n.startswith("classes"))
ok1 = b"org/nobody/multitts/ui/setting/A;" in blob
ok2 = b"org/nobody/multitts/ui/setting/a;" in blob
print("  setting/A 类:", "OK" if ok1 else "丢失!")
print("  setting/a 类:", "OK" if ok2 else "丢失!")
if not (ok1 and ok2):
    raise SystemExit("大小写互吞对不完整，APK 不合格")
EOF
ls -la out/
echo "==== 完成：out/mtts_arscfix_signed.apk ===="
