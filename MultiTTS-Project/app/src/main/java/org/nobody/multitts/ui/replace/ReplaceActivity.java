package org.nobody.multitts.ui.replace;

import h.g;
import h.m;

import B.p;
import B0.F;
import B0.e0;
import N6.a;
import W6.e;
import Z6.f;
import Z6.v;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.accessibility.AccessibilityManager;
import android.widget.LinearLayout;
import androidx.appcompat.widget.SearchView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import b7.h;
import com.google.common.base.Ascii;
import com.google.common.primitives.SignedBytes;
import com.google.common.primitives.UnsignedBytes;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.concurrent.CopyOnWriteArrayList;
import okio.Utf8;
import org.nobody.multitts.tts.replace.ReplaceRule;
import org.nobody.multitts.ui.replace.ReplaceActivity;



import o0.c;


/* JADX INFO: loaded from: D:\mtts_work\dex\classes3.dex */
public class ReplaceActivity extends m {

    /* JADX INFO: renamed from: C, reason: collision with root package name */
    public static final /* synthetic */ int C = 0;

    /* JADX INFO: renamed from: A, reason: collision with root package name */
    public e A;

    /* JADX INFO: renamed from: B, reason: collision with root package name */
    public boolean B = false;

    /* deobf(oracle) 反推自混淆名: y */
    public AccessibilityManager accessibilityManager;

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
        final int i = 2;
        final int i8 = 0;
        final int i9 = 1;
        super.onCreate(bundle);
        A6.e eVarInflate = A6.e.inflate(getLayoutInflater());
        setContentView(eVarInflate.a);
        s(eVarInflate.d);
        l().T();
        this.accessibilityManager = (AccessibilityManager) getSystemService("accessibility");
        final b7.h hVar = new b7.h(N6.a.c);
        RecyclerView recyclerView = eVarInflate.c;
        recyclerView.setAdapter(hVar);
        recyclerView.setLayoutManager(new LinearLayoutManager(1));
        recyclerView.getViewTreeObserver().addOnGlobalLayoutListener(new b7.c(recyclerView, hVar, i8));
        new z1.y(new V6.e(hVar, new F(5, this, recyclerView))).i(recyclerView);
        eVarInflate.e.setOnClickListener(new View.OnClickListener() { // from class: b7.a

            /* JADX INFO: renamed from: b, reason: collision with root package name */
            public final /* synthetic */ ReplaceActivity b;

            {
                this.b = ReplaceActivity.this;   // OBF: 原为 jadx 误渲染的构造参数 this
            }

            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                h hVar2 = hVar;
                ReplaceActivity replaceActivity = this.b;
                switch (i8) {
                    case 0:
                        int i10 = ReplaceActivity.C;
                        replaceActivity.getClass();
                        ReplaceRule replaceRule = new ReplaceRule();
                        B2.c.k(replaceActivity, replaceRule, new F(6, hVar2, replaceRule));
                        break;
                    default:
                        Comparator comparatorReverseOrder = replaceActivity.B ? Collections.reverseOrder() : new D0.c(12);
                        CopyOnWriteArrayList copyOnWriteArrayList = (CopyOnWriteArrayList) hVar2.k;
                        Collections.sort(copyOnWriteArrayList, comparatorReverseOrder);
                        ArrayList arrayList = hVar2.i;
                        Collections.sort(arrayList, comparatorReverseOrder);
                        ArrayList arrayList2 = hVar2.j;
                        arrayList2.clear();
                        arrayList2.addAll(b7.h.F(arrayList));
                        hVar2.i();
                        N6.a.c = copyOnWriteArrayList;
                        N6.a.f();
                        replaceActivity.B = !replaceActivity.B;
                        break;
                }
            }
        });
        SearchView searchView = eVarInflate.h;
        this.z = searchView;
        this.A = new e(this, searchView, new e0(29));
        p pVar = new p();
        ConstraintLayout constraintLayout = eVarInflate.b;
        pVar.c(constraintLayout);
        this.z.setOnSearchClickListener(new f(this, i9, pVar, constraintLayout));
        this.z.setOnCloseListener(new D0.g(this, i, pVar, constraintLayout));
        this.A.b("none", hVar);
        eVarInflate.f.setOnClickListener(new View.OnClickListener() { // from class: b7.b

            /* JADX INFO: renamed from: b, reason: collision with root package name */
            public final /* synthetic */ ReplaceActivity b;

            {
                this.b = ReplaceActivity.this;   // OBF: 原为 jadx 误渲染的构造参数 this
            }

            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                int i10 = 1;
                ReplaceActivity replaceActivity = this.b;
                switch (i8) {
                    case 0:
                        SearchView searchView2 = replaceActivity.z;
                        if (!searchView2.S) {
                            searchView2.setIconified(true);
                        } else {
                            replaceActivity.finish();
                        }
                        break;
                    case 1:
                        int i11 = ReplaceActivity.C;
                        replaceActivity.getClass();
                        o.w c0586w = new o.w(replaceActivity, null, 0);
                        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(-1, -2);
                        LinearLayout linearLayout = new LinearLayout(replaceActivity);
                        linearLayout.setPadding(50, 20, 50, 20);
                        linearLayout.setLayoutParams(layoutParams);
                        linearLayout.addView(c0586w);
                        c0586w.setLayoutParams(layoutParams);
                        B4.a aVar = new B4.a(replaceActivity, 2131952281);
                        ((g) aVar.c).r = linearLayout;
                        aVar.q(2131890553);
                        aVar.p(replaceActivity.getString(2131886365), new v(i10, replaceActivity, c0586w));
                        aVar.o(replaceActivity.getString(2131886359), null);
                        aVar.r();
                        break;
                    default:
                        int i12 = ReplaceActivity.C;
                        replaceActivity.getClass();
                        try {
                            h7.d.h(replaceActivity.getExternalCacheDir());
                            File file = new File(replaceActivity.getExternalCacheDir(), "replacement.zip");
                            h7.e.x(file, "replaces.yaml");
                            replaceActivity.t(file);
                        } catch (Exception e8) {
                            h7.e.e("org.nobody.multitts.ui.replace.ReplaceActivity", "exportReplacement: ", e8);
                        }
                        break;
                }
            }
        });
        eVarInflate.j.setOnClickListener(new View.OnClickListener() { // from class: b7.b

            /* JADX INFO: renamed from: b, reason: collision with root package name */
            public final /* synthetic */ ReplaceActivity b;

            {
                this.b = ReplaceActivity.this;   // OBF: 原为 jadx 误渲染的构造参数 this
            }

            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                int i10 = 1;
                ReplaceActivity replaceActivity = this.b;
                switch (i9) {
                    case 0:
                        SearchView searchView2 = replaceActivity.z;
                        if (!searchView2.S) {
                            searchView2.setIconified(true);
                        } else {
                            replaceActivity.finish();
                        }
                        break;
                    case 1:
                        int i11 = ReplaceActivity.C;
                        replaceActivity.getClass();
                        o.w c0586w = new o.w(replaceActivity, null, 0);
                        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(-1, -2);
                        LinearLayout linearLayout = new LinearLayout(replaceActivity);
                        linearLayout.setPadding(50, 20, 50, 20);
                        linearLayout.setLayoutParams(layoutParams);
                        linearLayout.addView(c0586w);
                        c0586w.setLayoutParams(layoutParams);
                        B4.a aVar = new B4.a(replaceActivity, 2131952281);
                        ((g) aVar.c).r = linearLayout;
                        aVar.q(2131890553);
                        aVar.p(replaceActivity.getString(2131886365), new v(i10, replaceActivity, c0586w));
                        aVar.o(replaceActivity.getString(2131886359), null);
                        aVar.r();
                        break;
                    default:
                        int i12 = ReplaceActivity.C;
                        replaceActivity.getClass();
                        try {
                            h7.d.h(replaceActivity.getExternalCacheDir());
                            File file = new File(replaceActivity.getExternalCacheDir(), "replacement.zip");
                            h7.e.x(file, "replaces.yaml");
                            replaceActivity.t(file);
                        } catch (Exception e8) {
                            h7.e.e("org.nobody.multitts.ui.replace.ReplaceActivity", "exportReplacement: ", e8);
                        }
                        break;
                }
            }
        });
        eVarInflate.g.setOnClickListener(new View.OnClickListener() { // from class: b7.b

            /* JADX INFO: renamed from: b, reason: collision with root package name */
            public final /* synthetic */ ReplaceActivity b;

            {
                this.b = ReplaceActivity.this;   // OBF: 原为 jadx 误渲染的构造参数 this
            }

            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                int i10 = 1;
                ReplaceActivity replaceActivity = this.b;
                switch (i) {
                    case 0:
                        SearchView searchView2 = replaceActivity.z;
                        if (!searchView2.S) {
                            searchView2.setIconified(true);
                        } else {
                            replaceActivity.finish();
                        }
                        break;
                    case 1:
                        int i11 = ReplaceActivity.C;
                        replaceActivity.getClass();
                        o.w c0586w = new o.w(replaceActivity, null, 0);
                        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(-1, -2);
                        LinearLayout linearLayout = new LinearLayout(replaceActivity);
                        linearLayout.setPadding(50, 20, 50, 20);
                        linearLayout.setLayoutParams(layoutParams);
                        linearLayout.addView(c0586w);
                        c0586w.setLayoutParams(layoutParams);
                        B4.a aVar = new B4.a(replaceActivity, 2131952281);
                        ((g) aVar.c).r = linearLayout;
                        aVar.q(2131890553);
                        aVar.p(replaceActivity.getString(2131886365), new v(i10, replaceActivity, c0586w));
                        aVar.o(replaceActivity.getString(2131886359), null);
                        aVar.r();
                        break;
                    default:
                        int i12 = ReplaceActivity.C;
                        replaceActivity.getClass();
                        try {
                            h7.d.h(replaceActivity.getExternalCacheDir());
                            File file = new File(replaceActivity.getExternalCacheDir(), "replacement.zip");
                            h7.e.x(file, "replaces.yaml");
                            replaceActivity.t(file);
                        } catch (Exception e8) {
                            h7.e.e("org.nobody.multitts.ui.replace.ReplaceActivity", "exportReplacement: ", e8);
                        }
                        break;
                }
            }
        });
        eVarInflate.i.setOnClickListener(new View.OnClickListener() { // from class: b7.a

            /* JADX INFO: renamed from: b, reason: collision with root package name */
            public final /* synthetic */ ReplaceActivity b;

            {
                this.b = ReplaceActivity.this;   // OBF: 原为 jadx 误渲染的构造参数 this
            }

            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                h hVar2 = hVar;
                ReplaceActivity replaceActivity = this.b;
                switch (i9) {
                    case 0:
                        int i10 = ReplaceActivity.C;
                        replaceActivity.getClass();
                        ReplaceRule replaceRule = new ReplaceRule();
                        B2.c.k(replaceActivity, replaceRule, new F(6, hVar2, replaceRule));
                        break;
                    default:
                        Comparator comparatorReverseOrder = replaceActivity.B ? Collections.reverseOrder() : new D0.c(12);
                        CopyOnWriteArrayList copyOnWriteArrayList = (CopyOnWriteArrayList) hVar2.k;
                        Collections.sort(copyOnWriteArrayList, comparatorReverseOrder);
                        ArrayList arrayList = hVar2.i;
                        Collections.sort(arrayList, comparatorReverseOrder);
                        ArrayList arrayList2 = hVar2.j;
                        arrayList2.clear();
                        arrayList2.addAll(b7.h.F(arrayList));
                        hVar2.i();
                        N6.a.c = copyOnWriteArrayList;
                        N6.a.f();
                        replaceActivity.B = !replaceActivity.B;
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
