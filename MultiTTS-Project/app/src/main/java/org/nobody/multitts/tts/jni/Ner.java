package org.nobody.multitts.tts.jni;

import B.i;
import S6.b;
import com.google.common.base.Ascii;
import com.google.common.primitives.SignedBytes;
import h7.d;
import java.io.File;
import java.util.ArrayList;
import org.nobody.multitts.AppContext;
import o0.c;
import z6.a;

/* JADX INFO: loaded from: D:\mtts_work\dex\classes3.dex */
public class Ner {

    /* smali 事实：构造函数里对 a 有两次赋值（`const-wide/16 v1, 0x0` + `iput-wide`，
       末尾 `init(...)` 后再一次 `iput-wide`），但 dex 字段标志带 final ——
       javac 不允许 final 字段二次赋值，R8 的「字段 final 化」在这里过于激进。
       保留两次赋值、去掉 final，运行时行为不变。 */
    public long a;

    static {
        System.loadLibrary("ngr");
    }

    public Ner() {
        this.a = 0L;
        StringBuilder sb = new StringBuilder();
        sb.append(AppContext.getContext().getExternalFilesDir("data"));
        String strQ = i.q(new byte[]{-84, -98, Ascii.ETB, -25}, new byte[]{-125, -16, 112, -107, 120, -22, -109, 4}, sb);
        File file = new File(strQ, "ner_cn_model.onnx");
        File file2 = new File(strQ, "cn_vocab.txt");
        z6.a aVar = z6.a.a0;
        if (aVar.b()) {
            d.c(AppContext.getContext(), "model", strQ);
            aVar.l();
        } else if (!file.exists() || !file2.exists()) {
            d.c(AppContext.getContext(), "model", strQ);
        }
        this.a = init(file.getAbsolutePath(), file2.getAbsolutePath());
    }

    private native long init(String str, String str2);

    private native ArrayList<String> recognize(long j2, String str);

    private native void release(long j2);

    public final ArrayList a(String str) {
        return recognize(this.a, b.c(str));
    }

    public final void b() {
        long j2 = this.a;
        if (j2 != 0) {
            release(j2);
        }
    }
}
