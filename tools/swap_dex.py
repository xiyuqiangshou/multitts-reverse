#!/usr/bin/env python
# -*- coding: utf-8 -*-
"""
dex-swap（缺陷③修复方案）：以官方 1.8.3 为底包，仅替换 classes*.dex
---------------------------------------------------------------
保留官方 APK 的 resources.arsc / res/ / assets/ / lib/ / AndroidManifest.xml
（deps.jar 里的类带原版硬编码资源常量，重建包 aapt2 重分配 ID 会炸
Resources$NotFoundException，且 public.xml 钉 typeId 已证伪）。

本版为 mtts_build_kit 相对路径版：任何位置放置 kit 均可用。
用法：python tools/swap_dex.py   （在 kit 根目录运行）
"""
import zipfile, os, sys, re

KIT  = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))  # kit 根
ORIG = os.path.join(KIT, "base", "multitts_orig.apk")               # 官方底包
REB  = os.path.join(KIT, "MultiTTS-Project", "app", "build", "outputs",
                    "apk", "release", "app-release.apk")            # gradle 产物
OUT  = os.path.join(KIT, "out", "mtts_unsigned.apk")
TMP  = OUT + ".tmp"

def dex_sort_key(name):
    m = re.match(r"classes(\d*)\.dex$", name)
    if not m:
        return (1, 0)
    idx = m.group(1)
    return (0, int(idx) if idx else 1)

def is_dex(n):
    return re.match(r"classes\d*\.dex$", n) is not None

def is_sig(n):
    if not n.startswith("META-INF/"):
        return False
    base = n.rsplit("/", 1)[-1]
    if base == "MANIFEST.MF":
        return True
    return base.upper().endswith((".SF", ".RSA", ".DSA", ".EC"))

def main():
    for p in (ORIG, REB):
        if not os.path.exists(p):
            sys.exit("缺文件: " + p)
    zreb = zipfile.ZipFile(REB)
    dex_names = sorted([n for n in zreb.namelist() if is_dex(n)], key=dex_sort_key)
    if not dex_names:
        sys.exit("重建包里没有 dex")
    print("重建包 dex:", dex_names)
    dex_blobs = []
    for n in dex_names:
        data = zreb.read(n)
        dex_blobs.append((n, data))
        print("  %-14s %d bytes" % (n, len(data)))
    zreb.close()

    zin = zipfile.ZipFile(ORIG)
    copied = skipped_dex = skipped_sig = 0
    os.makedirs(os.path.dirname(OUT), exist_ok=True)

    with zipfile.ZipFile(TMP, "w", zipfile.ZIP_DEFLATED) as zout:
        for info in zin.infolist():
            n = info.filename
            if is_dex(n):
                skipped_dex += 1
                continue
            if is_sig(n):
                skipped_sig += 1
                continue
            with zin.open(info) as src:
                zout.writestr(info, src.read(), compress_type=info.compress_type)
            copied += 1
        # 我方 dex 以 STORED 写入（Android 标准做法）
        for n, data in dex_blobs:
            zi = zipfile.ZipInfo(n, date_time=(1981, 1, 1, 1, 1, 1))
            zi.compress_type = zipfile.ZIP_STORED
            zi.external_attr = 0
            zout.writestr(zi, data)
    zin.close()

    os.replace(TMP, OUT)
    print("---")
    print("复制条目 %d / 跳过原dex %d / 跳过原签名 %d" % (copied, skipped_dex, skipped_sig))
    print("输出:", OUT, os.path.getsize(OUT), "bytes")

main()
