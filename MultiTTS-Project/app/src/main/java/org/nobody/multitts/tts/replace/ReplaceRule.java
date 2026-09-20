package org.nobody.multitts.tts.replace;

import T6.a;
import com.google.common.base.Ascii;
import java.util.regex.Pattern;
import o0.c;

/* JADX INFO: loaded from: D:\mtts_work\dex\classes3.dex */
public class ReplaceRule implements a, Comparable<ReplaceRule> {
    public String group;
    public String name;
    public Pattern pattern;
    public String source;
    public String target;
    public boolean regex = false;
    public boolean activate = true;

    @Override // T6.a
    public String getGroup() {
        return this.group;
    }

    @Override // T6.a
    public boolean isActivate() {
        return this.activate;
    }

    public String toString() {
        return "ReplaceRule{name='" + this.name + '\'' + ", pattern=" + this.pattern + ", source='" + this.source + '\'' + ", target='" + this.target + '\'' + ", group='" + this.group + '\'' + ", regex=" + this.regex + ", activate=" + this.activate + '}';
    }

    @Override // java.lang.Comparable
    public int compareTo(ReplaceRule replaceRule) {
        String str;
        String str2 = this.group;
        if (str2 == null || (str = replaceRule.group) == null) {
            throw new IllegalArgumentException("group field of ReplaceRule cannot be null, this=" + this + ", other=" + replaceRule);
        }
        if (this.name != null && replaceRule.name != null) {
            return this.name.compareTo(replaceRule.name) + (str2.compareTo(str) * 10000);
        }
        throw new IllegalArgumentException("name field of ReplaceRule cannot be null, this=" + this + ", other=" + replaceRule);
    }
}
