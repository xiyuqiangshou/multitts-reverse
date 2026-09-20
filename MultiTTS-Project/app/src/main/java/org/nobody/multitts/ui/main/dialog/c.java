package org.nobody.multitts.ui.main.dialog;

import Y6.j;
import java.io.File;


import z1.k0;

/* JADX INFO: loaded from: D:\mtts_work\dex\classes3.dex */
public final class c extends z1.w {

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    public final /* synthetic */ j d;

    public c(j jVar) {
        this.d = jVar;
    }

    @Override // z1.w
    public final int d(k0 k0Var) {
        return z1.w.f(0);
    }

    @Override // z1.w
    public final boolean h(k0 k0Var, k0 k0Var2) {
        return false;
    }

    @Override // z1.w
    public final void k(k0 k0Var) {
        int iC = k0Var.c();
        j jVar = this.d;
        File file = (File) jVar.f.get(iC);
        B4.a aVar = new B4.a(jVar.e, 2131952281);
        String string = jVar.e.getString(2131886474);
        h.g c0307g = (h.g) aVar.c;
        c0307g.d = string;
        c0307g.f = jVar.e.getString(2131886371, file.getName());
        aVar.o(jVar.e.getString(2131886359), new Y6.d(jVar, iC, 1));
        aVar.p(jVar.e.getString(2131886365), new Y6.f(jVar, iC, file, 1));
        aVar.r();
    }
}
