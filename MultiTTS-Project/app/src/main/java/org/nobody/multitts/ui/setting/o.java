package org.nobody.multitts.ui.setting;

import android.content.Intent;
import androidx.preference.Preference;
import com.google.common.base.Ascii;
import java.util.HashMap;
import okio.Utf8;
import b.f;
import g0.F;
import g0.J;

/* JADX INFO: loaded from: D:\mtts_work\dex\classes3.dex */
public final class o implements w1.m {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final /* synthetic */ SettingActivity.a a;

    public o(SettingActivity.a aVar) {
        this.a = aVar;
    }

    @Override // w1.m
    public final void l(Preference preference) {
        // OBF: int i = SettingActivity.a.r0;   // jadx 坏代码: dex 中不存在 SettingActivity.a.r0; 该局部变量未被使用
        SettingActivity.a aVar = this.a;
        aVar.getClass();
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("text/plain");
        intent.addCategory("android.intent.category.OPENABLE");
        if (aVar.t == null) {
            throw new IllegalStateException("Fragment " + aVar + " not attached to Activity");
        }
        J jL = aVar.l();
        if (jL.B == null) {
            jL.v.getClass();
            throw new IllegalStateException("Starting activity with a requestCode requires a FragmentActivity host");
        }
        String str = aVar.e;
        F f3 = new F();
        f3.a = str;
        f3.b = 41;
        jL.E.addLast(f3);
        Y1.d dVar = jL.B;
        f fVar = (f) dVar.d;
        HashMap map = fVar.b;
        String str2 = (String) dVar.b;
        Integer num = (Integer) map.get(str2);
        g6.g gVar = (g6.g) dVar.c;
        if (num != null) {
            fVar.d.add(str2);
            try {
                fVar.b(num.intValue(), gVar, intent);
                return;
            } catch (Exception e8) {
                fVar.d.remove(str2);
                throw e8;
            }
        }
        throw new IllegalStateException("Attempting to launch an unregistered ActivityResultLauncher with contract " + gVar + " and input " + intent + ". You must ensure the ActivityResultLauncher is registered before calling launch().");
    }
}
