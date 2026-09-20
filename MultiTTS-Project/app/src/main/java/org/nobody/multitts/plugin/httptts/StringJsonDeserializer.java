package org.nobody.multitts.plugin.httptts;

import U3.q;
import U3.r;
import U3.s;
import U3.t;
import U3.v;
import com.google.common.base.Ascii;
import com.google.common.primitives.SignedBytes;
import java.lang.reflect.Type;
import kotlin.jvm.internal.i;
import o0.c;

/* JADX INFO: loaded from: D:\mtts_work\dex\classes3.dex */
public final class StringJsonDeserializer implements r {
    @Override // U3.r
    public String deserialize(s sVar, Type type, q qVar) {
        i.f(sVar, "json");
        i.f(type, "typeOfT");
        if (sVar instanceof v) {
            return sVar.b();
        }
        if (sVar instanceof t) {
            return null;
        }
        return sVar.toString();
    }
}
