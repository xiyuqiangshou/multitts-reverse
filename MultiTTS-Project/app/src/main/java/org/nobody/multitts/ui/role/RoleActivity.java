package org.nobody.multitts.ui.role;

import h.m;

import A6.f;
import B.p;
import B0.F;
import D0.g;
import M3.j;
import O6.d;
import W6.e;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.accessibility.AccessibilityManager;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.SearchView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import b7.h;
import com.bytedance.frameworks.baselib.network.http.cronet.impl.b;
import com.google.common.base.Ascii;
import com.google.common.primitives.UnsignedBytes;
import java.io.File;
import okio.Utf8;
import org.nobody.multitts.ui.role.RoleActivity;

import o0.c;


/* JADX INFO: loaded from: D:\mtts_work\dex\classes3.dex */
public class RoleActivity extends m {

    /* JADX INFO: renamed from: B, reason: collision with root package name */
    public static final /* synthetic */ int B = 0;

    /* JADX INFO: renamed from: A, reason: collision with root package name */
    public e A;

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
        int i = 4;
        final int i8 = 1;
        super.onCreate(bundle);
        f fVarInflate = A6.f.inflate(getLayoutInflater());
        setContentView(fVarInflate.a);
        s(fVarInflate.d);
        l().T();
        this.accessibilityManager = (AccessibilityManager) getSystemService("accessibility");
        h hVar = new h(O6.d.k);
        RecyclerView recyclerView = fVarInflate.c;
        recyclerView.setAdapter(hVar);
        recyclerView.setLayoutManager(new LinearLayoutManager(1));
        recyclerView.getViewTreeObserver().addOnGlobalLayoutListener(new b7.c(recyclerView, hVar, i8));
        new z1.y(new V6.e(hVar, new F(8, this, recyclerView))).i(recyclerView);
        j jVar = new j(i, this, hVar);
        AppCompatImageButton appCompatImageButton = fVarInflate.e;
        appCompatImageButton.setOnClickListener(jVar);
        appCompatImageButton.setOnLongClickListener(new Y6.h(2, this, hVar));
        SearchView searchView = fVarInflate.h;
        this.z = searchView;
        this.A = new e(this, searchView, new b(2));
        p pVar = new p();
        ConstraintLayout constraintLayout = fVarInflate.b;
        pVar.c(constraintLayout);
        this.z.setOnSearchClickListener(new Z6.f(this, i, pVar, constraintLayout));
        this.z.setOnCloseListener(new g(this, 3, pVar, constraintLayout));
        this.A.b("none", hVar);
        final int i9 = 0;
        fVarInflate.f.setOnClickListener(new View.OnClickListener() { // from class: d7.a

            /* JADX INFO: renamed from: b, reason: collision with root package name */
            public final /* synthetic */ RoleActivity b;

            {
                this.b = RoleActivity.this;   // OBF: 原为 jadx 误渲染的构造参数 this
            }

            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                RoleActivity roleActivity = this.b;
                switch (i9) {
                    case 0:
                        SearchView searchView2 = roleActivity.z;
                        if (!searchView2.S) {
                            searchView2.setIconified(true);
                        } else {
                            roleActivity.finish();
                        }
                        break;
                    default:
                        int i10 = RoleActivity.B;
                        roleActivity.getClass();
                        try {
                            h7.d.h(roleActivity.getExternalCacheDir());
                            File file = new File(roleActivity.getExternalCacheDir(), "roles.zip");
                            h7.e.x(file, "roles.yaml");
                            roleActivity.t(file);
                        } catch (Exception e8) {
                            h7.e.e("org.nobody.multitts.ui.role.RoleActivity", "exportReplacement: ", e8);
                        }
                        break;
                }
            }
        });
        fVarInflate.g.setOnClickListener(new View.OnClickListener() { // from class: d7.a

            /* JADX INFO: renamed from: b, reason: collision with root package name */
            public final /* synthetic */ RoleActivity b;

            {
                this.b = RoleActivity.this;   // OBF: 原为 jadx 误渲染的构造参数 this
            }

            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                RoleActivity roleActivity = this.b;
                switch (i8) {
                    case 0:
                        SearchView searchView2 = roleActivity.z;
                        if (!searchView2.S) {
                            searchView2.setIconified(true);
                        } else {
                            roleActivity.finish();
                        }
                        break;
                    default:
                        int i10 = RoleActivity.B;
                        roleActivity.getClass();
                        try {
                            h7.d.h(roleActivity.getExternalCacheDir());
                            File file = new File(roleActivity.getExternalCacheDir(), "roles.zip");
                            h7.e.x(file, "roles.yaml");
                            roleActivity.t(file);
                        } catch (Exception e8) {
                            h7.e.e("org.nobody.multitts.ui.role.RoleActivity", "exportReplacement: ", e8);
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
