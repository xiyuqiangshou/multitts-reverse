package org.nobody.multitts.plugin.httptts;

import U3.p;
import U3.q;
import U3.r;
import U3.s;
import U3.u;
import U3.v;
import W3.l;
import W3.m;
import W3.n;
import W3.o;
import com.google.common.base.Ascii;
import com.google.common.primitives.SignedBytes;
import com.google.gson.JsonParseException;
import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import kotlin.jvm.internal.i;
import o0.c;

/* JADX INFO: loaded from: D:\mtts_work\dex\classes3.dex */
public final class MapDeserializerDoubleAsIntFix implements r {
    public final Object read(s sVar) {
        i.f(sVar, "json");
        boolean z5 = sVar instanceof p;
        if (z5) {
            ArrayList arrayList = new ArrayList();
            if (!z5) {
                throw new IllegalStateException("Not a JSON Array: " + sVar);
            }
            // U3.p.a 的声明类型是原始 ArrayList（R8 抹掉泛型），直接 for-each 元素会退化成 Object。
            for (s sVar2 : (ArrayList<s>) ((p) sVar).a) {
                i.e(sVar2, "anArr");
                arrayList.add(read(sVar2));
            }
            return arrayList;
        }
        boolean z8 = sVar instanceof u;
        if (!z8) {
            if (sVar instanceof v) {
                v vVarA = sVar.a();
                Serializable serializable = vVarA.a;
                if (serializable instanceof Boolean) {
                    return Boolean.valueOf(vVarA.d());
                }
                if (serializable instanceof String) {
                    return vVarA.b();
                }
                if (serializable instanceof Number) {
                    Number numberE = vVarA.e();
                    i.e(numberE, "prim.asNumber");
                    return Math.ceil(numberE.doubleValue()) == ((double) numberE.longValue()) ? Long.valueOf(numberE.longValue()) : Double.valueOf(numberE.doubleValue());
                }
            }
            return null;
        }
        o oVar = new o(true);
        if (!z8) {
            throw new IllegalStateException("Not a JSON Object: " + sVar);
        }
        Iterator it = ((m) ((u) sVar).a.entrySet()).iterator();
        while (((l) it).hasNext()) {
            n nVarB = ((l) it).b();
            // [R8 已移除的调用] 原字符串: "entitySet"
            String str = (String) nVarB.getKey();
            s sVar3 = (s) nVarB.getValue();
            i.e(str, "key");
            i.e(sVar3, "value");
            oVar.put(str, read(sVar3));
        }
        return oVar;
    }

    @Override // U3.r
    public Map<String, Object> deserialize(s sVar, Type type, q qVar) throws JsonParseException {
        i.f(sVar, "jsonElement");
        i.f(type, "type");
        i.f(qVar, "jsonDeserializationContext");
        Object obj = read(sVar);
        if (obj instanceof Map) {
            return (Map) obj;
        }
        return null;
    }
}
