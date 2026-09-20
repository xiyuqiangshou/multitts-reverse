package org.nobody.multitts.tts.jni;

import B.i;
import com.google.common.base.Ascii;
import com.google.common.primitives.SignedBytes;
import h7.d;
import java.io.File;
import java.util.LinkedHashMap;
import org.nobody.multitts.AppContext;
import o0.c;
import z6.a;

/* JADX INFO: loaded from: D:\mtts_work\dex\classes3.dex */
public class Ngr {

    /* 同 Ner：dex 字段带 final 却在构造函数里被赋值两次（0L + init(...)），
       javac 禁止 final 二次赋值 -> 去掉 final，保留两次赋值。 */
    public long a;

    static {
        System.loadLibrary("ngr");
    }

    public Ngr() {
        this.a = 0L;
        StringBuilder sb = new StringBuilder();
        sb.append(AppContext.getContext().getExternalFilesDir("data"));
        String strQ = i.q(new byte[]{10, 62, 9, -91}, new byte[]{37, 80, 110, -41, 1, 19, -49, 108}, sb);
        File file = new File(strQ, "ngr_cn_model.onnx");
        File file2 = new File(strQ, "ngr_cn_word.txt");
        z6.a aVar = z6.a.a0;
        if (aVar.b()) {
            d.c(AppContext.getContext(), "model", strQ);
            aVar.l();
        } else if (!file.exists() || !file2.exists()) {
            d.c(AppContext.getContext(), "model", strQ);
        }
        this.a = init(file.getAbsolutePath(), file2.getAbsolutePath());
    }

    public static LinkedHashMap a(String str) {
        return discovery(str, 2, 5, 1000, "");
    }

    private static native LinkedHashMap<String, Integer> discovery(String str, int i, int i8, int i9, String str2);

    private native long init(String str, String str2);

    private native int predict(long j2, String str);

    private native void release(long j2);

    public final int b(String str) {
        return predict(this.a, str);
    }

    public final void c() {
        long j2 = this.a;
        if (j2 != 0) {
            release(j2);
        }
    }
}
