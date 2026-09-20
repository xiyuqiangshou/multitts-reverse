package org.nobody.multitts.ui.other;

import h.m;

import A6.h;
import B.i;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.MenuItem;
import android.webkit.WebView;
import com.google.common.base.Ascii;
import com.google.common.primitives.SignedBytes;
import h7.d;
import h7.e;
import java.io.IOException;
import okio.Utf8;
import org.nobody.multitts.AppContext;

import o0.c;
import z6.a;

/* JADX INFO: loaded from: D:\mtts_work\dex\classes3.dex */
public class TipsActivity extends m {

    /* JADX INFO: renamed from: y, reason: collision with root package name */
    public h y;

    @Override // h.m, b.l, F.i, android.app.Activity
    public final void onCreate(Bundle bundle) {
        String language;
        super.onCreate(bundle);
        h hVarInflate = A6.h.inflate(getLayoutInflater());
        this.y = hVarInflate;
        setContentView(hVarInflate.a);
        s(this.y.b);
        if (l() != null) {
            l().S(true);
            l().X(2131886147);
        }
        String strL = "help.html";
        String str = z6.a.a0.Q;
        if (TextUtils.isEmpty(str)) {
            Configuration configuration = getResources().getConfiguration();
            language = (Build.VERSION.SDK_INT >= 24 ? configuration.getLocales().get(0) : configuration.locale).getLanguage();
        } else {
            language = str.split("-")[0];
        }
        StringBuilder sb = new StringBuilder();
        B.i.v(new byte[]{-116, -61, 97, 38, -79}, new byte[]{-28, -90, Ascii.CR, 86, -18, 59, -121, -96}, sb, language);
        String strQ = B.i.q(new byte[]{35, -88, -57, 56, 10}, new byte[]{Ascii.CR, -64, -77, 85, 102, 111, Ascii.FS, 90}, sb);
        String strL2 = "html";
        String[] strArr = h7.d.a;
        try {
            String[] list = AppContext.getContext().getAssets().list(strL2);
            if (list != null && list.length != 0) {
                for (String str2 : list) {
                    if (str2.equals(strQ)) {
                        strL = strQ;
                        break;
                    }
                }
            }
        } catch (IOException e8) {
            h7.e.e("d", "isAssetsFileExists: ", e8);
        }
        String strA = n6.a.a(0, h7.d.m("html/" + strL), new String[]{"__FONT_COLOR__", "__VERSION_NAME__", "__ANDROID_ID__"}, new String[]{h7.e.l(this), "1.8.3", Settings.Secure.getString(getContentResolver(), "android_id")});
        WebView webView = this.y.c;
        webView.getSettings().setAllowFileAccess(true);
        webView.setBackgroundColor(Color.parseColor("#00000000"));
        webView.loadDataWithBaseURL(null, strA, "text/html;charset=utf-8", "utf-8", null);
    }

    @Override // h.m, android.app.Activity
    public final void onDestroy() {
        super.onDestroy();
        this.y = null;
    }

    @Override // android.app.Activity
    public final boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() != 16908332) {
            return super.onOptionsItemSelected(menuItem);
        }
        finish();
        return true;
    }
}
