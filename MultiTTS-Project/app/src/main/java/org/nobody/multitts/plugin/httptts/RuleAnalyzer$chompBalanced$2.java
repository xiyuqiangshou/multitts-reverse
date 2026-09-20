package org.nobody.multitts.plugin.httptts;

import com.google.common.base.Ascii;
import kotlin.jvm.internal.h;
import o0.c;

/* JADX INFO: loaded from: D:\mtts_work\dex\classes3.dex */
public class RuleAnalyzer$chompBalanced$2 extends h implements BooleanFunc {
    public RuleAnalyzer$chompBalanced$2(Object obj) {
        super(2, obj, RuleAnalyzer.class, "chompRuleBalanced", "chompRuleBalanced(CC)Z", 0);
    }

    @Override // org.nobody.multitts.plugin.httptts.BooleanFunc
    public final Boolean invoke(char c8, char c9) {
        return Boolean.valueOf(((RuleAnalyzer) this.receiver).chompRuleBalanced(c8, c9));
    }

    public Boolean invoke(Character ch, Character ch2) {
        return invoke(ch.charValue(), ch2.charValue());
    }
}
