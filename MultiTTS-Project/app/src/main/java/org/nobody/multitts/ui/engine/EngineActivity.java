package org.nobody.multitts.ui.engine;

import a.a;
import B.p;
import B0.F;
import B0.e0;
import K6.d;
import V6.e;
import Y6.b;
import Y6.j;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.accessibility.AccessibilityManager;
import androidx.appcompat.widget.SearchView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.common.base.Ascii;
import org.nobody.multitts.tts.engine.Engine;
import org.nobody.multitts.ui.engine.EngineActivity;

import o0.c;


/* JADX INFO: loaded from: D:\mtts_work\dex\classes3.dex */
public class EngineActivity extends h.m {

    /* JADX INFO: renamed from: C, reason: collision with root package name */
    public static final /* synthetic */ int C = 0;

    /* JADX INFO: renamed from: A, reason: collision with root package name */
    public SearchView A;

    /* JADX INFO: renamed from: B, reason: collision with root package name */
    public j B;

    /* deobf(oracle) 反推自混淆名: y */
    public AccessibilityManager accessibilityManager;

    /* JADX INFO: renamed from: z, reason: collision with root package name */
    public F6.j z;

    @Override // h.m, b.l, android.app.Activity
    public final void onActivityResult(int i, int i8, Intent intent) {
        F6.j jVar;
        super.onActivityResult(i, i8, intent);
        if (intent == null || i != 7) {
            return;
        }
        String stringExtra = intent.getStringExtra("data");
        F6.j jVar2 = this.z;
        if (jVar2 != null) {
            jVar2.B(stringExtra);
        }
        j jVar3 = this.B;
        if (jVar3 == null || (jVar = (F6.j) jVar3.i) == null) {
            return;
        }
        jVar.B(stringExtra);
    }

    @Override // b.l, android.app.Activity
    public final void onBackPressed() {
        SearchView searchView = this.A;
        if (searchView.S) {
            super.onBackPressed();
        } else {
            searchView.setIconified(true);
        }
    }

    @Override // h.m, b.l, F.i, android.app.Activity
    public final void onCreate(Bundle bundle) {
        final int i = 0;
        final int i8 = 1;
        super.onCreate(bundle);
        A6.a aVarInflate = A6.a.inflate(getLayoutInflater());
        setContentView(aVarInflate.a);
        s(aVarInflate.d);
        a aVarL = l();
        if (aVarL != null) {
            aVarL.T();
        }
        this.accessibilityManager = (AccessibilityManager) getSystemService("accessibility");
        j jVar = new j(K6.d.c());
        this.B = jVar;
        RecyclerView recyclerView = aVarInflate.c;
        recyclerView.setAdapter(jVar);
        recyclerView.setLayoutManager(new LinearLayoutManager(1));
        new z1.y(new e(this.B, new F(i8, this, recyclerView))).i(recyclerView);
        aVarInflate.e.setOnClickListener(new View.OnClickListener() { // from class: Y6.a

            /* JADX INFO: renamed from: b, reason: collision with root package name */
            public final /* synthetic */ EngineActivity b;

            {
                this.b = EngineActivity.this;   // OBF: 原为 jadx 误渲染的构造参数 this
            }

            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                EngineActivity engineActivity = this.b;
                switch (i) {
                    case 0:
                        int i9 = EngineActivity.C;
                        engineActivity.getClass();
                        Engine engine = new Engine();
                        F6.j jVar2 = new F6.j(engineActivity, engine, new F(2, engineActivity, engine));
                        engineActivity.z = jVar2;
                        jVar2.c();
                        break;
                    default:
                        SearchView searchView = engineActivity.A;
                        if (!searchView.S) {
                            searchView.setIconified(true);
                        } else {
                            engineActivity.finish();
                        }
                        break;
                }
            }
        });
        SearchView searchView = aVarInflate.g;
        this.A = searchView;
        W6.e eVar = new W6.e(this, searchView, new e0(24));
        p pVar = new p();
        ConstraintLayout constraintLayout = aVarInflate.b;
        pVar.c(constraintLayout);
        this.A.setOnSearchClickListener(new b(pVar, constraintLayout, eVar, 0));
        this.A.setOnCloseListener(new Y6.c(pVar, constraintLayout, eVar, 0));
        eVar.b("none", this.B);
        aVarInflate.f.setOnClickListener(new View.OnClickListener() { // from class: Y6.a

            /* JADX INFO: renamed from: b, reason: collision with root package name */
            public final /* synthetic */ EngineActivity b;

            {
                this.b = EngineActivity.this;   // OBF: 原为 jadx 误渲染的构造参数 this
            }

            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                EngineActivity engineActivity = this.b;
                switch (i8) {
                    case 0:
                        int i9 = EngineActivity.C;
                        engineActivity.getClass();
                        Engine engine = new Engine();
                        F6.j jVar2 = new F6.j(engineActivity, engine, new F(2, engineActivity, engine));
                        engineActivity.z = jVar2;
                        jVar2.c();
                        break;
                    default:
                        SearchView searchView2 = engineActivity.A;
                        if (!searchView2.S) {
                            searchView2.setIconified(true);
                        } else {
                            engineActivity.finish();
                        }
                        break;
                }
            }
        });
    }
}
