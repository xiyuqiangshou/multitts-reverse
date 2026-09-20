package org.nobody.multitts.ui.other;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import com.google.common.base.Ascii;
import com.google.common.primitives.SignedBytes;
import com.google.common.primitives.UnsignedBytes;
import java.util.ArrayList;
import java.util.Collections;
import o0.c;
import z6.a;

/* JADX INFO: loaded from: D:\mtts_work\dex\classes3.dex */
public class CheckVoiceData extends Activity {
    @Override // android.app.Activity
    public final void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        Intent intent = new Intent();
        intent.putStringArrayListExtra("availableVoices", new ArrayList<>(a.a0.m));
        intent.putStringArrayListExtra("unavailableVoices", new ArrayList<>(Collections.singletonList("trr-PER")));
        setResult(1, intent);
        finish();
    }
}
