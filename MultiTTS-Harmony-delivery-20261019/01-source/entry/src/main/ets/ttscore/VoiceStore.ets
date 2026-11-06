/**
 * 音色仓库 —— 导入/存储/查询发音人。
 * 兼容两种格式:
 *   1. MultiTTS 引擎导出 JSON (Engine/Speaker 字段子集)
 *   2. legado httpTTS JSON (legado://import/httpTTS 生态, url/header/body 模板)
 * 持久化: @ohos.data.preferences (JSON 串存 preferences, phase1 无 RDB 依赖)
 */
import { Engine, Speaker } from './TtsModels';
import common from '@ohos.app.ability.common';
import preferences from '@ohos.data.preferences';

const STORE_NAME = 'multitts_voices';
const KEY_VOICES = 'voices_json';

export class VoiceEntry {
  engine: Engine = new Engine();
  speaker: Speaker = new Speaker();
  source: string = 'multitts'; // multitts | legado
}

export class VoiceStore {
  private static cache: VoiceEntry[] | null = null;

  static async all(context: common.Context): Promise<VoiceEntry[]> {
    if (VoiceStore.cache !== null) {
      return VoiceStore.cache;
    }
const store: preferences.Preferences = await preferences.getPreferences(context, STORE_NAME);
    const raw: preferences.ValueType = store.getSync(KEY_VOICES, '[]');
    try {
      VoiceStore.cache = JSON.parse(raw as string) as VoiceEntry[];
    } catch (e) {
      VoiceStore.cache = [];
    }
    return VoiceStore.cache as VoiceEntry[];
  }

  static async import(context: Context, jsonText: string): Promise<number> {
    const list = await VoiceStore.all(context);
    const imported = VoiceStore.parseAny(jsonText);
    let count = 0;
    for (const entry of imported) {
      // 按 code 去重, 覆盖更新
      const idx = list.findIndex((v) => v.speaker.code === entry.speaker.code
        && v.engine.type === entry.engine.type);
      if (idx >= 0) {
        list[idx] = entry;
      } else {
        list.push(entry);
      }
      count++;
    }
    await VoiceStore.persist(context, list);
    return count;
  }

  static async remove(context: Context, code: string): Promise<void> {
    const list = await VoiceStore.all(context);
    const next = list.filter((v) => v.speaker.code !== code);
    await VoiceStore.persist(context, next);
  }

  private static async persist(context: common.Context, list: VoiceEntry[]): Promise<void> {
    VoiceStore.cache = list;
const store: preferences.Preferences = await preferences.getPreferences(context, STORE_NAME);
    await store.put(KEY_VOICES, JSON.stringify(list));
    await store.flush();
  }

  /**
   * 自适应解析: 对象数组 / 单对象 / legado httpTTS。
   * legado httpTTS 字段: { id/name, url(模板), contentType, requestBody, headers(对象) }
   */
  static parseAny(jsonText: string): VoiceEntry[] {
    const out: VoiceEntry[] = [];
    let data: Object | null = null;
    try {
      data = JSON.parse(jsonText);
    } catch (e) {
      throw new Error('音色 JSON 解析失败: 不是合法 JSON');
    }
    if (data === null || data === undefined) {
      return out;
    }
    const arr: Object[] = Array.isArray(data) ? data as Object[] : [data];
    for (const item of arr) {
      const entry = VoiceStore.parseOne(item as Record<string, Object>);
      if (entry !== null) {
        out.push(entry);
      }
    }
    return out;
  }

  private static parseOne(o: Record<string, Object>): VoiceEntry | null {
    const url = o['url'];
    if (typeof url !== 'string' || url.length === 0) {
      return null; // 无 url 的条目(如系统包描述)跳过
    }
    const entry = new VoiceEntry();
    const isLegado = o['voiceId'] !== undefined || o['contentType'] !== undefined
      || o['requestBody'] !== undefined;
    entry.engine.type = 'httpTts';
    entry.engine.url = url;
    const name = o['name'];
    entry.speaker.name = typeof name === 'string' ? name : '未命名';
    const sid = o['voiceId'] !== undefined ? o['voiceId'] : o['id'];
    entry.speaker.code = String(sid !== undefined && sid !== null ? sid : entry.speaker.name);
    entry.speaker.sampleRate = 0; // 由响应自描述
    if (isLegado) {
      entry.source = 'legado';
      const ct = o['contentType'];
      const body = o['requestBody'];
      // legado POST 音色: url 不含 body 时把 requestBody 拼进选项 JSON
      if (typeof body === 'string' && body.length > 0) {
        const opt: Record<string, Object> = { 'method': 'POST', 'body': body };
        if (typeof ct === 'string' && ct.length > 0) {
          const hs: Record<string, string> = { 'Content-Type': ct };
          opt['headers'] = hs;
        }
        entry.engine.url = url + ', ' + JSON.stringify(opt);
      } else if (typeof ct === 'string' && ct.length > 0 && url.indexOf('{{') < 0) {
        // GET + 指定 content-type 少见, 只透传 header
        const hs: Record<string, string> = { 'Content-Type': ct };
        entry.engine.header = JSON.stringify(hs);
      }
      const hs = o['headers'];
      if (hs !== null && hs !== undefined && typeof hs === 'object' && !Array.isArray(hs)) {
        entry.engine.header = JSON.stringify(hs);
      }
    } else {
      entry.source = 'multitts';
      const sr = o['sampleRate'];
      if (typeof sr === 'number' && sr > 0) {
        entry.speaker.sampleRate = sr;
      }
      const spd = o['speed'];
      if (typeof spd === 'number' && spd > 0) {
        entry.speaker.speed = spd;
      }
    }
    return entry;
  }
}
