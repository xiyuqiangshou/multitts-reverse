/**
 * 文本净化 —— 对应安卓端 tts/replace 包的核心职责(简化):
 *   1. 规范空白与控制字符
 *   2. 数字读法: 123 → 一百二十三(中文语境), 年份/小数/序号特判
 *   3. 符号读法: & → "和", % → "百分之" 等
 *   4. 装饰行静音: 纯符号行(分隔线)置空 —— 对齐 wip3l 朗读过滤行为
 * 自定义替换规则(用户正则)在 VoiceStore 层之上另行叠加(phase3)。
 */

const DIGITS = ['零', '一', '二', '三', '四', '五', '六', '七', '八', '九'];
const UNITS = ['', '十', '百', '千'];
const BIG_UNITS = ['', '万', '亿', '万亿'];

export class TextSanitizer {
  /** 主入口: 中英混合文本 → 适合 TTS 的口语化文本 */
  static sanitize(input: string, digitReading: boolean = true): string {
    if (input === null || input === undefined) {
      return '';
    }
    let s = input;
    // 1. 控制字符/多余空白
    s = s.replace(/[\u0000-\u0008\u000B\u000C\u000E-\u001F]/g, '');
    s = s.replace(/[ \t]+/g, ' ');
    s = s.replace(/\n{3,}/g, '\n\n');
    // 2. 装饰分隔行(纯符号) → 连同多余换行收敛
    s = s.replace(/^[\s\*\#\-\=_~—·•◆■●★☆↔⇔]+$/gm, '');
    s = s.replace(/\n\n+/g, '\n');
    s = s.replace(/^\n+|\n+$/g, '');
    // 3. 常见符号读法: 百分比先整体捕获(数字阶段再转中文), 避免"五十百分之"别扭读法
    s = s.replace(/(\d+(?:\.\d+)?)%/g, '百分之$1');
    s = s.replace(/&/g, ' 和 ');
    s = s.replace(/℃/g, '摄氏度');
    s = s.replace(/°/g, '度');
    // URL/邮箱等长串折叠(避免逐字符朗读): 只保留读法提示
    s = s.replace(/https?:\/\/\S+/g, '链接');
    // 4. 数字读法
    if (digitReading) {
      s = TextSanitizer.convertNumbers(s);
    }
    return s;
  }

  /** 数字 → 中文读法(整数/小数/百分比上下文已在前一步处理) */
  static convertNumbers(s: string): string {
    // 小数: 3.14 → 三点一四
    s = s.replace(/(\d+)\.(\d+)/g, (m, intPart: string, frac: string) => {
      const fracRead = frac.split('').map((d) => DIGITS[Number(d)]).join('');
      return TextSanitizer.intToCn(Number(intPart)) + '点' + fracRead;
    });
    // 年份(4 位且前后非数字): 1998 → 一九九八
    s = s.replace(/(?<!\d)([12]\d{3})(?!\d)/g, (m, y: string) => {
      return y.split('').map((d) => DIGITS[Number(d)]).join('');
    });
    // 其余整数
    s = s.replace(/\d+/g, (m) => TextSanitizer.intToCn(Number(m)));
    return s;
  }

  /** 整数 → 中文(支持到万亿级); 组间补零(10001 → 一万零一) */
  static intToCn(n: number): string {
    if (n === 0) {
      return '零';
    }
    if (n < 0) {
      return '负' + TextSanitizer.intToCn(-n);
    }
    if (!Number.isInteger(n) || n >= 1e16) {
      return String(n); // 超范围/非法保持原文
    }
    // 按 4 位分组
    const groups: number[] = [];
    let x = n;
    while (x > 0) {
      groups.push(x % 10000);
      x = Math.floor(x / 10000);
    }
    let out = '';
    let pendingZero = false;
    for (let gi = groups.length - 1; gi >= 0; gi--) {
      const g = groups[gi];
      if (g === 0) {
        if (out.length > 0) {
          pendingZero = true;
        }
        continue;
      }
      // 组内有效数字不足 4 位(且不是最高组) → 需要补零连接
      if (out.length > 0 && g < 1000) {
        out += '零';
      } else if (pendingZero) {
        out += '零';
      }
      pendingZero = false;
      out += TextSanitizer.groupToCn(g) + BIG_UNITS[gi];
    }
    // 一十X → 十X 的口语化(仅组内)
    out = out.replace(/^一十/, '十');
    return out;
  }

  private static groupToCn(g: number): string {
    let out = '';
    let zeroPending = false;
    const parts = [
      Math.floor(g / 1000),
      Math.floor((g % 1000) / 100),
      Math.floor((g % 100) / 10),
      g % 10
    ];
    for (let i = 0; i < 4; i++) {
      const d = parts[i];
      if (d === 0) {
        zeroPending = out.length > 0;
        continue;
      }
      if (zeroPending) {
        out += '零';
        zeroPending = false;
      }
      out += DIGITS[d] + UNITS[3 - i];
    }
    return out;
  }
}
