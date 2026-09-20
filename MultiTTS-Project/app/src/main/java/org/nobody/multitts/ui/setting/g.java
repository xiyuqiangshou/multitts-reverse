package org.nobody.multitts.ui.setting;

import java.io.Serializable;
import org.nobody.multitts.AppContext;

/* JADX INFO: loaded from: D:\mtts_work\dex\classes3.dex */
public final class g implements w1.l {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final /* synthetic */ z6.a a;

    public g(z6.a aVar) {
        this.a = aVar;
    }

    @Override // w1.l
    public final boolean b(Serializable serializable) {
        // OBF: int i = SettingActivity.a.r0;   // jadx 坏代码: dex 中不存在 SettingActivity.a.r0; 该局部变量未被使用
        boolean zBooleanValue = ((Boolean) serializable).booleanValue();
        this.a.E = zBooleanValue;
        AppContext.putBoolean("config_new_person_only", zBooleanValue);
        return true;
    }
}
