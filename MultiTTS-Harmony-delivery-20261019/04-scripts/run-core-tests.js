/**
 * 移植逻辑验证 harness (v4):
 *   借助 node --experimental-strip-types 直接加载 .ets 源文件(剥成 .ts 语义),
 *   对核心算法层做行为级测试: 模板解析 / 表达式求值 / MD5 / WAV / 净化 / 分句。
 * 运行: node --experimental-strip-types run-core-tests.js
 *   (仅测纯逻辑层; 鸿蒙 API 依赖模块如 TtsPlayer/VoiceStore/ForwardServer 由真机验证)
 */
import fs from 'node:fs';
import os from 'node:os';
import path from 'node:path';
import { pathToFileURL } from 'node:url';
import { fileURLToPath } from 'node:url';

const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);

// 工程根目录自动定位(兼容原目录结构 / 交付包 01-源码 / Windows)
function resolveProjectDir() {
  const candidates = [
    process.env.MTTS_SRC,
    path.join(__dirname, '..', 'MultiTTS-Harmony'),
    path.join(__dirname, '..', '01-source'),
    path.join(__dirname, '..', '01-源码'),
    path.join(__dirname, 'MultiTTS-Harmony'),
    path.join(process.cwd(), 'MultiTTS-Harmony'),
    path.join(process.cwd(), '01-source'),
    path.join(process.cwd(), '01-源码'),
  ].filter(Boolean);
  for (const c of candidates) {
    if (fs.existsSync(path.join(c, 'entry', 'src', 'main', 'ets', 'ttscore'))) return c;
  }
  console.error('找不到鸿蒙工程目录, 请设置环境变量 MTTS_SRC 指向 MultiTTS-Harmony 根目录');
  process.exit(2);
}
const PROJECT = resolveProjectDir();
const SRC = path.join(PROJECT, 'entry', 'src', 'main', 'ets', 'ttscore');
const TMP = process.env.MTTS_TMP || path.join(os.tmpdir(), 'mth-core-tests');

fs.mkdirSync(TMP, { recursive: true });
fs.writeFileSync(path.join(TMP, 'package.json'), JSON.stringify({ type: 'module' }));

// 纯逻辑模块(无鸿蒙 API 依赖)列表
const PURE = ['Md5.ets', 'RuleAnalyzer.ets', 'WavParser.ets', 'SafeEvaluator.ets',
  'HttpTtsClient.ets', 'TextSanitizer.ets', 'SentenceSplitter.ets', 'TtsModels.ets'];

for (const f of PURE) {
  let src = fs.readFileSync(path.join(SRC, f), 'utf8');
  src = src.replace(/^import\s+(\{[^}]*\})\s+from\s+'\.\/([A-Za-z0-9_$]+)';\s*$/gm,
    (m, names, mod) => `import ${names} from './${mod}.ts';`);
  src = src.replace(/from '\.\/([A-Za-z0-9_$]+)'/g, "from './$1.ts'");
  src = src.replace(/^import\s+(?!.*'\.\/).*$/gm, ''); // @ohos/* 等外部导入丢弃
  fs.writeFileSync(path.join(TMP, f.replace(/\.ets$/, '.ts')), src);
}

const { RuleAnalyzer } = await import(pathToFileURL(path.join(TMP, 'RuleAnalyzer.ts')));
const { WavParser } = await import(pathToFileURL(path.join(TMP, 'WavParser.ts')));
const { HttpTtsClient } = await import(pathToFileURL(path.join(TMP, 'HttpTtsClient.ts')));
const { SafeEvaluator, EvalError } = await import(pathToFileURL(path.join(TMP, 'SafeEvaluator.ts')));
const { Md5 } = await import(pathToFileURL(path.join(TMP, 'Md5.ts')));
const { TextSanitizer } = await import(pathToFileURL(path.join(TMP, 'TextSanitizer.ts')));
const { SentenceSplitter } = await import(pathToFileURL(path.join(TMP, 'SentenceSplitter.ts')));

let pass = 0, fail = 0;
function eq(name, actual, expected) {
  const a = JSON.stringify(actual), e = JSON.stringify(expected);
  if (a === e) { pass++; console.log('PASS ' + name); }
  else { fail++; console.log('FAIL ' + name + '\n  期望: ' + e + '\n  实际: ' + a); }
}

// ===== 1. RuleAnalyzer =====
{
  const hit = RuleAnalyzer.findInnerRule('https://a.com/t?text={{text}}&v=1', 0);
  eq('findInnerRule 基础命中', [hit.isValid, hit.inner], [true, 'text']);
}
{
  const hit = RuleAnalyzer.findInnerRule('前缀{{speed}}尾', 0);
  eq('findInnerRule 内容与起点', [hit.inner, hit.start], ['speed', 2]);
}
{
  const hit = RuleAnalyzer.findInnerRule('无模板', 0);
  eq('findInnerRule 未命中', hit.isValid, false);
}
{
  const hit = RuleAnalyzer.findInnerRule('{"a":1}', 0);
  eq('单个 { 不算模板起点', hit.isValid, false);
}
{
  const hit = RuleAnalyzer.findInnerRule('{{a{{b}}}}', 0);
  eq('findInnerRule 原版收口语义', hit.inner, 'a{{b');
}
eq('splitUrlOption 纯 URL', RuleAnalyzer.splitUrlOption('https://a.com/t?x={{text}}'), ['https://a.com/t?x={{text}}', '']);
eq('splitUrlOption 带选项', RuleAnalyzer.splitUrlOption('https://a.com/t, {"method":"POST","body":"t={{text}}"}'),
  ['https://a.com/t', '{"method":"POST","body":"t={{text}}"}']);
eq('splitUrlOption 选项含逗号(串内)', RuleAnalyzer.splitUrlOption('https://a.com/t, {"body":"a,b {{text}}"}'),
  ['https://a.com/t', '{"body":"a,b {{text}}"}']);

// ===== 2. MD5 测试向量 =====
eq('MD5 空串', Md5.hex(''), 'd41d8cd98f00b204e9800998ecf8427e');
eq('MD5 abc', Md5.hex('abc'), '900150983cd24fb0d6963f7d28e17f72');
eq('MD5 fox over lazy dog', Md5.hex('The quick brown fox jumps over the lazy dog'),
  '9e107d9d372bb6826bd81d3542a419d6');
eq('MD5 中文 UTF-8', Md5.hex('你好'), '7eca689f0d3389d9dea66ae112e5cfd7');
eq('MD5 16位口径', Md5.hex16('abc'), '3cd24fb0d6963f7d');

// ===== 3. SafeEvaluator =====
{
  const vars = { text: '你好', speed: '1.5', percent: '150', empty: '' };
  eq('eval 拼接', SafeEvaluator.evalExpr("'prefix-' + text + '-suffix'", vars), 'prefix-你好-suffix');
  eq('eval 三元+比较', SafeEvaluator.evalExpr("speed >= '1.5' ? 'fast' : 'slow'", vars), 'fast');
  eq('eval 空串判空', SafeEvaluator.evalExpr("empty == '' ? 'none' : 'has'", vars), 'none');
  eq('eval Java.md5Encode32', SafeEvaluator.evalExpr("Java.md5Encode32('abc')", vars),
    '900150983cd24fb0d6963f7d28e17f72');
  eq('eval Java.urlEncode', SafeEvaluator.evalExpr("Java.urlEncode('你好')", vars), '%E4%BD%A0%E5%A5%BD');
  eq('eval toFixed', SafeEvaluator.evalExpr("(speed * 100).toFixed(0)", vars), '150');
  eq('eval nullish 未定义回落', SafeEvaluator.evalExpr("missing ?? 'fallback'", vars), 'fallback');
  eq('eval Math.max', SafeEvaluator.evalExpr('Math.max(3, 7, 5)', vars), '7');
  eq('eval replace 全局替换(TTS 模板语义)', SafeEvaluator.evalExpr("'a-b-c'.replace('-', '+')", vars), 'a+b+c');
  eq('eval substring', SafeEvaluator.evalExpr("'abcdef'.substring(1, 3)", vars), 'bc');
  eq('eval Java.timeFormat', /^\d{4}-\d{2}-\d{2} \d{2}:\d{2}:\d{2}$/.test(
    SafeEvaluator.evalExpr("Java.timeFormat('yyyy-MM-dd HH:mm:ss')", vars)), true);
  let threw = false;
  try { SafeEvaluator.evalExpr('constructor.constructor("return 1")()', vars); } catch (e) { threw = e instanceof EvalError; }
  eq('eval 拒绝原型逃逸', threw, true);
  threw = false;
  try { SafeEvaluator.evalExpr('globalThis', vars); } catch (e) { threw = true; }
  eq('eval 拒绝全局对象', threw, true);
}

// ===== 4. HttpTtsClient =====
{
  const req = HttpTtsClient.parseTemplate('https://a.com/t?text={{text}}&rate={{rate}}', { text: '%E4%BD%A0%E5%A5%BD', rate: '50' });
  eq('parseTemplate GET 展开', req.url, 'https://a.com/t?text=%E4%BD%A0%E5%A5%BD&rate=50');
  eq('parseTemplate 默认 GET', req.method, 'GET');
}
{
  const req = HttpTtsClient.parseTemplate('https://a.com/t?sign={{Java.md5Encode32(text)}}&p={{(speed*50).toFixed(0)}}',
    { text: 'abc', speed: '1' });
  eq('parseTemplate 表达式签名', req.url, 'https://a.com/t?sign=900150983cd24fb0d6963f7d28e17f72&p=50');
}
{
  const req = HttpTtsClient.parseTemplate('https://a.com/t, {"method":"post","body":"txt={{text}}","timeout":9000,"headers":{"X-K":"V1"}}',
    { text: 'hi' });
  eq('parseTemplate POST+timeout+jsonHeader', [req.method, req.body, req.timeoutMs, req.headers['X-K']],
    ['POST', 'txt=hi', 9000, 'V1']);
  eq('POST 自动补 Content-Type', req.headers['Content-Type'], 'application/x-www-form-urlencoded');
}
{
  const req = HttpTtsClient.parseTemplate('https://a.com/t?x={{unknown}}', {});
  eq('求值失败原样保留', req.url, 'https://a.com/t?x={{unknown}}');
}
{
  const req = HttpTtsClient.parseTemplate('https://a.com/t, {bad json', {});
  eq('坏选项容错', req.method, 'GET');
}
{
  const target = {};
  HttpTtsClient.mergeHeaderInto(target, 'Authorization: Bearer ***\nX-A :  y ');
  eq('mergeHeaderInto 行式解析', target, { 'Authorization': 'Bearer ***', 'X-A': 'y' });
}
{
  const target = {};
  HttpTtsClient.mergeHeaderInto(target, '{"X-J": 123}');
  eq('mergeHeaderInto JSON 解析', target, { 'X-J': '123' });
}

// ===== 5. WavParser =====
function makeWav(sampleRate, channels, bits, frames) {
  const dataBytes = frames * channels * (bits / 8);
  const buf = Buffer.alloc(44 + dataBytes);
  buf.write('RIFF', 0); buf.writeUInt32LE(36 + dataBytes, 4); buf.write('WAVE', 8);
  buf.write('fmt ', 12); buf.writeUInt32LE(16, 16); buf.writeUInt16LE(1, 20);
  buf.writeUInt16LE(channels, 22); buf.writeUInt32LE(sampleRate, 24);
  buf.writeUInt32LE(sampleRate * channels * bits / 8, 28); buf.writeUInt16LE(channels * bits / 8, 32);
  buf.writeUInt16LE(bits, 34); buf.write('data', 36); buf.writeUInt32LE(dataBytes, 40);
  return buf;
}
{
  const wav = makeWav(24000, 1, 16, 24000);
  const ab = wav.buffer.slice(wav.byteOffset, wav.byteOffset + wav.byteLength);
  eq('isWav 判定', WavParser.isWav(ab), true);
  const p = WavParser.parse(ab);
  eq('WAV 解析参数', [p.sampleRate, p.channels, p.bitsPerSample], [24000, 1, 16]);
  eq('WAV 时长≈1000ms', Math.abs(p.durationMs - 1000) <= 1, true);
}
{
  const wav = makeWav(44100, 2, 16, 44100);
  const ab = wav.buffer.slice(wav.byteOffset, wav.byteOffset + wav.byteLength);
  const p = WavParser.parse(ab);
  eq('WAV 立体声', [p.sampleRate, p.channels, Math.abs(p.durationMs - 1000) <= 1], [44100, 2, true]);
}
{
  const ab = new ArrayBuffer(8);
  eq('isWav 拒绝非 WAV', WavParser.isWav(ab), false);
  let threw = false;
  try { WavParser.parse(ab); } catch (e) { threw = true; }
  eq('parse 非 WAV 抛错', threw, true);
}

// ===== 6. TextSanitizer =====
{
  eq('净化: 数字读法 123', TextSanitizer.sanitize('我有123个', true), '我有一百二十三个');
  eq('净化: 年份读法', TextSanitizer.sanitize('1998年', true), '一九九八年');
  eq('净化: 小数', TextSanitizer.sanitize('圆周率3.14很神奇', true), '圆周率三点一四很神奇');
  eq('净化: 符号 &', TextSanitizer.sanitize('A&B', true), 'A 和 B');
  eq('净化: 百分号整体读法', TextSanitizer.sanitize('增长了50%', true), '增长了百分之五十');
  eq('净化: URL 折叠', TextSanitizer.sanitize('详见 https://example.com/x?q=1 说明', true), '详见 链接 说明');
  eq('净化: 装饰行移除', TextSanitizer.sanitize('正文一\n——————\n正文二', true), '正文一\n正文二');
  eq('净化: 关闭数字读法', TextSanitizer.sanitize('我有123个', false), '我有123个');
  eq('intToCn 十万', TextSanitizer.intToCn(100000), '十万');
  eq('intToCn 百万', TextSanitizer.intToCn(1000000), '一百万');
  eq('intToCn 组间补零', TextSanitizer.intToCn(10001), '一万零一');
  eq('intToCn 零', TextSanitizer.intToCn(0), '零');
  eq('intToCn 组内零', TextSanitizer.intToCn(1001), '一千零一');
}

// ===== 7. SentenceSplitter =====
{
  const parts = SentenceSplitter.split('今天天气好。我们去公园！好吗？', 80);
  eq('分句: 强标点', parts.map((p) => p.text), ['今天天气好。', '我们去公园！', '好吗？']);
}
{
  const parts = SentenceSplitter.split('无标点连续文本在超过上限时会被弱标点或硬切分', 10);
  eq('分句: 超长硬切', parts.length >= 2, true);
  // 偏移连贯性
  let ok = true;
  let prev = 0;
  for (const p of parts) {
    if (p.start < prev) { ok = false; }
    if (p.end <= p.start) { ok = false; }
    prev = p.end;
  }
  eq('分句: 偏移单调递增', ok, true);
}
{
  const parts = SentenceSplitter.split('', 80);
  eq('分句: 空文本', parts.length, 0);
}
{
  // 弱标点不切英文单词+单位
  const parts = SentenceSplitter.split('重量是3kg,5kg,8kg组合,重量是3kg,5kg,8kg组合,重量是3kg,5kg,8kg组合', 12);
  let noWordCut = true;
  for (const p of parts) {
    // 不应有句子以数字+字母切断开头(简化校验: 切点不在 kg 内部)
    if (/^[a-z]/.test(p.text) && /[0-9]$/.test(parts[parts.indexOf(p) - 1]?.text ?? '')) { noWordCut = false; }
  }
  eq('分句: 避让数字单位', noWordCut, true);
}

console.log('\n===== 结果: ' + pass + ' pass, ' + fail + ' fail =====');
process.exit(fail ? 1 : 0);
