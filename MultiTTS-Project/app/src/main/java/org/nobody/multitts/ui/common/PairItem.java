package org.nobody.multitts.ui.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/* JADX INFO: loaded from: D:\mtts_work\dex\classes3.dex */
public class PairItem {
    public String name;
    public String value;

    public PairItem(String str, String str2) {
        this.name = str;
        this.value = str2;
    }

    public static PairItem findPairByValue(List<PairItem> list, String str) {
        for (PairItem pairItem : list) {
            if (pairItem.value.equals(str)) {
                return pairItem;
            }
        }
        return null;
    }

    public static List<PairItem> map2PairList(Map<String, String> map) {
        ArrayList arrayList = new ArrayList(map.size());
        for (Map.Entry<String, String> entry : map.entrySet()) {
            arrayList.add(new PairItem(entry.getValue(), entry.getKey()));
        }
        return arrayList;
    }

    public static Map<String, String> pairList2Map(List<PairItem> list) {
        HashMap map = new HashMap(list.size());
        for (PairItem pairItem : list) {
            map.put(pairItem.value, pairItem.name);
        }
        return map;
    }

    public String toString() {
        return this.name;
    }

    public PairItem(String str, String str2, boolean z5) {
        this.name = str2;
        this.value = str;
    }
}
