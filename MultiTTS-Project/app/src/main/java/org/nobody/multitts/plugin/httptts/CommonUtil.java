package org.nobody.multitts.plugin.httptts;

import A5.a;
import com.google.common.base.Ascii;
import com.iflytek.tts.TtsService.Tts;
import java.util.BitSet;
import m5.b;
import m5.i;
import o0.c;

/* JADX INFO: loaded from: D:\mtts_work\dex\classes3.dex */
public final class CommonUtil {
    public static final CommonUtil INSTANCE = new CommonUtil();
    private static final b notNeedEncoding$delegate = new i(new a() { // from class: org.nobody.multitts.plugin.httptts.CommonUtil.1
        @Override // A5.a
        public final BitSet invoke() {
            BitSet bitSet = new BitSet(Tts.ivTTS_PARAM_LANGUAGE);
            for (int i = 97; i < 123; i++) {
                bitSet.set(i);
            }
            for (int i8 = 65; i8 < 91; i8++) {
                bitSet.set(i8);
            }
            for (int i9 = 48; i9 < 58; i9++) {
                bitSet.set(i9);
            }
            int length = "+-_.$:()!*@&#,[]".length();
            for (int i10 = 0; i10 < length; i10++) {
                bitSet.set("+-_.$:()!*@&#,[]".charAt(i10));
            }
            return bitSet;
        }
    });

    private CommonUtil() {
    }

    private final BitSet getNotNeedEncoding() {
        return (BitSet) notNeedEncoding$delegate.getValue();
    }

    private final boolean isDigit16Char(char c8) {
        if ('0' <= c8 && c8 < ':') {
            return true;
        }
        if ('A' > c8 || c8 >= 'G') {
            return 'a' <= c8 && c8 < 'g';
        }
        return true;
    }

    public final boolean hasUrlEncoded(String str) {
        int i;
        kotlin.jvm.internal.i.f(str, "str");
        boolean z5 = false;
        for (int i8 = 0; i8 < str.length(); i8++) {
            char cCharAt = str.charAt(i8);
            if (!getNotNeedEncoding().get(cCharAt)) {
                if (cCharAt == '%' && (i = i8 + 2) < str.length()) {
                    char cCharAt2 = str.charAt(i8 + 1);
                    char cCharAt3 = str.charAt(i);
                    if (isDigit16Char(cCharAt2)) {
                        isDigit16Char(cCharAt3);
                    }
                }
                z5 = true;
                break;
            }
        }
        return !z5;
    }
}
