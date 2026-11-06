// 生成最小可用的应用图标(纯 node, 无依赖): 深色底 + 均衡器竖条
const zlib = require('zlib');
const fs = require('fs');
const path = require('path');

const CRC_TABLE = (() => {
  const t = new Uint32Array(256);
  for (let n = 0; n < 256; n++) {
    let c = n;
    for (let k = 0; k < 8; k++) c = (c & 1) ? (0xEDB88320 ^ (c >>> 1)) : (c >>> 1);
    t[n] = c >>> 0;
  }
  return t;
})();
function crc32(buf) {
  let c = 0xFFFFFFFF;
  for (let i = 0; i < buf.length; i++) c = CRC_TABLE[(c ^ buf[i]) & 0xFF] ^ (c >>> 8);
  return (c ^ 0xFFFFFFFF) >>> 0;
}
function chunk(type, data) {
  const len = Buffer.alloc(4);
  len.writeUInt32BE(data.length);
  const t = Buffer.from(type, 'ascii');
  const crc = Buffer.alloc(4);
  crc.writeUInt32BE(crc32(Buffer.concat([t, data])));
  return Buffer.concat([len, t, data, crc]);
}
function png(size) {
  const bg = [0x14, 0x21, 0x2B];
  const accent = [0x4F, 0xC3, 0xF7];
  const heights = [0.45, 0.85, 0.65, 1.0, 0.55, 0.9, 0.4, 0.75];
  const bars = heights.length;
  const raw = Buffer.alloc((size * 4 + 1) * size);
  const bandHalf = size * 0.30;
  const unit = size / (bars * 2);
  for (let y = 0; y < size; y++) {
    const rowStart = y * (size * 4 + 1);
    raw[rowStart] = 0;
    for (let x = 0; x < size; x++) {
      const o = rowStart + 1 + x * 4;
      let r = bg[0], g = bg[1], b = bg[2];
      const barIdx = Math.floor(x / unit);
      if (barIdx % 2 === 0) {
        const i = barIdx / 2;
        const half = bandHalf * heights[i];
        if (Math.abs(y - size / 2) <= half) {
          r = accent[0]; g = accent[1]; b = accent[2];
        }
      }
      raw[o] = r; raw[o + 1] = g; raw[o + 2] = b; raw[o + 3] = 255;
    }
  }
  const ihdr = Buffer.alloc(13);
  ihdr.writeUInt32BE(size, 0);
  ihdr.writeUInt32BE(size, 4);
  ihdr[8] = 8; ihdr[9] = 6;
  return Buffer.concat([
    Buffer.from([0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A]),
    chunk('IHDR', ihdr),
    chunk('IDAT', zlib.deflateSync(raw)),
    chunk('IEND', Buffer.alloc(0))
  ]);
}
const targets = [
  'MultiTTS-Harmony/AppScope/resources/base/media/app_icon.png',
  'MultiTTS-Harmony/entry/src/main/resources/base/media/icon.png'
];
for (const t of targets) {
  fs.mkdirSync(path.dirname(t), { recursive: true });
  fs.writeFileSync(t, png(108));
  console.log('wrote', t, png(108).length, 'bytes');
}
