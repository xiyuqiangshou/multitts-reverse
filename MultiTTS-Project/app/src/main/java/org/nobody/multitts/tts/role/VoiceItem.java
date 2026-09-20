package org.nobody.multitts.tts.role;

import T6.a;
import com.google.common.base.Ascii;
import org.nobody.multitts.ui.common.PairItem;
import o0.c;

/* JADX INFO: loaded from: D:\mtts_work\dex\classes3.dex */
public class VoiceItem extends PairItem implements a {
    public boolean activate;
    public String group;
    public String id;

    public VoiceItem() {
        super(null, null);
    }

    public void fillId() {
        this.id = this.group + "_" + this.value;
    }

    @Override // T6.a
    public String getGroup() {
        return this.group;
    }

    @Override // T6.a
    public boolean isActivate() {
        return this.activate;
    }

    public VoiceItem(String str, String str2) {
        super(str, str2);
        this.activate = true;
    }

    public VoiceItem(String str, PairItem pairItem) {
        super(pairItem.name, pairItem.value);
        this.group = str;
        this.activate = true;
    }

    public VoiceItem(String str, String str2, String str3) {
        super(str2, str3);
        this.group = str;
        this.activate = true;
    }
}
