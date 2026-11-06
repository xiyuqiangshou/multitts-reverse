/**
 * 文本分句器 —— 朗读单元切分。
 * 对齐 wip3l/legado-harmony 的分句目标: 减少弱标点断句、避免半句停顿
 * (避开英文单词/数字+单位/emoji 硬切), 供 UI 朗读页与后台播放使用。
 */

export class SentenceSplitter {
  /** 强停顿标点 */
  private static readonly STRONG = '。！？!?；;\n';
  /** 弱停顿标点(仅超长句时才切) */
  private static readonly WEAK = '，,、：:）)】》”"\'';

  /** 返回句子数组; 每句含起止偏移(供高亮), maxLen 为软上限(默认 80 字符) */
  static split(text: string, maxLen: number = 80): Sentence[] {
    const out: Sentence[] = [];
    if (text === null || text === undefined || text.length === 0) {
      return out;
    }
    let start = 0;
    for (let i = 0; i < text.length; i++) {
      const ch = text.charAt(i);
      if (SentenceSplitter.STRONG.indexOf(ch) >= 0) {
        // 强标点: 消费连续标点后切
        let end = i + 1;
        while (end < text.length && (SentenceSplitter.STRONG.indexOf(text.charAt(end)) >= 0
          || text.charAt(end) === '"' || text.charAt(end) === '”' || text.charAt(end) === ')'
          || text.charAt(end) === '）')) {
          end++;
        }
        const seg = text.substring(start, end);
        if (seg.trim().length > 0) {
          const s: Sentence = { text: seg, start: start, end: end };
          out.push(s);
        }
        start = end;
        i = end - 1;
      }
    }
    if (start < text.length) {
      const tail = text.substring(start);
      if (tail.trim().length > 0) {
        // 尾段可能超长: 按弱标点二次切
        const pieces = SentenceSplitter.splitWeak(tail, maxLen);
        for (const p of pieces) {
          const s: Sentence = { text: p.text, start: start + p.start, end: start + p.end };
          out.push(s);
        }
      }
    }
    return out;
  }

  /** 弱标点切分: 仅在超出 maxLen 时找最近的弱标点, 且不切断英文单词/数字单位 */
  private static splitWeak(seg: string, maxLen: number): OffsetText[] {
    if (seg.length <= maxLen) {
      const one: OffsetText = { text: seg, start: 0, end: seg.length };
      return [one];
    }
    const out: OffsetText[] = [];
    let pos = 0;
    while (pos < seg.length) {
      const remain = seg.length - pos;
      if (remain <= maxLen) {
        const t: OffsetText = { text: seg.substring(pos), start: pos, end: seg.length };
        out.push(t);
        break;
      }
      // 从 pos+maxLen 往回找弱标点(回退窗口 3/4, 提高中长句命中率)
      let cut = -1;
      for (let i = pos + maxLen; i > pos + Math.floor(maxLen / 4); i--) {
        const ch = seg.charAt(i);
        if (SentenceSplitter.WEAK.indexOf(ch) >= 0) {
          // 避免切断 "3kg,5kg" 这类数字+单位串: 切点后是数字/字母且前方紧邻数字时跳过
          if (i + 1 < seg.length && /[0-9]/.test(seg.charAt(i - 1))
            && /[0-9]/.test(seg.charAt(i + 1))) {
            continue;
          }
          cut = i + 1;
          break;
        }
      }
      if (cut < 0) {
        cut = pos + maxLen; // 硬切(极端无标点长串)
      }
      const t2: OffsetText = { text: seg.substring(pos, cut), start: pos, end: cut };
      out.push(t2);
      pos = cut;
    }
    return out;
  }
}

export interface Sentence {
  text: string;
  start: number;
  end: number;
}

interface OffsetText {
  text: string;
  start: number;
  end: number;
}
