#!/usr/bin/env bash
# =============================================================================
# MultiTTS-Harmony 一键构建+签名脚本 (Linux, 无需 DevEco Studio / GUI)
# 流程已在 Linux 实测通过(2026-09-24): 下载工具链 → 编译 HAP → 自签 → 出安装包
# 工具链镜像: hf-mirror.com/csukuangfj/harmonyos-commandline-tools
#
# 用法:
#   bash build-linux.sh            # 全自动: 下载工具链 → 编译 → 签名
#   SKIP_DL=1 bash build-linux.sh  # 工具链已下载/解压时跳过前两步
#
# 依赖: curl unzip python3 openssl java(JRE 17) node(可选)
# 产物: ~/mtts-build/entry-default-signed.hap   ← 直接 hdc install 装
# =============================================================================
set -euo pipefail

CLT_VER="5.0.5.200"
CLT_ZIP="commandline-tools-linux-x64-${CLT_VER}.zip"
CLT_URL="https://hf-mirror.com/csukuangfj/harmonyos-commandline-tools/resolve/main/${CLT_ZIP}"
WORK="${WORK:-$HOME/mtts-build}"
PROJ="$(cd "$(dirname "$0")" && pwd)/MultiTTS-Harmony"

echo "==> [1/5] 准备目录: $WORK"
mkdir -p "$WORK" && cd "$WORK"

if [[ "${SKIP_DL:-0}" != "1" ]]; then
  echo "==> [2/5] 下载 commandline-tools (约 3.2GB, 断点续传, 中断重跑即可)"
  for attempt in 1 2 3 4 5; do
    curl -L -C - --retry 3 --retry-delay 5 -o "$CLT_ZIP" "$CLT_URL" && break
    echo "    下载中断(第 $attempt 次), 10 秒后续传..."
    sleep 10
  done
  echo "==> [3/5] 解压工具链"
  unzip -q -o "$CLT_ZIP"
fi

CLT_DIR="$(find "$WORK" -maxdepth 2 -type d -name 'command-line-tools*' | head -1)"
[[ -n "$CLT_DIR" ]] || { echo "错误: 未找到 command-line-tools 目录"; exit 1; }
export DEVECO_SDK_HOME="$CLT_DIR/sdk"
export PATH="$CLT_DIR/bin:$PATH"
JAR="$(find "$CLT_DIR" -name 'hap-sign-tool.jar' | head -1)"
echo "    SDK: $DEVECO_SDK_HOME"

echo "==> [4/5] 编译 HAP"
cd "$PROJ"
ohpm install 2>/dev/null || echo "    (ohpm install 跳过: 无第三方依赖)"
hvigorw assembleHap --mode module -p module=entry@default -p product=default --no-daemon

UNSIGNED="$PROJ/entry/build/default/outputs/default/entry-default-unsigned.hap"
[[ -f "$UNSIGNED" ]] || { echo "错误: 未找到构建产物"; exit 1; }
echo "    未签名产物: $UNSIGNED"

echo "==> [5/5] 本地自签(debug)"
# ===== 已实测通过的自签链: root CA → sub CA → 证书链 → profile p7b → 签名 HAP =====
KS="$WORK/mtts-keystore.p12"

java -jar "$JAR" generate-ca -keyAlias root -keyPwd 123456 -keyAlg ECC -keySize NIST-P-256 \
  -subject "CN=root" -validity 7300 -signAlg SHA256withECDSA \
  -keystoreFile "$KS" -keystorePwd 123456 -outFile "$WORK/root.cer"

java -jar "$JAR" generate-ca -keyAlias subca -keyPwd 123456 -keyAlg ECC -keySize NIST-P-256 \
  -issuer "CN=root" -issuerKeyAlias root -issuerKeyPwd 123456 \
  -subject "C=CN,O=MultiTTS,CN=MultiTTS CA" -validity 7300 -signAlg SHA256withECDSA \
  -keystoreFile "$KS" -keystorePwd 123456 -outFile "$WORK/subca.cer"

java -jar "$JAR" generate-keypair -keyAlias app -keyPwd 123456 -keyAlg ECC -keySize NIST-P-256 \
  -keystoreFile "$KS" -keystorePwd 123456
java -jar "$JAR" generate-keypair -keyAlias profile -keyPwd 123456 -keyAlg ECC -keySize NIST-P-256 \
  -keystoreFile "$KS" -keystorePwd 123456

java -jar "$JAR" generate-cert -keyAlias root -keyPwd 123456 \
  -issuer "CN=root" -issuerKeyAlias root -issuerKeyPwd 123456 -subject "CN=root" \
  -validity 7300 -keyUsage digitalSignature -extKeyUsage codeSignature \
  -signAlg SHA256withECDSA -keystoreFile "$KS" -keystorePwd 123456 -outFile "$WORK/root.cer"

java -jar "$JAR" generate-app-cert -keyAlias app -keyPwd 123456 \
  -issuer "C=CN,O=MultiTTS,CN=MultiTTS CA" -issuerKeyAlias subca -issuerKeyPwd 123456 \
  -subject "C=CN,O=MultiTTS,CN=app-debug" -validity 7300 -signAlg SHA256withECDSA \
  -keystoreFile "$KS" -keystorePwd 123456 -outForm certChain \
  -rootCaCertFile "$WORK/root.cer" -subCaCertFile "$WORK/subca.cer" \
  -outFile "$WORK/app_debug_chain.cer"

java -jar "$JAR" generate-profile-cert -keyAlias profile -keyPwd 123456 \
  -issuer "C=CN,O=MultiTTS,CN=MultiTTS CA" -issuerKeyAlias subca -issuerKeyPwd 123456 \
  -subject "C=CN,O=MultiTTS,CN=profile-debug" -validity 7300 -signAlg SHA256withECDSA \
  -keystoreFile "$KS" -keystorePwd 123456 -outForm certChain \
  -rootCaCertFile "$WORK/root.cer" -subCaCertFile "$WORK/subca.cer" \
  -outFile "$WORK/profile_debug_chain.cer"

# profile JSON: 证书用 url-safe base64(规避 sign-app 的标准 base64 '+' 解码 bug)
FP="$(openssl x509 -in "$WORK/app_debug_chain.cer" -noout -serial | cut -d= -f2 | tr 'A-F' 'a-f')"
CERT_B64="$(python3 -c "import base64,re; t=open('$WORK/app_debug_chain.cer').read(); d=re.search(r'-----BEGIN CERTIFICATE-----(.*?)-----END CERTIFICATE-----',t,re.S).group(1); print(base64.urlsafe_b64encode(base64.b64decode(d)).decode())")"

python3 - "$FP" "$CERT_B64" > "$WORK/debug-profile.json" <<'PYEOF'
import json, sys
fp, cert = sys.argv[1], sys.argv[2]
profile = {
  "version-name": "1.0.0", "version-code": 1, "app-dist-mode": "debug",
  "uuid": "mtts-debug-0001",
  "validity": {"not-before": 1727000000000, "not-after": 4102444800000},
  "type": "debug",
  "bundle-info": {
    "developer-id": "local-debug", "cert-type": "debug",
    "app-cert-sn": fp, "development-certificate": cert,
    "profile-id": "mtts-debug",
    "app-identifier": "org.nobody.multitts.harmony",
    "bundle-name": "org.nobody.multitts.harmony", "developer": "local"
  },
  "acls": {"allowed-acls": [""]},
  "app-privilege-capabilities": [""],
  "debug-info": {"device-ids": ["*"]},
  "issuer-info": {"issuer-id": "local", "serial-no": "1"}
}
json.dump(profile, sys.stdout, indent=2)
PYEOF

java -jar "$JAR" sign-profile -mode localSign -keyAlias profile -keyPwd 123456 \
  -profileCertFile "$WORK/profile_debug_chain.cer" -inFile "$WORK/debug-profile.json" \
  -signAlg SHA256withECDSA -keystoreFile "$KS" -keystorePwd 123456 \
  -outFile "$WORK/debug-profile.p7b"

SIGNED="$WORK/entry-default-signed.hap"
java -jar "$JAR" sign-app -mode localSign -keyAlias app -keyPwd 123456 \
  -appCertFile "$WORK/app_debug_chain.cer" -profileFile "$WORK/debug-profile.p7b" \
  -inFile "$UNSIGNED" -signAlg SHA256withECDSA \
  -keystoreFile "$KS" -keystorePwd 123456 -outFile "$SIGNED"

echo ""
echo "✅ 构建完成(已签名): $SIGNED"
echo "   安装: hdc install $SIGNED   (或 DevEco 云真机拖入安装)"
