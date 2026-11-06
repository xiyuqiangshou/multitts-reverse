/**
 * 纯 TS MD5 实现 —— 无平台依赖(自含 UTF-8 编码), ArkTS/Node 双端可跑。
 * 用途: 音色模板里的 java.md5Encode32 / md5Encode16 签名参数。
 * 正确性由测试向量保证(空串/"abc"/fox)。
 */

function utf8Encode(msg: string): Uint8Array {
  const out: number[] = [];
  for (let i = 0; i < msg.length; i++) {
    let code = msg.charCodeAt(i);
    if (code >= 0xD800 && code <= 0xDBFF && i + 1 < msg.length) {
      const lo = msg.charCodeAt(i + 1);
      if (lo >= 0xDC00 && lo <= 0xDFFF) {
        code = 0x10000 + ((code - 0xD800) << 10) + (lo - 0xDC00);
        i++;
      }
    }
    if (code < 0x80) {
      out.push(code);
    } else if (code < 0x800) {
      out.push(0xC0 | (code >> 6), 0x80 | (code & 0x3F));
    } else if (code < 0x10000) {
      out.push(0xE0 | (code >> 12), 0x80 | ((code >> 6) & 0x3F), 0x80 | (code & 0x3F));
    } else {
      out.push(0xF0 | (code >> 18), 0x80 | ((code >> 12) & 0x3F),
        0x80 | ((code >> 6) & 0x3F), 0x80 | (code & 0x3F));
    }
  }
  return new Uint8Array(out);
}

const SHIFTS: number[] = [
  7, 12, 17, 22, 7, 12, 17, 22, 7, 12, 17, 22, 7, 12, 17, 22,
  5, 9, 14, 20, 5, 9, 14, 20, 5, 9, 14, 20, 5, 9, 14, 20,
  4, 11, 16, 23, 4, 11, 16, 23, 4, 11, 16, 23, 4, 11, 16, 23,
  6, 10, 15, 21, 6, 10, 15, 21, 6, 10, 15, 21, 6, 10, 15, 21
];

const K: Int32Array = (() => {
  const k = new Int32Array(64);
  for (let i = 0; i < 64; i++) {
    k[i] = Math.floor(Math.abs(Math.sin(i + 1)) * 4294967296) | 0;
  }
  return k;
})();

export class Md5 {
  /** 返回 32 位小写 hex */
  static hex(msg: string): string {
    const bytes = utf8Encode(msg);
    const bitLen = bytes.length * 8;
    // 填充: 0x80 + 0... + 64bit LE 长度
    const padded = (((bytes.length + 8) >> 6) + 1) << 6;
    const buf = new Uint8Array(padded);
    buf.set(bytes);
    buf[bytes.length] = 0x80;
    const dv = new DataView(buf.buffer);
    dv.setUint32(padded - 8, bitLen >>> 0, true);
    dv.setUint32(padded - 4, Math.floor(bitLen / 4294967296), true);

    let a0 = 0x67452301 | 0;
    let b0 = 0xEFCDAB89 | 0;
    let c0 = 0x98BADCFE | 0;
    let d0 = 0x10325476 | 0;
    const M = new Int32Array(16);

    for (let chunk = 0; chunk < padded; chunk += 64) {
      for (let j = 0; j < 16; j++) {
        M[j] = dv.getInt32(chunk + j * 4, true);
      }
      let A = a0, B = b0, C = c0, D = d0;
      for (let i = 0; i < 64; i++) {
        let F: number;
        let g: number;
        if (i < 16) {
          F = (B & C) | (~B & D);
          g = i;
        } else if (i < 32) {
          F = (D & B) | (~D & C);
          g = (5 * i + 1) % 16;
        } else if (i < 48) {
          F = B ^ C ^ D;
          g = (3 * i + 5) % 16;
        } else {
          F = C ^ (B | ~D);
          g = (7 * i) % 16;
        }
        F = (F + A + K[i] + M[g]) | 0;
        const s = SHIFTS[i];
        const rotated = ((F << s) | (F >>> (32 - s))) | 0;
        A = D;
        D = C;
        C = B;
        B = (B + rotated) | 0;
      }
      a0 = (a0 + A) | 0;
      b0 = (b0 + B) | 0;
      c0 = (c0 + C) | 0;
      d0 = (d0 + D) | 0;
    }
    const out = new Uint8Array(16);
    const odv = new DataView(out.buffer);
    odv.setInt32(0, a0, true);
    odv.setInt32(4, b0, true);
    odv.setInt32(8, c0, true);
    odv.setInt32(12, d0, true);
    let hex = '';
    for (let i = 0; i < 16; i++) {
      hex += Md5.hex2(out[i]);
    }
    return hex;
  }

  /** legado java.md5Encode16 口径: 取 32 位 hex 的 [8,24) 区间 */
  static hex16(msg: string): string {
    return Md5.hex(msg).substring(8, 24);
  }

  private static hex2(b: number): string {
    const t = '0123456789abcdef';
    return t.charAt((b >> 4) & 0xF) + t.charAt(b & 0xF);
  }
}
