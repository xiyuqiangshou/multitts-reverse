package org.nobody.multitts.tts.role;

import T6.a;
import com.google.common.base.Ascii;
import com.google.common.primitives.SignedBytes;
import com.google.common.primitives.UnsignedBytes;
import java.util.regex.Pattern;
import okio.Utf8;
import g0.U;
import o0.c;

/* JADX INFO: loaded from: D:\mtts_work\dex\classes3.dex */
public class RoleRule implements a {
    public static final int ROLE_RULE_MODE_LANG = 1;
    public static final int ROLE_RULE_MODE_ROLE = 2;
    public static final int ROLE_RULE_MODE_TEXT = 0;
    public boolean activate;
    public String group;
    public boolean isAbstract;
    public boolean matchAfter;
    public boolean matchBefore;
    public String message;
    public int mode;
    public String name;
    public Pattern pattern;
    public String regex;
    public String speakerId;
    public String speakerName;
    public static final String MARK_RULE_NAME = "lang:mark";
    public static final String LATIN_RULE_NAME = "lang:latin";

    public RoleRule() {
        this.mode = 2;
    }

    @Override // T6.a
    public String getGroup() {
        return this.group;
    }

    @Override // T6.a
    public boolean isActivate() {
        return this.activate;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("RoleRule{activate=");
        sb.append(this.activate);
        sb.append(", isAbstract=");
        sb.append(this.isAbstract);
        sb.append(", matchAfter=");
        sb.append(this.matchAfter);
        sb.append(", matchBefore=");
        sb.append(this.matchBefore);
        sb.append(", message='");
        sb.append(this.message);
        sb.append('\'');
        sb.append(", name='");
        sb.append(this.name);
        sb.append('\'');
        sb.append(", group='");
        sb.append(this.group);
        sb.append('\'');
        sb.append(", pattern=");
        sb.append(this.pattern);
        sb.append(", regex='");
        sb.append(this.regex);
        sb.append('\'');
        sb.append(", speakerId='");
        sb.append(this.speakerId);
        sb.append('\'');
        sb.append(", speakerName='");
        return U.o(sb, this.speakerName, "'}");
    }

    public RoleRule(String str) {
        this.activate = true;
        this.matchBefore = true;
        this.matchAfter = true;
        this.isAbstract = false;
        this.name = str;
        this.mode = 2;
    }

    public RoleRule(String str, String str2, boolean z5) {
        this.activate = true;
        this.matchBefore = true;
        this.matchAfter = true;
        this.name = str;
        this.regex = str2;
        this.isAbstract = z5;
        this.mode = 2;
    }

    public RoleRule(String str, String str2) {
        this(str);
        if (str2 != null) {
            this.regex = "lang:".concat(str2);
            this.mode = 1;
        }
    }

    public RoleRule(String str, String str2, int i) {
        this(str, str2, false);
        this.mode = i;
    }
}
