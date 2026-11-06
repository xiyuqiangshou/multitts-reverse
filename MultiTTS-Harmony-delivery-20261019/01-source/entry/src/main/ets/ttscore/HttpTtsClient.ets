/**
 * HTTP TTS 客户端 —— 移植自 plugin/httptts/RequestObject.java(模板/选项/请求三件套)。
 * 原版用 Rhino + Cronet;鸿蒙侧 phase1 用 @ohos.net.http,JS 表达式走可选 resolver 钩子,
 * phase2 接 QuickJS(见 tools/quickjs-wiring.md)后即可覆盖 java.ajax 等扩展语法。
 */
import http from '@ohos.net.http';
import { RuleAnalyzer } from './RuleAnalyzer';
import { SafeEvaluator, EvalError } from './SafeEvaluator';

export class HttpTtsRequest {
  url: string = '';
  method: string = 'GET';
  headers: Record<string, string> = {};
  body: string = '';
  charset: string = 'utf-8';
  timeoutMs: number = 15000;
}

/** {{...}} 求值钩子: phase1 为纯变量替换, phase2 为 QuickJS */
export type RuleResolver = (expression: string) => string;

export class HttpTtsClient {
  /**
   * 解析并展开音色 URL 模板。
   * 支持变量见 EngineHost.buildTemplateVars;未命中 resolver 的 {{...}} 原样保留。
   * 选项 JSON 支持 method/headers/body/charset/timeout(RequestObject.UrlOption 子集)。
   */
  static parseTemplate(tpl: string, vars: Record<string, string>,
    resolver: RuleResolver | null = null): HttpTtsRequest {
    const req = new HttpTtsRequest();
    const parts = RuleAnalyzer.splitUrlOption(tpl);
    req.url = HttpTtsClient.expand(parts[0], vars, resolver);

    const optRaw = parts[1];
    if (optRaw.length > 0) {
      const optExpanded = HttpTtsClient.expand(optRaw, vars, resolver);
      try {
        const opt = JSON.parse(optExpanded) as Record<string, Object>;
        const method = opt['method'];
        if (typeof method === 'string' && method.length > 0) {
          req.method = method.toUpperCase();
        }
        const body = opt['body'];
        if (typeof body === 'string') {
          req.body = body;
        } else if (body !== null && body !== undefined) {
          req.body = JSON.stringify(body);
        }
        const charset = opt['charset'];
        if (typeof charset === 'string' && charset.length > 0) {
          req.charset = charset;
        }
        const timeout = opt['timeout'];
        if (typeof timeout === 'number' && timeout > 0) {
          req.timeoutMs = timeout;
        }
        const hs = opt['headers'];
        if (typeof hs === 'string') {
          HttpTtsClient.mergeHeaderLines(req.headers, hs);
        } else if (hs !== null && hs !== undefined && typeof hs === 'object') {
          const hObj = hs as Record<string, Object>;
          const keys = Object.keys(hObj);
          for (const k of keys) {
            const v = hObj[k];
            if (typeof v === 'string') {
              req.headers[k] = v;
            } else if (v !== null && v !== undefined) {
              req.headers[k] = JSON.stringify(v);
            }
          }
        }
      } catch (e) {
        // 选项非法时保持 GET 语义,与原版容错一致
      }
    }
    if (req.method === 'POST' && req.body.length > 0) {
      const lk = HttpTtsClient.pickHeaderKey(req.headers, 'Content-Type');
      if (lk === null) {
        req.headers['Content-Type'] = 'application/x-www-form-urlencoded';
      }
    }
    return req;
  }

  /** 把引擎 header 字段("k: v" 行 或 JSON 对象)合并进 headers */
  static mergeHeaderInto(target: Record<string, string>, headerField: string): void {
    const h = headerField.trim();
    if (h.length === 0) {
      return;
    }
    if (h.charAt(0) === '{') {
      try {
        const obj = JSON.parse(h) as Record<string, Object>;
        const keys = Object.keys(obj);
        for (const k of keys) {
          const v = obj[k];
          if (typeof v === 'string') {
            target[k] = v;
          } else if (v !== null && v !== undefined) {
            target[k] = JSON.stringify(v);
          }
        }
        return;
      } catch (e) {
        // 落回行解析
      }
    }
    HttpTtsClient.mergeHeaderLines(target, h);
  }

  /** 发起请求,返回原始字节;非 2xx 抛错(信息含状态码) */
  static async fetchAudio(req: HttpTtsRequest): Promise<ArrayBuffer> {
    const client = http.createHttp();
    try {
      const methodMap: Record<string, http.RequestMethod> = {
        'GET': http.RequestMethod.GET,
        'POST': http.RequestMethod.POST,
        'PUT': http.RequestMethod.PUT,
        'DELETE': http.RequestMethod.DELETE
      };
      let m = methodMap[req.method];
      if (m === undefined) {
        m = http.RequestMethod.GET;
      }
      const opt: http.HttpRequestOptions = {
        method: m,
        header: req.headers,
        expectDataType: http.HttpDataType.ARRAY_BUFFER,
        readTimeout: req.timeoutMs,
        connectTimeout: req.timeoutMs
      };
      if (req.method === 'POST' && req.body.length > 0) {
        opt.extraData = req.body;
      }
      const resp = await client.request(req.url, opt);
      if (resp.responseCode < 200 || resp.responseCode >= 300) {
        throw new Error(`HTTP TTS 请求失败: ${resp.responseCode}`);
      }
      const data = resp.result;
      if (!(data instanceof ArrayBuffer)) {
        throw new Error('HTTP TTS 响应不是二进制音频');
      }
      return data;
    } finally {
      client.destroy();
    }
  }

  private static expand(tpl: string, vars: Record<string, string>,
    resolver: RuleResolver | null): string {
    let out = '';
    let pos = 0;
    for (;;) {
      const hit = RuleAnalyzer.findInnerRule(tpl, pos);
      if (!hit.isValid) {
        out += tpl.substring(pos);
        return out;
      }
      out += tpl.substring(pos, hit.start);
      const expr = hit.inner.trim();
      let val: string | null = null;
      if (vars[expr] !== undefined) {
        val = vars[expr];
      } else {
        // 变量表未命中 → 安全表达式求值(Java.xxx()/Math.xxx()/三元/拼接), 失败保留原文
        try {
          val = SafeEvaluator.evalExpr(expr, vars);
        } catch (e) {
          val = null;
        }
      }
      out += val !== null ? val : tpl.substring(hit.start, hit.end);
      pos = hit.end;
    }
  }

  private static mergeHeaderLines(target: Record<string, string>, text: string): void {
    const lines = text.split('\n');
    for (const line of lines) {
      const t = line.trim();
      if (t.length === 0) {
        continue;
      }
      const idx = t.indexOf(':');
      if (idx <= 0) {
        continue;
      }
      target[t.substring(0, idx).trim()] = t.substring(idx + 1).trim();
    }
  }

  private static pickHeaderKey(headers: Record<string, string>, name: string): string | null {
    const keys = Object.keys(headers);
    for (const k of keys) {
      if (k.toLowerCase() === name.toLowerCase()) {
        return k;
      }
    }
    return null;
  }
}
