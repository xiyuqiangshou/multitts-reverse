package org.nobody.multitts.plugin.httptts;

import U3.q;
import U3.r;
import U3.s;
import U3.v;
import com.google.common.base.Ascii;
import com.google.common.primitives.UnsignedBytes;
import java.lang.reflect.Type;
import kotlin.jvm.internal.i;
import o0.c;

/* JADX INFO: loaded from: D:\mtts_work\dex\classes3.dex */
public final class IntJsonDeserializer implements r {
    @Override // U3.r
    public Integer deserialize(s sVar, Type type, q qVar) {
        i.f(sVar, "json");
        if (sVar instanceof v) {
            v vVarA = sVar.a();
            if (vVarA.a instanceof Number) {
                return Integer.valueOf(vVarA.e().intValue());
            }
        }
        return null;
    }
}
