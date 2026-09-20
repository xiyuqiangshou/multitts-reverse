package org.nobody.multitts.ui.common;

import U3.n;
import android.text.TextUtils;
import com.google.common.base.Ascii;
import com.google.common.primitives.SignedBytes;
import com.google.common.primitives.UnsignedBytes;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import h7.e;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import okio.Utf8;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import o0.c;

/* JADX INFO: loaded from: D:\mtts_work\dex\classes3.dex */
public class ExtendUI {
    private static final String TAG = "ExtendUI";
    public Map<String, String> candidate;
    public String code;
    public String name;
    public String value;

    /* JADX WARN: Code duplicated, block: B:7:0x001b  */
    public static ExtendUI create(String str, String str2, Map<String, String> map) {
        String next;
        if (map.isEmpty()) {
            next = "";
        } else {
            Iterator<String> it = map.keySet().iterator();
            if (it.hasNext()) {
                next = it.next();
            } else {
                next = "";
            }
        }
        return create(str, str2, next, map);
    }

    public static String getRefValue(String str, String str2) {
        return !str2.startsWith("@") ? str2 : parseExtendParams(str).get(str2);
    }

    public static Map<String, String> parseExtendParams(String str) {
        if (TextUtils.isEmpty(str)) {
            return Collections.emptyMap();
        }
        HashMap map = new HashMap();
        try {
            // U3.n.b(String, Type) 的返回类型是 Object（泛型已擦除），必须自己补上元素类型。
            for (ExtendUI extendUI : (List<ExtendUI>) new n().b(str, new TypeToken<List<ExtendUI>>() { // from class: org.nobody.multitts.ui.common.ExtendUI.1
            }.getType())) {
                if (extendUI != null && !TextUtils.isEmpty(extendUI.name)) {
                    map.put('@' + extendUI.code, extendUI.value);
                }
            }
        } catch (JsonSyntaxException e8) {
            e.e(TAG, "parseExtendParams: ", e8);
        }
        return map;
    }

    public String toJsonArrayString() {
        JSONArray jSONArray = new JSONArray();
        JSONObject jSONObject = new JSONObject();
        try {
            jSONObject.put("code", this.code);
            jSONObject.put("name", this.name);
            jSONObject.put("value", this.value);
            JSONObject jSONObject2 = new JSONObject();
            for (Map.Entry<String, String> entry : this.candidate.entrySet()) {
                jSONObject2.put(entry.getKey(), entry.getValue());
            }
            jSONObject.put("candidate", jSONObject2);
            jSONArray.put(0, jSONObject);
            return jSONArray.toString(2);
        } catch (JSONException e8) {
            e.e(TAG, "toJsonString: ", e8);
            return "";
        }
    }

    public String toJsonString() {
        JSONObject jSONObject = new JSONObject();
        try {
            jSONObject.put("code", this.code);
            jSONObject.put("name", this.name);
            jSONObject.put("value", this.value);
            JSONObject jSONObject2 = new JSONObject();
            for (Map.Entry<String, String> entry : this.candidate.entrySet()) {
                jSONObject2.put(entry.getKey(), entry.getValue());
            }
            jSONObject.put("candidate", jSONObject2);
            return jSONObject.toString(2);
        } catch (JSONException e8) {
            e.e(TAG, "toJsonString: ", e8);
            return "";
        }
    }

    public String toString() {
        return "ExtendUI{name='" + this.name + "', code='" + this.code + "', value='" + this.value + "', candidate=" + this.candidate + '}';
    }

    public static ExtendUI create(String str, String str2, String str3, Map<String, String> map) {
        ExtendUI extendUI = new ExtendUI();
        extendUI.code = str;
        extendUI.name = str2;
        extendUI.value = str3;
        extendUI.candidate = new LinkedHashMap(map);
        return extendUI;
    }
}
