package org.nobody.multitts.ui.main.dialog;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.accessibility.AccessibilityManager;
import android.widget.SeekBar;
import com.google.common.base.Ascii;
import org.nobody.multitts.service.TTSService;
import b2.V;

/* JADX INFO: loaded from: D:\mtts_work\dex\classes3.dex */
public final class b implements SeekBar.OnSeekBarChangeListener {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final /* synthetic */ AccessibilityManager a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final /* synthetic */ Context b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final /* synthetic */ z6.a c;

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    public final /* synthetic */ V d;

    public b(V v8, AccessibilityManager accessibilityManager, Context context, z6.a aVar) {
        this.d = v8;
        this.a = accessibilityManager;
        this.b = context;
        this.c = aVar;
    }

    @Override // android.widget.SeekBar.OnSeekBarChangeListener
    public final void onProgressChanged(SeekBar seekBar, int i, boolean z5) {
        if (this.a.isTouchExplorationEnabled()) {
            onStopTrackingTouch(seekBar);
            if (seekBar.isAccessibilityFocused()) {
                seekBar.announceForAccessibility(this.b.getString(2131886471) + i);
            }
        }
    }

    @Override // android.widget.SeekBar.OnSeekBarChangeListener
    public final void onStartTrackingTouch(SeekBar seekBar) {
    }

    @Override // android.widget.SeekBar.OnSeekBarChangeListener
    public final void onStopTrackingTouch(SeekBar seekBar) {
        float progress = seekBar.getProgress() / 100.0f;
        this.c.e = progress;
        String strL = "music_volume_arg";
        SharedPreferences.Editor editorEdit = y6.a.a.edit();
        editorEdit.putFloat(strL, progress);
        editorEdit.apply();
        this.d.getClass();
        Context context = this.b;
        Intent intent = new Intent(context, (Class<?>) TTSService.class);
        intent.setAction("action_update_bgm_volume");
        context.startService(intent);
    }
}
