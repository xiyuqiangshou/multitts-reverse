package org.nobody.multitts.ui.main.dialog;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;
import androidx.core.content.FileProvider;
import com.google.common.base.Ascii;
import java.io.File;
import org.nobody.multitts.tts.speaker.Speaker;

/* JADX INFO: loaded from: D:\mtts_work\dex\classes3.dex */
public final /* synthetic */ class f implements Runnable {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final /* synthetic */ int a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final /* synthetic */ Context b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final /* synthetic */ Speaker c;

    public /* synthetic */ f(Context context, Speaker speaker, int i) {
        this.a = i;
        this.b = context;
        this.c = speaker;
    }

    @Override // java.lang.Runnable
    public final void run() {
        Context context = this.b;
        Speaker speaker = this.c;
        int i = 1;
        switch (this.a) {
            case 0:
                Activity activity = (Activity) context;
                try {
                    if (!activity.isFinishing()) {
                        activity.runOnUiThread(new f(context, speaker, i));
                        h7.d.h(context.getExternalCacheDir());
                        File file = new File(context.getExternalCacheDir().getAbsolutePath(), speaker.group + "_" + speaker.code + ".zip");
                        h7.e.w(speaker, file);
                        Intent intent = new Intent("android.intent.action.SEND");
                        intent.putExtra("android.intent.extra.STREAM", FileProvider.d(context, "org.nobody.multitts", file));
                        intent.setType("application/zip");
                        intent.setFlags(268435456);
                        intent.addFlags(1);
                        context.startActivity(Intent.createChooser(intent, context.getString(2131886446)));
                    }
                } catch (Exception e8) {
                    h7.e.e("c", "exportSpeaker: ", e8);
                    if (activity.isFinishing()) {
                        return;
                    }
                    activity.runOnUiThread(new Z6.a(context, e8, 1));
                    return;
                }
                break;
            default:
                Toast.makeText(context, context.getString(2131886421, speaker.name), 0).show();
                break;
        }
    }
}
