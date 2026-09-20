package org.nobody.multitts.service;

import B0.Z;
import F6.j;

import H6.f;
import P6.d;
import Q6.b;
import R6.h;
import R6.i;
import R6.p;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.drawable.Icon;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Handler;
import android.os.PowerManager;
import android.speech.tts.SynthesisCallback;
import android.speech.tts.SynthesisRequest;
import android.speech.tts.TextToSpeechService;
import android.speech.tts.Voice;
import android.text.TextUtils;
import android.widget.Toast;
import androidx.room.v;
import com.bytedance.frameworks.baselib.network.http.util.HttpStatus;
import com.google.common.base.Ascii;
import com.google.common.primitives.SignedBytes;
import com.google.common.primitives.UnsignedBytes;
import h7.e;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Pattern;
import okio.Utf8;
import org.nobody.multitts.db.AppDatabase;
import org.nobody.multitts.tts.speaker.Speaker;
import org.nobody.multitts.ui.main.MainActivity;
import o0.c;

import z6.a;

/* JADX INFO: loaded from: D:\mtts_work\dex\classes3.dex */
public class TTSService extends TextToSpeechService {

    /* deobf(oracle) 反推自混淆名: k */
    public static final String ACTION_CLOSE_NOTIFY = "action_close_notify";

    /* deobf(oracle) 反推自混淆名: l */
    public static final String ACTION_STOP_SERVICE = "action_stop_service";

    /* deobf(oracle) 反推自混淆名: m */
    public static final String ACTION_UPDATE_BGM_VOLUME = "action_update_bgm_volume";

    /* deobf(oracle) 反推自混淆名: n */
    public static final String ACTION_UPDATE_MUSIC = "action_update_music";

    /* deobf(oracle) 反推自混淆名: o */
    public static final String TAG = "TTSService";

    /* deobf(V4.2) 反推自混淆名: a —— 类型 H6/f 经自有类同一性判定 = BgmPlayer(S4双射闭合) */
    public f bgmPlayer;

    /* deobf(oracle) 反推自混淆名: b */
    public Notification.BigTextStyle bigTextStyle;

    /* deobf(oracle) 反推自混淆名: c */
    public Handler handler;

    /* deobf(V4.2) 反推自混淆名: d —— 类型 R6/i 经自有类同一性判定 = MixSynthesizer(S3交叉引用锚) */
    public i mixEngine;

    /* deobf(oracle) 反推自混淆名: e */
    public Notification.Builder notificationBuilder;

    /* deobf(oracle) 反推自混淆名: f */
    public volatile boolean synthesising;

    /* deobf(oracle) 反推自混淆名: g */
    public PowerManager.WakeLock wakeLock;

    /* deobf(oracle) 反推自混淆名: h */
    public WifiManager.WifiLock wifiLock;
    /* deobf(V4.2) 反推自混淆名: i —— 描述符唯一匹配 oracle currentLanguage */
    public volatile String[] currentLanguage = {"zho", "CHN", ""};

    /* deobf(V4.2) 反推自混淆名: j —— 类型 z6/a = AppConfig（自实例单例 a0 ↔ instance，S1+S2） */
    public final a appConfig = z6.a.a0;

    public final Notification createNotification() {
        this.bigTextStyle = new Notification.BigTextStyle();
        Intent intent = new Intent(this, (Class<?>) TTSService.class);
        intent.setAction(ACTION_STOP_SERVICE);
        PendingIntent service = PendingIntent.getService(this, 0, intent, 201326592);
        int i = Build.VERSION.SDK_INT;
        Notification.Action actionBuild = i >= 23 ? A3.a.b(Icon.createWithResource(this, 2131230920), getString(2131886459), service).build() : new Notification.Action.Builder(2131689472, getString(2131886459), service).build();
        Intent intent2 = new Intent(this, (Class<?>) TTSService.class);
        intent2.setAction(ACTION_CLOSE_NOTIFY);
        PendingIntent service2 = PendingIntent.getService(this, 1, intent2, 201326592);
        Notification.Builder contentText = new Notification.Builder(this).setSmallIcon(2131230920).setOnlyAlertOnce(true).setVibrate(null).setSound(null).setLights(0, 0, 0).setContentIntent(PendingIntent.getActivity(this, 0, new Intent(this, (Class<?>) MainActivity.class), i >= 31 ? 67108864 : 0)).addAction(actionBuild).addAction(i >= 23 ? A3.a.b(Icon.createWithResource(this, 2131230920), getString(2131886361), service2).build() : new Notification.Action.Builder(2131689472, getString(2131886361), service2).build()).setContentTitle(getString(2131886437)).setContentText(getString(2131886461));
        this.notificationBuilder = contentText;
        if (i >= 26) {
            contentText.setChannelId("MultiTTS_Service");
        }
        return this.notificationBuilder.build();
    }

    /* deobf(自证) 反推自混淆名: b —— 方法体仅做 newWakeLock(SCREEN_DIM_WAKE_LOCK)+acquire(1200000)；
       oracle 无对应 ()V 辅助方法（其逻辑当时内联在 onCreate），故按语义命名，不计入 oracle 还原数 */
    public final void acquireWakeLock() {
        PowerManager.WakeLock wakeLockNewWakeLock = ((PowerManager) getSystemService("power")).newWakeLock(536870913, TTSService.class.getName());
        this.wakeLock = wakeLockNewWakeLock;
        wakeLockNewWakeLock.acquire(1200000L);
    }

    /* deobf(V4.2) 反推自混淆名: c —— oracle 唯一的私有 ()V 辅助方法即 startForegroundService；
       方法体 = createWifiLock + NotificationChannel + startForeground，语义吻合 */
    public final void startForegroundService() {
        WifiManager.WifiLock wifiLockCreateWifiLock = ((WifiManager) getApplicationContext().getSystemService("wifi")).createWifiLock(3, TTSService.class.getName());
        this.wifiLock = wifiLockCreateWifiLock;
        wifiLockCreateWifiLock.acquire();
        int i = Build.VERSION.SDK_INT;
        if (i >= 26) {
            G0.h.k();
            NotificationChannel notificationChannelV = G0.h.v("MultiTTS_Service", getString(2131890549));
            notificationChannelV.setLockscreenVisibility(0);
            ((NotificationManager) getSystemService("notification")).createNotificationChannel(notificationChannelV);
        }
        if (i < 33) {
            startForeground(10, createNotification());
        } else {
            startForeground(10, createNotification(), 2);
        }
    }

    /* deobf(V4.2) 反推自混淆名: d —— 描述符唯一匹配 oracle updateNotification(String,String)V */
    public final void updateNotification(String str, String str2) {
        if (this.appConfig.o) {
            if (this.notificationBuilder == null) {
                startForegroundService();
            }
            this.notificationBuilder.setContentTitle(str);
            this.notificationBuilder.setStyle(this.bigTextStyle.bigText(str2));
            if (Build.VERSION.SDK_INT < 33) {
                startForeground(10, this.notificationBuilder.build());
            } else {
                startForeground(10, this.notificationBuilder.build(), 2);
            }
        }
    }

    @Override // android.speech.tts.TextToSpeechService, android.app.Service
    public final void onCreate() {
        super.onCreate();
        this.handler = new Handler();
        i iVar = new i(this, new Z(this, 2));
        this.mixEngine = iVar;
        iVar.d();
        R6.i.r.put(iVar, Boolean.TRUE);
        iVar.o.j(h7.e.i());
        a aVar = this.appConfig;
        if (aVar.w) {
            acquireWakeLock();
        }
        if (aVar.o) {
            startForegroundService();
        }
        if (aVar.q) {
            this.bgmPlayer = new f(this, this.handler);
        }
    }

    @Override // android.speech.tts.TextToSpeechService, android.app.Service
    public final void onDestroy() {
        PowerManager.WakeLock wakeLock = this.wakeLock;
        if (wakeLock != null) {
            wakeLock.release();
        }
        WifiManager.WifiLock wifiLock = this.wifiLock;
        if (wifiLock != null && wifiLock.isHeld()) {
            this.wifiLock.release();
        }
        this.mixEngine.a();
        stopForeground(true);
        if (this.bgmPlayer != null) {
            this.handler.post(new G6.c(this, 4));
        }
        this.handler = null;
        super.onDestroy();
    }

    @Override // android.speech.tts.TextToSpeechService
    public final String onGetDefaultVoiceNameFor(String str, String str2, String str3) {
        return "NOT_SET";
    }

    @Override // android.speech.tts.TextToSpeechService
    public final Set onGetFeaturesForLanguage(String str, String str2, String str3) {
        return new HashSet();
    }

    @Override // android.speech.tts.TextToSpeechService
    public final String[] onGetLanguage() {
        return this.currentLanguage;
    }

    /* JADX WARN: Code duplicated, block: B:16:0x00ea  */
    /* JADX WARN: Code duplicated, block: B:21:0x0101  */
    @Override // android.speech.tts.TextToSpeechService
    public final List onGetVoices() {
        Locale locale;
        Locale locale2;
        char c8 = 0;
        v vVarP = r2.q.p(this, "speaker");
        vVarP.h = true;
        AppDatabase appDatabase = (AppDatabase) vVarP.a();
        List<Speaker> listE = ((j) appDatabase.speakerDao()).e();
        appDatabase.close();
        ArrayList arrayList = new ArrayList(listE.size() + 1);
        arrayList.add(new Voice("NOT_SET", h7.e.i(), HttpStatus.SC_MULTIPLE_CHOICES, HttpStatus.SC_MULTIPLE_CHOICES, false, new HashSet(Collections.singletonList(getString(2131886456)))));
        Pattern patternCompile = Pattern.compile("^[-+_a-zA-Z0-9() ]+\\d*$");
        HashMap map = new HashMap();
        for (Speaker speaker : listE) {
            String str = speaker.id;
            String str2 = speaker.locale;
            if (TextUtils.isEmpty(str2)) {
                if (TextUtils.isEmpty(speaker.name) && patternCompile.matcher(speaker.name).matches()) {
                    locale = Locale.US;
                } else {
                    locale = Locale.CHINA;
                }
                locale2 = locale;
            } else if (map.containsKey(str2)) {
                locale = (Locale) map.get(str2);
                locale2 = locale;
            } else {
                byte[] bArr = new byte[1];
                bArr[c8] = 101;
                String[] strArrSplit = str2.split(o0.c.l(bArr, new byte[]{72, -79, -3, -73, 106, -32, 6, -27}));
                try {
                    Locale locale3 = new Locale(strArrSplit[c8], strArrSplit[1]);
                    String str3 = locale3.getISO3Language() + locale3.getISO3Country();
                    map.put(str2, locale3);
                    if (TextUtils.isEmpty(str3)) {
                        if (TextUtils.isEmpty(speaker.name)) {
                            locale = Locale.CHINA;
                        } else {
                            locale = Locale.CHINA;
                        }
                        locale2 = locale;
                    } else {
                        locale2 = locale3;
                    }
                } catch (Exception unused) {
                    // smali 事实：:catch_e9 的方法体是裸 nop，直接落入共用的 :cond_ea 判定块，
                    // 该块以 :goto_a9（`move-object v11, v9`，v11 即 locale2）收尾。
                    // 所以异常路径同样会按 speaker.name 猜一个 locale 并赋给 locale2，
                    // jadx 把这个空 catch 展开丢了赋值 -> javac 报「可能尚未初始化变量 locale2」。
                    if (TextUtils.isEmpty(speaker.name) && patternCompile.matcher(speaker.name).matches()) {
                        locale = Locale.US;
                    } else {
                        locale = Locale.CHINA;
                    }
                    locale2 = locale;
                }
            }
            int i = speaker.sampleRate;
            int i8 = HttpStatus.SC_MULTIPLE_CHOICES;
            int i9 = i > 24000 ? HttpStatus.SC_BAD_REQUEST : HttpStatus.SC_MULTIPLE_CHOICES;
            short s8 = speaker.type;
            if (s8 != 0) {
                i8 = HttpStatus.SC_BAD_REQUEST;
            }
            arrayList.add(new Voice(str, locale2, i9, i8, s8 != 0, speaker.getFeatures(this)));
            c8 = 0;
        }
        return arrayList;
    }

    @Override // android.speech.tts.TextToSpeechService
    public final int onIsLanguageAvailable(String str, String str2, String str3) {
        Locale locale = new Locale(str, str2, str3);
        Iterator it = this.appConfig.m.iterator();
        boolean z5 = false;
        boolean z8 = false;
        while (it.hasNext()) {
            String[] strArrSplit = ((String) it.next()).split("-");
            Locale locale2 = new Locale(strArrSplit[0], strArrSplit[1]);
            if (locale.getISO3Language().equals(locale2.getISO3Language())) {
                z8 = true;
            }
            if (z8 && locale.getISO3Country().equals(locale2.getISO3Country())) {
                z5 = true;
            }
            if (z5 && locale.getVariant().equals(locale2.getVariant())) {
                return 2;
            }
        }
        if (z5) {
            return 1;
        }
        return z8 ? 0 : -2;
    }

    @Override // android.speech.tts.TextToSpeechService
    public final int onIsValidVoiceName(String str) {
        return ("NOT_SET".equals(str) || Q6.b.b(str) != null) ? 0 : -1;
    }

    @Override // android.speech.tts.TextToSpeechService
    public final int onLoadLanguage(String str, String str2, String str3) {
        if (!TextUtils.isEmpty(str3) && str3.equals(this.currentLanguage[2])) {
            return 2;
        }
        if (!TextUtils.isEmpty(str2) && str2.equals(this.currentLanguage[1])) {
            return 1;
        }
        if (!TextUtils.isEmpty(str) && str.equals(this.currentLanguage[0])) {
            return 0;
        }
        if (str == null) {
            str = "";
        }
        if (str2 == null) {
            str2 = "";
        }
        if (str3 == null) {
            str3 = "";
        }
        int iOnIsLanguageAvailable = onIsLanguageAvailable(str, str2, str3);
        if (iOnIsLanguageAvailable == 1 || iOnIsLanguageAvailable == 0 || iOnIsLanguageAvailable == 2) {
            this.currentLanguage = new String[]{str, str2, str3};
        }
        return iOnIsLanguageAvailable;
    }

    @Override // android.speech.tts.TextToSpeechService
    public final int onLoadVoice(String str) {
        return onIsValidVoiceName(str);
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* JADX WARN: Code duplicated, block: B:22:0x0052  */
    @Override // android.app.Service
    public final int onStartCommand(Intent intent, int i, int i8) {
        byte b8;
        int i9 = 3;
        int i10 = 2;
        if (intent == null) {
            return super.onStartCommand(intent, i, i8);
        }
        String action = intent.getAction();
        // [R8 已移除的调用] 原字符串: "onStartCommand: action="
        if (TextUtils.isEmpty(action)) {
            return 0;
        }
        switch (action.hashCode()) {
            case 971490009:
                if (!action.equals(ACTION_CLOSE_NOTIFY)) {
                    b8 = -1;
                } else {
                    b8 = 1;
                }
                break;
            case 1121945313:
                if (!action.equals(ACTION_STOP_SERVICE)) {
                    b8 = -1;
                } else {
                    b8 = 0;
                }
                break;
            case 1709392536:
                if (!action.equals(ACTION_UPDATE_MUSIC)) {
                    b8 = -1;
                } else {
                    b8 = 2;
                }
                break;
            case 2126934494:
                if (!action.equals(ACTION_UPDATE_BGM_VOLUME)) {
                    b8 = -1;
                } else {
                    b8 = 3;
                }
                break;
            default:
                b8 = -1;
                break;
        }
        if (b8 == 0) {
            onStop();
            stopForeground(true);
            stopSelf();
            WifiManager.WifiLock wifiLock = this.wifiLock;
            if (wifiLock != null && wifiLock.isHeld()) {
                this.wifiLock.release();
            }
        } else if (b8 == 1) {
            this.appConfig.g(false);
            stopForeground(true);
            WifiManager.WifiLock wifiLock2 = this.wifiLock;
            if (wifiLock2 != null && wifiLock2.isHeld()) {
                this.wifiLock.release();
            }
        } else if (b8 != 2) {
            if (b8 == 3 && this.bgmPlayer != null) {
                this.handler.post(new G6.c(this, i9));
            }
        } else if (this.bgmPlayer != null) {
            this.handler.post(new G6.c(this, i10));
        }
        return 0;
    }

    @Override // android.speech.tts.TextToSpeechService
    public final void onStop() {
        int i = 1;
        if (this.bgmPlayer != null) {
            this.handler.post(new G6.c(this, i));
        }
        i iVar = this.mixEngine;
        synchronized (iVar.n) {
            iVar.i = true;
            p pVar = iVar.q;
            if (pVar != null) {
                pVar.f = true;
                h hVar = pVar.l;
                if (hVar != null) {
                    hVar.cancel();
                }
            }
            S6.e.b = true;
            S6.e.c = Collections.emptyList();
            O6.f.a = 0;
            Arrays.fill(O6.f.e, (Object) null);
            d dVar = O6.f.d;
            if (dVar != null) {
                dVar.b();
            }
            // [R8 已移除的调用] 原字符串: "call synthesizer stop method."
        }
    }

    @Override // android.speech.tts.TextToSpeechService
    public final void onSynthesizeText(SynthesisRequest synthesisRequest, SynthesisCallback synthesisCallback) {
        O5.v vVarB;
        boolean z5 = true;
        int i = 0;
        String strC = S6.b.c(synthesisRequest.getCharSequenceText().toString());
        if (this.appConfig.U && S6.b.b(strC)) {
            synthesisCallback.start(16000, 2, 1);
            synthesisCallback.done();
            // [R8 已移除的调用] 原字符串: "onSynthesizeText: Empty text, skip."
            return;
        }
        if (onLoadLanguage(synthesisRequest.getLanguage(), synthesisRequest.getCountry(), synthesisRequest.getVariant()) == -2) {
            synthesisCallback.error(-3);
            h7.e.d(TAG, "onSynthesizeText: Language not supported: " + synthesisRequest.getLanguage());
            return;
        }
        if (this.appConfig.w) {
            PowerManager.WakeLock wakeLock = this.wakeLock;
            if (wakeLock == null) {
                acquireWakeLock();
            } else if (!wakeLock.isHeld()) {
                this.wakeLock.acquire(1200000L);
            }
        }
        if (this.appConfig.o) {
            updateNotification(getString(2131886460), strC);
        }
        if (this.appConfig.q) {
            // 原版 BGM 触发点：杀看门狗（e.b=-500）→ 首调走 k() 完整首播链，续调走 m() 保活链
            this.handler.post(new G6.c(this, 5));
        }
        this.synthesising = true;
        // [R8 已移除的调用] 原字符串: "onSynthesizeText: process request: "
        i iVar = this.mixEngine;
        synchronized (iVar.n) {
            iVar.i = false;
        }
        synchronized (iVar) {
            try {
                boolean z8 = !synthesisRequest.getParams().getBoolean("forceUse", false) && iVar.o.p;
                if (!"NOT_SET".equals(synthesisRequest.getVoiceName()) && !z8) {
                    z5 = false;
                }
                h7.a.b("i", "original text", synthesisRequest.getCharSequenceText().toString());
                iVar.g = N6.a.b(S6.b.c(Normalizer.normalize(synthesisRequest.getCharSequenceText(), Normalizer.Form.NFC)));
                // [patch 2026-09-20] 接活主界面"语速"滑条(voice_speed, 50=1x)：原版该设置只写不读，
                // 系统TTS链路语速完全由客户端 speechRate 决定(安卓 100=1x，BDeTTS 经 6.0×speed 抵消后收原始值)。
                // 有效语速 = 客户端语速 × (voice_speed/50)。滑条=50 时行为与原版完全一致。
                int vs = y6.a.a.getInt("voice_speed", 50);
                int effRate = Math.round(synthesisRequest.getSpeechRate() * (vs / 50.0f));
                android.util.Log.e("MTTSPATCH", "voice_speed=" + vs + " reqRate=" + synthesisRequest.getSpeechRate() + " effRate=" + effRate);
                iVar.f(z8, effRate, synthesisRequest.getParams().getFloat("volume", iVar.o.d), synthesisRequest.getPitch());
                vVarB = (iVar.o.n && z5) ? iVar.b(synthesisCallback) : iVar.g(synthesisRequest.getVoiceName(), synthesisCallback);
            } catch (Throwable th) {
                throw th;
            }
        }
        if (vVarB != null) {
            Toast.makeText(this, vVarB.b, 0).show();
        }
        if (this.appConfig.o) {
            if (vVarB != null) {
                updateNotification(getString(2131886385), vVarB.b);
            } else {
                updateNotification(getString(2131886393), getString(2131886418));
            }
        }
        if (this.bgmPlayer != null && this.appConfig.q) {
            this.handler.post(new G6.c(this, i));
        }
        this.synthesising = false;
    }

    @Override // android.app.Service
    public final void onTaskRemoved(Intent intent) {
        super.onTaskRemoved(intent);
        O6.j.k();
        O6.f.f();
    }
}
