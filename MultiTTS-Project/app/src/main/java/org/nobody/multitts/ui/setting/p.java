package org.nobody.multitts.ui.setting;

import com.google.common.primitives.SignedBytes;
import java.io.Serializable;
import org.nobody.multitts.AppContext;

/* JADX INFO: loaded from: D:\mtts_work\dex\classes3.dex */
public final class p implements w1.l {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final /* synthetic */ z6.a a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final /* synthetic */ SettingActivity.a b;

    public p(SettingActivity.a aVar, z6.a aVar2) {
        this.b = aVar;
        this.a = aVar2;
    }

    @Override // w1.l
    public final boolean b(Serializable serializable) {
        String str = (String) serializable;
        if (!SettingActivity.checkRegex(this.b.L(), str)) {
            return false;
        }
        z6.a aVar = this.a;
        aVar.getClass();
        if (str == null) {
            str = "";
        }
        aVar.K = str;
        AppContext.putString("person_name_regex", str);
        return true;
    }
}
