package org.nobody.multitts.tts.engine;

import B.i;
import com.google.common.base.Ascii;
import okio.Utf8;
import o0.c;

/* JADX INFO: loaded from: D:\mtts_work\dex\classes3.dex */
public class Engine {
    public String code;
    public transient int count;
    public String header;
    public String name;
    public String note;
    public String param;
    public String type;
    public String url;

    public Engine() {
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Engine{code='");
        sb.append(this.code);
        sb.append("', name='");
        sb.append(this.name);
        sb.append("', type='");
        sb.append(this.type);
        sb.append("', param='");
        sb.append(this.param);
        sb.append("', url='");
        sb.append(this.url);
        sb.append("', header='");
        sb.append(this.header);
        sb.append("', note='");
        sb.append(this.note);
        return i.q(new byte[]{Ascii.ETB, 54}, new byte[]{48, 75, 5, -88, -119, 107, -24, -43}, sb);
    }

    public Engine(String str, String str2) {
        this.code = str;
        this.name = str2;
    }
}
