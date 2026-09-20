package org.nobody.multitts;

import K3.e;
import N.h;
import Y1.d;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.text.TextUtils;
import androidx.room.F;
import com.bytedance.ttnet.config.f;
import com.bytedance.ttnet.config.i;
import com.google.common.base.Ascii;
import jq.util.Objects;
import java.io.File;
import java.util.Set;
import okio.Utf8;
import b2.V;

import h.p;
import h.q;
import h.s;
import o0.c;
import y6.a;
import y6.b;
import org.nobody.multitts.tts.engine.Engine;
import org.nobody.multitts.tts.engine.EngineConfig;

/* JADX INFO: loaded from: D:\mtts_work\dex\classes3.dex */
public class AppContext extends Application {
    private static final String TAG = "AppContext";

    private static Application a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public static final /* synthetic */ int b = 0;

    public static Application getApplication() {
        return a;
    }

    public static boolean getBoolean(String str, boolean z5) {
        return y6.a.a.getBoolean(str, z5);
    }

    public static Context getContext() {
        if (a == null) {
            try {
                a = (Application) Class.forName("android.app.ActivityThread").getDeclaredMethod("currentApplication", null).invoke(null, null);
            } catch (Exception e8) {
                throw new RuntimeException(e8);
            }
        }
        return a.getApplicationContext();
    }

    /* deobf(自证) 反推自混淆名: d —— 方法体即 y6.a.a.getInt(str, i) */
    public static int getInt(int i, String str) {
        return y6.a.a.getInt(str, i);
    }

    public static String getString(String str, String str2) {
        return y6.a.a.getString(str, str2);
    }

    public static void putBoolean(String str, boolean z5) {
        SharedPreferences.Editor editorEdit = y6.a.a.edit();
        editorEdit.putBoolean(str, z5);
        editorEdit.apply();
    }

    /* deobf(自证) 反推自混淆名: g —— 方法体即 editor.putInt(str, i) */
    public static void putInt(int i, String str) {
        SharedPreferences.Editor editorEdit = y6.a.a.edit();
        editorEdit.putInt(str, i);
        editorEdit.apply();
    }

    public static void putString(String str, String str2) {
        SharedPreferences.Editor editorEdit = y6.a.a.edit();
        editorEdit.putString(str, str2);
        editorEdit.apply();
    }

    public static void putStringSet(String str, Set set) {
        SharedPreferences.Editor editorEdit = y6.a.a.edit();
        editorEdit.remove(str);
        editorEdit.apply();
        editorEdit.putStringSet(str, set);
        editorEdit.apply();
    }

    public static void showToast(String str) {
        h hVarB = TextUtils.isEmpty(str) ? h.b : h.b(str);
        F f3 = s.a;
        Objects.requireNonNull(hVarB);
        if (Build.VERSION.SDK_INT >= 33) {
            Object objB = s.b();
            if (objB != null) {
                q.b(objB, p.a(hVarB.a.a()));
                return;
            }
            return;
        }
        if (hVarB.equals(s.c)) {
            return;
        }
        synchronized (s.h) {
            s.c = hVarB;
            s.a();
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // android.app.Application
    public final void onCreate() {
        int i = 7;
        int i8 = 0;
        super.onCreate();
        a = this;
        int i9 = 3;
        showToast(y6.a.a.getString("config_language", null));
        y6.b bVar = y6.b.c;
        bVar.a = this;
        bVar.b = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(bVar);
        String absolutePath = getContext().getExternalFilesDir("log").getAbsolutePath();
        File file = new File(absolutePath);
        if (!file.exists()) {
            file.mkdirs();
        }
        P2.a aVar = new P2.a(i8, false);
        aVar.b = absolutePath;
        V v8 = new V(i9, (char) 0);
        v8.b = new C5.b(1);
        aVar.c = v8;
        aVar.d = new M2.a(new e(11));
        aVar.e = new f(12);
        aVar.f = new C2.a();
        if (((S2.a) aVar.c) == null) {
            aVar.c = new e(13);
        }
        if (((M2.a) aVar.d) == null) {
            aVar.d = new M2.a(new i());
        }
        if (((R2.a) aVar.e) == null) {
            aVar.e = new i();
        }
        if (((d) aVar.g) == null) {
            aVar.g = new d(4, false);
        }
        O2.a[] aVarArr = {new P2.d(aVar), h7.h.c};
        B2.a aVar2 = new B2.a();
        aVar2.a = "X-LOG";
        B2.a aVarA = aVar2.a();
        if (B2.c.c) {
            L2.b.a.d();
        }
        B2.c.c = true;
        B2.c.a = aVarA;
        w2.a aVar3 = new w2.a(i, false);
        aVar3.b = aVarArr;
        B2.c.b = aVar3;

        /* === ADB 语音包导入钩子（重建版新增）===
         * 把 getExternalFilesDir("voice")/engines.yaml 里声明的引擎注册进 Room DB。
         * 推语音包到 /sdcard/Android/data/org.nobody.multitts/files/voice/ 后重启即生效。
         * 幂等：DB 已存在的 code 跳过。 */
        try {
            File voiceDir = getExternalFilesDir("voice");
            File ey = voiceDir == null ? null : new File(voiceDir, "engines.yaml");
            android.util.Log.i(TAG, "voice import: hook entered, dir=" + voiceDir + ", yaml=" + (ey != null && ey.exists()));
            if (ey != null && ey.exists()) {
                java.util.List<Engine> list = null;
                try {
                    java.io.FileInputStream fin = new java.io.FileInputStream(ey);
                    try {
                        Object o = new P2.a(2).k(fin, EngineConfig.class);
                        if (o instanceof EngineConfig) {
                            list = ((EngineConfig) o).engines;
                        }
                    } finally {
                        fin.close();
                    }
                } catch (Throwable t1) {
                    android.util.Log.w(TAG, "voice import parse: " + t1);
                }
                if (list != null) {
                    for (Engine en : list) {
                        if (en == null || en.code == null) continue;
                        try {
                            if (K6.d.e(en.code) == null) {
                                K6.d.l(en);
                                android.util.Log.i(TAG, "voice import: registered engine " + en.code);
                            }
                        } catch (Throwable t2) {
                            android.util.Log.w(TAG, "voice import insert " + en.code + ": " + t2);
                        }
                    }
                }
            }
        } catch (Throwable t0) {
            android.util.Log.w(TAG, "voice import: " + t0);
        }
    }
}
