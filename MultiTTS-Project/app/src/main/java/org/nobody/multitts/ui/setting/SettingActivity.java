package org.nobody.multitts.ui.setting;

import a.a;
import h.g;
import h.m;

import B0.J;
import android.app.ActivityManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.Process;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.preference.EditTextPreference;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.SeekBarPreference;
import androidx.preference.SwitchPreference;
import com.google.common.base.Ascii;
import com.google.common.primitives.SignedBytes;
import com.google.common.primitives.UnsignedBytes;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.regex.Pattern;
import okio.Utf8;
import org.nobody.multitts.AppContext;
import org.nobody.multitts.tts.jni.Ner;
import org.nobody.multitts.ui.setting.SettingActivity;





/* JADX INFO: loaded from: D:\mtts_work\dex\classes3.dex */
public class SettingActivity extends m {

    /* JADX INFO: renamed from: y, reason: collision with root package name */
    public static final /* synthetic */ int y = 0;

    public static class a extends w1.s {

        /* JADX INFO: renamed from: r0, reason: collision with root package name */
        public static final /* synthetic */ int r0 = 0;

        /* JADX INFO: renamed from: q0, reason: collision with root package name */
        public final HashMap q0 = new HashMap();

        static {
            // [R8 已移除的调用] 原字符串: "SettingsFragment"
        }

        public static void W(Context context, String str, String str2) {
            String strReplace = h7.d.m("html/".concat(str)).replace("__FONT_COLOR__", h7.e.l(context));
            WebView webView = new WebView(context);
            webView.getSettings().setAllowFileAccess(true);
            webView.setBackgroundColor(Color.parseColor("#00000000"));
            webView.loadDataWithBaseURL(null, strReplace, "text/html;charset=utf-8", "utf-8", null);
            B4.a aVar = new B4.a(context, 2131952281);
            g c0307g = (g) aVar.c;
            c0307g.d = str2;
            c0307g.r = webView;
            c0307g.g = c0307g.a.getText(2131886365);
            c0307g.h = null;
            aVar.r();
        }

        @Override // w1.s
        public final void S(String str) {
            final int i = 6;
            final int i8 = 1;
            final int i9 = 2;
            final int i10 = 8;
            T(str);
            final z6.a aVar = z6.a.a0;
            V();
            SwitchPreference switchPreference = (SwitchPreference) R("config_nokill");
            boolean zU = U();
            switchPreference.v(Boolean.valueOf(zU));
            if (zU) {
                switchPreference.B(true);
                switchPreference.w();
            } else {
                switchPreference.y(new k(this));
            }
            ((SwitchPreference) R("config_notify")).y(new t(this, aVar));
            ((SwitchPreference) R("config_cvst")).y(new u(aVar));
            ((SwitchPreference) R("config_priority")).y(new v(aVar));
            ((SwitchPreference) R("config_record_voice")).y(new w(aVar));
            ((SwitchPreference) R("config_record_text")).y(new x(aVar));
            ((SwitchPreference) R("config_continue_dialogue")).y(new y(aVar));
            ((SwitchPreference) R("config_check_network")).y(new z(aVar));
            ((SwitchPreference) R("config_check_silence")).y(new A(aVar));
            ((SwitchPreference) R("config_decode_stream")).y(new org.nobody.multitts.ui.setting.a(aVar));
            ((SwitchPreference) R("config_wake_lock")).y(new b(aVar));
            ((SwitchPreference) R("config_hide_extend_editor")).y(new c(aVar));
            ((SwitchPreference) R("config_hide_param_editor")).y(new d(aVar));
            ((SwitchPreference) R("config_hide_note_editor")).y(new e(aVar));
            ((SwitchPreference) R("config_auto_assign_role")).y(new f(aVar));
            ((SwitchPreference) R("config_new_person_only")).y(new org.nobody.multitts.ui.setting.g(aVar));
            ((SwitchPreference) R("config_debug_mode")).y(new h(aVar));
            ((SwitchPreference) R("config_filter_language")).y(new i(aVar));
            ((SwitchPreference) R("config_multiple_select")).y(new j(aVar));
            ((SwitchPreference) R("config_replace_emoji")).y(new l(aVar));
            final int i11 = 5;
            ((SwitchPreference) R("config_separate_english")).y(new w1.l() { // from class: f7.g
                @Override // w1.l
                public final boolean b(Serializable serializable) {
                    z6.a aVar2 = aVar;
                    switch (i11) {
                        case 0:
                            // OBF: int i12 = SettingActivity.a.r0;   // jadx 坏代码: dex 中不存在 SettingActivity.a.r0; 该局部变量未被使用
                            String str2 = (String) serializable;
                            aVar2.Q = str2;
                            AppContext.putString("config_language", str2);
                            AppContext.showToast(str2);
                            break;
                        case 1:
                            // OBF: int i13 = SettingActivity.a.r0;   // jadx 坏代码: dex 中不存在 SettingActivity.a.r0; 该局部变量未被使用
                            int i14 = Integer.parseInt(serializable.toString());
                            aVar2.V = i14;
                            AppContext.putInt(i14, "config_name_discoverer");
                            break;
                        case 2:
                            // OBF: int i15 = SettingActivity.a.r0;   // jadx 坏代码: dex 中不存在 SettingActivity.a.r0; 该局部变量未被使用
                            int i16 = Integer.parseInt(serializable.toString());
                            aVar2.W = i16;
                            AppContext.putInt(i16, "config_language_detector");
                            O6.d.d();
                            break;
                        case 3:
                            // OBF: int i17 = SettingActivity.a.r0;   // jadx 坏代码: dex 中不存在 SettingActivity.a.r0; 该局部变量未被使用
                            int i18 = Integer.parseInt(serializable.toString());
                            aVar2.X = i18;
                            AppContext.putInt(i18, "config_audio_processor");
                            break;
                        case 4:
                            // OBF: int i19 = SettingActivity.a.r0;   // jadx 坏代码: dex 中不存在 SettingActivity.a.r0; 该局部变量未被使用
                            int i20 = Integer.parseInt(serializable.toString());
                            aVar2.Y = i20;
                            AppContext.putInt(i20, "config_min_sample_rate");
                            R6.i.e();
                            break;
                        case 5:
                            // OBF: int i21 = SettingActivity.a.r0;   // jadx 坏代码: dex 中不存在 SettingActivity.a.r0; 该局部变量未被使用
                            boolean zBooleanValue = ((Boolean) serializable).booleanValue();
                            aVar2.T = zBooleanValue;
                            AppContext.putBoolean("config_separate_english", zBooleanValue);
                            break;
                        case 6:
                            // OBF: int i22 = SettingActivity.a.r0;   // jadx 坏代码: dex 中不存在 SettingActivity.a.r0; 该局部变量未被使用
                            int iIntValue = ((Integer) serializable).intValue();
                            aVar2.h = iIntValue;
                            AppContext.putInt(iIntValue, "config_max_auto_checked_person");
                            break;
                        case 7:
                            // OBF: int i23 = SettingActivity.a.r0;   // jadx 坏代码: dex 中不存在 SettingActivity.a.r0; 该局部变量未被使用
                            int iIntValue2 = ((Integer) serializable).intValue();
                            aVar2.i = iIntValue2;
                            AppContext.putInt(iIntValue2, "config_max_sentence_length");
                            break;
                        default:
                            // OBF: int i24 = SettingActivity.a.r0;   // jadx 坏代码: dex 中不存在 SettingActivity.a.r0; 该局部变量未被使用
                            boolean zBooleanValue2 = ((Boolean) serializable).booleanValue();
                            aVar2.U = zBooleanValue2;
                            AppContext.putBoolean("config_filter_unspoken", zBooleanValue2);
                            break;
                    }
                    return true;
                }
            });
            ((SwitchPreference) R("config_filter_unspoken")).y(new w1.l() { // from class: f7.g
                @Override // w1.l
                public final boolean b(Serializable serializable) {
                    z6.a aVar2 = aVar;
                    switch (i10) {
                        case 0:
                            // OBF: int i12 = SettingActivity.a.r0;   // jadx 坏代码: dex 中不存在 SettingActivity.a.r0; 该局部变量未被使用
                            String str2 = (String) serializable;
                            aVar2.Q = str2;
                            AppContext.putString("config_language", str2);
                            AppContext.showToast(str2);
                            break;
                        case 1:
                            // OBF: int i13 = SettingActivity.a.r0;   // jadx 坏代码: dex 中不存在 SettingActivity.a.r0; 该局部变量未被使用
                            int i14 = Integer.parseInt(serializable.toString());
                            aVar2.V = i14;
                            AppContext.putInt(i14, "config_name_discoverer");
                            break;
                        case 2:
                            // OBF: int i15 = SettingActivity.a.r0;   // jadx 坏代码: dex 中不存在 SettingActivity.a.r0; 该局部变量未被使用
                            int i16 = Integer.parseInt(serializable.toString());
                            aVar2.W = i16;
                            AppContext.putInt(i16, "config_language_detector");
                            O6.d.d();
                            break;
                        case 3:
                            // OBF: int i17 = SettingActivity.a.r0;   // jadx 坏代码: dex 中不存在 SettingActivity.a.r0; 该局部变量未被使用
                            int i18 = Integer.parseInt(serializable.toString());
                            aVar2.X = i18;
                            AppContext.putInt(i18, "config_audio_processor");
                            break;
                        case 4:
                            // OBF: int i19 = SettingActivity.a.r0;   // jadx 坏代码: dex 中不存在 SettingActivity.a.r0; 该局部变量未被使用
                            int i20 = Integer.parseInt(serializable.toString());
                            aVar2.Y = i20;
                            AppContext.putInt(i20, "config_min_sample_rate");
                            R6.i.e();
                            break;
                        case 5:
                            // OBF: int i21 = SettingActivity.a.r0;   // jadx 坏代码: dex 中不存在 SettingActivity.a.r0; 该局部变量未被使用
                            boolean zBooleanValue = ((Boolean) serializable).booleanValue();
                            aVar2.T = zBooleanValue;
                            AppContext.putBoolean("config_separate_english", zBooleanValue);
                            break;
                        case 6:
                            // OBF: int i22 = SettingActivity.a.r0;   // jadx 坏代码: dex 中不存在 SettingActivity.a.r0; 该局部变量未被使用
                            int iIntValue = ((Integer) serializable).intValue();
                            aVar2.h = iIntValue;
                            AppContext.putInt(iIntValue, "config_max_auto_checked_person");
                            break;
                        case 7:
                            // OBF: int i23 = SettingActivity.a.r0;   // jadx 坏代码: dex 中不存在 SettingActivity.a.r0; 该局部变量未被使用
                            int iIntValue2 = ((Integer) serializable).intValue();
                            aVar2.i = iIntValue2;
                            AppContext.putInt(iIntValue2, "config_max_sentence_length");
                            break;
                        default:
                            // OBF: int i24 = SettingActivity.a.r0;   // jadx 坏代码: dex 中不存在 SettingActivity.a.r0; 该局部变量未被使用
                            boolean zBooleanValue2 = ((Boolean) serializable).booleanValue();
                            aVar2.U = zBooleanValue2;
                            AppContext.putBoolean("config_filter_unspoken", zBooleanValue2);
                            break;
                    }
                    return true;
                }
            });
            R("add_to_legado").f = new org.nobody.multitts.ui.setting.m(this);
            ((EditTextPreference) R("audition_text")).y(new n(aVar));
            R("analyze_role").f = new o(this);
            ((EditTextPreference) R("person_name_regex")).y(new p(this, aVar));
            ((EditTextPreference) R("male_name_regex")).y(new q(this, aVar));
            ((EditTextPreference) R("female_name_regex")).y(new w1.l() { // from class: f7.f

                /* JADX INFO: renamed from: b, reason: collision with root package name */
                public final /* synthetic */ SettingActivity.a b;

                {
                    this.b = SettingActivity.a.this;   // OBF: 原为 jadx 误渲染的构造参数 this
                }

                @Override // w1.l
                public final boolean b(Serializable serializable) {
                    z6.a aVar2 = aVar;
                    SettingActivity.a aVar3 = this.b;
                    switch (i8) {
                        case 0:
                            // OBF: int i12 = SettingActivity.a.r0;   // jadx 坏代码: dex 中不存在 SettingActivity.a.r0; 该局部变量未被使用
                            aVar3.getClass();
                            String str2 = (String) serializable;
                            if (TextUtils.isEmpty(str2)) {
                                return false;
                            }
                            Context contextL = aVar3.L();
                            View viewInflate = LayoutInflater.from(contextL).inflate(2131492927, (ViewGroup) null);
                            TextView textView = (TextView) viewInflate.findViewById(2131296724);
                            TextView textView2 = (TextView) viewInflate.findViewById(2131296726);
                            textView.setText(String.format("  %s", str2));
                            Ner ner = new Ner();
                            ArrayList arrayListA = ner.a(str2);
                            ner.b();
                            StringBuilder sb = new StringBuilder("  ");
                            Iterator it = arrayListA.iterator();
                            while (it.hasNext()) {
                                sb.append((String) it.next());
                                sb.append(", ");
                            }
                            sb.delete(sb.length() - 2, sb.length());
                            textView2.setText(sb.toString());
                            B4.a aVar4 = new B4.a(contextL, 2131952281);
                            ((g) aVar4.c).r = viewInflate;
                            aVar4.p("OK", new a7.e(3));
                            aVar4.r();
                            aVar2.getClass();
                            AppContext.putString("nature_test_text", str2);
                            return true;
                        default:
                            // OBF: int i13 = SettingActivity.a.r0;   // jadx 坏代码: dex 中不存在 SettingActivity.a.r0; 该局部变量未被使用
                            String str3 = (String) serializable;
                            if (!SettingActivity.checkRegex(aVar3.L(), str3)) {
                                return false;
                            }
                            aVar2.getClass();
                            if (str3 == null) {
                                str3 = "";
                            }
                            aVar2.L = str3;
                            AppContext.putString("female_name_regex", str3);
                            return true;
                    }
                }
            });
            EditTextPreference editTextPreference = (EditTextPreference) R("nature_match_regex");
            editTextPreference.y(new D0.g(this, 5, aVar, editTextPreference));
            final int i12 = 0;
            ((EditTextPreference) R("nature_test_text")).y(new w1.l() { // from class: f7.f

                /* JADX INFO: renamed from: b, reason: collision with root package name */
                public final /* synthetic */ SettingActivity.a b;

                {
                    this.b = SettingActivity.a.this;   // OBF: 原为 jadx 误渲染的构造参数 this
                }

                @Override // w1.l
                public final boolean b(Serializable serializable) {
                    z6.a aVar2 = aVar;
                    SettingActivity.a aVar3 = this.b;
                    switch (i12) {
                        case 0:
                            // OBF: int i13 = SettingActivity.a.r0;   // jadx 坏代码: dex 中不存在 SettingActivity.a.r0; 该局部变量未被使用
                            aVar3.getClass();
                            String str2 = (String) serializable;
                            if (TextUtils.isEmpty(str2)) {
                                return false;
                            }
                            Context contextL = aVar3.L();
                            View viewInflate = LayoutInflater.from(contextL).inflate(2131492927, (ViewGroup) null);
                            TextView textView = (TextView) viewInflate.findViewById(2131296724);
                            TextView textView2 = (TextView) viewInflate.findViewById(2131296726);
                            textView.setText(String.format("  %s", str2));
                            Ner ner = new Ner();
                            ArrayList arrayListA = ner.a(str2);
                            ner.b();
                            StringBuilder sb = new StringBuilder("  ");
                            Iterator it = arrayListA.iterator();
                            while (it.hasNext()) {
                                sb.append((String) it.next());
                                sb.append(", ");
                            }
                            sb.delete(sb.length() - 2, sb.length());
                            textView2.setText(sb.toString());
                            B4.a aVar4 = new B4.a(contextL, 2131952281);
                            ((g) aVar4.c).r = viewInflate;
                            aVar4.p("OK", new a7.e(3));
                            aVar4.r();
                            aVar2.getClass();
                            AppContext.putString("nature_test_text", str2);
                            return true;
                        default:
                            // OBF: int i14 = SettingActivity.a.r0;   // jadx 坏代码: dex 中不存在 SettingActivity.a.r0; 该局部变量未被使用
                            String str3 = (String) serializable;
                            if (!SettingActivity.checkRegex(aVar3.L(), str3)) {
                                return false;
                            }
                            aVar2.getClass();
                            if (str3 == null) {
                                str3 = "";
                            }
                            aVar2.L = str3;
                            AppContext.putString("female_name_regex", str3);
                            return true;
                    }
                }
            });
            final int i13 = 0;
            ((ListPreference) R("config_language")).y(new w1.l() { // from class: f7.g
                @Override // w1.l
                public final boolean b(Serializable serializable) {
                    z6.a aVar2 = aVar;
                    switch (i13) {
                        case 0:
                            // OBF: int i14 = SettingActivity.a.r0;   // jadx 坏代码: dex 中不存在 SettingActivity.a.r0; 该局部变量未被使用
                            String str2 = (String) serializable;
                            aVar2.Q = str2;
                            AppContext.putString("config_language", str2);
                            AppContext.showToast(str2);
                            break;
                        case 1:
                            // OBF: int i15 = SettingActivity.a.r0;   // jadx 坏代码: dex 中不存在 SettingActivity.a.r0; 该局部变量未被使用
                            int i16 = Integer.parseInt(serializable.toString());
                            aVar2.V = i16;
                            AppContext.putInt(i16, "config_name_discoverer");
                            break;
                        case 2:
                            // OBF: int i17 = SettingActivity.a.r0;   // jadx 坏代码: dex 中不存在 SettingActivity.a.r0; 该局部变量未被使用
                            int i18 = Integer.parseInt(serializable.toString());
                            aVar2.W = i18;
                            AppContext.putInt(i18, "config_language_detector");
                            O6.d.d();
                            break;
                        case 3:
                            // OBF: int i19 = SettingActivity.a.r0;   // jadx 坏代码: dex 中不存在 SettingActivity.a.r0; 该局部变量未被使用
                            int i110 = Integer.parseInt(serializable.toString());
                            aVar2.X = i110;
                            AppContext.putInt(i110, "config_audio_processor");
                            break;
                        case 4:
                            // OBF: int i111 = SettingActivity.a.r0;   // jadx 坏代码: dex 中不存在 SettingActivity.a.r0; 该局部变量未被使用
                            int i20 = Integer.parseInt(serializable.toString());
                            aVar2.Y = i20;
                            AppContext.putInt(i20, "config_min_sample_rate");
                            R6.i.e();
                            break;
                        case 5:
                            // OBF: int i21 = SettingActivity.a.r0;   // jadx 坏代码: dex 中不存在 SettingActivity.a.r0; 该局部变量未被使用
                            boolean zBooleanValue = ((Boolean) serializable).booleanValue();
                            aVar2.T = zBooleanValue;
                            AppContext.putBoolean("config_separate_english", zBooleanValue);
                            break;
                        case 6:
                            // OBF: int i22 = SettingActivity.a.r0;   // jadx 坏代码: dex 中不存在 SettingActivity.a.r0; 该局部变量未被使用
                            int iIntValue = ((Integer) serializable).intValue();
                            aVar2.h = iIntValue;
                            AppContext.putInt(iIntValue, "config_max_auto_checked_person");
                            break;
                        case 7:
                            // OBF: int i23 = SettingActivity.a.r0;   // jadx 坏代码: dex 中不存在 SettingActivity.a.r0; 该局部变量未被使用
                            int iIntValue2 = ((Integer) serializable).intValue();
                            aVar2.i = iIntValue2;
                            AppContext.putInt(iIntValue2, "config_max_sentence_length");
                            break;
                        default:
                            // OBF: int i24 = SettingActivity.a.r0;   // jadx 坏代码: dex 中不存在 SettingActivity.a.r0; 该局部变量未被使用
                            boolean zBooleanValue2 = ((Boolean) serializable).booleanValue();
                            aVar2.U = zBooleanValue2;
                            AppContext.putBoolean("config_filter_unspoken", zBooleanValue2);
                            break;
                    }
                    return true;
                }
            });
            ((ListPreference) R("config_name_discoverer_x")).y(new w1.l() { // from class: f7.g
                @Override // w1.l
                public final boolean b(Serializable serializable) {
                    z6.a aVar2 = aVar;
                    switch (i8) {
                        case 0:
                            // OBF: int i14 = SettingActivity.a.r0;   // jadx 坏代码: dex 中不存在 SettingActivity.a.r0; 该局部变量未被使用
                            String str2 = (String) serializable;
                            aVar2.Q = str2;
                            AppContext.putString("config_language", str2);
                            AppContext.showToast(str2);
                            break;
                        case 1:
                            // OBF: int i15 = SettingActivity.a.r0;   // jadx 坏代码: dex 中不存在 SettingActivity.a.r0; 该局部变量未被使用
                            int i16 = Integer.parseInt(serializable.toString());
                            aVar2.V = i16;
                            AppContext.putInt(i16, "config_name_discoverer");
                            break;
                        case 2:
                            // OBF: int i17 = SettingActivity.a.r0;   // jadx 坏代码: dex 中不存在 SettingActivity.a.r0; 该局部变量未被使用
                            int i18 = Integer.parseInt(serializable.toString());
                            aVar2.W = i18;
                            AppContext.putInt(i18, "config_language_detector");
                            O6.d.d();
                            break;
                        case 3:
                            // OBF: int i19 = SettingActivity.a.r0;   // jadx 坏代码: dex 中不存在 SettingActivity.a.r0; 该局部变量未被使用
                            int i110 = Integer.parseInt(serializable.toString());
                            aVar2.X = i110;
                            AppContext.putInt(i110, "config_audio_processor");
                            break;
                        case 4:
                            // OBF: int i111 = SettingActivity.a.r0;   // jadx 坏代码: dex 中不存在 SettingActivity.a.r0; 该局部变量未被使用
                            int i20 = Integer.parseInt(serializable.toString());
                            aVar2.Y = i20;
                            AppContext.putInt(i20, "config_min_sample_rate");
                            R6.i.e();
                            break;
                        case 5:
                            // OBF: int i21 = SettingActivity.a.r0;   // jadx 坏代码: dex 中不存在 SettingActivity.a.r0; 该局部变量未被使用
                            boolean zBooleanValue = ((Boolean) serializable).booleanValue();
                            aVar2.T = zBooleanValue;
                            AppContext.putBoolean("config_separate_english", zBooleanValue);
                            break;
                        case 6:
                            // OBF: int i22 = SettingActivity.a.r0;   // jadx 坏代码: dex 中不存在 SettingActivity.a.r0; 该局部变量未被使用
                            int iIntValue = ((Integer) serializable).intValue();
                            aVar2.h = iIntValue;
                            AppContext.putInt(iIntValue, "config_max_auto_checked_person");
                            break;
                        case 7:
                            // OBF: int i23 = SettingActivity.a.r0;   // jadx 坏代码: dex 中不存在 SettingActivity.a.r0; 该局部变量未被使用
                            int iIntValue2 = ((Integer) serializable).intValue();
                            aVar2.i = iIntValue2;
                            AppContext.putInt(iIntValue2, "config_max_sentence_length");
                            break;
                        default:
                            // OBF: int i24 = SettingActivity.a.r0;   // jadx 坏代码: dex 中不存在 SettingActivity.a.r0; 该局部变量未被使用
                            boolean zBooleanValue2 = ((Boolean) serializable).booleanValue();
                            aVar2.U = zBooleanValue2;
                            AppContext.putBoolean("config_filter_unspoken", zBooleanValue2);
                            break;
                    }
                    return true;
                }
            });
            ((ListPreference) R("config_language_detector_x")).y(new w1.l() { // from class: f7.g
                @Override // w1.l
                public final boolean b(Serializable serializable) {
                    z6.a aVar2 = aVar;
                    switch (i9) {
                        case 0:
                            // OBF: int i14 = SettingActivity.a.r0;   // jadx 坏代码: dex 中不存在 SettingActivity.a.r0; 该局部变量未被使用
                            String str2 = (String) serializable;
                            aVar2.Q = str2;
                            AppContext.putString("config_language", str2);
                            AppContext.showToast(str2);
                            break;
                        case 1:
                            // OBF: int i15 = SettingActivity.a.r0;   // jadx 坏代码: dex 中不存在 SettingActivity.a.r0; 该局部变量未被使用
                            int i16 = Integer.parseInt(serializable.toString());
                            aVar2.V = i16;
                            AppContext.putInt(i16, "config_name_discoverer");
                            break;
                        case 2:
                            // OBF: int i17 = SettingActivity.a.r0;   // jadx 坏代码: dex 中不存在 SettingActivity.a.r0; 该局部变量未被使用
                            int i18 = Integer.parseInt(serializable.toString());
                            aVar2.W = i18;
                            AppContext.putInt(i18, "config_language_detector");
                            O6.d.d();
                            break;
                        case 3:
                            // OBF: int i19 = SettingActivity.a.r0;   // jadx 坏代码: dex 中不存在 SettingActivity.a.r0; 该局部变量未被使用
                            int i110 = Integer.parseInt(serializable.toString());
                            aVar2.X = i110;
                            AppContext.putInt(i110, "config_audio_processor");
                            break;
                        case 4:
                            // OBF: int i111 = SettingActivity.a.r0;   // jadx 坏代码: dex 中不存在 SettingActivity.a.r0; 该局部变量未被使用
                            int i20 = Integer.parseInt(serializable.toString());
                            aVar2.Y = i20;
                            AppContext.putInt(i20, "config_min_sample_rate");
                            R6.i.e();
                            break;
                        case 5:
                            // OBF: int i21 = SettingActivity.a.r0;   // jadx 坏代码: dex 中不存在 SettingActivity.a.r0; 该局部变量未被使用
                            boolean zBooleanValue = ((Boolean) serializable).booleanValue();
                            aVar2.T = zBooleanValue;
                            AppContext.putBoolean("config_separate_english", zBooleanValue);
                            break;
                        case 6:
                            // OBF: int i22 = SettingActivity.a.r0;   // jadx 坏代码: dex 中不存在 SettingActivity.a.r0; 该局部变量未被使用
                            int iIntValue = ((Integer) serializable).intValue();
                            aVar2.h = iIntValue;
                            AppContext.putInt(iIntValue, "config_max_auto_checked_person");
                            break;
                        case 7:
                            // OBF: int i23 = SettingActivity.a.r0;   // jadx 坏代码: dex 中不存在 SettingActivity.a.r0; 该局部变量未被使用
                            int iIntValue2 = ((Integer) serializable).intValue();
                            aVar2.i = iIntValue2;
                            AppContext.putInt(iIntValue2, "config_max_sentence_length");
                            break;
                        default:
                            // OBF: int i24 = SettingActivity.a.r0;   // jadx 坏代码: dex 中不存在 SettingActivity.a.r0; 该局部变量未被使用
                            boolean zBooleanValue2 = ((Boolean) serializable).booleanValue();
                            aVar2.U = zBooleanValue2;
                            AppContext.putBoolean("config_filter_unspoken", zBooleanValue2);
                            break;
                    }
                    return true;
                }
            });
            final int i14 = 3;
            ((ListPreference) R("config_audio_processor_x")).y(new w1.l() { // from class: f7.g
                @Override // w1.l
                public final boolean b(Serializable serializable) {
                    z6.a aVar2 = aVar;
                    switch (i14) {
                        case 0:
                            // OBF: int i15 = SettingActivity.a.r0;   // jadx 坏代码: dex 中不存在 SettingActivity.a.r0; 该局部变量未被使用
                            String str2 = (String) serializable;
                            aVar2.Q = str2;
                            AppContext.putString("config_language", str2);
                            AppContext.showToast(str2);
                            break;
                        case 1:
                            // OBF: int i16 = SettingActivity.a.r0;   // jadx 坏代码: dex 中不存在 SettingActivity.a.r0; 该局部变量未被使用
                            int i17 = Integer.parseInt(serializable.toString());
                            aVar2.V = i17;
                            AppContext.putInt(i17, "config_name_discoverer");
                            break;
                        case 2:
                            // OBF: int i18 = SettingActivity.a.r0;   // jadx 坏代码: dex 中不存在 SettingActivity.a.r0; 该局部变量未被使用
                            int i19 = Integer.parseInt(serializable.toString());
                            aVar2.W = i19;
                            AppContext.putInt(i19, "config_language_detector");
                            O6.d.d();
                            break;
                        case 3:
                            // OBF: int i110 = SettingActivity.a.r0;   // jadx 坏代码: dex 中不存在 SettingActivity.a.r0; 该局部变量未被使用
                            int i111 = Integer.parseInt(serializable.toString());
                            aVar2.X = i111;
                            AppContext.putInt(i111, "config_audio_processor");
                            break;
                        case 4:
                            // OBF: int i112 = SettingActivity.a.r0;   // jadx 坏代码: dex 中不存在 SettingActivity.a.r0; 该局部变量未被使用
                            int i20 = Integer.parseInt(serializable.toString());
                            aVar2.Y = i20;
                            AppContext.putInt(i20, "config_min_sample_rate");
                            R6.i.e();
                            break;
                        case 5:
                            // OBF: int i21 = SettingActivity.a.r0;   // jadx 坏代码: dex 中不存在 SettingActivity.a.r0; 该局部变量未被使用
                            boolean zBooleanValue = ((Boolean) serializable).booleanValue();
                            aVar2.T = zBooleanValue;
                            AppContext.putBoolean("config_separate_english", zBooleanValue);
                            break;
                        case 6:
                            // OBF: int i22 = SettingActivity.a.r0;   // jadx 坏代码: dex 中不存在 SettingActivity.a.r0; 该局部变量未被使用
                            int iIntValue = ((Integer) serializable).intValue();
                            aVar2.h = iIntValue;
                            AppContext.putInt(iIntValue, "config_max_auto_checked_person");
                            break;
                        case 7:
                            // OBF: int i23 = SettingActivity.a.r0;   // jadx 坏代码: dex 中不存在 SettingActivity.a.r0; 该局部变量未被使用
                            int iIntValue2 = ((Integer) serializable).intValue();
                            aVar2.i = iIntValue2;
                            AppContext.putInt(iIntValue2, "config_max_sentence_length");
                            break;
                        default:
                            // OBF: int i24 = SettingActivity.a.r0;   // jadx 坏代码: dex 中不存在 SettingActivity.a.r0; 该局部变量未被使用
                            boolean zBooleanValue2 = ((Boolean) serializable).booleanValue();
                            aVar2.U = zBooleanValue2;
                            AppContext.putBoolean("config_filter_unspoken", zBooleanValue2);
                            break;
                    }
                    return true;
                }
            });
            final int i15 = 4;
            ((ListPreference) R("config_min_sample_rate_x")).y(new w1.l() { // from class: f7.g
                @Override // w1.l
                public final boolean b(Serializable serializable) {
                    z6.a aVar2 = aVar;
                    switch (i15) {
                        case 0:
                            // OBF: int i16 = SettingActivity.a.r0;   // jadx 坏代码: dex 中不存在 SettingActivity.a.r0; 该局部变量未被使用
                            String str2 = (String) serializable;
                            aVar2.Q = str2;
                            AppContext.putString("config_language", str2);
                            AppContext.showToast(str2);
                            break;
                        case 1:
                            // OBF: int i17 = SettingActivity.a.r0;   // jadx 坏代码: dex 中不存在 SettingActivity.a.r0; 该局部变量未被使用
                            int i18 = Integer.parseInt(serializable.toString());
                            aVar2.V = i18;
                            AppContext.putInt(i18, "config_name_discoverer");
                            break;
                        case 2:
                            // OBF: int i19 = SettingActivity.a.r0;   // jadx 坏代码: dex 中不存在 SettingActivity.a.r0; 该局部变量未被使用
                            int i110 = Integer.parseInt(serializable.toString());
                            aVar2.W = i110;
                            AppContext.putInt(i110, "config_language_detector");
                            O6.d.d();
                            break;
                        case 3:
                            // OBF: int i111 = SettingActivity.a.r0;   // jadx 坏代码: dex 中不存在 SettingActivity.a.r0; 该局部变量未被使用
                            int i112 = Integer.parseInt(serializable.toString());
                            aVar2.X = i112;
                            AppContext.putInt(i112, "config_audio_processor");
                            break;
                        case 4:
                            // OBF: int i113 = SettingActivity.a.r0;   // jadx 坏代码: dex 中不存在 SettingActivity.a.r0; 该局部变量未被使用
                            int i20 = Integer.parseInt(serializable.toString());
                            aVar2.Y = i20;
                            AppContext.putInt(i20, "config_min_sample_rate");
                            R6.i.e();
                            break;
                        case 5:
                            // OBF: int i21 = SettingActivity.a.r0;   // jadx 坏代码: dex 中不存在 SettingActivity.a.r0; 该局部变量未被使用
                            boolean zBooleanValue = ((Boolean) serializable).booleanValue();
                            aVar2.T = zBooleanValue;
                            AppContext.putBoolean("config_separate_english", zBooleanValue);
                            break;
                        case 6:
                            // OBF: int i22 = SettingActivity.a.r0;   // jadx 坏代码: dex 中不存在 SettingActivity.a.r0; 该局部变量未被使用
                            int iIntValue = ((Integer) serializable).intValue();
                            aVar2.h = iIntValue;
                            AppContext.putInt(iIntValue, "config_max_auto_checked_person");
                            break;
                        case 7:
                            // OBF: int i23 = SettingActivity.a.r0;   // jadx 坏代码: dex 中不存在 SettingActivity.a.r0; 该局部变量未被使用
                            int iIntValue2 = ((Integer) serializable).intValue();
                            aVar2.i = iIntValue2;
                            AppContext.putInt(iIntValue2, "config_max_sentence_length");
                            break;
                        default:
                            // OBF: int i24 = SettingActivity.a.r0;   // jadx 坏代码: dex 中不存在 SettingActivity.a.r0; 该局部变量未被使用
                            boolean zBooleanValue2 = ((Boolean) serializable).booleanValue();
                            aVar2.U = zBooleanValue2;
                            AppContext.putBoolean("config_filter_unspoken", zBooleanValue2);
                            break;
                    }
                    return true;
                }
            });
            ((SeekBarPreference) R("config_max_cache_voice_num")).y(new r(aVar));
            ((SeekBarPreference) R("config_max_cache_engine_num")).y(new s(aVar));
            ((SeekBarPreference) R("config_max_auto_checked_person")).y(new w1.l() { // from class: f7.g
                @Override // w1.l
                public final boolean b(Serializable serializable) {
                    z6.a aVar2 = aVar;
                    switch (i) {
                        case 0:
                            // OBF: int i16 = SettingActivity.a.r0;   // jadx 坏代码: dex 中不存在 SettingActivity.a.r0; 该局部变量未被使用
                            String str2 = (String) serializable;
                            aVar2.Q = str2;
                            AppContext.putString("config_language", str2);
                            AppContext.showToast(str2);
                            break;
                        case 1:
                            // OBF: int i17 = SettingActivity.a.r0;   // jadx 坏代码: dex 中不存在 SettingActivity.a.r0; 该局部变量未被使用
                            int i18 = Integer.parseInt(serializable.toString());
                            aVar2.V = i18;
                            AppContext.putInt(i18, "config_name_discoverer");
                            break;
                        case 2:
                            // OBF: int i19 = SettingActivity.a.r0;   // jadx 坏代码: dex 中不存在 SettingActivity.a.r0; 该局部变量未被使用
                            int i110 = Integer.parseInt(serializable.toString());
                            aVar2.W = i110;
                            AppContext.putInt(i110, "config_language_detector");
                            O6.d.d();
                            break;
                        case 3:
                            // OBF: int i111 = SettingActivity.a.r0;   // jadx 坏代码: dex 中不存在 SettingActivity.a.r0; 该局部变量未被使用
                            int i112 = Integer.parseInt(serializable.toString());
                            aVar2.X = i112;
                            AppContext.putInt(i112, "config_audio_processor");
                            break;
                        case 4:
                            // OBF: int i113 = SettingActivity.a.r0;   // jadx 坏代码: dex 中不存在 SettingActivity.a.r0; 该局部变量未被使用
                            int i20 = Integer.parseInt(serializable.toString());
                            aVar2.Y = i20;
                            AppContext.putInt(i20, "config_min_sample_rate");
                            R6.i.e();
                            break;
                        case 5:
                            // OBF: int i21 = SettingActivity.a.r0;   // jadx 坏代码: dex 中不存在 SettingActivity.a.r0; 该局部变量未被使用
                            boolean zBooleanValue = ((Boolean) serializable).booleanValue();
                            aVar2.T = zBooleanValue;
                            AppContext.putBoolean("config_separate_english", zBooleanValue);
                            break;
                        case 6:
                            // OBF: int i22 = SettingActivity.a.r0;   // jadx 坏代码: dex 中不存在 SettingActivity.a.r0; 该局部变量未被使用
                            int iIntValue = ((Integer) serializable).intValue();
                            aVar2.h = iIntValue;
                            AppContext.putInt(iIntValue, "config_max_auto_checked_person");
                            break;
                        case 7:
                            // OBF: int i23 = SettingActivity.a.r0;   // jadx 坏代码: dex 中不存在 SettingActivity.a.r0; 该局部变量未被使用
                            int iIntValue2 = ((Integer) serializable).intValue();
                            aVar2.i = iIntValue2;
                            AppContext.putInt(iIntValue2, "config_max_sentence_length");
                            break;
                        default:
                            // OBF: int i24 = SettingActivity.a.r0;   // jadx 坏代码: dex 中不存在 SettingActivity.a.r0; 该局部变量未被使用
                            boolean zBooleanValue2 = ((Boolean) serializable).booleanValue();
                            aVar2.U = zBooleanValue2;
                            AppContext.putBoolean("config_filter_unspoken", zBooleanValue2);
                            break;
                    }
                    return true;
                }
            });
            SeekBarPreference seekBarPreference = (SeekBarPreference) R("config_max_sentence_length");
            if (seekBarPreference.B() < 32) {
                seekBarPreference.C(h7.e.j());
            }
            final int i16 = 7;
            seekBarPreference.y(new w1.l() { // from class: f7.g
                @Override // w1.l
                public final boolean b(Serializable serializable) {
                    z6.a aVar2 = aVar;
                    switch (i16) {
                        case 0:
                            // OBF: int i17 = SettingActivity.a.r0;   // jadx 坏代码: dex 中不存在 SettingActivity.a.r0; 该局部变量未被使用
                            String str2 = (String) serializable;
                            aVar2.Q = str2;
                            AppContext.putString("config_language", str2);
                            AppContext.showToast(str2);
                            break;
                        case 1:
                            // OBF: int i18 = SettingActivity.a.r0;   // jadx 坏代码: dex 中不存在 SettingActivity.a.r0; 该局部变量未被使用
                            int i19 = Integer.parseInt(serializable.toString());
                            aVar2.V = i19;
                            AppContext.putInt(i19, "config_name_discoverer");
                            break;
                        case 2:
                            // OBF: int i110 = SettingActivity.a.r0;   // jadx 坏代码: dex 中不存在 SettingActivity.a.r0; 该局部变量未被使用
                            int i111 = Integer.parseInt(serializable.toString());
                            aVar2.W = i111;
                            AppContext.putInt(i111, "config_language_detector");
                            O6.d.d();
                            break;
                        case 3:
                            // OBF: int i112 = SettingActivity.a.r0;   // jadx 坏代码: dex 中不存在 SettingActivity.a.r0; 该局部变量未被使用
                            int i113 = Integer.parseInt(serializable.toString());
                            aVar2.X = i113;
                            AppContext.putInt(i113, "config_audio_processor");
                            break;
                        case 4:
                            // OBF: int i114 = SettingActivity.a.r0;   // jadx 坏代码: dex 中不存在 SettingActivity.a.r0; 该局部变量未被使用
                            int i20 = Integer.parseInt(serializable.toString());
                            aVar2.Y = i20;
                            AppContext.putInt(i20, "config_min_sample_rate");
                            R6.i.e();
                            break;
                        case 5:
                            // OBF: int i21 = SettingActivity.a.r0;   // jadx 坏代码: dex 中不存在 SettingActivity.a.r0; 该局部变量未被使用
                            boolean zBooleanValue = ((Boolean) serializable).booleanValue();
                            aVar2.T = zBooleanValue;
                            AppContext.putBoolean("config_separate_english", zBooleanValue);
                            break;
                        case 6:
                            // OBF: int i22 = SettingActivity.a.r0;   // jadx 坏代码: dex 中不存在 SettingActivity.a.r0; 该局部变量未被使用
                            int iIntValue = ((Integer) serializable).intValue();
                            aVar2.h = iIntValue;
                            AppContext.putInt(iIntValue, "config_max_auto_checked_person");
                            break;
                        case 7:
                            // OBF: int i23 = SettingActivity.a.r0;   // jadx 坏代码: dex 中不存在 SettingActivity.a.r0; 该局部变量未被使用
                            int iIntValue2 = ((Integer) serializable).intValue();
                            aVar2.i = iIntValue2;
                            AppContext.putInt(iIntValue2, "config_max_sentence_length");
                            break;
                        default:
                            // OBF: int i24 = SettingActivity.a.r0;   // jadx 坏代码: dex 中不存在 SettingActivity.a.r0; 该局部变量未被使用
                            boolean zBooleanValue2 = ((Boolean) serializable).booleanValue();
                            aVar2.U = zBooleanValue2;
                            AppContext.putBoolean("config_filter_unspoken", zBooleanValue2);
                            break;
                    }
                    return true;
                }
            });
            final int i17 = 0;
            R("about_grateful").f = new w1.m() { // from class: f7.i

                /* JADX INFO: renamed from: b, reason: collision with root package name */
                public final /* synthetic */ SettingActivity.a b;

                {
                    this.b = SettingActivity.a.this;   // OBF: 原为 jadx 误渲染的构造参数 this
                }

                @Override // w1.m
                public final void l(Preference preference) {
                    SettingActivity.a aVar2 = this.b;
                    switch (i17) {
                        case 0:
                            // OBF: int i18 = SettingActivity.a.r0;   // jadx 坏代码: dex 中不存在 SettingActivity.a.r0; 该局部变量未被使用
                            aVar2.W(aVar2.L(), "grateful.html", aVar2.L().getString(2131886607));
                            break;
                        case 1:
                            // OBF: int i19 = SettingActivity.a.r0;   // jadx 坏代码: dex 中不存在 SettingActivity.a.r0; 该局部变量未被使用
                            aVar2.W(aVar2.L(), "license.html", aVar2.L().getString(2131886608));
                            break;
                        default:
                            // OBF: int i20 = SettingActivity.a.r0;   // jadx 坏代码: dex 中不存在 SettingActivity.a.r0; 该局部变量未被使用
                            final Context contextL = aVar2.L();
                            B4.a aVar3 = new B4.a(contextL, 2131952281);
                            aVar3.q(2131886190);
                            g c0307g = (g) aVar3.c;
                            c0307g.f = c0307g.a.getText(2131886191);
                            c0307g.l = false;
                            c0307g.g = c0307g.a.getText(2131886350);
                            c0307g.h = null;
                            DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() { // from class: f7.h
                                @Override // android.content.DialogInterface.OnClickListener
                                public final void onClick(DialogInterface dialogInterface, int i21) {
                                    // OBF: int i22 = SettingActivity.a.r0;   // jadx 坏代码: dex 中不存在 SettingActivity.a.r0; 该局部变量未被使用
                                    ((ActivityManager) contextL.getApplicationContext().getSystemService("activity")).clearApplicationUserData();
                                    Process.killProcess(Process.myPid());
                                    System.exit(0);
                                }
                            };
                            c0307g.i = c0307g.a.getText(2131886431);
                            c0307g.j = onClickListener;
                            aVar3.r();
                            break;
                    }
                }
            };
            R("about_license").f = new w1.m() { // from class: f7.i

                /* JADX INFO: renamed from: b, reason: collision with root package name */
                public final /* synthetic */ SettingActivity.a b;

                {
                    this.b = SettingActivity.a.this;   // OBF: 原为 jadx 误渲染的构造参数 this
                }

                @Override // w1.m
                public final void l(Preference preference) {
                    SettingActivity.a aVar2 = this.b;
                    switch (i8) {
                        case 0:
                            // OBF: int i18 = SettingActivity.a.r0;   // jadx 坏代码: dex 中不存在 SettingActivity.a.r0; 该局部变量未被使用
                            aVar2.W(aVar2.L(), "grateful.html", aVar2.L().getString(2131886607));
                            break;
                        case 1:
                            // OBF: int i19 = SettingActivity.a.r0;   // jadx 坏代码: dex 中不存在 SettingActivity.a.r0; 该局部变量未被使用
                            aVar2.W(aVar2.L(), "license.html", aVar2.L().getString(2131886608));
                            break;
                        default:
                            // OBF: int i20 = SettingActivity.a.r0;   // jadx 坏代码: dex 中不存在 SettingActivity.a.r0; 该局部变量未被使用
                            final Context contextL = aVar2.L();
                            B4.a aVar3 = new B4.a(contextL, 2131952281);
                            aVar3.q(2131886190);
                            g c0307g = (g) aVar3.c;
                            c0307g.f = c0307g.a.getText(2131886191);
                            c0307g.l = false;
                            c0307g.g = c0307g.a.getText(2131886350);
                            c0307g.h = null;
                            DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() { // from class: f7.h
                                @Override // android.content.DialogInterface.OnClickListener
                                public final void onClick(DialogInterface dialogInterface, int i21) {
                                    // OBF: int i22 = SettingActivity.a.r0;   // jadx 坏代码: dex 中不存在 SettingActivity.a.r0; 该局部变量未被使用
                                    ((ActivityManager) contextL.getApplicationContext().getSystemService("activity")).clearApplicationUserData();
                                    Process.killProcess(Process.myPid());
                                    System.exit(0);
                                }
                            };
                            c0307g.i = c0307g.a.getText(2131886431);
                            c0307g.j = onClickListener;
                            aVar3.r();
                            break;
                    }
                }
            };
            R("about_disclaimer").f = new w1.m() { // from class: f7.i

                /* JADX INFO: renamed from: b, reason: collision with root package name */
                public final /* synthetic */ SettingActivity.a b;

                {
                    this.b = SettingActivity.a.this;   // OBF: 原为 jadx 误渲染的构造参数 this
                }

                @Override // w1.m
                public final void l(Preference preference) {
                    SettingActivity.a aVar2 = this.b;
                    switch (i9) {
                        case 0:
                            // OBF: int i18 = SettingActivity.a.r0;   // jadx 坏代码: dex 中不存在 SettingActivity.a.r0; 该局部变量未被使用
                            aVar2.W(aVar2.L(), "grateful.html", aVar2.L().getString(2131886607));
                            break;
                        case 1:
                            // OBF: int i19 = SettingActivity.a.r0;   // jadx 坏代码: dex 中不存在 SettingActivity.a.r0; 该局部变量未被使用
                            aVar2.W(aVar2.L(), "license.html", aVar2.L().getString(2131886608));
                            break;
                        default:
                            // OBF: int i20 = SettingActivity.a.r0;   // jadx 坏代码: dex 中不存在 SettingActivity.a.r0; 该局部变量未被使用
                            final Context contextL = aVar2.L();
                            B4.a aVar3 = new B4.a(contextL, 2131952281);
                            aVar3.q(2131886190);
                            g c0307g = (g) aVar3.c;
                            c0307g.f = c0307g.a.getText(2131886191);
                            c0307g.l = false;
                            c0307g.g = c0307g.a.getText(2131886350);
                            c0307g.h = null;
                            DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() { // from class: f7.h
                                @Override // android.content.DialogInterface.OnClickListener
                                public final void onClick(DialogInterface dialogInterface, int i21) {
                                    // OBF: int i22 = SettingActivity.a.r0;   // jadx 坏代码: dex 中不存在 SettingActivity.a.r0; 该局部变量未被使用
                                    ((ActivityManager) contextL.getApplicationContext().getSystemService("activity")).clearApplicationUserData();
                                    Process.killProcess(Process.myPid());
                                    System.exit(0);
                                }
                            };
                            c0307g.i = c0307g.a.getText(2131886431);
                            c0307g.j = onClickListener;
                            aVar3.r();
                            break;
                    }
                }
            };
        }

        public final boolean U() {
            Context contextL = L();
            String packageName = contextL.getPackageName();
            PowerManager powerManager = (PowerManager) contextL.getSystemService("power");
            if (Build.VERSION.SDK_INT >= 23) {
                return powerManager.isIgnoringBatteryOptimizations(packageName);
            }
            return true;
        }

        public final void V() {
            for (String str : h7.d.m("data/natures.txt").split("\n")) {
                String[] strArrSplit = str.split("=");
                this.q0.put(S6.b.c(strArrSplit[0]), S6.b.c(strArrSplit[1]));
            }
        }
    }

    public static boolean checkRegex(Context context, String str) {
        try {
            Pattern.compile(str);
            return true;
        } catch (Exception e8) {
            B4.a aVar = new B4.a(context, 2131952281);
            String string = context.getString(2131886430);
            g c0307g = (g) aVar.c;
            c0307g.d = string;
            c0307g.f = e8.getLocalizedMessage();
            aVar.p("OK", new a7.e(2));
            aVar.r();
            return false;
        }
    }

    @Override // h.m, b.l, android.app.Activity
    public final void onActivityResult(int i, int i8, Intent intent) {
        super.onActivityResult(i, i8, intent);
        if (intent == null) {
            return;
        }
        Toast.makeText(this, getString(2131886429), 1).show();
        h7.e.k().execute(new J(18, this, intent));
    }

    @Override // h.m, b.l, F.i, android.app.Activity
    public final void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        A6.g gVarInflate = A6.g.inflate(getLayoutInflater());
        setContentView(gVarInflate.a);
        s(gVarInflate.b);
        var aVarL = l(); // OBF: 原为 a.a；成员类 a 遮蔽 import a.a 的简单名，a.a 又无法限定引用，故用 var（语义与 dex 一致）
        if (aVarL != null) {
            aVarL.S(true);
            aVarL.X(2131886144);
        }
        if (bundle == null) {
            g0.J j2 = ((g0.u) this.s.b).d;
            j2.getClass();
            g0.a c0280a = new g0.a(j2);
            c0280a.j(2131296826, new a(), null);
            c0280a.e(false);
        }
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
