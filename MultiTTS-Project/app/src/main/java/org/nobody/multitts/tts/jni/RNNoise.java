package org.nobody.multitts.tts.jni;

import com.google.common.base.Ascii;
import o0.c;

/* JADX INFO: loaded from: D:\mtts_work\dex\classes3.dex */
public class RNNoise {
    static {
        System.loadLibrary("rnnoise");
    }

    private native void destroy(long j2);

    private native long init();

    private native float[] process(long j2, float[] fArr);
}
