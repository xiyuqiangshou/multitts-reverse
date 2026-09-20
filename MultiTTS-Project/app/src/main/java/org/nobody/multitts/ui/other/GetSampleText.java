package org.nobody.multitts.ui.other;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import com.google.common.base.Ascii;
import h7.e;
import java.security.SecureRandom;
import java.util.Locale;
import o0.c;

/* JADX INFO: loaded from: D:\mtts_work\dex\classes3.dex */
public class GetSampleText extends Activity {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public static final int[] a = {2131890569, 2131890570, 2131890571, 2131890572, 2131890573};

    @Override // android.app.Activity
    public final void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        Intent intent = getIntent();
        String string = intent.getExtras().getString("language");
        String string2 = intent.getExtras().getString("country");
        String string3 = intent.getExtras().getString("variant");
        Intent intent2 = new Intent();
        if (string == null) {
            string = "";
        }
        if (string2 == null) {
            string2 = "";
        }
        if (string3 == null) {
            string3 = "";
        }
        intent2.putExtra("sampleText", (TextUtils.isEmpty(string) || e.i().getLanguage().equals(string)) ? getString(a[new SecureRandom().nextInt(5)]) : e.g(this, new Locale(string, string2, string3)));
        setResult(0, intent2);
        finish();
    }
}
