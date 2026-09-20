package org.nobody.multitts.plugin.httptts;

import A5.l;
import com.google.common.base.Ascii;
import com.google.common.primitives.SignedBytes;
import com.google.common.primitives.UnsignedBytes;
import java.util.ArrayList;
import java.util.Arrays;
import kotlin.jvm.internal.e;
import kotlin.jvm.internal.i;
import okio.Utf8;
import n5.j;
import n5.k;
import o0.c;

/* JADX INFO: loaded from: D:\mtts_work\dex\classes3.dex */
public final class RuleAnalyzer {
    public static final Companion Companion = new Companion(null);
    private static final char ESC = '\\';
    private final BooleanFunc chompBalanced;
    private String elementsType;
    private int pos;
    private String queue;
    private ArrayList<String> rule;
    private int start;
    private int startX;
    private int step;

    public static final class Companion {
        public Companion(e eVar) {
            this();
        }

        private Companion() {
        }
    }

    public RuleAnalyzer(String str, boolean z5) {
        kotlin.jvm.internal.i.f(str, "data");
        this.queue = str;
        this.rule = new ArrayList<>();
        this.elementsType = "";
        this.chompBalanced = z5 ? new RuleAnalyzer$chompBalanced$1(this) : new RuleAnalyzer$chompBalanced$2(this);
    }

    private final boolean consumeTo(String str) {
        int i = this.pos;
        this.start = i;
        int iIndexOf = this.queue.indexOf(str, i);
        if (iIndexOf == -1) {
            return false;
        }
        this.pos = iIndexOf;
        return true;
    }

    private final boolean consumeToAny(String... strArr) {
        for (int i = this.pos; i != this.queue.length(); i++) {
            for (String str : strArr) {
                if (this.queue.regionMatches(i, str, 0, str.length())) {
                    this.step = str.length();
                    this.pos = i;
                    return true;
                }
            }
        }
        return false;
    }

    private final int findToAny(char... cArr) {
        for (int i = this.pos; i != this.queue.length(); i++) {
            for (char c8 : cArr) {
                if (this.queue.charAt(i) == c8) {
                    return i;
                }
            }
        }
        return -1;
    }

    public static String innerRule$default(RuleAnalyzer ruleAnalyzer, String str, int i, int i8, l lVar, int i9, Object obj) {
        if ((i9 & 2) != 0) {
            i = 1;
        }
        if ((i9 & 4) != 0) {
            i8 = 1;
        }
        return ruleAnalyzer.innerRule(str, i, i8, lVar);
    }

    private final ArrayList<String> splitRuleNext() {
        int i;
        int i8 = this.pos;
        this.pos = this.start;
        while (true) {
            int iFindToAny = findToAny('[', '(');
            if (iFindToAny == -1) {
                String strSubstring = this.queue.substring(this.startX, i8);
                kotlin.jvm.internal.i.e(strSubstring, "this as java.lang.String…ing(startIndex, endIndex)");
                ArrayList<String> arrayList = this.rule;
                kotlin.jvm.internal.i.f(arrayList, "<this>");
                arrayList.addAll(j.e0(new String[]{strSubstring}));
                this.pos = this.step + i8;
                while (consumeTo(this.elementsType)) {
                    String strSubstring2 = this.queue.substring(this.start, this.pos);
                    kotlin.jvm.internal.i.e(strSubstring2, "this as java.lang.String…ing(startIndex, endIndex)");
                    this.rule.add(strSubstring2);
                    this.pos += this.step;
                }
                String strSubstring3 = this.queue.substring(this.pos);
                kotlin.jvm.internal.i.e(strSubstring3, "this as java.lang.String).substring(startIndex)");
                this.rule.add(strSubstring3);
                return this.rule;
            }
            if (iFindToAny > i8) {
                String strSubstring4 = this.queue.substring(this.startX, i8);
                kotlin.jvm.internal.i.e(strSubstring4, "this as java.lang.String…ing(startIndex, endIndex)");
                k.N(this.rule, n5.l.F(strSubstring4));
                this.pos = this.step + i8;
                while (consumeTo(this.elementsType) && (i = this.pos) < iFindToAny) {
                    String strSubstring5 = this.queue.substring(this.start, i);
                    kotlin.jvm.internal.i.e(strSubstring5, "this as java.lang.String…ing(startIndex, endIndex)");
                    this.rule.add(strSubstring5);
                    this.pos += this.step;
                }
                int i9 = this.pos;
                if (i9 <= iFindToAny) {
                    String strSubstring6 = this.queue.substring(i9);
                    kotlin.jvm.internal.i.e(strSubstring6, "this as java.lang.String).substring(startIndex)");
                    this.rule.add(strSubstring6);
                    return this.rule;
                }
                this.startX = this.start;
            } else {
                this.pos = iFindToAny;
                if (!this.chompBalanced.invoke(this.queue.charAt(this.pos), this.queue.charAt(iFindToAny) == '[' ? ']' : ')').booleanValue()) {
                    String strSubstring7 = this.queue.substring(0, this.start);
                    kotlin.jvm.internal.i.e(strSubstring7, "this as java.lang.String…ing(startIndex, endIndex)");
                    throw new Error(strSubstring7.concat("后未平衡"));
                }
                int i10 = this.pos;
                if (i8 <= i10) {
                    this.start = i10;
                    if (!consumeTo(this.elementsType)) {
                        String strSubstring8 = this.queue.substring(this.startX);
                        kotlin.jvm.internal.i.e(strSubstring8, "this as java.lang.String).substring(startIndex)");
                        this.rule.add(strSubstring8);
                        return this.rule;
                    }
                } else {
                    continue;
                }
            }
        }
    }

    public final boolean chompCodeBalanced(char c8, char c9) {
        int i = this.pos;
        boolean z5 = false;
        boolean z8 = false;
        int i8 = 0;
        int i9 = 0;
        while (i != this.queue.length()) {
            int i10 = i + 1;
            char cCharAt = this.queue.charAt(i);
            if (cCharAt != '\\') {
                if (cCharAt == '\'' && !z5) {
                    z8 = !z8;
                } else if (cCharAt == '\"' && !z8) {
                    z5 = !z5;
                }
                if (!z8 && !z5) {
                    if (cCharAt == '[') {
                        i8++;
                    } else if (cCharAt == ']') {
                        i8--;
                    } else if (i8 == 0) {
                        if (cCharAt == c8) {
                            i9++;
                        } else if (cCharAt == c9) {
                            i9--;
                        }
                    }
                }
                i = i10;
            } else {
                i += 2;
            }
            if (i8 <= 0 && i9 <= 0) {
                break;
            }
        }
        if (i8 > 0 || i9 > 0) {
            return false;
        }
        this.pos = i;
        return true;
    }

    public final boolean chompRuleBalanced(char c8, char c9) {
        int i = this.pos;
        boolean z5 = false;
        boolean z8 = false;
        int i8 = 0;
        while (i != this.queue.length()) {
            int i9 = i + 1;
            char cCharAt = this.queue.charAt(i);
            if (cCharAt == '\'' && !z5) {
                z8 = !z8;
            } else if (cCharAt == '\"' && !z8) {
                z5 = !z5;
            }
            if (!z8 && !z5) {
                if (cCharAt != '\\') {
                    if (cCharAt != c8) {
                        if (cCharAt != c9) {
                            if (i8 <= 0) {
                                break;
                            }
                        } else {
                            i8--;
                        }
                    } else {
                        i8++;
                    }
                } else {
                    i += 2;
                }
            }
            i = i9;
        }
        if (i8 > 0) {
            return false;
        }
        this.pos = i;
        return true;
    }

    public final F5.e getChompBalanced() {
        return null;
    }

    public final String getElementsType() {
        return this.elementsType;
    }

    public final String innerRule(String str, int i, int i8, l lVar) {
        kotlin.jvm.internal.i.f(str, "inner");
        kotlin.jvm.internal.i.f(lVar, "fr");
        StringBuilder sb = new StringBuilder();
        while (consumeTo(str)) {
            int i9 = this.pos;
            if (chompCodeBalanced('{', '}')) {
                String strSubstring = this.queue.substring(i9 + i, this.pos - i8);
                kotlin.jvm.internal.i.e(strSubstring, "this as java.lang.String…ing(startIndex, endIndex)");
                String str2 = (String) lVar.invoke(strSubstring);
                if (str2 != null && str2.length() != 0) {
                    String strSubstring2 = this.queue.substring(this.startX, i9);
                    kotlin.jvm.internal.i.e(strSubstring2, "this as java.lang.String…ing(startIndex, endIndex)");
                    sb.append(strSubstring2.concat(str2));
                    this.startX = this.pos;
                }
            }
            this.pos = str.length() + this.pos;
        }
        int i10 = this.startX;
        if (i10 == 0) {
            return "";
        }
        String strSubstring3 = this.queue.substring(i10);
        kotlin.jvm.internal.i.e(strSubstring3, "this as java.lang.String).substring(startIndex)");
        sb.append(strSubstring3);
        String string = sb.toString();
        kotlin.jvm.internal.i.e(string, "st.apply {\n            a…tX))\n        }.toString()");
        return string;
    }

    public final void reSetPos() {
        this.pos = 0;
        this.startX = 0;
    }

    public final void setElementsType(String str) {
        kotlin.jvm.internal.i.f(str, "<set-?>");
        this.elementsType = str;
    }

    public final ArrayList<String> splitRule(String... strArr) {
        int i;
        int i8;
        kotlin.jvm.internal.i.f(strArr, "split");
        while (strArr.length != 1) {
            if (!consumeToAny((String[]) Arrays.copyOf(strArr, strArr.length))) {
                String strSubstring = this.queue.substring(this.startX);
                kotlin.jvm.internal.i.e(strSubstring, "this as java.lang.String).substring(startIndex)");
                this.rule.add(strSubstring);
                return this.rule;
            }
            int i9 = this.pos;
            this.pos = this.start;
            do {
                int iFindToAny = findToAny('[', '(');
                if (iFindToAny == -1) {
                    String strSubstring2 = this.queue.substring(this.startX, i9);
                    kotlin.jvm.internal.i.e(strSubstring2, "this as java.lang.String…ing(startIndex, endIndex)");
                    this.rule = n5.l.F(strSubstring2);
                    String strSubstring3 = this.queue.substring(i9, this.step + i9);
                    kotlin.jvm.internal.i.e(strSubstring3, "this as java.lang.String…ing(startIndex, endIndex)");
                    this.elementsType = strSubstring3;
                    this.pos = this.step + i9;
                    while (consumeTo(this.elementsType)) {
                        String strSubstring4 = this.queue.substring(this.start, this.pos);
                        kotlin.jvm.internal.i.e(strSubstring4, "this as java.lang.String…ing(startIndex, endIndex)");
                        this.rule.add(strSubstring4);
                        this.pos += this.step;
                    }
                    String strSubstring5 = this.queue.substring(this.pos);
                    kotlin.jvm.internal.i.e(strSubstring5, "this as java.lang.String).substring(startIndex)");
                    this.rule.add(strSubstring5);
                    return this.rule;
                }
                if (iFindToAny > i9) {
                    String strSubstring6 = this.queue.substring(this.startX, i9);
                    kotlin.jvm.internal.i.e(strSubstring6, "this as java.lang.String…ing(startIndex, endIndex)");
                    this.rule = n5.l.F(strSubstring6);
                    String strSubstring7 = this.queue.substring(i9, this.step + i9);
                    kotlin.jvm.internal.i.e(strSubstring7, "this as java.lang.String…ing(startIndex, endIndex)");
                    this.elementsType = strSubstring7;
                    this.pos = this.step + i9;
                    while (consumeTo(this.elementsType) && (i = this.pos) < iFindToAny) {
                        String strSubstring8 = this.queue.substring(this.start, i);
                        kotlin.jvm.internal.i.e(strSubstring8, "this as java.lang.String…ing(startIndex, endIndex)");
                        this.rule.add(strSubstring8);
                        this.pos += this.step;
                    }
                    int i10 = this.pos;
                    if (i10 > iFindToAny) {
                        this.startX = this.start;
                        return splitRuleNext();
                    }
                    String strSubstring9 = this.queue.substring(i10);
                    kotlin.jvm.internal.i.e(strSubstring9, "this as java.lang.String).substring(startIndex)");
                    this.rule.add(strSubstring9);
                    return this.rule;
                }
                this.pos = iFindToAny;
                if (!this.chompBalanced.invoke(this.queue.charAt(this.pos), this.queue.charAt(iFindToAny) == '[' ? ']' : ')').booleanValue()) {
                    String strSubstring10 = this.queue.substring(0, this.start);
                    kotlin.jvm.internal.i.e(strSubstring10, "this as java.lang.String…ing(startIndex, endIndex)");
                    throw new Error(strSubstring10.concat("后未平衡"));
                }
                i8 = this.pos;
            } while (i9 > i8);
            this.start = i8;
            strArr = (String[]) Arrays.copyOf(strArr, strArr.length);
        }
        String str = strArr[0];
        this.elementsType = str;
        if (consumeTo(str)) {
            this.step = this.elementsType.length();
            return splitRuleNext();
        }
        String strSubstring11 = this.queue.substring(this.startX);
        kotlin.jvm.internal.i.e(strSubstring11, "this as java.lang.String).substring(startIndex)");
        this.rule.add(strSubstring11);
        return this.rule;
    }

    public final void trim() {
        if (this.queue.charAt(this.pos) != '@' && kotlin.jvm.internal.i.i(this.queue.charAt(this.pos), 33) >= 0) {
            return;
        }
        this.pos++;
        while (true) {
            if (this.queue.charAt(this.pos) != '@' && kotlin.jvm.internal.i.i(this.queue.charAt(this.pos), 33) >= 0) {
                int i = this.pos;
                this.start = i;
                this.startX = i;
                return;
            }
            this.pos++;
        }
    }

    public RuleAnalyzer(String str, boolean z5, int i, e eVar) {
        this(str, (i & 2) == 0 && z5);
    }

    public final String innerRule(String str, String str2, l lVar) {
        kotlin.jvm.internal.i.f(str, "startStr");
        kotlin.jvm.internal.i.f(str2, "endStr");
        kotlin.jvm.internal.i.f(lVar, "fr");
        StringBuilder sb = new StringBuilder();
        while (consumeTo(str)) {
            int length = str.length() + this.pos;
            this.pos = length;
            if (consumeTo(str2)) {
                String strSubstring = this.queue.substring(length, this.pos);
                kotlin.jvm.internal.i.e(strSubstring, "this as java.lang.String…ing(startIndex, endIndex)");
                String str3 = (String) lVar.invoke(strSubstring);
                String strSubstring2 = this.queue.substring(this.startX, length - str.length());
                kotlin.jvm.internal.i.e(strSubstring2, "this as java.lang.String…ing(startIndex, endIndex)");
                sb.append(strSubstring2 + str3);
                int length2 = str2.length() + this.pos;
                this.pos = length2;
                this.startX = length2;
            }
        }
        int i = this.startX;
        if (i == 0) {
            return this.queue;
        }
        String strSubstring3 = this.queue.substring(i);
        kotlin.jvm.internal.i.e(strSubstring3, "this as java.lang.String).substring(startIndex)");
        sb.append(strSubstring3);
        String string = sb.toString();
        kotlin.jvm.internal.i.e(string, "st.apply {\n            a…tX))\n        }.toString()");
        return string;
    }
}
