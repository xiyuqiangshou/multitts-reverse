package org.nobody.multitts.ui.voicepool;

import A6.i;
import B.p;
import B0.F;
import B0.Z;
import D0.g;
import H6.f;
import W6.e;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.widget.SearchView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.FileProvider;
import androidx.viewpager2.widget.ViewPager2;
import com.bytedance.frameworks.baselib.network.http.cronet.impl.b;
import com.google.common.base.Ascii;
import com.google.common.primitives.SignedBytes;
import g7.a;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import okio.Utf8;
import org.nobody.multitts.AppContext;
import org.nobody.multitts.tts.role.VoiceItem;
import org.nobody.multitts.tts.role.VoicePair;
import org.nobody.multitts.ui.voicepool.VoicePoolActivity;



import o0.c;
import z1.J;

/* JADX INFO: loaded from: D:\mtts_work\dex\classes3.dex */
public class VoicePoolActivity extends h.m {

    /* JADX INFO: renamed from: B, reason: collision with root package name */
    // [2026-09-18] 这个字段名必须保留为 B（不能用 B_synth 之类改名）：
    //   库里有 3 处按名硬引用它 —— com/bytedance/.../cronet/impl/b、Z6/f、g7/g 的
    //   `getstatic Lorg/nobody/multitts/ui/voicepool/VoicePoolActivity;.B:I`（OwnRefScan 实测），
    //   改名 → 运行期 NoSuchFieldError。
    //   但字段名 B 会遮蔽根包 B，令下方 B.i.q(...) 无法编译；本类继承链
    //   VoicePoolActivity → h/m → b.l，而 b.l 的成员（g/f/h/i/j/k/l..r、a()/i() 等）已占满 a–z，
    //   无任何空闲简单名可借，故 3 个调用点改用 `((B.i) null).q(...)` 消歧：
    //   带括号的转换里 B.i 处于「类型上下文」，不受变量遮蔽影响，字节码等价（每处多 3 条
    //   aconst_null/checkcast/pop，属已知且可接受的极小偏差）。
    public static final /* synthetic */ int B = 0;

    /* JADX INFO: renamed from: A, reason: collision with root package name */
    public e A;

    /* JADX INFO: renamed from: y, reason: collision with root package name */
    public i y;

    /* JADX INFO: renamed from: z, reason: collision with root package name */
    public SearchView z;

    @Override // b.l, android.app.Activity
    public final void onBackPressed() {
        SearchView searchView = this.z;
        if (searchView.S) {
            super.onBackPressed();
        } else {
            searchView.setIconified(true);
        }
    }

    @Override // h.m, b.l, F.i, android.app.Activity
    public final void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        i iVarInflate = A6.i.inflate(getLayoutInflater());
        this.y = iVarInflate;
        setContentView(iVarInflate.a);
        s(this.y.c);
        l().T();
        final int i = 0;
        this.y.d.setOnClickListener(new View.OnClickListener() { // from class: g7.g

            /* JADX INFO: renamed from: b, reason: collision with root package name */
            public final /* synthetic */ VoicePoolActivity b;

            {
                this.b = VoicePoolActivity.this;   // OBF: 原为 jadx 误渲染的构造参数 this
            }

            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                g7.c cVar;
                VoicePoolActivity voicePoolActivity = this.b;
                switch (i) {
                    case 0:
                        ViewPager2 viewPager2 = voicePoolActivity.y.i;
                        J adapter = viewPager2.getAdapter();
                        if (adapter != null) {
                            g0.s abstractComponentCallbacksC0297sD = ((g0.u) voicePoolActivity.s.b).d.D("f" + adapter.f(viewPager2.getCurrentItem()));
                            if (abstractComponentCallbacksC0297sD instanceof g7.l) {
                                VoiceItem voiceItem = new VoiceItem("", "");
                                S5.b.u(voicePoolActivity, voiceItem, new F(11, (g7.l) abstractComponentCallbacksC0297sD, voiceItem));
                                break;
                            } else if ((abstractComponentCallbacksC0297sD instanceof g7.f) && (cVar = ((g7.f) abstractComponentCallbacksC0297sD).Y) != null) {
                                ArrayList arrayList = cVar.d;
                                int size = arrayList.size();
                                arrayList.clear();
                                cVar.e.clear();
                                O6.j.d.invalidateAll();
                                VoicePair.save(Collections.emptyList());
                                P6.d dVar = O6.f.d;
                                if (dVar != null) {
                                    P6.b bVar = (P6.b) dVar.f;
                                    P6.a aVar = bVar.a;
                                    aVar.a.clear();
                                    aVar.b = false;
                                    aVar.c = 0;
                                    P6.a aVar2 = bVar.b;
                                    aVar2.a.clear();
                                    aVar2.b = false;
                                    aVar2.c = 0;
                                }
                                StringBuilder sb = new StringBuilder();
                                sb.append(AppContext.getContext().getCacheDir().getAbsolutePath());
                                new File(((B.i) null).q(new byte[]{-8, 40, 33, 73, 112, -51, -30, -39, -76, 41, 54, 65, 103, -20, -7, -124, -92, 35, 50}, new byte[]{-41, 70, SignedBytes.MAX_POWER_OF_TWO, 36, Ascii.NAK, -119, -117, -86}, sb)).delete();
                                Arrays.fill(O6.j.c, true);
                                cVar.a.f(0, size);
                                break;
                            }
                        }
                        break;
                    case 1:
                        SearchView searchView = voicePoolActivity.z;
                        if (!searchView.S) {
                            searchView.setIconified(true);
                        } else {
                            voicePoolActivity.finish();
                        }
                        break;
                    default:
                        int i8 = VoicePoolActivity.B;
                        voicePoolActivity.getClass();
                        try {
                            h7.d.h(voicePoolActivity.getExternalCacheDir());
                            File file = new File(voicePoolActivity.getExternalCacheDir(), "voicePool.zip");
                            h7.e.x(file, "voice_pool.yaml");
                            voicePoolActivity.t(file);
                        } catch (Exception e8) {
                            h7.e.e("VoicePoolActivity", "exportReplacement: ", e8);
                        }
                        break;
                }
            }
        });
        SearchView searchView = this.y.g;
        this.z = searchView;
        e eVar = new e(this, searchView, new b(3));
        this.A = eVar;
        ViewPager2 viewPager2 = this.y.i;
        viewPager2.setAdapter(new a(this, eVar));
        new f(this.y.h, viewPager2, new Z(new String[]{getString(2131890581), getString(2131890568)}, 19)).g();
        ((ArrayList) viewPager2.c.b).add(new V1.b(this, 2));
        ConstraintLayout constraintLayout = this.y.b;
        p pVar = new p();
        pVar.c(constraintLayout);
        this.z.setOnSearchClickListener(new Z6.f(this, 8, pVar, constraintLayout));
        this.z.setOnCloseListener(new g(this, 7, pVar, constraintLayout));
        final int i8 = 1;
        this.y.e.setOnClickListener(new View.OnClickListener() { // from class: g7.g

            /* JADX INFO: renamed from: b, reason: collision with root package name */
            public final /* synthetic */ VoicePoolActivity b;

            {
                this.b = VoicePoolActivity.this;   // OBF: 原为 jadx 误渲染的构造参数 this
            }

            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                g7.c cVar;
                VoicePoolActivity voicePoolActivity = this.b;
                switch (i8) {
                    case 0:
                        ViewPager2 viewPager3 = voicePoolActivity.y.i;
                        J adapter = viewPager3.getAdapter();
                        if (adapter != null) {
                            g0.s abstractComponentCallbacksC0297sD = ((g0.u) voicePoolActivity.s.b).d.D("f" + adapter.f(viewPager3.getCurrentItem()));
                            if (abstractComponentCallbacksC0297sD instanceof g7.l) {
                                VoiceItem voiceItem = new VoiceItem("", "");
                                S5.b.u(voicePoolActivity, voiceItem, new F(11, (g7.l) abstractComponentCallbacksC0297sD, voiceItem));
                                break;
                            } else if ((abstractComponentCallbacksC0297sD instanceof g7.f) && (cVar = ((g7.f) abstractComponentCallbacksC0297sD).Y) != null) {
                                ArrayList arrayList = cVar.d;
                                int size = arrayList.size();
                                arrayList.clear();
                                cVar.e.clear();
                                O6.j.d.invalidateAll();
                                VoicePair.save(Collections.emptyList());
                                P6.d dVar = O6.f.d;
                                if (dVar != null) {
                                    P6.b bVar = (P6.b) dVar.f;
                                    P6.a aVar = bVar.a;
                                    aVar.a.clear();
                                    aVar.b = false;
                                    aVar.c = 0;
                                    P6.a aVar2 = bVar.b;
                                    aVar2.a.clear();
                                    aVar2.b = false;
                                    aVar2.c = 0;
                                }
                                StringBuilder sb = new StringBuilder();
                                sb.append(AppContext.getContext().getCacheDir().getAbsolutePath());
                                new File(((B.i) null).q(new byte[]{-8, 40, 33, 73, 112, -51, -30, -39, -76, 41, 54, 65, 103, -20, -7, -124, -92, 35, 50}, new byte[]{-41, 70, SignedBytes.MAX_POWER_OF_TWO, 36, Ascii.NAK, -119, -117, -86}, sb)).delete();
                                Arrays.fill(O6.j.c, true);
                                cVar.a.f(0, size);
                                break;
                            }
                        }
                        break;
                    case 1:
                        SearchView searchView2 = voicePoolActivity.z;
                        if (!searchView2.S) {
                            searchView2.setIconified(true);
                        } else {
                            voicePoolActivity.finish();
                        }
                        break;
                    default:
                        int i9 = VoicePoolActivity.B;
                        voicePoolActivity.getClass();
                        try {
                            h7.d.h(voicePoolActivity.getExternalCacheDir());
                            File file = new File(voicePoolActivity.getExternalCacheDir(), "voicePool.zip");
                            h7.e.x(file, "voice_pool.yaml");
                            voicePoolActivity.t(file);
                        } catch (Exception e8) {
                            h7.e.e("VoicePoolActivity", "exportReplacement: ", e8);
                        }
                        break;
                }
            }
        });
        final int i9 = 2;
        this.y.f.setOnClickListener(new View.OnClickListener() { // from class: g7.g

            /* JADX INFO: renamed from: b, reason: collision with root package name */
            public final /* synthetic */ VoicePoolActivity b;

            {
                this.b = VoicePoolActivity.this;   // OBF: 原为 jadx 误渲染的构造参数 this
            }

            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                g7.c cVar;
                VoicePoolActivity voicePoolActivity = this.b;
                switch (i9) {
                    case 0:
                        ViewPager2 viewPager3 = voicePoolActivity.y.i;
                        J adapter = viewPager3.getAdapter();
                        if (adapter != null) {
                            g0.s abstractComponentCallbacksC0297sD = ((g0.u) voicePoolActivity.s.b).d.D("f" + adapter.f(viewPager3.getCurrentItem()));
                            if (abstractComponentCallbacksC0297sD instanceof g7.l) {
                                VoiceItem voiceItem = new VoiceItem("", "");
                                S5.b.u(voicePoolActivity, voiceItem, new F(11, (g7.l) abstractComponentCallbacksC0297sD, voiceItem));
                                break;
                            } else if ((abstractComponentCallbacksC0297sD instanceof g7.f) && (cVar = ((g7.f) abstractComponentCallbacksC0297sD).Y) != null) {
                                ArrayList arrayList = cVar.d;
                                int size = arrayList.size();
                                arrayList.clear();
                                cVar.e.clear();
                                O6.j.d.invalidateAll();
                                VoicePair.save(Collections.emptyList());
                                P6.d dVar = O6.f.d;
                                if (dVar != null) {
                                    P6.b bVar = (P6.b) dVar.f;
                                    P6.a aVar = bVar.a;
                                    aVar.a.clear();
                                    aVar.b = false;
                                    aVar.c = 0;
                                    P6.a aVar2 = bVar.b;
                                    aVar2.a.clear();
                                    aVar2.b = false;
                                    aVar2.c = 0;
                                }
                                StringBuilder sb = new StringBuilder();
                                sb.append(AppContext.getContext().getCacheDir().getAbsolutePath());
                                new File(((B.i) null).q(new byte[]{-8, 40, 33, 73, 112, -51, -30, -39, -76, 41, 54, 65, 103, -20, -7, -124, -92, 35, 50}, new byte[]{-41, 70, SignedBytes.MAX_POWER_OF_TWO, 36, Ascii.NAK, -119, -117, -86}, sb)).delete();
                                Arrays.fill(O6.j.c, true);
                                cVar.a.f(0, size);
                                break;
                            }
                        }
                        break;
                    case 1:
                        SearchView searchView2 = voicePoolActivity.z;
                        if (!searchView2.S) {
                            searchView2.setIconified(true);
                        } else {
                            voicePoolActivity.finish();
                        }
                        break;
                    default:
                        int i10 = VoicePoolActivity.B;
                        voicePoolActivity.getClass();
                        try {
                            h7.d.h(voicePoolActivity.getExternalCacheDir());
                            File file = new File(voicePoolActivity.getExternalCacheDir(), "voicePool.zip");
                            h7.e.x(file, "voice_pool.yaml");
                            voicePoolActivity.t(file);
                        } catch (Exception e8) {
                            h7.e.e("VoicePoolActivity", "exportReplacement: ", e8);
                        }
                        break;
                }
            }
        });
    }

    public final void t(File file) {
        Intent intent = new Intent("android.intent.action.SEND");
        intent.putExtra("android.intent.extra.STREAM", FileProvider.d(this, getPackageName(), file));
        intent.setType("application/zip");
        intent.setFlags(268435456);
        intent.addFlags(1);
        startActivity(Intent.createChooser(intent, getString(2131886446)));
    }
}
