package org.nobody.multitts.plugin.httptts;

import H5.a;
import android.util.Base64;
import com.google.common.base.Ascii;
import com.google.common.primitives.SignedBytes;
import com.google.common.primitives.UnsignedBytes;
import kotlin.jvm.internal.i;
import okio.Utf8;
import o0.c;

/* JADX INFO: loaded from: D:\mtts_work\dex\classes3.dex */
public final class EncoderUtils {
    public static final EncoderUtils INSTANCE = new EncoderUtils();

    private EncoderUtils() {
    }

    public static String base64Decode$default(EncoderUtils encoderUtils, String str, int i, int i8, Object obj) {
        if ((i8 & 2) != 0) {
            i = 0;
        }
        return encoderUtils.base64Decode(str, i);
    }

    public static byte[] base64DecodeToByteArray$default(EncoderUtils encoderUtils, String str, int i, int i8, Object obj) {
        if ((i8 & 2) != 0) {
            i = 0;
        }
        return encoderUtils.base64DecodeToByteArray(str, i);
    }

    public static String base64Encode$default(EncoderUtils encoderUtils, String str, int i, int i8, Object obj) {
        if ((i8 & 2) != 0) {
            i = 2;
        }
        return encoderUtils.base64Encode(str, i);
    }

    public final String base64Decode(String str) {
        kotlin.jvm.internal.i.f(str, "str");
        return base64Decode$default(this, str, 0, 2, null);
    }

    public final byte[] base64DecodeToByteArray(String str) {
        kotlin.jvm.internal.i.f(str, "str");
        return base64DecodeToByteArray$default(this, str, 0, 2, null);
    }

    public final String base64Encode(String str) {
        kotlin.jvm.internal.i.f(str, "str");
        return base64Encode$default(this, str, 0, 2, null);
    }

    public final String escape(String str) {
        kotlin.jvm.internal.i.f(str, "src");
        StringBuilder sb = new StringBuilder();
        int length = str.length();
        for (int i = 0; i < length; i++) {
            char cCharAt = str.charAt(i);
            if (('0' > cCharAt || cCharAt >= ':') && (('A' > cCharAt || cCharAt >= '[') && 'a' <= cCharAt && cCharAt < '{')) {
                sb.append(cCharAt < 16 ? "%0" : cCharAt < 256 ? "%" : "%u");
                String string = Integer.toString(cCharAt, 16);
                kotlin.jvm.internal.i.e(string, "toString(this, checkRadix(radix))");
                sb.append(string);
            }
            sb.append(cCharAt);
        }
        String string2 = sb.toString();
        kotlin.jvm.internal.i.e(string2, "tmp.toString()");
        return string2;
    }

    public final String base64Decode(String str, int i) {
        kotlin.jvm.internal.i.f(str, "str");
        byte[] bArrDecode = Base64.decode(str, i);
        kotlin.jvm.internal.i.e(bArrDecode, "bytes");
        return new String(bArrDecode, a.a);
    }

    public final byte[] base64DecodeToByteArray(String str, int i) {
        kotlin.jvm.internal.i.f(str, "str");
        byte[] bArrDecode = Base64.decode(str, i);
        kotlin.jvm.internal.i.e(bArrDecode, "decode(str, flags)");
        return bArrDecode;
    }

    public final String base64Encode(String str, int i) {
        kotlin.jvm.internal.i.f(str, "str");
        byte[] bytes = str.getBytes(a.a);
        kotlin.jvm.internal.i.e(bytes, "this as java.lang.String).getBytes(charset)");
        return Base64.encodeToString(bytes, i);
    }
}
