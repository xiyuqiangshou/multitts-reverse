package org.nobody.multitts.tts.jni;

import okio.Utf8;
import o0.c;

/* JADX INFO: loaded from: D:\mtts_work\dex\classes3.dex */
public class SpeexBridge {
    static {
        System.loadLibrary("speexdsp");
    }

    public static native int code2id(String str);

    public static native void denoiseDestroy();

    public static native void denoiseInit(int i, int i8);

    public static native byte[] denoiseProcess(byte[] bArr);

    public static native void denoiseSetParam(int i, int i8, int i9);

    public static native String getLicense(int i);

    public static native String init();

    public static native void release();

    public static native void resampleDestroy();

    public static native void resampleInit(int i, int i8, int i9);

    public static native byte[] resampleProcess(byte[] bArr);
}
