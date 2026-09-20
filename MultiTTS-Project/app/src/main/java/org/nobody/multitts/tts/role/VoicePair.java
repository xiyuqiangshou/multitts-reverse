package org.nobody.multitts.tts.role;

import B6.c;
import B6.d;
import B6.e;
import androidx.room.v;
import com.google.common.base.Ascii;
import java.util.List;
import okio.Utf8;
import org.nobody.multitts.AppContext;
import org.nobody.multitts.db.AppDatabase;
import org.nobody.multitts.db.AppDatabase_Impl;
import c2.a;


/* JADX INFO: loaded from: D:\mtts_work\dex\classes3.dex */
public class VoicePair {
    public String person;
    public String voice;

    public VoicePair() {
        this.person = "";
    }

    public static void add(VoicePair voicePair) {
        AppDatabase appDatabaseCreateVoicePairDatabase = createVoicePairDatabase();
        a aVar = (a) appDatabaseCreateVoicePairDatabase.voicePairDao();
        aVar.getClass();
        r2.q.C((AppDatabase_Impl) aVar.b, false, true, new c(6, aVar, voicePair));
        appDatabaseCreateVoicePairDatabase.close();
    }

    public static AppDatabase createVoicePairDatabase() {
        v vVarP = r2.q.p(AppContext.getApplication(), "voicepair");
        vVarP.b();
        vVarP.h = true;
        return (AppDatabase) vVarP.a();
    }

    public static void delete(String str) {
        AppDatabase appDatabaseCreateVoicePairDatabase = createVoicePairDatabase();
        a aVar = (a) appDatabaseCreateVoicePairDatabase.voicePairDao();
        aVar.getClass();
        // [R8 已移除的调用] 原字符串: "DELETE FROM voicepair WHERE person = ?"
        r2.q.C((AppDatabase_Impl) aVar.b, false, true, new e(str, 10));
        appDatabaseCreateVoicePairDatabase.close();
    }

    public static List<VoicePair> load() {
        AppDatabase appDatabaseCreateVoicePairDatabase = createVoicePairDatabase();
        a aVar = (a) appDatabaseCreateVoicePairDatabase.voicePairDao();
        aVar.getClass();
        // [R8 已移除的调用] 原字符串: "SELECT * FROM voicepair"
        List<VoicePair> list = (List) r2.q.C((AppDatabase_Impl) aVar.b, true, false, new d(11));
        appDatabaseCreateVoicePairDatabase.close();
        return list;
    }

    public static void save(List<VoicePair> list) {
        AppDatabase appDatabaseCreateVoicePairDatabase = createVoicePairDatabase();
        a aVar = (a) appDatabaseCreateVoicePairDatabase.voicePairDao();
        aVar.getClass();
        // [R8 已移除的调用] 原字符串: "DELETE FROM voicepair"
        r2.q.C((AppDatabase_Impl) aVar.b, false, true, new d(12));
        r2.q.C((AppDatabase_Impl) aVar.b, false, true, new c(5, aVar, list));
        appDatabaseCreateVoicePairDatabase.close();
    }

    public VoicePair(String str, String str2) {
        this.person = str;
        this.voice = str2;
    }
}
