package org.nobody.multitts.ui.setting;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.PowerManager;
import com.google.common.base.Ascii;
import java.io.Serializable;
import okio.Utf8;

/* JADX INFO: loaded from: D:\mtts_work\dex\classes3.dex */
public final class k implements w1.l {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final /* synthetic */ SettingActivity.a a;

    public k(SettingActivity.a aVar) {
        this.a = aVar;
    }

    @Override // w1.l
    public final boolean b(Serializable serializable) {
        SettingActivity.a aVar = this.a;
        aVar.getClass();
        if (((Boolean) serializable).booleanValue()) {
            Context contextL = aVar.L();
            Intent intent = new Intent();
            String packageName = contextL.getPackageName();
            PowerManager powerManager = (PowerManager) contextL.getSystemService("power");
            if (Build.VERSION.SDK_INT < 23 || !powerManager.isIgnoringBatteryOptimizations(packageName)) {
                intent.setAction("android.settings.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS");
                intent.setData(Uri.parse("package:" + packageName));
                aVar.Q(intent);
            } else {
                intent.setAction("android.settings.IGNORE_BATTERY_OPTIMIZATION_SETTINGS");
            }
        }
        return true;
    }
}
