package org.nobody.multitts.ui.setting;

import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;
import androidx.preference.Preference;
import com.google.common.base.Ascii;
import com.google.common.primitives.UnsignedBytes;
import okio.Utf8;

/* JADX INFO: loaded from: D:\mtts_work\dex\classes3.dex */
public final class m implements w1.m {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final /* synthetic */ SettingActivity.a a;

    public m(SettingActivity.a aVar) {
        this.a = aVar;
    }

    @Override // w1.m
    public final void l(Preference preference) {
        SettingActivity.a aVar = this.a;
        if (!h7.e.q(aVar.L())) {
            Toast.makeText(aVar.L(), 2131886389, 0).show();
            return;
        }
        try {
            Intent intent = new Intent("android.intent.action.VIEW");
            intent.setData(Uri.parse("legado://import/httpTTS?src=http://localhost:8774/legado"));
            aVar.Q(intent);
        } catch (Exception unused) {
            // [R8 已移除的调用] 原字符串: "SettingsFragment"
            // [R8 已移除的调用] 原字符串: "onAddToLegado: "
            Toast.makeText(aVar.L(), 2131886400, 0).show();
        }
    }
}
