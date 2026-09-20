package org.nobody.multitts.ui.setting;

import android.content.Context;
import android.content.Intent;
import com.google.common.base.Ascii;
import com.google.common.primitives.SignedBytes;
import com.google.common.primitives.UnsignedBytes;
import java.io.Serializable;
import org.nobody.multitts.service.TTSService;

/* JADX INFO: loaded from: D:\mtts_work\dex\classes3.dex */
public final class t implements w1.l {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final /* synthetic */ z6.a a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final /* synthetic */ SettingActivity.a b;

    public t(SettingActivity.a aVar, z6.a aVar2) {
        this.b = aVar;
        this.a = aVar2;
    }

    @Override // w1.l
    public final boolean b(Serializable serializable) {
        boolean zBooleanValue = ((Boolean) serializable).booleanValue();
        if (!zBooleanValue) {
            // OBF: int i = SettingActivity.a.r0;   // jadx 坏代码: dex 中不存在 SettingActivity.a.r0; 该局部变量未被使用
            Context contextL = this.b.L();
            Intent intent = new Intent(contextL, (Class<?>) TTSService.class);
            intent.setAction("action_close_notify");
            contextL.startService(intent);
        }
        this.a.g(zBooleanValue);
        return true;
    }
}
