package org.nobody.multitts.ui.other;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import com.google.common.base.Ascii;
import h7.e;
import h7.r;
import okio.Utf8;
import o0.c;
import w2.a;

/* JADX INFO: loaded from: D:\mtts_work\dex\classes3.dex */
public class DownloadVoiceData extends Activity {
    @Override // android.app.Activity
    public final void onActivityResult(int i, int i8, Intent intent) {
        super.onActivityResult(i, i8, intent);
        if (i != 1 || intent == null) {
            finish();
        } else {
            e.k().execute(new r(this, intent, new a(this, 15)));
        }
    }

    @Override // android.app.Activity
    public final void onCreate(Bundle bundle) {
        requestWindowFeature(1);
        super.onCreate(bundle);
        Intent intent = getIntent();
        if (intent != null && "android.intent.action.VIEW".equals(intent.getAction())) {
            e.k().execute(new r(this, intent, new d5.a(this, 18)));
            return;
        }
        Intent intent2 = new Intent("android.intent.action.GET_CONTENT");
        intent2.setType("application/zip");
        intent2.addCategory("android.intent.category.OPENABLE");
        startActivityForResult(intent2, 1);
    }
}
