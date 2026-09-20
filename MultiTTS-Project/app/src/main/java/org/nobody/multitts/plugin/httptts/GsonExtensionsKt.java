package org.nobody.multitts.plugin.httptts;

import J1.c;
import U3.n;
import U3.o;
import com.google.common.base.Ascii;
import com.google.common.primitives.SignedBytes;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import jq.util.Objects;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import okio.Utf8;
import m5.b;
import m5.i;

/* JADX INFO: loaded from: D:\mtts_work\dex\classes3.dex */
public final class GsonExtensionsKt {
    private static final b INITIAL_GSON$delegate = new i(new c(6));
    private static final b GSON$delegate = new i(new c(7));

    /* JADX INFO: Add missing generic type declarations: [T] */
    /* JADX INFO: renamed from: org.nobody.multitts.plugin.httptts.GsonExtensionsKt$2, reason: invalid class name */
    public class AnonymousClass2<T> extends TypeToken<T> {
    }

    /* JADX INFO: Add missing generic type declarations: [T] */
    /* JADX INFO: renamed from: org.nobody.multitts.plugin.httptts.GsonExtensionsKt$3, reason: invalid class name */
    public class AnonymousClass3<T> extends TypeToken<T> {
    }

    /* JADX INFO: Add missing generic type declarations: [T] */
    /* JADX INFO: renamed from: org.nobody.multitts.plugin.httptts.GsonExtensionsKt$4, reason: invalid class name */
    public class AnonymousClass4<T> extends TypeToken<T> {
    }

    public static final <T> Object fromJsonArray(n nVar, String str) {
        kotlin.jvm.internal.i.f(nVar, "<this>");
        try {
            if (str == null) {
                throw new JsonSyntaxException("解析字符串为空");
            }
            // [R8 已移除的调用] 原字符串: "T"
            kotlin.jvm.internal.i.o();
            throw null;
        } catch (Throwable unused) {
            return null;
        }
    }

    public static final <T> Object fromJsonObject(n nVar, String str) {
        kotlin.jvm.internal.i.f(nVar, "<this>");
        try {
            if (str == null) {
                throw new JsonSyntaxException("解析字符串为空");
            }
            kotlin.jvm.internal.i.o();
            throw null;
        } catch (Throwable unused) {
            return null;
        }
    }

    public static final <T> Type genericType() {
        kotlin.jvm.internal.i.o();
        throw null;
    }

    public static final n getGSON() {
        Object value = GSON$delegate.getValue();
        kotlin.jvm.internal.i.e(value, "<get-GSON>(...)");
        return (n) value;
    }

    public static final n getINITIAL_GSON() {
        Object value = INITIAL_GSON$delegate.getValue();
        kotlin.jvm.internal.i.e(value, "<get-INITIAL_GSON>(...)");
        return (n) value;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static n a() {
        o oVar = new o();
        oVar.b(new TypeToken<Map<String, ? extends Object>>() { // from class: org.nobody.multitts.plugin.httptts.GsonExtensionsKt.1
        }.getType(), new MapDeserializerDoubleAsIntFix());
        oVar.b(Integer.TYPE, new IntJsonDeserializer());
        oVar.b(String.class, new StringJsonDeserializer());
        oVar.i = false;
        U3.i iVar = U3.i.e;
        Objects.requireNonNull(iVar);
        oVar.j = iVar;
        return oVar.a();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static n b() {
        n initial_gson = getINITIAL_GSON();
        initial_gson.getClass();
        return new o(initial_gson).a();
    }

    public static final void writeToOutputStream(n nVar, OutputStream outputStream, Object obj) throws IOException {
        kotlin.jvm.internal.i.f(nVar, "<this>");
        kotlin.jvm.internal.i.f(outputStream, "out");
        kotlin.jvm.internal.i.f(obj, "any");
        b4.b bVar = new b4.b(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
        U3.i iVar = U3.i.e;
        bVar.H(new U3.i(iVar.a, "  ", iVar.c));
        if (obj instanceof List) {
            bVar.s();
            for (Object obj2 : (Iterable) obj) {
                if (obj2 != null) {
                    nVar.i(obj2, obj2.getClass(), bVar);
                }
            }
            bVar.x();
        } else {
            nVar.i(obj, obj.getClass(), bVar);
        }
        bVar.close();
    }

    public static final <T> Object fromJsonObject(n nVar, InputStream inputStream) {
        kotlin.jvm.internal.i.f(nVar, "<this>");
        try {
            if (inputStream == null) {
                throw new JsonSyntaxException("解析流为空");
            }
            new InputStreamReader(inputStream);
            kotlin.jvm.internal.i.o();
            throw null;
        } catch (Throwable unused) {
            return null;
        }
    }

    public static final <T> Object fromJsonArray(n nVar, InputStream inputStream) {
        kotlin.jvm.internal.i.f(nVar, "<this>");
        try {
            if (inputStream == null) {
                throw new JsonSyntaxException("解析流为空");
            }
            new InputStreamReader(inputStream);
            // [R8 已移除的调用] 原字符串: "T"
            kotlin.jvm.internal.i.o();
            throw null;
        } catch (Throwable unused) {
            return null;
        }
    }
}
