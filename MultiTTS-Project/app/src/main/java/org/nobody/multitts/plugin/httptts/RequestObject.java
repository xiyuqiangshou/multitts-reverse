package org.nobody.multitts.plugin.httptts;

import A5.l;
import B.i;
import B2.b;
import H5.u;
import U3.n;
import android.os.SystemClock;
import android.text.TextUtils;
import com.google.common.base.Ascii;
import com.google.common.primitives.SignedBytes;
import com.google.common.primitives.UnsignedBytes;
import com.google.gson.reflect.TypeToken;
import com.iflytek.tts.TtsService.Tts;
import com.script.Bindings;
import com.script.ScriptEngine;
import com.script.rhino.RhinoScriptEngine;
import com.vivo.speechsdk.module.net.NetModule;
import h7.a;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import okio.Utf8;
import org.chromium.net.impl.CronetUrlRequest;
import org.chromium.net.impl.CronetUrlRequestContext;
import n5.j;
import o0.c;
import t6.d;
import w.e;
import w6.k;

/* JADX INFO: loaded from: D:\mtts_work\dex\classes3.dex */
public final class RequestObject {
    private static ScriptEngine jsEngine;
    private String body;
    private String bodyType;
    private String charset;
    private final Map<String, String> fieldMap;
    private Map<String, String> headerMap;
    private String method;
    private String queryStr;
    private String ruleUrl;
    private int timeout;
    private String type;
    private String url;
    private String urlNoQuery;
    public static final String TAG = "UrlParser";
    private static final Pattern paramPattern = Pattern.compile("\\s*,\\s*(?=\\{)");
    private static final Pattern JS_PATTERN = Pattern.compile("<js>([\\w\\W]*?)</js>|@js:([\\w\\W]*)", 2);
    private static final AtomicReference<Executor> executorRef = new AtomicReference<>();
    private static final AtomicReference<d> cronetEngineRef = new AtomicReference<>();

    public static class Java {
        static Java INSTANCE = new Java();

        private Java() {
        }

        public static String ajax(String str) {
            // [R8 已移除的调用] 原字符串: "UrlParser"
            // [R8 已移除的调用] 原字符串: "ajax: input="
            String str2 = new String(new RequestObject(str).request(), StandardCharsets.UTF_8);
            // [R8 已移除的调用] 原字符串: "UrlParser"
            // [R8 已移除的调用] 原字符串: "ajax: ret="
            return str2;
        }

        public static String encodeURI(String str) {
            try {
                return URLEncoder.encode(str, "UTF-8");
            } catch (Exception unused) {
                return "";
            }
        }

        public static String fetch(String str) {
            return ajax(str);
        }

        public static void log(String str) {
            String strL = "Rhino";
            if (a.a.F) {
                b bVar = new b();
                bVar.a = strL;
                bVar.a().C(4, str);
            }
        }

        public static void sleep(long j2) {
            SystemClock.sleep(j2);
        }

        public static String fetch(String str, String str2) {
            return ajax(str, str2);
        }

        public static String ajax(String str, String str2) {
            // [R8 已移除的调用] 原字符串: "UrlParser"
            // [R8 已移除的调用] 原字符串: "ajax: url="
            // [R8 已移除的调用] 原字符串: ", options="
            String str3 = new String(new RequestObject(i.r(new byte[]{-107}, new byte[]{-71, -102, Ascii.ETB, -111, 116, -34, -95, -96}, e.a(str), str2)).request(), StandardCharsets.UTF_8);
            // [R8 已移除的调用] 原字符串: "UrlParser"
            // [R8 已移除的调用] 原字符串: "ajax: ret="
            return str3;
        }

        public static void log(String str, String str2) {
            if (a.a.F) {
                b bVar = new b();
                bVar.a = str;
                bVar.a().C(4, str2);
            }
        }
    }

    public static final class UrlOption {
        private Object body;
        private String charset;
        private Object headers;
        private String js;
        private String method;
        private String origin;
        private Integer retry;
        private Long serverID;
        private Long timeout;
        private String type;
        private String webJs;
        private Object webView;

        public UrlOption() {
            this(null, null, null, null, null, null, null, null, null, null, null, 2047, null);
        }

        public final UrlOption copy(String str, String str2, Object obj, Object obj2, String str3, Integer num, String str4, Object obj3, String str5, String str6, Long l8) {
            return new UrlOption(str, str2, obj, obj2, str3, num, str4, obj3, str5, str6, l8);
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (!(obj instanceof UrlOption)) {
                return false;
            }
            UrlOption urlOption = (UrlOption) obj;
            return kotlin.jvm.internal.i.a(this.method, urlOption.method) && kotlin.jvm.internal.i.a(this.charset, urlOption.charset) && kotlin.jvm.internal.i.a(this.headers, urlOption.headers) && kotlin.jvm.internal.i.a(this.body, urlOption.body) && kotlin.jvm.internal.i.a(this.origin, urlOption.origin) && kotlin.jvm.internal.i.a(this.retry, urlOption.retry) && kotlin.jvm.internal.i.a(this.type, urlOption.type) && kotlin.jvm.internal.i.a(this.webView, urlOption.webView) && kotlin.jvm.internal.i.a(this.webJs, urlOption.webJs) && kotlin.jvm.internal.i.a(this.js, urlOption.js) && kotlin.jvm.internal.i.a(this.serverID, urlOption.serverID);
        }

        public final String getBody() {
            Object obj = this.body;
            if (obj != null) {
                return obj instanceof String ? (String) obj : GsonExtensionsKt.getGSON().g(obj);
            }
            return null;
        }

        public final String getCharset() {
            return this.charset;
        }

        public final Map<?, ?> getHeaderMap() {
            Object obj = this.headers;
            if (obj instanceof Map) {
                return (Map) obj;
            }
            Object objB = null;
            if (!(obj instanceof String)) {
                return null;
            }
            n gson = GsonExtensionsKt.getGSON();
            String str = (String) obj;
            try {
                Type type = new TypeToken<Map<String, ? extends Object>>() { // from class: org.nobody.multitts.plugin.httptts.RequestObject.UrlOption.2
                }.getType();
                kotlin.jvm.internal.i.e(type, "object : TypeToken<T>() {}.type");
                objB = gson.b(str, type);
            } catch (Throwable unused) {
            }
            return objB == null ? Collections.emptyMap() : (Map) objB;
        }

        public final String getJs() {
            return this.js;
        }

        public final String getMethod() {
            return this.method;
        }

        public final String getOrigin() {
            return this.origin;
        }

        public final int getRetry() {
            Integer num = this.retry;
            if (num != null) {
                return num.intValue();
            }
            return 0;
        }

        public final Long getServerID() {
            return this.serverID;
        }

        public Long getTimeout() {
            return this.timeout;
        }

        public final String getType() {
            return this.type;
        }

        public final String getWebJs() {
            return this.webJs;
        }

        public int hashCode() {
            String str = this.method;
            int iHashCode = (str == null ? 0 : str.hashCode()) * 31;
            String str2 = this.charset;
            int iHashCode2 = (iHashCode + (str2 == null ? 0 : str2.hashCode())) * 31;
            Object obj = this.headers;
            int iHashCode3 = (iHashCode2 + (obj == null ? 0 : obj.hashCode())) * 31;
            Object obj2 = this.body;
            int iHashCode4 = (iHashCode3 + (obj2 == null ? 0 : obj2.hashCode())) * 31;
            String str3 = this.origin;
            int iHashCode5 = (iHashCode4 + (str3 == null ? 0 : str3.hashCode())) * 31;
            Integer num = this.retry;
            int iHashCode6 = (iHashCode5 + (num == null ? 0 : num.hashCode())) * 31;
            String str4 = this.type;
            int iHashCode7 = (iHashCode6 + (str4 == null ? 0 : str4.hashCode())) * 31;
            Object obj3 = this.webView;
            int iHashCode8 = (iHashCode7 + (obj3 == null ? 0 : obj3.hashCode())) * 31;
            String str5 = this.webJs;
            int iHashCode9 = (iHashCode8 + (str5 == null ? 0 : str5.hashCode())) * 31;
            String str6 = this.js;
            int iHashCode10 = (iHashCode9 + (str6 == null ? 0 : str6.hashCode())) * 31;
            Long l8 = this.serverID;
            return iHashCode10 + (l8 != null ? l8.hashCode() : 0);
        }

        public final void setBody(String str) {
            if (str == null || H5.n.X(str)) {
                str = null;
            } else if (StringExtensionsKt.isJsonObject(str)) {
                n gson = GsonExtensionsKt.getGSON();
                Type type = new TypeToken<Map<String, ? extends Object>>() { // from class: org.nobody.multitts.plugin.httptts.RequestObject.UrlOption.3
                }.getType();
                kotlin.jvm.internal.i.e(type, "object : TypeToken<T>() {}.type");
                Object objB = gson.b(str, type);
                if (objB == null) {
                    throw new NullPointerException("null cannot be cast to non-null type kotlin.collections.Map<kotlin.String, kotlin.Any>");
                }
                str = objB.toString();
            } else if (StringExtensionsKt.isJsonArray(str)) {
                kotlin.jvm.internal.i.d(GsonExtensionsKt.getGSON().b(str, TypeToken.getParameterized(List.class, Map.class).getType()), "null cannot be cast to non-null type kotlin.collections.List<T of org.nobody.multitts.tts.synthesizer.plugin.httptts.GsonExtensionsKt.fromJsonArray$lambda$1>");
                str = "";
            }
            this.body = str;
        }

        public final void setCharset(String str) {
            if (str == null || H5.n.X(str)) {
                str = null;
            }
            this.charset = str;
        }

        public final void setHeaders(String str) {
            if (str == null || H5.n.X(str)) {
                return;
            }
            n gson = GsonExtensionsKt.getGSON();
            Type type = new TypeToken<Map<String, ? extends Object>>() { // from class: org.nobody.multitts.plugin.httptts.RequestObject.UrlOption.1
            }.getType();
            kotlin.jvm.internal.i.e(type, "object : TypeToken<T>() {}.type");
            Object objB = gson.b(str, type);
            if (objB == null) {
                throw new NullPointerException("null cannot be cast to non-null type kotlin.collections.Map<kotlin.String, kotlin.Any>");
            }
            this.headers = objB;
        }

        public final void setJs(String str) {
            if (str == null || H5.n.X(str)) {
                str = null;
            }
            this.js = str;
        }

        public final void setMethod(String str) {
            if (str == null || H5.n.X(str)) {
                str = null;
            }
            this.method = str;
        }

        public final void setOrigin(String str) {
            if (str == null || H5.n.X(str)) {
                str = null;
            }
            this.origin = str;
        }

        public final void setRetry(String str) {
            this.retry = (str == null || str.length() == 0) ? null : u.O(str);
        }

        public final void setServerID(String str) {
            this.serverID = (str == null || H5.n.X(str)) ? null : Long.valueOf(Long.parseLong(str));
        }

        public void setTimeout(Long l8) {
            this.timeout = l8;
        }

        public final void setType(String str) {
            if (str == null || H5.n.X(str)) {
                str = null;
            }
            this.type = str;
        }

        public final void setWebJs(String str) {
            if (str == null || H5.n.X(str)) {
                str = null;
            }
            this.webJs = str;
        }

        public String toString() {
            String str = this.method;
            String str2 = this.charset;
            Object obj = this.headers;
            Object obj2 = this.body;
            String str3 = this.origin;
            Integer num = this.retry;
            String str4 = this.type;
            Object obj3 = this.webView;
            String str5 = this.webJs;
            String str6 = this.js;
            StringBuilder sb = new StringBuilder();
            i.v(new byte[]{Ascii.SI, -86, -87, 76, -102, -102, 8, -118, 52, -16, -88, 102, -98, -122, Ascii.SO, -127, 103}, new byte[]{90, -40, -59, 3, -22, -18, 97, -27}, sb, str);
            i.v(new byte[]{42, 62, 121, Ascii.US, -63, -61, 1, 103, 114, 35}, new byte[]{6, Ascii.RS, Ascii.SUB, 119, -96, -79, 114, 2}, sb, str2);
            sb.append(", headers=");
            sb.append(obj);
            sb.append(", body=");
            sb.append(obj2);
            i.v(new byte[]{-104, 55, -75, 96, -94, -34, 103, 93, -119}, new byte[]{-76, Ascii.ETB, -38, Ascii.DC2, -53, -71, Ascii.SO, 51}, sb, str3);
            sb.append(", retry=");
            sb.append(num);
            i.v(new byte[]{-106, -115, 91, 121, Ascii.DEL, 110, -59}, new byte[]{-70, -83, 47, 0, Ascii.SI, 11, -8, -23}, sb, str4);
            sb.append(", webView=");
            sb.append(obj3);
            i.v(new byte[]{116, 62, -62, -32, 74, Ascii.FS, Ascii.SO, -125}, new byte[]{88, Ascii.RS, -75, -123, 40, 86, 125, -66}, sb, str5);
            i.v(new byte[]{107, 57, -19, -81, 98}, new byte[]{71, Ascii.EM, -121, -36, 95, 10, -90, -61}, sb, str6);
            sb.append(", serverID=");
            sb.append(this.serverID);
            return i.q(new byte[]{-87}, new byte[]{UnsignedBytes.MAX_POWER_OF_TWO, -55, 70, -19, -8, -64, -97, 41}, sb);
        }

        public UrlOption(String str, String str2, Object obj, Object obj2, String str3, Integer num, String str4, Object obj3, String str5, String str6, Long l8) {
            this.timeout = Long.valueOf(NetModule.j);
            this.method = str;
            this.charset = str2;
            this.headers = obj;
            this.body = obj2;
            this.origin = str3;
            this.retry = num;
            this.type = str4;
            this.webView = obj3;
            this.webJs = str5;
            this.js = str6;
            this.serverID = l8;
        }

        public UrlOption(String str, String str2, Object obj, Object obj2, String str3, Integer num, String str4, Object obj3, String str5, String str6, Long l8, int i, kotlin.jvm.internal.e eVar) {
            this((i & 1) != 0 ? null : str, (i & 2) != 0 ? null : str2, (i & 4) != 0 ? null : obj, (i & 8) != 0 ? null : obj2, (i & 16) != 0 ? null : str3, (i & 32) != 0 ? null : num, (i & 64) != 0 ? null : str4, (i & 128) != 0 ? null : obj3, (i & Tts.ivTTS_PARAM_LANGUAGE) != 0 ? null : str5, (i & 512) != 0 ? null : str6, (i & 1024) == 0 ? l8 : null);
        }
    }

    public RequestObject(String str, String str2, String str3, float f3, float f8, String[] strArr, Map<String, String> map, String str4, d dVar, Executor executor) {
        this.timeout = 5;
        this.url = "";
        this.urlNoQuery = "";
        this.fieldMap = new LinkedHashMap();
        this.method = "GET";
        this.ruleUrl = str;
        this.bodyType = str4;
        this.headerMap = map;
        jsEngine.put("params", strArr);
        jsEngine.put("voiceCode", str2);
        jsEngine.put("speakText", str3);
        jsEngine.put("speakSpeed", Float.valueOf(f3));
        jsEngine.put("speakVolume", Float.valueOf(f8));
        AtomicReference<Executor> atomicReference = executorRef;
        atomicReference.set(executor);
        AtomicReference<d> atomicReference2 = cronetEngineRef;
        atomicReference2.set(dVar);
        analyzeJs();
        replaceKeyJs();
        analyzeUrl();
        atomicReference2.set(null);
        atomicReference.set(null);
    }

    private final void analyzeFields(String str) {
        this.queryStr = str;
        for (String str2 : StringExtensionsKt.splitNotBlank$default(str, new String[]{"&"}, 0, 2, (Object) null)) {
            String[] strArrSplitNotBlank = StringExtensionsKt.splitNotBlank(str2, new String[]{"="}, 2);
            String str3 = strArrSplitNotBlank[0];
            String str4 = (String) j.o0(strArrSplitNotBlank, 1);
            if (str4 == null) {
                str4 = "";
            }
            String str5 = this.charset;
            if (str5 == null || str5.isEmpty()) {
                if (CommonUtil.INSTANCE.hasUrlEncoded(str4)) {
                    this.fieldMap.put(str3, str4);
                } else {
                    try {
                        this.fieldMap.put(str3, URLEncoder.encode(str4, "UTF-8"));
                    } catch (UnsupportedEncodingException e8) {
                        h7.e.e(TAG, "analyzeFields: ", e8);
                    }
                }
            } else if (kotlin.jvm.internal.i.a(this.charset, "escape")) {
                this.fieldMap.put(str3, EncoderUtils.INSTANCE.escape(str4));
            } else {
                try {
                    this.fieldMap.put(str3, URLEncoder.encode(str4, this.charset));
                } catch (UnsupportedEncodingException e9) {
                    h7.e.e(TAG, "analyzeFields: ", e9);
                }
            }
        }
    }

    private final void analyzeJs() {
        Matcher matcher = JS_PATTERN.matcher(this.ruleUrl);
        String strReplace = this.ruleUrl;
        int iEnd = 0;
        while (matcher.find()) {
            if (matcher.start() > iEnd) {
                String string = H5.n.j0(this.ruleUrl.substring(iEnd, matcher.start())).toString();
                if (!string.isEmpty()) {
                    string.replace("@result", strReplace);
                }
            }
            String strGroup = matcher.group(2);
            if (strGroup == null) {
                strGroup = matcher.group(1);
            }
            strReplace = String.valueOf(evalJS(strGroup));
            iEnd = matcher.end();
        }
        if (this.ruleUrl.length() > iEnd) {
            String string2 = H5.n.j0(this.ruleUrl.substring(iEnd)).toString();
            if (!string2.isEmpty()) {
                strReplace = string2.replace("@result", strReplace);
            }
        }
        this.ruleUrl = strReplace;
    }

    private final void analyzeUrl() {
        String strSubstring;
        int iIndexOf;
        Matcher matcher = paramPattern.matcher(this.ruleUrl);
        if (matcher.find()) {
            strSubstring = this.ruleUrl.substring(0, matcher.start());
            kotlin.jvm.internal.i.e(strSubstring, "this as java.lang.String(startIndex, endIndex)");
        } else {
            strSubstring = this.ruleUrl;
        }
        this.url = strSubstring;
        if (strSubstring.length() != this.ruleUrl.length()) {
            String strSubstring2 = this.ruleUrl.substring(matcher.end());
            kotlin.jvm.internal.i.e(strSubstring2, "this as java.lang.String).substring(startIndex)");
            n gson = GsonExtensionsKt.getGSON();
            Type type = new TypeToken<UrlOption>() { // from class: org.nobody.multitts.plugin.httptts.RequestObject.3
            }.getType();
            kotlin.jvm.internal.i.e(type, "object : TypeToken<T>() {}.type");
            Object objB = gson.b(strSubstring2, type);
            kotlin.jvm.internal.i.d(objB, "null cannot be cast to non-null type org.nobody.multitts.tts.synthesizer.plugin.httptts.RequestObject.UrlOption");
            UrlOption urlOption = (UrlOption) objB;
            String strL = "POST".equals(urlOption.getMethod()) ? "POST" : "GET";
            Map<?, ?> headerMap = urlOption.getHeaderMap();
            if (headerMap != null && !headerMap.isEmpty()) {
                for (Map.Entry<?, ?> entry : headerMap.entrySet()) {
                    this.headerMap.put(entry.getKey().toString(), entry.getValue().toString());
                }
            }
            Map<String, String> map = this.headerMap;
            if (map != null && !map.isEmpty()) {
                String str = this.headerMap.get("Content-Type");
                if (TextUtils.isEmpty(str)) {
                    str = this.headerMap.get("content-type");
                }
                if (!TextUtils.isEmpty(str)) {
                    this.bodyType = str;
                }
            }
            this.method = strL;
            this.body = urlOption.getBody();
            this.type = urlOption.getType();
            this.charset = urlOption.getCharset();
            urlOption.getRetry();
            this.timeout = (int) (urlOption.getTimeout().longValue() / 1000);
        }
        this.urlNoQuery = this.url;
        if (!kotlin.jvm.internal.i.a(this.method, "GET") || (iIndexOf = this.url.indexOf(63)) == -1) {
            return;
        }
        String strSubstring3 = this.url.substring(iIndexOf + 1);
        kotlin.jvm.internal.i.e(strSubstring3, "this as java.lang.String).substring(startIndex)");
        analyzeFields(strSubstring3);
        String strSubstring4 = this.url.substring(0, iIndexOf);
        kotlin.jvm.internal.i.e(strSubstring4, "this as java.lang.String…ing(startIndex, endIndex)");
        this.urlNoQuery = strSubstring4;
    }

    public static synchronized void initJsEngine() {
        if (jsEngine != null) {
            return;
        }
        RhinoScriptEngine rhinoScriptEngine = RhinoScriptEngine.INSTANCE;
        jsEngine = rhinoScriptEngine;
        Bindings bindingsCreateBindings = rhinoScriptEngine.createBindings();
        bindingsCreateBindings.put("java", (Object) Java.INSTANCE);
        bindingsCreateBindings.put("GET", (Object) "GET");
        bindingsCreateBindings.put("POST", (Object) "POST");
        jsEngine.setBindings(bindingsCreateBindings, 200);
    }

    public static Map<String, String> json2map(String str) {
        Object objB;
        if (TextUtils.isEmpty(str)) {
            return Collections.emptyMap();
        }
        try {
            Type type = new TypeToken<Map<String, ? extends String>>() { // from class: org.nobody.multitts.plugin.httptts.RequestObject.1
            }.getType();
            kotlin.jvm.internal.i.e(type, "object : TypeToken<T>() {}.type");
            objB = GsonExtensionsKt.getGSON().b(str, type);
        } catch (Throwable th) {
            h7.e.e(TAG, "json2map: ", th);
            objB = null;
        }
        return objB != null ? (Map) objB : Collections.emptyMap();
    }

    public static synchronized void releaseJsEngine() {
        jsEngine = null;
    }

    private final void replaceKeyJs() {
        if (this.ruleUrl.contains("{{") && this.ruleUrl.contains("}}")) {
            String strInnerRule = new RuleAnalyzer(this.ruleUrl, false, 2, null).innerRule("{{", "}}", new l() { // from class: org.nobody.multitts.plugin.httptts.RequestObject.2
                // A5/l 里唯一的抽象方法是 invoke(Object)（泛型被 R8 擦除后的形参）；
                // 原来的 invoke(String) 只是它的 Kotlin 特化。dex 里本类另有一条
                // `public bridge synthetic invoke(Ljava/lang/Object;)Ljava/lang/Object;`，
                // 被 jadx 当合成方法丢弃了，这里按事实补回。
                @Override // A5.l
                public final Object invoke(Object obj) {
                    return invoke((String) obj);
                }

                public final String invoke(String str) {
                    kotlin.jvm.internal.i.f(str, "it");
                    Object objEvalJS = RequestObject.this.evalJS(str);
                    if (objEvalJS == null) {
                        objEvalJS = "";
                    }
                    if (objEvalJS instanceof String) {
                        return (String) objEvalJS;
                    }
                    if (!(objEvalJS instanceof Double) || ((Number) objEvalJS).doubleValue() % 1.0d != 0.0d) {
                        return objEvalJS.toString();
                    }
                    String str2 = String.format(Locale.US, "%.0f", Arrays.copyOf(new Object[]{objEvalJS}, 1));
                    // [R8 已移除的调用] 原字符串: "format(format, *args)"
                    return str2;
                }
            });
            if (strInnerRule.isEmpty()) {
                return;
            }
            this.ruleUrl = strInnerRule;
        }
    }

    public final Object evalJS(String str) {
        try {
            return jsEngine.eval(str);
        } catch (Exception e8) {
            h7.e.e(TAG, "evalJS: ", e8);
            return null;
        }
    }

    public final String getBody() {
        return this.body;
    }

    public String getBodyType() {
        return this.bodyType;
    }

    public final String getCharset() {
        return this.charset;
    }

    public final Map<String, String> getFieldMap() {
        return this.fieldMap;
    }

    public Map<String, String> getHeaderMap() {
        return this.headerMap;
    }

    public final String getMethod() {
        return this.method;
    }

    public final String getQueryStr() {
        return this.queryStr;
    }

    public final String getRuleUrl() {
        return this.ruleUrl;
    }

    public int getTimeout() {
        return this.timeout;
    }

    public final String getType() {
        return this.type;
    }

    public final String getUrl() {
        return this.url;
    }

    public final String getUrlNoQuery() {
        return this.urlNoQuery;
    }

    public byte[] request() {
        k kVar;
        E6.a aVar = new E6.a(this.timeout);
        if ("POST".equals(getMethod())) {
            // [R8 已移除的调用] 原字符串: "request: bodyType="
            d dVar = cronetEngineRef.get();
            String urlNoQuery = getUrlNoQuery();
            AtomicReference<Executor> atomicReference = executorRef;
            Executor executor = atomicReference.get();
            CronetUrlRequestContext cronetUrlRequestContext = (CronetUrlRequestContext) dVar;
            cronetUrlRequestContext.getClass();
            kVar = new k(urlNoQuery, aVar, executor, cronetUrlRequestContext);
            kVar.e = "POST";
            kVar.a("Content-Type", this.bodyType);
            kVar.d(c.h(TextUtils.isEmpty(getBody()) ? new byte[0] : getBody().getBytes(StandardCharsets.UTF_8)), atomicReference.get());
        } else {
            d dVar2 = cronetEngineRef.get();
            String url = getUrl();
            Executor executor2 = executorRef.get();
            CronetUrlRequestContext cronetUrlRequestContext2 = (CronetUrlRequestContext) dVar2;
            cronetUrlRequestContext2.getClass();
            kVar = new k(url, aVar, executor2, cronetUrlRequestContext2);
            kVar.e = "GET";
        }
        for (Map.Entry<String, String> entry : this.headerMap.entrySet()) {
            kVar.a(entry.getKey(), entry.getValue());
        }
        // [R8 已移除的调用] 原字符串: "request: method="
        getMethod();
        // [R8 已移除的调用] 原字符串: ", url="
        getUrl();
        // [R8 已移除的调用] 原字符串: ", body="
        getBody();
        CronetUrlRequest cronetUrlRequestB = kVar.b();
        cronetUrlRequestB.B();
        try {
            try {
                E6.b bVarS = aVar.s();
                try {
                    if (bVarS.u() || !bVarS.w()) {
                        bVarS.close();
                        cronetUrlRequestB.c();
                        return new byte[0];
                    }
                    byte[] bArrF = bVarS.f();
                    bVarS.close();
                    cronetUrlRequestB.c();
                    return bArrF;
                } catch (Throwable th) {
                    if (bVarS != null) {
                        try {
                            bVarS.close();
                        } catch (Throwable th2) {
                            th.addSuppressed(th2);
                        }
                    }
                    throw th;
                }
            } catch (IOException e8) {
                h7.e.e(TAG, "request: ", e8);
                // smali: :catch_147 -> :goto_15d(打日志) -> :goto_14e(c()) -> :goto_173(new byte[0])
                cronetUrlRequestB.c();
                return new byte[0];
            }
        } catch (Throwable th3) {
            cronetUrlRequestB.c();
            throw th3;
        }
    }

    public String toString() {
        String str = this.ruleUrl;
        String str2 = this.url;
        String str3 = this.body;
        String str4 = this.method;
        String str5 = this.urlNoQuery;
        StringBuilder sb = new StringBuilder();
        i.v(new byte[]{75, 109, 51, 79, -2, -46, 45, -84}, new byte[]{57, Ascii.CAN, 95, 42, -85, -96, 65, -111}, sb, str);
        i.v(new byte[]{-80, 118, -121, -102, -9, 54}, new byte[]{-100, 86, -14, -24, -101, 11, Ascii.US, -98}, sb, str2);
        i.v(new byte[]{40, -98, Ascii.CR, 59, 62, -41, -5}, new byte[]{4, -66, 111, 84, 90, -82, -58, -71}, sb, str3);
        i.v(new byte[]{-60, 55, 108, -70, -92, -15, 55, Ascii.DLE, -43}, new byte[]{-24, Ascii.ETB, 1, -33, -48, -103, 88, 116}, sb, str4);
        i.v(new byte[]{-81, 97, UnsignedBytes.MAX_POWER_OF_TWO, 77, -68, -25, -125, 117, -10, 36, -121, 70, -19}, new byte[]{-125, 65, -11, Utf8.REPLACEMENT_BYTE, -48, -87, -20, 36}, sb, str5);
        sb.append(", queryStr=");
        sb.append(this.queryStr);
        return sb.toString();
    }

    public RequestObject(String str) {
        this.timeout = 5;
        this.url = "";
        this.urlNoQuery = "";
        this.method = "GET";
        this.ruleUrl = str;
        this.bodyType = "application/json";
        this.headerMap = new LinkedHashMap();
        this.fieldMap = new LinkedHashMap();
        analyzeJs();
        replaceKeyJs();
        analyzeUrl();
    }
}
