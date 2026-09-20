package org.nobody.multitts.ui.main;

import A6.d;
import A6.j;
import B.i;
import B0.J;
import B0.e0;
import F.g;
import F3.f;
import W6.b;
import W6.e;
import Z6.k;
import Z6.m;
import Z6.n;
import Z6.o;
import android.app.SearchManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.accessibility.AccessibilityManager;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.A;
import androidx.lifecycle.N;
import androidx.lifecycle.U;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.navigation.NavigationView;
import com.google.common.base.Ascii;
import com.google.common.primitives.SignedBytes;
import com.google.common.primitives.UnsignedBytes;
import h7.r;
import java.io.File;
import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import kotlin.jvm.internal.p;
import okio.Utf8;
import org.nobody.multitts.AppContext;
import org.nobody.multitts.db.AppDatabase;
import org.nobody.multitts.db.AppDatabase_Impl;
import org.nobody.multitts.service.ForwardService;
import org.nobody.multitts.ui.main.MainActivity;
import b2.V;


import o0.c;


import y6.a;







/* JADX INFO: loaded from: D:\mtts_work\dex\classes3.dex */
public class MainActivity extends h.m implements f {

    /* JADX INFO: renamed from: G, reason: collision with root package name */
    public static final /* synthetic */ int G = 0;

    /* JADX INFO: renamed from: A, reason: collision with root package name */
    public V A;

    /* JADX INFO: renamed from: B, reason: collision with root package name */
    public e B_;

    /* JADX INFO: renamed from: C, reason: collision with root package name */
    public DrawerLayout C;

    /* JADX INFO: renamed from: D, reason: collision with root package name */
    public H6.f D;

    /* JADX INFO: renamed from: E, reason: collision with root package name */
    public ViewPager2 E;

    /* JADX INFO: renamed from: F, reason: collision with root package name */
    // [2026-09-18] 字段名必须是 dex 真名 F：库里有硬引用 `b2/V#d()V -> MainActivity.F:LZ6/o;`
    // （RefAudit 全量审计实测；OwnRefScan 亦见 deps.jar!b2/V.class），改名 → 运行期 NoSuchFieldError。
    // 但字段名 F 会遮蔽根包 F，令下方 F.g.a/F.g.i 无法编译；本类继承链 MainActivity → h/m → b.l，
    // 而 b.l 成员（g/f/h/i/j/k/l..r、a()/b()/c()/e()/g()/i()/j() 等）已占满 a–z，无空闲简单名可借，
    // 故这两个调用点改用 `((F.g) null).a(...)` 消歧：带括号的转换里 F.g 处于「类型上下文」，
    // 不受变量遮蔽影响，字节码等价（每处多 aconst_null/checkcast/pop 三条，属已知极小偏差）。
    public o F;

    /* deobf(oracle) 反推自混淆名: y */
    public AccessibilityManager accessibilityManager;

    /* JADX INFO: renamed from: z, reason: collision with root package name */
    public d z;

    @Override // h.m, b.l, android.app.Activity
    public final void onActivityResult(int i, int i8, Intent intent) {
        super.onActivityResult(i, i8, intent);
        if (intent == null) {
            return;
        }
        if (i == 1) {
            h7.e.k().execute(new r(this, intent, new V(this, 15)));
        } else if (i == 2) {
            h7.e.k().execute(new J(9, this, intent));
        }
    }

    @Override // b.l, android.app.Activity
    public final void onBackPressed() {
        View viewF = this.C.f(8388611);
        if (viewF != null ? DrawerLayout.o(viewF) : false) {
            this.C.d();
        } else {
            super.onBackPressed();
        }
    }

    @Override // h.m, b.l, F.i, android.app.Activity
    public final void onCreate(Bundle bundle) {
        int i = 0;
        int i8 = 7;
        int i9 = 1;
        int i10 = 8;
        super.onCreate(bundle);
        d dVarInflate = A6.d.inflate(getLayoutInflater());
        this.z = dVarInflate;
        setContentView(dVarInflate.a);
        j jVar = this.z.b;
        s(jVar.b);
        l().T();
        String[] strArr = h7.d.a;
        try {
            if (((F.g) null).a(this, "android.permission.WRITE_EXTERNAL_STORAGE") != 0) {
                ((F.g) null).i(this, h7.d.a, 1);
            }
        } catch (Exception e8) {
            e8.printStackTrace();
        }
        StringBuilder sb = new StringBuilder();
        sb.append(getExternalFilesDir("log"));
        String strQ = B.i.q(new byte[]{-120, -51, 11, 102, 118, -63, -59, -1, -56, -55}, new byte[]{-89, -82, 121, 7, 5, -87, -21, -109}, sb);
        String strN = h7.d.n(new File(strQ));
        if (strN != null) {
            ((ClipboardManager) getSystemService("clipboard")).setPrimaryClip(ClipData.newPlainText(getString(2131886156), strN));
            Toast.makeText(this, getString(2131886367), 1).show();
            new File(strQ).renameTo(new File(getExternalFilesDir("log"), String.format("crash_%s.log", new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss", Locale.US).format(new Date()))));
        }
        d dVar = this.z;
        DrawerLayout drawerLayout = dVar.c;
        this.C = drawerLayout;
        h.a c0301a = new h.a(this, drawerLayout, dVar.b.b);
        this.C.a(c0301a);
        DrawerLayout drawerLayout2 = c0301a.b;
        View viewF = drawerLayout2.f(8388611);
        if (viewF != null ? DrawerLayout.o(viewF) : false) {
            c0301a.d(1.0f);
        } else {
            c0301a.d(0.0f);
        }
        View viewF2 = drawerLayout2.f(8388611);
        int i11 = (viewF2 != null ? DrawerLayout.o(viewF2) : false) ? c0301a.e : c0301a.d;
        boolean z5 = c0301a.f;
        e5.d dVar2 = c0301a.a;
        if (!z5) {
            dVar2.getClass();
        }
        ((Toolbar) dVar2.b).setNavigationIcon(c0301a.c);
        dVar2.H(i11);
        NavigationView navigationView = dVar.d;
        navigationView.setNavigationItemSelectedListener(this);
        ((TextView) navigationView.i.b.getChildAt(0).findViewById(2131296685)).setText(String.format(Locale.US, "v%s (%d)", "1.8.3", 84));
        String strL = "voice_speed";
        int i12 = AppContext.b;
        SharedPreferences sharedPreferences = y6.a.a;
        int i13 = sharedPreferences.getInt(strL, 50);
        int i14 = sharedPreferences.getInt("voice_volume", 50);
        int i15 = sharedPreferences.getInt("voice_pitch", 25);
        SeekBar seekBar = (SeekBar) findViewById(2131296950);
        TextView textView = (TextView) findViewById(2131296951);
        SeekBar seekBar2 = (SeekBar) findViewById(2131296952);
        TextView textView2 = (TextView) findViewById(2131296953);
        SeekBar seekBar3 = (SeekBar) findViewById(2131296948);
        TextView textView3 = (TextView) findViewById(2131296949);
        seekBar.setProgress(i13);
        seekBar2.setProgress(i14);
        seekBar3.setProgress(i15);
        textView.setText(String.valueOf(i13));
        textView2.setText(String.valueOf(i14));
        textView3.setText(String.valueOf(i15));
        this.accessibilityManager = (AccessibilityManager) getSystemService("accessibility");
        seekBar.setOnSeekBarChangeListener(new m(this, textView, i));
        seekBar2.setOnSeekBarChangeListener(new m(this, textView2, i9));
        seekBar3.setOnSeekBarChangeListener(new m(this, textView3, 2));
        AppDatabase appDatabaseT = Q6.c.t();
        F6.j jVar2 = (F6.j) appDatabaseT.speakerDao();
        jVar2.getClass();
        // [R8 已移除的调用] 原字符串: "SELECT EXISTS(SELECT 1 FROM speaker)"
        B6.d dVar3 = new B6.d(i8);
        AppDatabase_Impl appDatabase_Impl = (AppDatabase_Impl) jVar2.a;
        boolean zBooleanValue = ((Boolean) r2.q.C(appDatabase_Impl, true, false, dVar3)).booleanValue();
        // [R8 已移除的调用] 原字符串: "SELECT DISTINCT locale FROM speaker"
        List list = (List) r2.q.C(appDatabase_Impl, true, false, new B6.d(i10));
        appDatabaseT.close();
        if (zBooleanValue) {
            HashSet hashSet = new HashSet(Arrays.asList("zho-CHN", "zho-HKG", "zho-TWN", "eng-USA", "eng-GBR"));
            for (String str : new HashSet<String>(list)) {
                if (!TextUtils.isEmpty(str)) {
                    String[] strArrSplit = str.split("-");
                    try {
                        Locale locale = new Locale(strArrSplit[0], strArrSplit[1]);
                        hashSet.add(locale.getISO3Language() + "-" + locale.getISO3Country());
                    } catch (Exception e9) {
                        h7.e.e("c", "loadDataFromDb: locale=".concat(str), e9);
                    }
                }
            }
            z6.a.a0.k(hashSet);
        } else {
            K6.d.i(this);
            if (!Q6.c.K(this)) {
                Toast.makeText(this, getString(2131886449), 1).show();
                return;
            }
        }
        if (K6.d.c().isEmpty()) {
            Toast.makeText(this, getString(2131886375), 0).show();
            return;
        }
        this.B_ = new e(this, new e0(26));
        this.accessibilityManager = (AccessibilityManager) getSystemService("accessibility");
        final n nVar = new n(this, this.B_);
        ViewPager2 viewPager2 = jVar.c;
        this.E = viewPager2;
        viewPager2.setSaveEnabled(true);
        this.E.setAdapter(nVar);
        U store = b();
        if (this.g == null) {
            this.g = new N(getApplication(), this, getIntent() != null ? getIntent().getExtras() : null);
        }
        N factory = this.g;
        k0.d dVarA = a();
        kotlin.jvm.internal.i.f(store, "store");
        kotlin.jvm.internal.i.f(factory, "factory");
        r2.i0 c0622i0 = new r2.i0(store, factory, dVarA);
        kotlin.jvm.internal.d dVarA2 = kotlin.jvm.internal.p.a(o.class);
        String strB = dVarA2.b();
        if (strB == null) {
            throw new IllegalArgumentException("Local and anonymous classes can not be ViewModels");
        }
        o oVar = (o) c0622i0.k(dVarA2, "androidx.lifecycle.ViewModelProvider.DefaultKey:".concat(strB));
        this.F = oVar;
        oVar.b.d(this, new A() { // from class: Z6.l

            /* JADX INFO: renamed from: a, reason: collision with root package name */
            public final /* synthetic */ MainActivity a;

            {
                this.a = MainActivity.this;   // OBF: 原为 jadx 误渲染的构造参数 this
            }

            /* JADX WARN: Code duplicated, block: B:211:0x049f  */
            /* JADX WARN: Code duplicated, block: B:232:0x013f A[ADDED_TO_REGION, REMOVE, SYNTHETIC] */
            /* JADX WARN: Code duplicated, block: B:33:0x00f9 A[ADDED_TO_REGION] */
            /* JADX WARN: Code duplicated, block: B:36:0x00ff  */
            /* JADX WARN: Code duplicated, block: B:40:0x0106  */
            /* JADX WARN: Code duplicated, block: B:47:0x011b  */
            /* JADX WARN: Code duplicated, block: B:49:0x0123  */
            /* JADX WARN: Code duplicated, block: B:54:0x013d  */
            @Override // androidx.lifecycle.A
            public final void a(Object obj) {
                int[] iArr;
                int[] iArr2;
                c2.a aVar;
                int[] iArr3;
                c2.a aVar2;
                int i16;
                ArrayList arrayList;
                int i17;
                int i18;
                int i19;
                List list2;
                n nVar2;
                ArrayList arrayList2;
                ArrayList arrayList3;
                z1.l c0696l;
                z1.m c0697m;
                ArrayList arrayList4;
                ArrayList arrayList5;
                z1.l c0696l2;
                z1.j c0694j;
                int i20;
                z1.m c0697m2;
                z1.m c0697m3;
                int i21;
                int i22;
                int i23;
                int i24;
                int i25;
                int i26;
                int i27;
                boolean z8;
                int i28;
                List list3 = (List) obj;
                MainActivity mainActivity = this.a;
                H6.f fVar = mainActivity.D;
                if (fVar != null) {
                    fVar.h();
                }
                H6.f fVar2 = new H6.f(mainActivity.z.b.a, mainActivity.E, new B0.F(4, mainActivity, list3));
                mainActivity.D = fVar2;
                fVar2.g();
                n nVar3 = nVar;
                c2.a aVar3 = new c2.a(21, nVar3, list3);
                int iN = aVar3.n();
                int size = list3.size();
                ArrayList<z1.j> arrayList6 = new ArrayList();
                ArrayList arrayList7 = new ArrayList();
                z1.l c0696l3 = new z1.l();
                c0696l3.a = 0;
                c0696l3.b = iN;
                c0696l3.c = 0;
                c0696l3.d = size;
                arrayList7.add(c0696l3);
                int i29 = iN + size;
                int i30 = 1;
                int i31 = (((i29 + 1) / 2) * 2) + 1;
                int[] iArr4 = new int[i31];
                int i32 = i31 / 2;
                int[] iArr5 = new int[i31];
                ArrayList arrayList8 = new ArrayList();
                while (!arrayList7.isEmpty()) {
                    z1.l c0696l4 = (z1.l) arrayList7.remove(arrayList7.size() - i30);
                    if (c0696l4.b() < i30 || c0696l4.a() < i30) {
                        list2 = list3;
                        nVar2 = nVar3;
                        arrayList2 = arrayList7;
                        arrayList3 = arrayList8;
                        c0696l = c0696l4;
                        c0697m = null;
                        break;
                    }
                    int iA = ((c0696l4.a() + c0696l4.b()) + 1) / 2;
                    int i33 = i30 + i32;
                    iArr4[i33] = c0696l4.a;
                    iArr5[i33] = c0696l4.b;
                    int i34 = 0;
                    while (true) {
                        if (i34 >= iA) {
                            list2 = list3;
                            nVar2 = nVar3;
                            arrayList2 = arrayList7;
                            arrayList3 = arrayList8;
                            c0696l = c0696l4;
                            c0697m = null;
                            break;
                        }
                        boolean z9 = Math.abs(c0696l4.b() - c0696l4.a()) % 2 == i30;
                        int iB = c0696l4.b() - c0696l4.a();
                        int i35 = -i34;
                        int i36 = i35;
                        while (true) {
                            if (i36 > i34) {
                                list2 = list3;
                                nVar2 = nVar3;
                                arrayList2 = arrayList7;
                                i20 = iA;
                                c0697m2 = null;
                                break;
                            }
                            if (i36 == i35 || (i36 != i34 && iArr4[i36 + 1 + i32] > iArr4[(i36 - 1) + i32])) {
                                i24 = iArr4[i36 + 1 + i32];
                                i25 = i24;
                            } else {
                                i24 = iArr4[(i36 - 1) + i32];
                                i25 = i24 + 1;
                            }
                            i20 = iA;
                            nVar2 = nVar3;
                            list2 = list3;
                            i26 = ((i25 - c0696l4.a) + c0696l4.c) - i36;
                            if (i34 == 0 && i25 == i24) {
                                i27 = i26 - 1;
                            } else {
                                i27 = i26;
                            }
                            arrayList2 = arrayList7;
                            while (i25 < c0696l4.b && i26 < c0696l4.d && aVar3.c(i25, i26)) {
                                i25++;
                                i26++;
                            }
                            iArr4[i36 + i32] = i25;
                            if (z9) {
                                i28 = iB - i36;
                                z8 = z9;
                                if (i28 < i35 + 1 && i28 <= i34 - 1 && iArr5[i28 + i32] <= i25) {
                                    c0697m2 = new z1.m();
                                    c0697m2.a = i24;
                                    c0697m2.b = i27;
                                    c0697m2.c = i25;
                                    c0697m2.d = i26;
                                    c0697m2.e = false;
                                    break;
                                }
                            } else {
                                z8 = z9;
                            }
                            i36 += 2;
                            nVar3 = nVar2;
                            iA = i20;
                            list3 = list2;
                            arrayList7 = arrayList2;
                            z9 = z8;
                        }
                        if (c0697m2 != null) {
                            c0697m = c0697m2;
                            arrayList3 = arrayList8;
                            c0696l = c0696l4;
                            break;
                        }
                        boolean z10 = (c0696l4.b() - c0696l4.a()) % 2 == 0;
                        int iB2 = c0696l4.b() - c0696l4.a();
                        int i37 = i35;
                        while (true) {
                            if (i37 > i34) {
                                arrayList3 = arrayList8;
                                c0696l = c0696l4;
                                c0697m3 = null;
                                break;
                            }
                            if (i37 == i35 || (i37 != i34 && iArr5[i37 + 1 + i32] < iArr5[(i37 - 1) + i32])) {
                                i21 = iArr5[i37 + 1 + i32];
                                i22 = i21;
                            } else {
                                i21 = iArr5[(i37 - 1) + i32];
                                i22 = i21 - 1;
                            }
                            int i38 = c0696l4.d - ((c0696l4.b - i22) - i37);
                            int i39 = (i34 == 0 || i22 != i21) ? i38 : i38 + 1;
                            arrayList3 = arrayList8;
                            while (true) {
                                if (i22 <= c0696l4.a || i38 <= c0696l4.c) {
                                    c0696l = c0696l4;
                                    break;
                                }
                                c0696l = c0696l4;
                                if (!aVar3.c(i22 - 1, i38 - 1)) {
                                    break;
                                }
                                i22--;
                                i38--;
                                c0696l4 = c0696l;
                            }
                            iArr5[i37 + i32] = i22;
                            if (z10 && (i23 = iB2 - i37) >= i35 && i23 <= i34 && iArr4[i23 + i32] >= i22) {
                                c0697m3 = new z1.m();
                                c0697m3.a = i22;
                                c0697m3.b = i38;
                                c0697m3.c = i21;
                                c0697m3.d = i39;
                                c0697m3.e = true;
                                break;
                            }
                            i37 += 2;
                            arrayList8 = arrayList3;
                            c0696l4 = c0696l;
                        }
                        if (c0697m3 != null) {
                            c0697m = c0697m3;
                            break;
                        }
                        i34++;
                        arrayList8 = arrayList3;
                        nVar3 = nVar2;
                        iA = i20;
                        list3 = list2;
                        arrayList7 = arrayList2;
                        c0696l4 = c0696l;
                        i30 = 1;
                    }
                    if (c0697m != null) {
                        if (c0697m.a() > 0) {
                            int i40 = c0697m.d;
                            int i41 = c0697m.b;
                            int i42 = i40 - i41;
                            int i43 = c0697m.c;
                            int i44 = c0697m.a;
                            int i45 = i43 - i44;
                            if (i42 == i45) {
                                c0694j = new z1.j(i44, i41, i45);
                            } else if (c0697m.e) {
                                c0694j = new z1.j(i44, i41, c0697m.a());
                            } else {
                                c0694j = i42 > i45 ? new z1.j(i44, i41 + 1, c0697m.a()) : new z1.j(i44 + 1, i41, c0697m.a());
                            }
                            arrayList6.add(c0694j);
                        }
                        if (arrayList3.isEmpty()) {
                            c0696l2 = new z1.l();
                            arrayList4 = arrayList3;
                        } else {
                            arrayList4 = arrayList3;
                            c0696l2 = (z1.l) arrayList4.remove(arrayList3.size() - 1);
                        }
                        z1.l c0696l5 = c0696l;
                        c0696l2.a = c0696l5.a;
                        c0696l2.c = c0696l5.c;
                        c0696l2.b = c0697m.a;
                        c0696l2.d = c0697m.b;
                        arrayList5 = arrayList2;
                        arrayList5.add(c0696l2);
                        c0696l5.b = c0696l5.b;
                        c0696l5.d = c0696l5.d;
                        c0696l5.a = c0697m.c;
                        c0696l5.c = c0697m.d;
                        arrayList5.add(c0696l5);
                    } else {
                        arrayList4 = arrayList3;
                        arrayList5 = arrayList2;
                        arrayList4.add(c0696l);
                    }
                    arrayList8 = arrayList4;
                    arrayList7 = arrayList5;
                    nVar3 = nVar2;
                    list3 = list2;
                    i30 = 1;
                }
                List list4 = list3;
                n nVar4 = nVar3;
                Collections.sort(arrayList6, z1.n.a);
                e5.a aVar4 = new e5.a();
                aVar4.d = iArr4;
                aVar4.e = iArr5;
                Arrays.fill(iArr4, 0);
                Arrays.fill(iArr5, 0);
                aVar4.f = aVar3;
                int iN2 = aVar3.n();
                aVar4.a = iN2;
                int size2 = ((List) aVar3.c).size();
                aVar4.b = size2;
                aVar4.c = true;
                z1.j c0694j2 = arrayList6.isEmpty() ? null : (z1.j) arrayList6.get(0);
                if (c0694j2 == null || c0694j2.a != 0 || c0694j2.b != 0) {
                    arrayList6.add(0, new z1.j(0, 0, 0));
                }
                arrayList6.add(new z1.j(iN2, size2, 0));
                Iterator it = arrayList6.iterator();
                while (true) {
                    boolean zHasNext = it.hasNext();
                    iArr = (int[]) aVar4.e;
                    iArr2 = (int[]) aVar4.d;
                    aVar = (c2.a) aVar4.f;
                    if (!zHasNext) {
                        break;
                    }
                    z1.j c0694j3 = (z1.j) it.next();
                    for (int i46 = 0; i46 < c0694j3.c; i46++) {
                        int i47 = c0694j3.a + i46;
                        int i48 = c0694j3.b + i46;
                        int i49 = aVar.b(i47, i48) ? 1 : 2;
                        iArr2[i47] = (i48 << 4) | i49;
                        iArr[i48] = (i47 << 4) | i49;
                    }
                }
                if (aVar4.c) {
                    int i50 = 0;
                    for (z1.j c0694j4 : arrayList6) {
                        while (true) {
                            i18 = c0694j4.a;
                            if (i50 >= i18) {
                                break;
                            }
                            if (iArr2[i50] == 0) {
                                int size3 = arrayList6.size();
                                int i51 = 0;
                                found:
                                for (int i52 = 0; i52 < size3; i52++) {
                                    z1.j c0694j5 = (z1.j) arrayList6.get(i52);
                                    while (true) {
                                        i19 = c0694j5.b;
                                        if (i51 >= i19) {
                                            break;
                                        }
                                        if (iArr[i51] == 0 && aVar.c(i50, i51)) {
                                            int i53 = aVar.b(i50, i51) ? 8 : 4;
                                            iArr2[i50] = (i51 << 4) | i53;
                                            iArr[i51] = i53 | (i50 << 4);
                                            break found;
                                        }
                                        i51++;
                                    }
                                    i51 = c0694j5.c + i19;
                                }
                            }
                            i50++;
                        }
                        i50 = c0694j4.c + i18;
                    }
                }
                nVar4.m = new ArrayList(list4);
                z1.b c0686b = new z1.b(new q3.b(nVar4));
                ArrayDeque arrayDeque = new ArrayDeque();
                int size4 = arrayList6.size() - 1;
                int i54 = aVar4.a;
                int i55 = aVar4.b;
                int i56 = i54;
                while (size4 >= 0) {
                    z1.j c0694j6 = (z1.j) arrayList6.get(size4);
                    int i57 = c0694j6.a;
                    int i58 = c0694j6.c;
                    int i59 = i57 + i58;
                    int i60 = c0694j6.b;
                    int i61 = i60 + i58;
                    while (true) {
                        iArr3 = (int[]) aVar4.d;
                        aVar2 = (c2.a) aVar4.f;
                        if (i56 <= i59) {
                            break;
                        }
                        i56--;
                        int i62 = iArr3[i56];
                        if ((i62 & 12) != 0) {
                            arrayList = arrayList6;
                            z1.k c0695kA = e5.a.a(arrayDeque, i62 >> 4, false);
                            if (c0695kA != null) {
                                int i63 = (i54 - c0695kA.b) - 1;
                                c0686b.c(i56, i63);
                                if ((i62 & 4) != 0) {
                                    aVar2.getClass();
                                    c0686b.b(i63, 1);
                                }
                            } else {
                                arrayDeque.add(new z1.k(i56, (i54 - i56) - 1, true));
                            }
                        } else {
                            arrayList = arrayList6;
                            if (c0686b.b != 2 || (i17 = c0686b.c) < i56 || i17 > i56 + 1) {
                                c0686b.a();
                                c0686b.c = i56;
                                c0686b.d = 1;
                                c0686b.b = 2;
                            } else {
                                c0686b.d++;
                                c0686b.c = i56;
                            }
                            i54--;
                        }
                        arrayList6 = arrayList;
                    }
                    ArrayList arrayList9 = arrayList6;
                    while (i55 > i61) {
                        i55--;
                        int i64 = ((int[]) aVar4.e)[i55];
                        if ((i64 & 12) != 0) {
                            z1.k c0695kA2 = e5.a.a(arrayDeque, i64 >> 4, true);
                            if (c0695kA2 == null) {
                                arrayDeque.add(new z1.k(i55, i54 - i56, false));
                            } else {
                                c0686b.c((i54 - c0695kA2.b) - 1, i56);
                                if ((i64 & 4) != 0) {
                                    aVar2.getClass();
                                    c0686b.b(i56, 1);
                                }
                            }
                        } else {
                            if (c0686b.b != 1 || i56 < (i16 = c0686b.c)) {
                                c0686b.a();
                                c0686b.c = i56;
                                c0686b.d = 1;
                                c0686b.b = 1;
                            } else {
                                int i65 = c0686b.d;
                                if (i56 <= i16 + i65) {
                                    c0686b.d = i65 + 1;
                                    c0686b.c = Math.min(i56, i16);
                                } else {
                                    c0686b.a();
                                    c0686b.c = i56;
                                    c0686b.d = 1;
                                    c0686b.b = 1;
                                }
                            }
                            i54++;
                        }
                    }
                    i56 = c0694j6.a;
                    int i66 = i56;
                    for (int i67 = 0; i67 < i58; i67++) {
                        if ((iArr3[i66] & 15) == 2) {
                            aVar2.getClass();
                            c0686b.b(i66, 1);
                        }
                        i66++;
                    }
                    size4--;
                    arrayList6 = arrayList9;
                    i55 = i60;
                }
                c0686b.a();
            }
        });
        if (z6.a.a0.x) {
            startService(new Intent(this, (Class<?>) ForwardService.class));
        }
    }

    @Override // android.app.Activity
    public final boolean onCreateOptionsMenu(Menu menu) {
        int i = 0;
        int i8 = 1;
        if (menu != null) {
            try {
                Method declaredMethod = menu.getClass().getDeclaredMethod("setOptionalIconsVisible", Boolean.TYPE);
                declaredMethod.setAccessible(true);
                declaredMethod.invoke(menu, Boolean.TRUE);
            } catch (Exception e8) {
                h7.e.e("MainActivity", "onCreateOptionsMenu: ", e8);
            }
        }
        getMenuInflater().inflate(2131623938, menu);
        if (menu != null) {
            SwitchCompat switchCompat = (SwitchCompat) this.z.d.getMenu().findItem(2131296674).getActionView();
            switchCompat.setChecked(z6.a.a0.x);
            switchCompat.setOnCheckedChangeListener(new k(this, i));
            e eVar = this.B_;
            if (eVar != null) {
                MenuItem menuItemFindItem = menu.findItem(2131296332);
                SearchView searchView = (SearchView) menuItemFindItem.getActionView();
                searchView.setSearchableInfo(((SearchManager) getSystemService("search")).getSearchableInfo(getComponentName()));
                searchView.setIconifiedByDefault(true);
                searchView.setSubmitButtonEnabled(true);
                ((SearchView.SearchAutoComplete) searchView.findViewById(2131296818)).setThreshold(1);
                searchView.setSuggestionsAdapter(eVar.g);
                searchView.setOnSuggestionListener(new W6.a(eVar, searchView, i8));
                searchView.setOnQueryTextListener(new d5.a(eVar, 15));
                menuItemFindItem.setOnActionExpandListener(new b(eVar));
            }
        }
        return true;
    }

    @Override // h.m, android.app.Activity
    public final void onDestroy() {
        V v8 = this.A;
        if (v8 != null) {
            s3.e eVar = (s3.e) v8.b;
            if (eVar.isShowing()) {
                eVar.dismiss();
            }
        }
        H6.f fVar = this.D;
        if (fVar != null) {
            fVar.h();
        }
        if (this.F != null) {
            Z6.o.c = null;
        }
        if (h7.e.a != null) {
            h7.e.a.shutdown();
            h7.e.a = null;
        }
        WeakReference weakReference = h7.j.a;
        synchronized (h7.j.class) {
            try {
                TextToSpeech textToSpeech = (TextToSpeech) h7.j.a.get();
                if (textToSpeech != null) {
                    textToSpeech.shutdown();
                    h7.j.a.clear();
                }
            } catch (Throwable th) {
                throw th;
            }
        }
        super.onDestroy();
    }

    @Override // android.app.Activity
    public final boolean onOptionsItemSelected(MenuItem menuItem) {
        int itemId = menuItem.getItemId();
        if (itemId == 2131296322) {
            Intent intent = new Intent("android.intent.action.OPEN_DOCUMENT");
            intent.addCategory("android.intent.category.OPENABLE");
            intent.setType("application/zip");
            startActivityForResult(intent, 1);
        } else if (itemId == 2131296334) {
            Intent intent2 = new Intent("android.settings.ACCESSIBILITY_SETTINGS");
            intent2.setAction("com.android.settings.TTS_SETTINGS");
            intent2.setFlags(268435456);
            startActivity(intent2);
        } else if (itemId == 2131296328) {
            V v8 = this.A;
            if (v8 != null) {
                s3.e eVar = (s3.e) v8.b;
                if (eVar.isShowing()) {
                    eVar.dismiss();
                }
            }
            this.A = new V(this);
        } else {
            if (itemId != 2131296329) {
                return super.onOptionsItemSelected(menuItem);
            }
            K6.d.i(this);
            Q6.c.K(this);
            N6.a.e();
            recreate();
        }
        return true;
    }

    public final void t(String str) {
        try {
            startActivity(new Intent("android.intent.action.VIEW", Uri.parse("tg://resolve?domain=MultiTTS_".concat(str))));
        } catch (Exception unused) {
            startActivity(new Intent("android.intent.action.VIEW", Uri.parse("https://t.me/MultiTTS_".concat(str))));
        }
    }
}
