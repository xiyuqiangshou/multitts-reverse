package org.nobody.multitts.ui.other;

import h.g;
import h.k;
import h.m;

import A6.b;
import B.p;
import B0.Z;
import B4.a;
import D3.A;
import android.R;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Pair;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.ValueCallback;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.SearchView;
import androidx.constraintlayout.widget.ConstraintLayout;
import com.google.common.base.Ascii;
import com.google.common.primitives.SignedBytes;
import com.google.common.primitives.UnsignedBytes;
import h7.e;
import jq.util.Objects;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import okio.Utf8;
import org.json.JSONException;
import org.json.JSONObject;
import org.nobody.multitts.ui.other.LoginActivity;
import a7.f;
import a7.h;
import a7.i;



import o0.c;

/* JADX INFO: loaded from: D:\mtts_work\dex\classes3.dex */
public class LoginActivity extends m {

    /* JADX INFO: renamed from: F, reason: collision with root package name */
    public static final /* synthetic */ int F = 0;

    /* JADX INFO: renamed from: A, reason: collision with root package name */
    public WebView A;

    /* JADX INFO: renamed from: B, reason: collision with root package name */
    public final HashMap<Object, Object> B = new HashMap<>();

    /* JADX INFO: renamed from: C, reason: collision with root package name */
    public final AtomicBoolean C = new AtomicBoolean(false);

    /* JADX INFO: renamed from: D, reason: collision with root package name */
    public final CookieManager D = CookieManager.getInstance();

    /* JADX INFO: renamed from: E, reason: collision with root package name */
    public final AtomicReference E = new AtomicReference(null);

    /* JADX INFO: renamed from: y, reason: collision with root package name */
    public b y;

    /* JADX INFO: renamed from: z, reason: collision with root package name */
    public SearchView z;

    @Override // b.l, android.app.Activity
    public final void onBackPressed() {
        SearchView searchView = this.z;
        if (searchView.S) {
            super.onBackPressed();
        } else {
            searchView.setIconified(true);
        }
    }

    @Override // h.m, b.l, F.i, android.app.Activity
    public final void onCreate(Bundle bundle) {
        final int i = 0;
        final int i8 = 1;
        super.onCreate(bundle);
        b bVarInflate = A6.b.inflate(getLayoutInflater());
        this.y = bVarInflate;
        setContentView(bVarInflate.a);
        s(this.y.c);
        String stringExtra = getIntent().getStringExtra("url");
        String stringExtra2 = getIntent().getStringExtra("ua");
        String stringExtra3 = getIntent().getStringExtra("title");
        if (TextUtils.isEmpty(stringExtra)) {
            finish();
            return;
        }
        this.y.g.setText(getString(2131890564, stringExtra3));
        this.A = (WebView) findViewById(2131296986);
        this.y.f.setOnClickListener(new View.OnClickListener() { // from class: a7.c

            /* JADX INFO: renamed from: b, reason: collision with root package name */
            public final /* synthetic */ LoginActivity b;

            {
                this.b = LoginActivity.this;   // OBF: 原为 jadx 误渲染的构造参数 this
            }

            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                switch (i) {
                    case 0:
                        LoginActivity loginActivity = this.b;
                        AtomicReference atomicReference = loginActivity.E;
                        if (atomicReference.get() == null || loginActivity.C.get()) {
                            Toast.makeText(loginActivity, loginActivity.getString(2131886473), 0).show();
                        } else {
                            String cookie = loginActivity.D.getCookie((String) atomicReference.get());
                            // [R8 已移除的调用] 原字符串: "onCreate: cookies="
                            String stringExtra4 = loginActivity.getIntent().getStringExtra("param");
                            HashMap map = new HashMap();
                            HashMap map2 = new HashMap();
                            try {
                                JSONObject jSONObject = new JSONObject(stringExtra4);
                                Iterator<String> itKeys = jSONObject.keys();
                                while (itKeys.hasNext()) {
                                    String next = itKeys.next();
                                    if (next.startsWith("cookies.")) {
                                        map.put(next.substring(8), jSONObject.getString(next));
                                    } else if (next.startsWith("localStorage.")) {
                                        map2.put(next.substring(13), jSONObject.getString(next));
                                    } else if (next.equals("cookies")) {
                                        map.put("cookies", jSONObject.getString(next));
                                    }
                                }
                            } catch (JSONException e8) {
                                h7.e.e("LoginActivity", "parseWebViewArgs: ", e8);
                            }
                            Pair pair = new Pair(map, map2);
                            Map map3 = (Map) pair.first;
                            HashMap map4 = new HashMap();
                            Set setKeySet = map3.keySet();
                            if (TextUtils.isEmpty(cookie)) {
                                h7.e.d("LoginActivity", "getCookiesParams: cookie==null.");
                            } else {
                                String[] strArrSplit = cookie.split("; ");
                                int length = strArrSplit.length;
                                int i10 = 0;
                                while (i10 < length) {
                                    String str = strArrSplit[i10];
                                    int iIndexOf = str.indexOf(61);
                                    if (iIndexOf == -1 && setKeySet.contains(str)) {
                                        map4.put((String) map3.get(str), "");
                                    } else {
                                        String strSubstring = str.substring(0, iIndexOf);
                                        if (setKeySet.contains(strSubstring)) {
                                            map4.put((String) map3.get(strSubstring), str.substring(iIndexOf + 1));
                                        }
                                    }
                                    i10++;
                                }
                                // [R8 已移除的调用] 原字符串: "cookies"
                                if (setKeySet.contains("cookies")) {
                                    map4.put((String) map3.get("cookies"), cookie);
                                }
                            }
                            Map<String, Object> map5 = (Map<String, Object>) pair.second;
                            HashMap map6 = new HashMap();
                            for (String str2 : map5.keySet()) {
                                map6.put((String) map5.get(str2), (String) loginActivity.B.get(str2));
                            }
                            map4.putAll(map6);
                            JSONObject jSONObject2 = new JSONObject(map4);
                            Intent intent = new Intent();
                            intent.putExtra("data", jSONObject2.toString());
                            loginActivity.setResult(-1, intent);
                            // [R8 已移除的调用] 原字符串: "onCreate: result="
                            jSONObject2.toString();
                            loginActivity.finish();
                        }
                        break;
                    default:
                        LoginActivity loginActivity2 = this.b;
                        SearchView searchView = loginActivity2.z;
                        if (searchView.S) {
                            loginActivity2.finish();
                        } else {
                            searchView.setIconified(true);
                        }
                        break;
                }
            }
        });
        this.y.f.setOnLongClickListener(new View.OnLongClickListener() { // from class: a7.d

            /* JADX INFO: renamed from: a, reason: collision with root package name */
            public final /* synthetic */ LoginActivity a;

            {
                this.a = LoginActivity.this;   // OBF: 原为 jadx 误渲染的构造参数 this
            }

            @Override // android.view.View.OnLongClickListener
            public final boolean onLongClick(View view) {
                int i9;
                int i10 = 0;
                int i11 = 1;
                LoginActivity loginActivity = this.a;
                AtomicReference atomicReference = loginActivity.E;
                if (atomicReference.get() == null || loginActivity.C.get()) {
                    Toast.makeText(loginActivity, loginActivity.getString(2131886473), 0).show();
                    return true;
                }
                a aVar = new a(loginActivity, 2131952281);
                String string = loginActivity.getString(2131890560);
                g c0307g = (g) aVar.c;
                c0307g.d = string;
                StringBuilder sb = new StringBuilder();
                String cookie = loginActivity.D.getCookie((String) atomicReference.get());
                // [R8 已移除的调用] 原字符串: "generateInfos: cookies="
                sb.append("**************Cookies**************\n\n");
                if (!TextUtils.isEmpty(cookie)) {
                    String[] strArrSplit = cookie.split("; ");
                    int length = strArrSplit.length;
                    int i12 = 0;
                    while (i12 < length) {
                        String str = strArrSplit[i12];
                        int iIndexOf = str.indexOf(61);
                        byte[] bArr = new byte[i11];
                        bArr[0] = -102;
                        sb.append(o0.c.l(bArr, new byte[]{-73, -126, 53, 8, -58, Ascii.GS, 81, 44}));
                        if (iIndexOf == -1) {
                            sb.append(str);
                            i9 = 1;
                        } else {
                            sb.append(str.substring(0, iIndexOf));
                            sb.append("\n  ");
                            i9 = 1;
                            sb.append(str.substring(iIndexOf + 1));
                        }
                        sb.append("\n\n");
                        i12 += i9;
                        i11 = 1;
                    }
                }
                sb.append("\n***********Local Storage***********\n\n");
                for (Map.Entry entry : loginActivity.B.entrySet()) {
                    sb.append("-");
                    sb.append((String) entry.getKey());
                    sb.append("\n  ");
                    sb.append((String) entry.getValue());
                    sb.append("\n\n");
                }
                c0307g.f = sb.toString();
                aVar.p("OK", new a7.e(i10));
                k dialogInterfaceC0311kI = aVar.i();
                dialogInterfaceC0311kI.show();
                TextView textView = (TextView) dialogInterfaceC0311kI.findViewById(R.id.message);
                if (textView != null) {
                    textView.setTextIsSelectable(true);
                }
                // [R8 已移除的调用] 原字符串: "onCreate: textView="
                Objects.toString(textView);
                return true;
            }
        });
        if (TextUtils.isEmpty(stringExtra2)) {
            stringExtra2 = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/126.0.0.0 Safari/537.36";
        }
        List listSingletonList = Collections.singletonList(stringExtra);
        SearchView searchView = this.y.e;
        this.z = searchView;
        W6.e eVar = new W6.e(this, searchView, new f(listSingletonList, i));
        ConstraintLayout constraintLayout = this.y.b;
        p pVar = new p();
        pVar.c(constraintLayout);
        this.z.setOnSearchClickListener(new Y6.b(pVar, constraintLayout, eVar, i8));
        this.z.setOnCloseListener(new Y6.c(pVar, constraintLayout, eVar, i8));
        eVar.b("none", new Z(this, 12));
        this.y.d.setOnClickListener(new View.OnClickListener() { // from class: a7.c

            /* JADX INFO: renamed from: b, reason: collision with root package name */
            public final /* synthetic */ LoginActivity b;

            {
                this.b = LoginActivity.this;   // OBF: 原为 jadx 误渲染的构造参数 this
            }

            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                switch (i8) {
                    case 0:
                        LoginActivity loginActivity = this.b;
                        AtomicReference atomicReference = loginActivity.E;
                        if (atomicReference.get() == null || loginActivity.C.get()) {
                            Toast.makeText(loginActivity, loginActivity.getString(2131886473), 0).show();
                        } else {
                            String cookie = loginActivity.D.getCookie((String) atomicReference.get());
                            // [R8 已移除的调用] 原字符串: "onCreate: cookies="
                            String stringExtra4 = loginActivity.getIntent().getStringExtra("param");
                            HashMap map = new HashMap();
                            HashMap map2 = new HashMap();
                            try {
                                JSONObject jSONObject = new JSONObject(stringExtra4);
                                Iterator<String> itKeys = jSONObject.keys();
                                while (itKeys.hasNext()) {
                                    String next = itKeys.next();
                                    if (next.startsWith("cookies.")) {
                                        map.put(next.substring(8), jSONObject.getString(next));
                                    } else if (next.startsWith("localStorage.")) {
                                        map2.put(next.substring(13), jSONObject.getString(next));
                                    } else if (next.equals("cookies")) {
                                        map.put("cookies", jSONObject.getString(next));
                                    }
                                }
                            } catch (JSONException e8) {
                                h7.e.e("LoginActivity", "parseWebViewArgs: ", e8);
                            }
                            Pair pair = new Pair(map, map2);
                            Map map3 = (Map) pair.first;
                            HashMap map4 = new HashMap();
                            Set setKeySet = map3.keySet();
                            if (TextUtils.isEmpty(cookie)) {
                                h7.e.d("LoginActivity", "getCookiesParams: cookie==null.");
                            } else {
                                String[] strArrSplit = cookie.split("; ");
                                int length = strArrSplit.length;
                                int i10 = 0;
                                while (i10 < length) {
                                    String str = strArrSplit[i10];
                                    int iIndexOf = str.indexOf(61);
                                    if (iIndexOf == -1 && setKeySet.contains(str)) {
                                        map4.put((String) map3.get(str), "");
                                    } else {
                                        String strSubstring = str.substring(0, iIndexOf);
                                        if (setKeySet.contains(strSubstring)) {
                                            map4.put((String) map3.get(strSubstring), str.substring(iIndexOf + 1));
                                        }
                                    }
                                    i10++;
                                }
                                // [R8 已移除的调用] 原字符串: "cookies"
                                if (setKeySet.contains("cookies")) {
                                    map4.put((String) map3.get("cookies"), cookie);
                                }
                            }
                            Map<String, Object> map5 = (Map<String, Object>) pair.second;
                            HashMap map6 = new HashMap();
                            for (String str2 : map5.keySet()) {
                                map6.put((String) map5.get(str2), (String) loginActivity.B.get(str2));
                            }
                            map4.putAll(map6);
                            JSONObject jSONObject2 = new JSONObject(map4);
                            Intent intent = new Intent();
                            intent.putExtra("data", jSONObject2.toString());
                            loginActivity.setResult(-1, intent);
                            // [R8 已移除的调用] 原字符串: "onCreate: result="
                            jSONObject2.toString();
                            loginActivity.finish();
                        }
                        break;
                    default:
                        LoginActivity loginActivity2 = this.b;
                        SearchView searchView2 = loginActivity2.z;
                        if (searchView2.S) {
                            loginActivity2.finish();
                        } else {
                            searchView2.setIconified(true);
                        }
                        break;
                }
            }
        });
        boolean z5 = !stringExtra2.contains("Mobile");
        CookieManager cookieManager = this.D;
        cookieManager.setAcceptCookie(true);
        cookieManager.flush();
        WebSettings settings = this.A.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setDatabaseEnabled(true);
        settings.setMixedContentMode(0);
        if (TextUtils.isEmpty(stringExtra2)) {
            stringExtra2 = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/126.0.0.0 Safari/537.36";
        }
        settings.setUserAgentString(stringExtra2);
        settings.setBuiltInZoomControls(true);
        settings.setDisplayZoomControls(false);
        settings.setSupportZoom(true);
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);
        this.A.setInitialScale(1);
        this.A.addJavascriptInterface(new i(this), "AndroidInterface");
        this.A.setWebViewClient(new h(this, z5));
        this.A.loadUrl(stringExtra);
        this.y.d.setOnLongClickListener(new Y6.h(i8, this, stringExtra));
    }

    public final void t(final boolean z5) {
        this.C.set(true);
        // [R8 已移除的调用] 原字符串: "javascript:(function() {    for (var i = 0; i < localStorage.length; i++) {        var key = localStorage.key(i);        var value = localStorage.getItem(key);        AndroidInterface.saveToMap(key, value);    }})()"
        this.A.evaluateJavascript("javascript:(function() {    for (var i = 0; i < localStorage.length; i++) {        var key = localStorage.key(i);        var value = localStorage.getItem(key);        AndroidInterface.saveToMap(key, value);    }})()", new ValueCallback() { // from class: a7.g

            /* JADX INFO: renamed from: a, reason: collision with root package name */
            public final /* synthetic */ LoginActivity a;

            {
                this.a = LoginActivity.this;   // OBF: 原为 jadx 误渲染的构造参数 this
            }

            @Override // android.webkit.ValueCallback
            public final void onReceiveValue(Object obj) {
                int i = LoginActivity.F;
                LoginActivity loginActivity = this.a;
                loginActivity.getClass();
                // [R8 已移除的调用] 原字符串: "injectJavaScript: map="
                HashMap map = loginActivity.B;
                Objects.toString(map);
                if (map.isEmpty() && z5) {
                    new Handler().postDelayed(new A(loginActivity, 11), 1000L);
                } else {
                    loginActivity.C.set(false);
                }
            }
        });
    }
}
