/**
 * 主界面 —— phase2 验证台:
 *   Tab1 试听: 模板/音色 → 合成 → 播放
 *   Tab2 音色: 导入 MultiTTS/legado httpTTS JSON
 *   Tab3 服务: 局域网转发开关
 */
import { Engine, Speaker, SynthProgress } from '../ttscore/TtsModels';
import { EngineHost, SynthResult } from '../ttscore/EngineHost';
import { TtsPlayer } from '../ttscore/TtsPlayer';
import { HttpTtsClient } from '../ttscore/HttpTtsClient';
import { VoiceStore, VoiceEntry } from '../ttscore/VoiceStore';
import { ForwardServer } from '../ttscore/ForwardServer';
import { common } from '@kit.AbilityKit';

const DEFAULT_TPL = 'https://api.example.com/tts?text={{text}}';

@Entry
@Component
struct Index {
  @State tpl: string = DEFAULT_TPL;
  @State sampleText: string = '你好,这是 MultiTTS 鸿蒙版。2024年增长50%。';
  @State stateText: string = '待命';
  @State previewUrl: string = '';
  @State busy: boolean = false;
  @State activeTab: number = 0;
  @State voiceList: VoiceEntry[] = [];
  @State serverOn: boolean = false;
  @State importText: string = '';
  private player: TtsPlayer = new TtsPlayer();

  aboutToAppear(): void {
    this.refreshVoices();
  }

  private ctx(): common.Context {
    return getContext(this) as common.Context;
  }

  private refreshVoices(): void {
    VoiceStore.all(this.ctx()).then((list: VoiceEntry[]) => {
      this.voiceList = list;
    }).catch(() => {
      this.voiceList = [];
    });
  }

  private onProgress(p: SynthProgress): void {
    this.stateText = `[${p.state}] ${p.message}`;
  }

  private async currentEntry(): Promise<VoiceEntry> {
    const list = await VoiceStore.all(this.ctx());
    if (list.length > 0) {
      return list[0];
    }
    const e = new VoiceEntry();
    e.engine.type = 'httpTts';
    e.engine.name = '临时模板';
    e.engine.url = this.tpl;
    e.speaker.name = '试听音色';
    return e;
  }

  private async preview(): Promise<void> {
    if (this.busy) {
      return;
    }
    this.busy = true;
    try {
      const entry = await this.currentEntry();
      if (entry.engine.url === DEFAULT_TPL) {
        entry.engine.url = this.tpl;
      }
      const req = HttpTtsClient.parseTemplate(entry.engine.url,
        EngineHost.buildTemplateVars(entry.speaker, this.sampleText));
      this.previewUrl = req.url;
      const parsed = new SynthProgress();
      parsed.state = 'parsing';
      parsed.message = '模板解析完成';
      this.onProgress(parsed);
      const result: SynthResult = await EngineHost.synthesize(entry.engine, entry.speaker,
        this.sampleText, (p) => this.onProgress(p));
      this.stateText = `播放中 ${result.sampleRate}Hz/${result.channels}ch/${result.durationMs}ms`;
      await this.player.playPcm(result.pcm, result.sampleRate, result.channels, result.bitsPerSample);
      this.stateText = '播放完成';
    } catch (e) {
      const msg = e instanceof Error ? e.message : String(e);
      this.stateText = `失败: ${msg}`;
    } finally {
      this.busy = false;
    }
  }

  private async stop(): Promise<void> {
    await this.player.stop();
    this.stateText = '已停止';
  }

  private async doImport(): Promise<void> {
    try {
      const n = await VoiceStore.import(this.ctx(), this.importText);
      this.stateText = `导入 ${n} 个音色`;
      this.importText = '';
      this.refreshVoices();
    } catch (e) {
      const msg = e instanceof Error ? e.message : String(e);
      this.stateText = `导入失败: ${msg}`;
    }
  }

  private async toggleServer(): Promise<void> {
    try {
      if (this.serverOn) {
        await ForwardServer.stop();
        this.serverOn = false;
        this.stateText = '转发服务已停止';
      } else {
        await ForwardServer.start(getContext(this) as common.Context, 18080);
        this.serverOn = true;
        this.stateText = `转发服务已启动: :${ForwardServer.getPort()}`;
      }
    } catch (e) {
      const msg = e instanceof Error ? e.message : String(e);
      this.stateText = `服务切换失败: ${msg}`;
    }
  }

  build() {
    Column() {
      Text('MultiTTS · 鸿蒙')
        .fontSize(22)
        .fontWeight(FontWeight.Bold)
        .alignSelf(ItemAlign.Start)
        .margin({ top: 12 })

      Row({ space: 10 }) {
        ForEach(['试听', '音色', '服务'], (name: string, idx: number) => {
          Text(name)
            .fontSize(15)
            .fontWeight(this.activeTab === idx ? FontWeight.Bold : FontWeight.Normal)
            .opacity(this.activeTab === idx ? 1 : 0.5)
            .onClick(() => {
              this.activeTab = idx;
            })
        }, (name: string) => name)
      }
      .margin({ top: 8, bottom: 8 })
      .alignSelf(ItemAlign.Start)

      if (this.activeTab === 0) {
        this.buildPreviewTab()
      } else if (this.activeTab === 1) {
        this.buildVoicesTab()
      } else {
        this.buildServerTab()
      }

      Text(this.stateText)
        .fontSize(12)
        .opacity(0.6)
        .alignSelf(ItemAlign.Start)
        .margin({ top: 10 })
    }
    .padding(16)
    .width('100%')
    .height('100%')
    .alignItems(HorizontalAlign.Start)
  }

  @Builder
  buildPreviewTab() {
    Column({ space: 10 }) {
      Text('音色 URL 模板').fontSize(13)
      TextArea({ text: this.tpl, placeholder: DEFAULT_TPL })
        .height(64)
        .onChange((v: string) => {
          this.tpl = v;
        })
      Text('试听文本').fontSize(13)
      TextArea({ text: this.sampleText })
        .height(64)
        .onChange((v: string) => {
          this.sampleText = v;
        })
      Row({ space: 12 }) {
        Button(this.busy ? '合成中…' : '▶ 试听')
          .enabled(!this.busy)
          .onClick(() => {
            this.preview();
          })
        Button('■ 停止')
          .onClick(() => {
            this.stop();
          })
      }
      Text(`解析: ${this.previewUrl}`)
        .fontSize(11)
        .opacity(0.55)
    }
    .alignItems(HorizontalAlign.Start)
    .width('100%')
  }

  @Builder
  buildVoicesTab() {
    Column({ space: 10 }) {
      Text(`已配置音色: ${this.voiceList.length}`).fontSize(13)
      List({ space: 4 }) {
        ForEach(this.voiceList, (v: VoiceEntry) => {
          ListItem() {
            Row() {
              Text(v.speaker.name).fontSize(14).layoutWeight(1)
              Text(v.source).fontSize(11).opacity(0.5)
              Text('删')
                .fontSize(12)
                .fontColor(Color.Red)
                .onClick(() => {
                  VoiceStore.remove(this.ctx(), v.speaker.code).then(() => this.refreshVoices());
                })
            }.width('100%')
          }
        }, (v: VoiceEntry) => v.speaker.code)
      }
      .height(140)

      Text('粘贴音色 JSON (MultiTTS / legado httpTTS):').fontSize(13)
      TextArea({ text: this.importText, placeholder: '[{"name":"…","url":"…"}]' })
        .height(90)
        .onChange((v: string) => {
          this.importText = v;
        })
      Button('导入')
        .onClick(() => {
          this.doImport();
        })
    }
    .alignItems(HorizontalAlign.Start)
    .width('100%')
  }

  @Builder
  buildServerTab() {
    Column({ space: 10 }) {
      Text('局域网转发服务').fontSize(14).fontWeight(FontWeight.Medium)
      Text('启动后, 局域网内的 legado(安卓)/浏览器可调用:\nGET /voices → 音色列表\nGET /tts?text=…&voice=… → WAV 音频')
        .fontSize(12)
        .opacity(0.65)
      Toggle({ type: ToggleType.Switch, isOn: this.serverOn })
        .onChange((on: boolean) => {
          this.toggleServer();
        })
    }
    .alignItems(HorizontalAlign.Start)
    .width('100%')
  }
}
