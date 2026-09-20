package org.nobody.multitts.plugin.httptts;

import H5.l;
import H5.n;
import H5.u;
import android.net.Uri;
import android.text.Editable;
import com.google.common.base.Ascii;
import com.google.common.primitives.SignedBytes;
import com.google.common.primitives.UnsignedBytes;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;
import kotlin.jvm.internal.i;
import okio.Utf8;
import o0.c;

/* JADX INFO: loaded from: D:\mtts_work\dex\classes3.dex */
public final class StringExtensionsKt {
    public static final boolean isAbsUrl(String str) {
        if (str != null) {
            return u.N(str, "http://", true) || u.N(str, "https://", true);
        }
        return false;
    }

    public static final boolean isChinese(String str) {
        kotlin.jvm.internal.i.f(str, "<this>");
        return Pattern.compile("[一-龥]").matcher(str).find();
    }

    public static final boolean isContentScheme(String str) {
        return str != null && str.startsWith("content://");
    }

    public static final boolean isJson(String str) {
        if (str == null) {
            return false;
        }
        String string = n.j0(str).toString();
        if (string.startsWith("{") && string.endsWith("}")) {
            return true;
        }
        return string.startsWith("[") && string.endsWith("]");
    }

    public static final boolean isJsonArray(String str) {
        if (str == null) {
            return false;
        }
        String string = n.j0(str).toString();
        return string.startsWith("[") && string.endsWith("]");
    }

    public static final boolean isJsonObject(String str) {
        if (str == null) {
            return false;
        }
        String string = n.j0(str).toString();
        return string.startsWith("{") && string.endsWith("}");
    }

    public static final boolean isTrue(String str, boolean z5) {
        if (str == null || n.X(str) || str.equals("null")) {
            return z5;
        }
        Pattern patternCompile = Pattern.compile("(?i)^(false|no|not|0)$");
        kotlin.jvm.internal.i.e(patternCompile, "compile(...)");
        String input = n.j0(str).toString();
        kotlin.jvm.internal.i.f(input, "input");
        return !patternCompile.matcher(input).matches();
    }

    public static boolean isTrue$default(String str, boolean z5, int i, Object obj) {
        if ((i & 1) != 0) {
            z5 = false;
        }
        return isTrue(str, z5);
    }

    public static final boolean isUri(String str) {
        if (str == null) {
            return false;
        }
        return u.N(str, "file://", true) || isContentScheme(str);
    }

    public static final boolean isXml(String str) {
        if (str == null) {
            return false;
        }
        String string = n.j0(str).toString();
        return string.startsWith("<") && string.endsWith(">");
    }

    public static final int memorySize(String str) {
        if (str == null) {
            return 0;
        }
        return (str.length() * 2) + 40;
    }

    public static final Uri parseToUri(String str) {
        Uri uriFromFile;
        String strL;
        kotlin.jvm.internal.i.f(str, "<this>");
        if (isUri(str)) {
            uriFromFile = Uri.parse(str);
            strL = "parse(this)";
        } else {
            uriFromFile = Uri.fromFile(new File(str));
            strL = "{\n        Uri.fromFile(File(this))\n    }";
        }
        kotlin.jvm.internal.i.e(uriFromFile, strL);
        return uriFromFile;
    }

    public static final String safeTrim(String str) {
        if (str == null || n.X(str)) {
            return null;
        }
        return n.j0(str).toString();
    }

    public static final String[] splitNotBlank(String str, String[] strArr, int i) {
        kotlin.jvm.internal.i.f(str, "<this>");
        kotlin.jvm.internal.i.f(strArr, "delimiter");
        StringBuilder sb = new StringBuilder();
        for (String str2 : strArr) {
            sb.append(str2);
            sb.append('|');
        }
        sb.deleteCharAt(sb.length() - 1);
        List listAsList = Arrays.asList(str.split(sb.toString(), i));
        ArrayList arrayList = new ArrayList(listAsList);
        Iterator it = listAsList.iterator();
        while (it.hasNext()) {
            arrayList.add(n.j0((String) it.next()).toString());
        }
        ArrayList arrayList2 = new ArrayList();
        for (Object obj : arrayList) {
            if (!n.X((String) obj)) {
                arrayList2.add(obj);
            }
        }
        return (String[]) arrayList2.toArray(new String[0]);
    }

    public static String[] splitNotBlank$default(String str, String[] strArr, int i, int i8, Object obj) {
        if ((i8 & 2) != 0) {
            i = 0;
        }
        return splitNotBlank(str, strArr, i);
    }

    public static final Editable toEditable(String str) {
        kotlin.jvm.internal.i.f(str, "<this>");
        Editable editableNewEditable = Editable.Factory.getInstance().newEditable(str);
        kotlin.jvm.internal.i.e(editableNewEditable, "getInstance().newEditable(this)");
        return editableNewEditable;
    }

    public static String[] toStringArray(CharSequence charSequence) {
        kotlin.jvm.internal.i.f(charSequence, "<this>");
        try {
            int i = 0;
            int iCodePointCount = Character.codePointCount(charSequence, 0, charSequence.length());
            String[] strArr = new String[iCodePointCount];
            int i8 = 0;
            while (i < iCodePointCount) {
                int iOffsetByCodePoints = Character.offsetByCodePoints(charSequence, i8, 1);
                strArr[i] = charSequence.subSequence(i8, iOffsetByCodePoints).toString();
                i++;
                i8 = iOffsetByCodePoints;
            }
            return strArr;
        } catch (Exception unused) {
            return charSequence.toString().split("");
        }
    }

    public static String[] splitNotBlank$default(String str, l lVar, int i, int i8, Object obj) {
        if ((i8 & 2) != 0) {
            i = 0;
        }
        return splitNotBlank(str, lVar, i);
    }

    public static final String[] splitNotBlank(String str, l lVar, int i) {
        kotlin.jvm.internal.i.f(str, "<this>");
        kotlin.jvm.internal.i.f(lVar, "regex");
        List listD = lVar.d(str, i);
        ArrayList arrayList = new ArrayList(listD);
        Iterator it = listD.iterator();
        while (it.hasNext()) {
            arrayList.add(n.j0((String) it.next()).toString());
        }
        ArrayList arrayList2 = new ArrayList();
        for (Object obj : arrayList) {
            if (!n.X((String) obj)) {
                arrayList2.add(obj);
            }
        }
        return (String[]) arrayList2.toArray(new String[0]);
    }
}
