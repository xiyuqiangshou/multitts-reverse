# MultiTTS-Harmony 上云真机检查单

## 包体状态(已在 Linux 云端验证)
- [x] zip 结构完整(8 文件, 262KB)
- [x] bundleName: org.nobody.multitts.harmony
- [x] deviceTypes: phone / tablet / 2in1
- [x] 签名密码学校验通过(hap-sign-tool verify-app: Verify success)
- [x] 签名材料: 本地自签 root→subca→app 三级链 + debug profile

## 关于签名(重要)
本包为本地自签 debug 签名。云真机安装时:
- 云测试(自动遍历): 平台会重签 → 可直接上传
- 手动调试(Remote Device): 建议改用你自己账号的自动签名包:
  DevEco Studio → 打开工程 → Project Structure → Signing Configs →
  勾 Automatically generate → Build → 重新得到 signed HAP

## 验收步骤(装上后 3 分钟)
1. App 打开不闪退, 三 Tab(试听/音色/服务)正常
2. 音色 Tab: 粘贴 httpTTS JSON → 导入 → 列表出现
3. 试听 Tab: 点试听 → 出声
4. 服务 Tab: 开转发开关 → 显示 :18080

## 常见问题映射
- 安装报 "签名校验失败/9568289" → 用自动签名重签
- 打开闪退 → 抓 hilog 贴给助手
- 试听无声音 → 检查音色 URL 是否可公网访问
