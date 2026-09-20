package org.nobody.multitts.ui.setting;

import com.google.common.base.Ascii;
import java.io.Serializable;
import org.nobody.multitts.AppContext;

/* JADX INFO: loaded from: D:\mtts_work\dex\classes3.dex */
public final class i implements w1.l {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final /* synthetic */ z6.a a;

    public i(z6.a aVar) {
        this.a = aVar;
    }

    @Override // w1.l
    public final boolean b(Serializable serializable) {
        boolean zBooleanValue = ((Boolean) serializable).booleanValue();
        this.a.H = zBooleanValue;
        AppContext.putBoolean("config_filter_language", zBooleanValue);
        return true;
    }
}
