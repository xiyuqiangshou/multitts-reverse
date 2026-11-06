/**
 * 规则分析器 —— 移植自 plugin/httptts/RuleAnalyzer.java 的 innerRule 语义。
 * MultiTTS URL 模板形如: https://api.example/tts?text={{text}}, {"method":"POST",...}
 * 本类负责 {{...}} 块的定位(含大括号配平)与恢复, JS 表达式求值在 ScriptEngine(phase2/QuickJS)。
 */

export class InnerRule {
  /** {{ 与 }} 之间的原文 */
  inner: string = '';
  /** {{ 起始位置, -1 表示未命中 */
  start: number = -1;
  /** 命中块结束位置(不含) */
  end: number = -1;
  isValid: boolean = false;
}

export class RuleAnalyzer {
  /**
   * 在 source 中从 from 开始找第一个 {{...}} 块。
   * 原版语义: 首个 "{{" 起始, 到其后首个 "}}" 收口 —— 不做深度配平;
   * {{a{{b}}}} 的 inner 即 "a{{b"。
   */
  static findInnerRule(source: string, from: number): InnerRule {
    const out = new InnerRule();
    const start = source.indexOf('{{', from);
    if (start < 0) {
      return out;
    }
    const close = source.indexOf('}}', start + 2);
    if (close < 0) {
      return out;
    }
    out.start = start;
    out.inner = source.substring(start + 2, close);
    out.end = close + 2;
    out.isValid = true;
    return out;
  }

  /**
   * 对应 paramPattern: \s*,\s*(?=\{) —— 在顶层把 "url, {选项}" 拆开。
   * 返回 [url 部分, 选项 JSON 部分(可为空串)]。
   */
  static splitUrlOption(tpl: string): string[] {
    let inBrace = 0;
    let inStr = false;
    let esc = false;
    for (let i = 0; i < tpl.length; i++) {
      const c = tpl.charAt(i);
      if (inStr) {
        if (esc) {
          esc = false;
        } else if (c === '\\') {
          esc = true;
        } else if (c === '"') {
          inStr = false;
        }
        continue;
      }
      if (c === '"') {
        inStr = true;
      } else if (c === '{') {
        inBrace++;
      } else if (c === '}') {
        if (inBrace > 0) {
          inBrace--;
        }
      } else if (c === ',' && inBrace === 0) {
        // 仅当逗号后(跳过空白)紧跟 '{' 才视为分隔符
        let j = i + 1;
        while (j < tpl.length && (tpl.charAt(j) === ' ' || tpl.charAt(j) === '\t')) {
          j++;
        }
        if (j < tpl.length && tpl.charAt(j) === '{') {
          const urlPart = tpl.substring(0, i).trim();
          const optPart = tpl.substring(j).trim();
          const arr: string[] = [urlPart, optPart];
          return arr;
        }
      }
    }
    const only: string[] = [tpl.trim(), ''];
    return only;
  }
}
