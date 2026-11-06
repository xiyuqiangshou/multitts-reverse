// 括号配平检查器 v3: 用栈记录每个 { 的行列号, 结束时打印未闭合者
const fs = require('fs');
function strip(s) {
  let out = '';
  let i = 0;
  const n = s.length;
  while (i < n) {
    const c = s[i];
    if (c === '/' && s[i + 1] === '/') { while (i < n && s[i] !== '\n') i++; continue; }
    if (c === '/' && s[i + 1] === '*') { i += 2; while (i < n && !(s[i] === '*' && s[i + 1] === '/')) i++; i += 2; continue; }
    if (c === '"' || c === "'" || c === '`') {
      const q = c;
      out += q; i++;
      while (i < n) {
        if (s[i] === '\\') { i += 2; continue; }
        if (s[i] === q) { break; }
        i++;
      }
      out += q; i++; continue;
    }
    out += c; i++;
  }
  return out;
}
const files = process.argv.slice(2);
let bad = 0;
for (const f of files) {
  const s = strip(fs.readFileSync(f, 'utf8'));
  const stack = [];
  let line = 1, col = 1;
  for (const c of s) {
    if (c === '\n') { line++; col = 1; continue; }
    if (c === '{') stack.push({ line, col });
    else if (c === '}') stack.pop();
    col++;
  }
  const ok = stack.length === 0;
  if (!ok) bad++;
  console.log((ok ? 'OK    ' : 'BAD x' + stack.length + '  ') + f);
  for (const u of stack.slice(0, 8)) {
    console.log('   未闭合 { @ line ' + u.line + ' col ' + u.col);
  }
}
process.exit(bad ? 1 : 0);
