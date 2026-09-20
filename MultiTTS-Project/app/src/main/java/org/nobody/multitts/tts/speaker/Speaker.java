package org.nobody.multitts.tts.speaker;

import K6.d;
import android.content.Context;
import android.text.TextUtils;
import com.google.common.base.Ascii;
import com.google.common.primitives.SignedBytes;
import com.google.common.primitives.UnsignedBytes;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import okio.Utf8;
import o0.c;

/* JADX INFO: loaded from: D:\mtts_work\dex\classes3.dex */
public class Speaker {
    public String avatar;
    public String code;
    public String desc;
    public String extendUI;
    public short gender;
    public String group;
    public String id;
    public String locale;
    public String name;
    public String note;
    public String param;
    public float pitch;
    public int sampleRate;
    public float speed;
    public short type;
    public float volume;

    public Speaker() {
        this.pitch = 1.0f;
    }

    public Set<String> getFeatures(Context context) {
        HashSet hashSet = new HashSet();
        hashSet.add(this.name);
        if (this.type != 0) {
            hashSet.add(context.getString(2131886559));
        }
        hashSet.add(context.getString(this.gender == 0 ? 2131886278 : 2131886309));
        if (!TextUtils.isEmpty(this.desc)) {
            hashSet.add(this.desc);
        }
        return hashSet;
    }

    public String getShortName() {
        return d.f(this.group) + "•" + this.name;
    }

    public String toString() {
        return "Speaker{code='" + this.code + '\'' + ", avatar='" + this.avatar + '\'' + ", desc='" + this.desc + '\'' + ", extendUI='" + this.extendUI + '\'' + ", gender=" + ((int) this.gender) + ", group='" + this.group + '\'' + ", id='" + this.id + '\'' + ", name='" + this.name + '\'' + ", note='" + this.note + '\'' + ", param='" + this.param + '\'' + ", locale='" + this.locale + '\'' + ", sampleRate=" + this.sampleRate + ", speed=" + this.speed + ", type=" + ((int) this.type) + ", volume=" + this.volume + ", pitch=" + this.pitch + '}';
    }

    public Speaker(String str) {
        this.type = (short) 1;
        this.gender = (short) 0;
        this.speed = 1.0f;
        this.volume = 1.0f;
        this.pitch = 1.0f;
        this.sampleRate = 16000;
        this.group = str;
        Locale locale = Locale.getDefault();
        this.locale = locale.getLanguage() + "-" + locale.getCountry();
    }

    public Speaker(String str, String str2) {
        this(str);
        this.id = str2;
    }

    public Speaker(Map<Object, Object> map) {
        this.pitch = 1.0f;
        Object obj = map.get("id");
        if (obj != null) {
            this.id = obj.toString();
        }
        Object obj2 = map.get("code");
        if (obj2 != null) {
            this.code = obj2.toString();
        }
        Object obj3 = map.get("name");
        if (obj3 != null) {
            this.name = obj3.toString();
        }
        Object obj4 = map.get("avatar");
        if (obj4 != null) {
            this.avatar = obj4.toString();
        }
        Object obj5 = map.get("desc");
        if (obj5 != null) {
            this.desc = obj5.toString();
        }
        Object obj6 = map.get("group");
        if (obj6 != null) {
            this.group = obj6.toString();
        }
        Object obj7 = map.get("param");
        if (obj7 != null) {
            this.param = obj7.toString();
        }
        Object obj8 = map.get("extendUI");
        if (obj8 != null) {
            this.extendUI = obj8.toString();
        }
        Object obj9 = map.get("type");
        if (obj9 != null) {
            this.type = ((Integer) obj9).shortValue();
        }
        Object obj10 = map.get("gender");
        if (obj10 != null) {
            this.gender = ((Integer) obj10).shortValue();
        }
        Object obj11 = map.get("volume");
        if (obj11 != null) {
            this.volume = ((Double) obj11).floatValue();
        }
        Object obj12 = map.get("speed");
        if (obj12 != null) {
            this.speed = ((Double) obj12).floatValue();
        }
        Object obj13 = map.get("pitch");
        if (obj13 != null) {
            this.pitch = ((Double) obj13).floatValue();
        }
        Object obj14 = map.get("sampleRate");
        if (obj14 != null) {
            this.sampleRate = ((Integer) obj14).intValue();
        }
        Object obj15 = map.get("locale");
        if (obj15 != null) {
            this.locale = (String) obj15;
        }
    }
}
