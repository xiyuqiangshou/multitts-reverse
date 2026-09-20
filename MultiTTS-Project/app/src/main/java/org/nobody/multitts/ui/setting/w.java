package org.nobody.multitts.ui.setting;

import com.google.common.base.Ascii;
import com.google.common.primitives.UnsignedBytes;
import java.io.Serializable;
import org.nobody.multitts.AppContext;

/* JADX INFO: loaded from: D:\mtts_work\dex\classes3.dex */
public final class w implements w1.l {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final /* synthetic */ z6.a a;

    public w(z6.a aVar) {
        this.a = aVar;
    }

    @Override // w1.l
    public final boolean b(Serializable serializable) {
        boolean zBooleanValue = ((Boolean) serializable).booleanValue();
        this.a.s = zBooleanValue;
        AppContext.putBoolean("config_record_voice", zBooleanValue);
        return true;
    }
}
