/**
 * 安全表达式求值器 —— 音色模板 {{...}} 的 JS 子集求值(QuickJS 的轻量替代)。
 * 设计目标: 不引入原生依赖/不受审计算法约束的前提下, 覆盖 MultiTTS/legado
 * 音色模板中 99% 的实际用法。白名单 AST 求值, 无循环/属性访问/全局对象 → 无注入面。
 *
 * 支持:
 *   字符串(单双引号, 反引号无插值), 数字, true/false/null, 空值合并, 或, 与, 非,
 *   比较与算术, 三元, 括号, 无赋值;
 *   字段访问仅限内置根对象: text/speed/volume/pitch/percent/rate/speaker/speakerName/locale/
 *     ts(时间戳秒) / Math / Java(内建函数集, 对齐 legado java.* 口径)
 *   方法: .toFixed .substring .toUpperCase .toLowerCase .replace .trim .slice .indexOf .padStart
 *   Java.md5Encode32/16, Java.md5, Java.urlEncode/Decode, Java.base64Encode/Decode,
 *   Java.timeFormat(fmt), Java.randomUUID, Java.parseInt/parseFloat, Java.length(str)
 */
import { Md5 } from './Md5';

export class NamespaceRef {
  __ns: string = '';
}

export type ESValue = Object;

export class ESNull {
  private static instance: ESNull | null = null;
  static get(): ESNull {
    if (ESNull.instance === null) {
      ESNull.instance = new ESNull();
    }
    return ESNull.instance;
  }
  isNull(): boolean {
    return true;
  }
}

export class ASTNode {
  t: string = '';
  v: Object | null = null;
  name: string = '';
  op: string = '';
  method: string = '';
  cond: ASTNode | null = null;
  yes: ASTNode | null = null;
  no: ASTNode | null = null;
  left: ASTNode | null = null;
  right: ASTNode | null = null;
  operand: ASTNode | null = null;
  object: ASTNode | null = null;
  callee: ASTNode | null = null;
  args: ASTNode[] = [];
}

class Tokenizer {
  private src: string;
  private pos: number = 0;
  constructor(src: string) {
    this.src = src;
  }
  private peek(): string {
    return this.src.charAt(this.pos);
  }
  private skipWs(): void {
    while (this.pos < this.src.length && /\s/.test(this.src.charAt(this.pos))) {
      this.pos++;
    }
  }
  next(): string | null {
    this.skipWs();
    if (this.pos >= this.src.length) {
      return null;
    }
    const c = this.peek();
    // 多字符运算符
    for (const op of ['??', '===', '!==', '==', '!=', '>=', '<=', '&&', '||']) {
      if (this.src.startsWith(op, this.pos)) {
        this.pos += op.length;
        return op;
      }
    }
    // 完整标识符/关键字
    if (/[A-Za-z_$]/.test(c)) {
      let w = '';
      while (this.pos < this.src.length && /[A-Za-z0-9_$]/.test(this.peek())) {
        w += this.src.charAt(this.pos);
        this.pos++;
      }
      return w;
    }
    // 完整数字(含小数)
    if (/[0-9]/.test(c) || (c === '.' && /[0-9]/.test(this.src.charAt(this.pos + 1)))) {
      let w = '';
      while (this.pos < this.src.length && /[0-9.]/.test(this.peek())) {
        w += this.src.charAt(this.pos);
        this.pos++;
      }
      return w;
    }
    // 完整字符串字面量(含转义)
    if (c === '"' || c === "'") {
      let w = c;
      this.pos++;
      while (this.pos < this.src.length) {
        const ch = this.src.charAt(this.pos);
        w += ch;
        this.pos++;
        if (ch === '\\') {
          if (this.pos < this.src.length) {
            w += this.src.charAt(this.pos);
            this.pos++;
          }
          continue;
        }
        if (ch === c) {
          break;
        }
      }
      return w;
    }
    this.pos++;
    return c;
  }
  peekToken(): string | null {
    const save = this.pos;
    const t = this.next();
    this.pos = save;
    return t;
  }
}

export class EvalError extends Error {
}

class Parser {
  private tk: Tokenizer;
  private ahead: string | null = null;
  constructor(src: string) {
    this.tk = new Tokenizer(src);
  }
  private next(): string | null {
    if (this.ahead !== null) {
      const t = this.ahead;
      this.ahead = null;
      return t;
    }
    return this.tk.next();
  }
  private peek(): string | null {
    if (this.ahead === null) {
      this.ahead = this.tk.next();
    }
    return this.ahead;
  }
  private expect(t: string): void {
    const got = this.next();
    if (got !== t) {
      throw new EvalError(`期望 ${t}, 实得 ${got}`);
    }
  }
  parseExpression(): ASTNode {
    return this.parseTernary();
  }
  private parseTernary(): ASTNode {
    const cond: ASTNode = this.parseNullish();
    if (this.peek() === '?') {
      this.next();
      const yes: ASTNode = this.parseTernary();
      this.expect(':');
      const no: ASTNode = this.parseTernary();
      return (() => { const n = new ASTNode();
    n.t = 'cond';
    n.cond = cond;
    n.yes = yes;
    n.no = no;
    return n; })();
    }
    return cond;
  }
  private parseNullish(): ASTNode {
    let left: ASTNode = this.parseOr();
    while (this.peek() === '??') {
      this.next();
      const right: ASTNode = this.parseOr();
      left = (() => { const n = new ASTNode();
    n.t = 'nullish';
    n.left = left;
    n.right = right;
    return n; })();
    }
    return left;
  }
  private parseOr(): ASTNode {
    let left: ASTNode = this.parseAnd();
    while (this.peek() === '||') {
      this.next();
      const right: ASTNode = this.parseAnd();
      left = (() => { const n = new ASTNode();
    n.t = 'or';
    n.left = left;
    n.right = right;
    return n; })();
    }
    return left;
  }
  private parseAnd(): ASTNode {
    let left: ASTNode = this.parseCompare();
    while (this.peek() === '&&') {
      this.next();
      const right = this.parseCompare();
      left = (() => { const n = new ASTNode();
    n.t = 'and';
    n.left = left;
    n.right = right;
    return n; })();
    }
    return left;
  }
  private parseCompare(): ASTNode {
    let left: ASTNode = this.parseAdd();
    for (;;) {
      const op = this.peek();
      if (op === '<' || op === '>' || op === '<=' || op === '>=' || op === '==' || op === '==='
        || op === '!=' || op === '!==') {
        this.next();
        const right: ASTNode = this.parseAdd();
        left = (() => { const n = new ASTNode();
    n.t = 'cmp';
    n.op = op;
    n.left = left;
    n.right = right;
    return n; })();
      } else {
        return left;
      }
    }
  }
  private parseAdd(): ASTNode {
    let left: ASTNode = this.parseMul();
    for (;;) {
      const op = this.peek();
      if (op === '+' || op === '-') {
        this.next();
        const right: ASTNode = this.parseMul();
        left = (() => { const n = new ASTNode();
    n.t = 'arith';
    n.op = op;
    n.left = left;
    n.right = right;
    return n; })();
      } else {
        return left;
      }
    }
  }
  private parseMul(): ASTNode {
    let left: ASTNode = this.parseUnary();
    for (;;) {
      const op = this.peek();
      if (op === '*' || op === '/' || op === '%') {
        this.next();
        const right: ASTNode = this.parseUnary();
        left = (() => { const n = new ASTNode();
    n.t = 'arith';
    n.op = op;
    n.left = left;
    n.right = right;
    return n; })();
      } else {
        return left;
      }
    }
  }
  private parseUnary(): ASTNode {
    const op = this.peek();
    if (op === '!' || op === '-') {
      this.next();
      const operand = this.parseUnary();
      return (() => { const n = new ASTNode();
    n.t = 'unary';
    n.op = op;
    n.operand = operand;
    return n; })();
    }
    return this.parsePostfix();
  }
  private parsePostfix(): ASTNode {
    let node: ASTNode = this.parsePrimary();
    for (;;) {
      if (this.peek() === '.') {
        this.next();
        const name = this.next();
        if (name === null || !/^[A-Za-z_$][A-Za-z0-9_$]*$/.test(name)) {
          throw new EvalError('非法成员名: ' + name);
        }
        if (this.peek() === '(') {
          this.next();
          const args = this.parseArgs();
          node = (() => { const n = new ASTNode();
    n.t = 'call';
    n.callee = node;
    n.method = name;
    n.args = args;
    return n; })();
        } else {
          node = (() => { const n = new ASTNode();
    n.t = 'member';
    n.name = name;
    n.object = node;
    return n; })();
        }
      } else {
        return node;
      }
    }
  }
  private parseArgs(): ASTNode[] {
    const args: ASTNode[] = [];
    if (this.peek() === ')') {
      this.next();
      return args;
    }
    for (;;) {
      args.push(this.parseExpression() as ASTNode);
      const t = this.next();
      if (t === ')') {
        return args;
      }
      if (t !== ',') {
        throw new EvalError('参数列表期待 , 或 )');
      }
    }
  }
  private parsePrimary(): ASTNode {
    const tok = this.next();
    if (tok === null) {
      throw new EvalError('表达式意外结束');
    }
    if (tok === '(') {
      const inner: ASTNode = this.parseExpression();
      this.expect(')');
      return inner;
    }
    if (tok.length >= 2 && (tok.charAt(0) === '"' || tok.charAt(0) === "'")) {
      // tokenizer 已吐出完整字符串字面量: 剥引号并反转义
      const body = tok.substring(1, tok.length - 1);
      const unesc = body.replace(/\\(.)/g, (m, x: string) => {
        if (x === 'n') {
          return '\n';
        }
        if (x === 't') {
          return '\t';
        }
        return x;
      });
      return (() => { const n = new ASTNode();
    n.t = 'str';
    n.v = unesc;
    return n; })();
    }
    if (/^[0-9]/.test(tok) || (tok.charAt(0) === '.' && tok.length > 1)) {
      return (() => { const n = new ASTNode();
    n.t = 'num';
    n.v = parseFloat(tok);
    return n; })();
    }
    if (/^[A-Za-z_$][A-Za-z0-9_$]*$/.test(tok)) {
      if (tok === 'true') {
        return (() => { const n = new ASTNode();
    n.t = 'bool';
    n.v = true;
    return n; })();
      }
      if (tok === 'false') {
        return (() => { const n = new ASTNode();
    n.t = 'bool';
    n.v = false;
    return n; })();
      }
      if (tok === 'null') {
        const nn = new ASTNode();
        nn.t = 'null';
        return nn;
      }
      const idn = new ASTNode();
      idn.t = 'ident';
      idn.name = tok;
      return idn;
    }
    throw new EvalError('意外记号: ' + tok);
  }
}

export class SafeEvaluator {
  /** 求值 expr; anyError 抛 EvalError(由调用方决定回退策略) */
  static evalExpr(expr: string, vars: Record<string, string>): string {
    const ast: ASTNode = new Parser(expr).parseExpression();
    const v = SafeEvaluator.evalASTNode(ast, vars);
    return SafeEvaluator.toStr(v);
  }

  private static toStr(v: Object): string {
    if (SafeEvaluator.isNullValue(v)) {
      return '';
    }
    if (typeof v === 'string') {
      return v;
    }
    if (typeof v === 'number') {
      return String(v);
    }
    if (typeof v === 'boolean') {
      return v ? 'true' : 'false';
    }
    return String(v);
  }

  private static evalASTNode(n: ASTNode, vars: Record<string, string>): ESValue {
    switch (n.t) {
      case 'str':
        return n.v !== null ? n.v : '';
      case 'num':
        return n.v !== null ? n.v : 0;
      case 'bool':
        return n.v !== null ? n.v : false;
      case 'null':
        return ESNull.get();
      case 'cond':
        return SafeEvaluator.truthy(SafeEvaluator.evalASTNode(SafeEvaluator.req(n.cond, 'cond'), vars))
          ? SafeEvaluator.evalASTNode(SafeEvaluator.req(n.yes, 'yes'), vars)
          : SafeEvaluator.evalASTNode(SafeEvaluator.req(n.no, 'no'), vars);
      case 'nullish': {
        let l: Object = new ESNull();
        try {
          l = SafeEvaluator.evalASTNode(SafeEvaluator.req(n.left, 'left'), vars);
        } catch (e) {
          // 模板习惯: {{missing ?? 'x'}} 中未定义变量视为 null(严格 JS 会抛, 这里宽容)
          if (e instanceof EvalError) {
            l = new ESNull();
          } else {
            throw e as Error;
          }
        }
        return SafeEvaluator.isNullValue(l) ? SafeEvaluator.evalASTNode(SafeEvaluator.req(n.right, 'right'), vars) : l;
      }
      case 'or': {
        const l = SafeEvaluator.evalASTNode(SafeEvaluator.req(n.left, 'left'), vars);
        return SafeEvaluator.truthy(l) ? l : SafeEvaluator.evalASTNode(SafeEvaluator.req(n.right, 'right'), vars);
      }
      case 'and': {
        const l = SafeEvaluator.evalASTNode(SafeEvaluator.req(n.left, 'left'), vars);
        return SafeEvaluator.truthy(l) ? SafeEvaluator.evalASTNode(SafeEvaluator.req(n.right, 'right'), vars) : l;
      }
      case 'cmp': {
        const a = SafeEvaluator.evalASTNode(SafeEvaluator.req(n.left, 'left'), vars);
        const b = SafeEvaluator.evalASTNode(SafeEvaluator.req(n.right, 'right'), vars);
        return SafeEvaluator.cmp(n.op, a, b);
      }
      case 'arith': {
        const a = SafeEvaluator.evalASTNode(SafeEvaluator.req(n.left, 'left'), vars);
        const b = SafeEvaluator.evalASTNode(SafeEvaluator.req(n.right, 'right'), vars);
        if (n.op === '+') {
          if (typeof a === 'string' || typeof b === 'string') {
            return SafeEvaluator.toStr(a) + SafeEvaluator.toStr(b);
          }
          return (Number(a) + Number(b));
        }
        const x = Number(a);
        const y = Number(b);
        if (n.op === '-') {
          return x - y;
        }
        if (n.op === '*') {
          return x * y;
        }
        if (n.op === '/') {
          return y === 0 ? 0 : x / y;
        }
        return y === 0 ? 0 : x % y;
      }
      case 'unary': {
        const v = SafeEvaluator.evalASTNode(SafeEvaluator.req(n.operand, 'operand'), vars);
        if (n.op === '!') {
          return !SafeEvaluator.truthy(v);
        }
        return -Number(v);
      }
      case 'ident':
        return SafeEvaluator.resolveIdent(n.name, vars);
      case 'member':
        return SafeEvaluator.evalMember(n, vars);
      case 'call':
        return SafeEvaluator.evalCall(n, vars);
      default:
        throw new EvalError('未知节点: ' + n.t);
    }
  }


  private static req(n: ASTNode | null, field: string): ASTNode {
    if (n === null) {
      throw new EvalError('AST 节点缺失: ' + field);
    }
    return n;
  }

  private static isNullValue(v: Object): boolean {
    return v === null || v === undefined || v instanceof ESNull;
  }

  private static truthy(v: Object): boolean {
    if (SafeEvaluator.isNullValue(v)) {
      return false;
    }
    if (typeof v === 'string') {
      return v.length > 0;
    }
    return Boolean(v);
  }

  private static cmp(op: string, a: ESValue, b: ESValue): boolean {
    const as = typeof a === 'string' || typeof b === 'string' ? SafeEvaluator.toStr(a) : Number(a);
    const bs = typeof a === 'string' || typeof b === 'string' ? SafeEvaluator.toStr(b) : Number(b);
    const eqv = SafeEvaluator.toStr(a) === SafeEvaluator.toStr(b);
    switch (op) {
      case '==':
      case '===':
        return eqv;
      case '!=':
      case '!==':
        return !eqv;
      case '<':
        return as < bs;
      case '<=':
        return as <= bs;
      case '>':
        return as > bs;
      default:
        return as >= bs;
    }
  }

  private static resolveIdent(name: string, vars: Record<string, string>): Object {
    if (name === 'Math') {
      const m = new NamespaceRef(); m.__ns = 'Math'; return m;
    }
    if (name === 'Java') {
      const j = new NamespaceRef(); j.__ns = 'Java'; return j;
    }
    if (vars[name] !== undefined) {
      return vars[name];
    }
    throw new EvalError('未定义变量: ' + name);
  }

  private static evalMember(n: ASTNode, vars: Record<string, string>): ESValue {
    // 仅支持字符串方法名引用(不立即求值, 由 call 处理); member on literal 走到这说明有属性读取
    throw new EvalError('不支持裸属性访问: .' + n.name);
  }

  private static evalCall(n: ASTNode, vars: Record<string, string>): ESValue {
    const args: ESValue[] = [];
    for (const a of n.args) {
      args.push(SafeEvaluator.evalASTNode(a, vars));
    }
    const callee = SafeEvaluator.req(n.callee, 'callee');

    // 根对象方法: Java.xxx(...) / Math.xxx(...)
    if (callee.t === 'ident' && (callee.name === 'Java' || callee.name === 'Math')) {
      const ns = callee.name;
      const fn = n.method;
      if (ns === 'Math') {
        return SafeEvaluator.mathFn(fn, args);
      }
      return SafeEvaluator.javaFn(fn, args);
    }

    // 字符串方法: expr.toFixed(...) 等 —— callee 必须是已求值表达式
    const target = SafeEvaluator.evalASTNode(callee, vars);
    return SafeEvaluator.stringFn(n.method, target, args);
  }

  private static mathFn(fn: string, args: ESValue[]): ESValue {
    const a0 = args.length > 0 ? Number(args[0]) : 0;
    if (fn === 'abs') {
      return Math.abs(a0);
    }
    if (fn === 'floor') {
      return Math.floor(a0);
    }
    if (fn === 'ceil') {
      return Math.ceil(a0);
    }
    if (fn === 'round') {
      return Math.round(a0);
    }
    if (fn === 'min') {
      return Math.min(...args.map((x) => Number(x)));
    }
    if (fn === 'max') {
      return Math.max(...args.map((x) => Number(x)));
    }
    if (fn === 'random') {
      return Math.random();
    }
    throw new EvalError('Math.' + fn + ' 未支持');
  }

  /** 对齐 legado/MultiTTS 脚本环境的 java.* 内建函数(安全子集) */
  private static javaFn(fn: string, args: ESValue[]): ESValue {
    const s0 = args.length > 0 ? SafeEvaluator.toStr(args[0]) : '';
    if (fn === 'md5Encode32' || fn === 'md5Encode') {
      return Md5.hex(s0);
    }
    if (fn === 'md5Encode16') {
      return Md5.hex16(s0);
    }
    if (fn === 'urlEncode' || fn === 'encodeURI') {
      return encodeURIComponent(s0);
    }
    if (fn === 'urlDecode' || fn === 'decodeURI') {
      return decodeURIComponent(s0);
    }
    if (fn === 'base64Encode' || fn === 'base64EncodeToString') {
      return SafeEvaluator.b64encode(s0);
    }
    if (fn === 'base64Decode' || fn === 'base64DecodeToString') {
      return SafeEvaluator.b64decode(s0);
    }
    if (fn === 'timeFormat') {
      // timeFormat(fmt) 或 timeFormat(ms, fmt)
      const fmt = args.length >= 2 ? SafeEvaluator.toStr(args[1]) : SafeEvaluator.toStr(args[0]);
      const ms = args.length >= 2 ? Number(args[0]) : Date.now();
      return SafeEvaluator.timeFormat(ms, fmt);
    }
    if (fn === 'currentTimeMillis') {
      return Date.now();
    }
    if (fn === 'randomUUID') {
      return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, (c) => {
        const r = Math.random() * 16 | 0;
        const v = c === 'x' ? r : (r & 0x3 | 0x8);
        return v.toString(16);
      });
    }
    if (fn === 'parseInt') {
      return parseInt(s0, 10);
    }
    if (fn === 'parseFloat') {
      return parseFloat(s0);
    }
    if (fn === 'length') {
      return s0.length;
    }
    if (fn === 'getString' || fn === 'ajax' || fn === 'fetch') {
      throw new EvalError('Java.' + fn + ' 需要网络与脚本引擎, 属 phase3 QuickJS 范围');
    }
    throw new EvalError('Java.' + fn + ' 未支持');
  }

  private static stringFn(fn: string, target: ESValue, args: ESValue[]): ESValue {
    const s = SafeEvaluator.toStr(target);
    if (fn === 'toFixed') {
      return Number(s).toFixed(args.length > 0 ? Number(args[0]) : 0);
    }
    if (fn === 'substring' || fn === 'slice') {
      const a = args.length > 0 ? Number(args[0]) : 0;
      const b = args.length > 1 ? Number(args[1]) : undefined;
      return b === undefined ? s.substring(a) : s.substring(a, b);
    }
    if (fn === 'toUpperCase') {
      return s.toUpperCase();
    }
    if (fn === 'toLowerCase') {
      return s.toLowerCase();
    }
    if (fn === 'trim') {
      return s.trim();
    }
    if (fn === 'replace') {
      if (args.length < 2) {
        return s;
      }
      return s.split(SafeEvaluator.toStr(args[0])).join(SafeEvaluator.toStr(args[1]));
    }
    if (fn === 'indexOf') {
      return s.indexOf(args.length > 0 ? SafeEvaluator.toStr(args[0]) : '');
    }
    if (fn === 'padStart') {
      const len = args.length > 0 ? Number(args[0]) : 0;
      const pad = args.length > 1 ? SafeEvaluator.toStr(args[1]) : ' ';
      return s.padStart(len, pad);
    }
    if (fn === 'charAt') {
      return s.charAt(args.length > 0 ? Number(args[0]) : 0);
    }
    if (fn === 'length') {
      return s.length;
    }
    throw new EvalError('方法 .' + fn + '() 未支持');
  }

  private static timeFormat(ms: number, fmt: string): string {
    const d = new Date(ms);
    const p2 = (x: number): string => (x < 10 ? '0' + x : String(x));
    return fmt
      .replace('yyyy', String(d.getFullYear()))
      .replace('MM', p2(d.getMonth() + 1))
      .replace('dd', p2(d.getDate()))
      .replace('HH', p2(d.getHours()))
      .replace('mm', p2(d.getMinutes()))
      .replace('ss', p2(d.getSeconds()));
  }

  private static b64encode(s: string): string {
    // 纯 TS base64(UTF-8 入)
    const bytes: number[] = [];
    for (const ch of s) {
      const c = (ch.codePointAt(0) as number);
      if (c < 0x80) {
        bytes.push(c);
      } else if (c < 0x800) {
        bytes.push(0xC0 | (c >> 6), 0x80 | (c & 0x3F));
      } else if (c < 0x10000) {
        bytes.push(0xE0 | (c >> 12), 0x80 | ((c >> 6) & 0x3F), 0x80 | (c & 0x3F));
      } else {
        bytes.push(0xF0 | (c >> 18), 0x80 | ((c >> 12) & 0x3F),
          0x80 | ((c >> 6) & 0x3F), 0x80 | (c & 0x3F));
      }
    }
    const T = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/';
    let out = '';
    for (let i = 0; i < bytes.length; i += 3) {
      const b0 = bytes[i];
      const b1 = i + 1 < bytes.length ? bytes[i + 1] : 0;
      const b2 = i + 2 < bytes.length ? bytes[i + 2] : 0;
      out += T.charAt(b0 >> 2);
      out += T.charAt(((b0 & 3) << 4) | (b1 >> 4));
      out += i + 1 < bytes.length ? T.charAt(((b1 & 15) << 2) | (b2 >> 6)) : '=';
      out += i + 2 < bytes.length ? T.charAt(b2 & 63) : '=';
    }
    return out;
  }

  private static b64decode(s: string): string {
    const T = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/';
    const clean = s.replace(/[^A-Za-z0-9+/]/g, '');
    const bytes: number[] = [];
    for (let i = 0; i < clean.length; i += 4) {
      const n = [0, 1, 2, 3].map((k) => {
        const idx = T.indexOf(clean.charAt(i + k));
        return idx < 0 ? 0 : idx;
      });
      bytes.push((n[0] << 2) | (n[1] >> 4));
      if (i + 2 < clean.length || clean.charAt(i + 2) !== '=') {
        bytes.push(((n[1] & 15) << 4) | (n[2] >> 2));
      }
      if (i + 3 < clean.length || clean.charAt(i + 3) !== '=') {
        bytes.push(((n[2] & 3) << 6) | n[3]);
      }
    }
    // UTF-8 解码
    let out = '';
    for (let i = 0; i < bytes.length;) {
      const b = bytes[i];
      if (b < 0x80) {
        out += String.fromCharCode(b);
        i += 1;
      } else if (b < 0xE0) {
        out += String.fromCharCode(((b & 0x1F) << 6) | (bytes[i + 1] & 0x3F));
        i += 2;
      } else {
        out += String.fromCharCode(((b & 0x0F) << 12) | ((bytes[i + 1] & 0x3F) << 6) | (bytes[i + 2] & 0x3F));
        i += 3;
      }
    }
    return out;
  }
}
