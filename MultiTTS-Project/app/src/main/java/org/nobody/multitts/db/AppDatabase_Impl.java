package org.nobody.multitts.db;

import B6.b;
import B6.g;
import B6.h;
import B6.i;
import B6.k;
import B6.l;
import F6.j;
import Y1.d;

import androidx.room.D;
import com.google.common.base.Ascii;
import com.google.common.primitives.UnsignedBytes;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import c2.a;
import o0.c;


/* JADX INFO: loaded from: D:\mtts_work\dex\classes3.dex */
public final class AppDatabase_Impl extends AppDatabase {
    private volatile b _engineDao;
    private volatile i _speakerDao;
    private volatile k _voiceItemDao;
    private volatile l _voicePairDao;

    /* 【2026-09-18 补回】R8/jadx 丢掉的合成访问方法。
       —— 第三方（压平包）类 B6/a#d(H1/a) 的字节码里硬写着：
            invokestatic org/nobody/multitts/db/AppDatabase_Impl.access$000:(Lorg/nobody/multitts/db/AppDatabase_Impl;LH1/a;)V
          不补回 → 运行期 NoSuchMethodError（Room 建表/打开数据库时必走）。
       原始实现（dexorig/classes3.dex @44e644）：
            invoke-virtual {v0, v1}, Landroidx/room/C;.internalInitInvalidationTracker:(LH1/a;)V
            return-void
       ⚠️ 必须 public static：调用者 B6/a 在另一个包，包私有会 IllegalAccessError。
       ⚠️ 参数类型写全限定名 H1.a，避免与文件里已有的 `import c2.a;` 冲突。 */
    public static void access$000(AppDatabase_Impl appDatabase_Impl, H1.a h1a) {
        appDatabase_Impl.internalInitInvalidationTracker(h1a);
    }

    @Override // androidx.room.C
    public void clearAllTables() {
        performClear(false, "Speaker", "Engine", "VoiceItem", "VoicePair");
    }

    @Override // androidx.room.C
    public androidx.room.j createInvalidationTracker() {
        return new androidx.room.j(this, new HashMap(0), new HashMap(0), "Speaker", "Engine", "VoiceItem", "VoicePair");
    }

    @Override // org.nobody.multitts.db.AppDatabase
    public b engineDao() {
        b bVar;
        if (this._engineDao != null) {
            return this._engineDao;
        }
        synchronized (this) {
            try {
                if (this._engineDao == null) {
                    this._engineDao = new r2.i0(this);
                }
                bVar = this._engineDao;
            } catch (Throwable th) {
                throw th;
            }
        }
        return bVar;
    }

    @Override // androidx.room.C
    public List<Object> getAutoMigrations(Map<Class<Object>, Object> map) {
        return new ArrayList();
    }

    @Override // androidx.room.C
    public Set<Class<Object>> getRequiredAutoMigrationSpecs() {
        return new HashSet();
    }

    @Override // androidx.room.C
    public Map<Class<?>, List<Class<?>>> getRequiredTypeConverters() {
        HashMap map = new HashMap();
        map.put(b.class, Collections.emptyList());
        map.put(i.class, Collections.emptyList());
        map.put(k.class, Collections.emptyList());
        map.put(l.class, Collections.emptyList());
        return map;
    }

    @Override // org.nobody.multitts.db.AppDatabase
    public i speakerDao() {
        i iVar;
        if (this._speakerDao != null) {
            return this._speakerDao;
        }
        synchronized (this) {
            try {
                if (this._speakerDao == null) {
                    j jVar = new j();
                    jVar.a = this;
                    jVar.b = new g(1);
                    jVar.c = new h(2);
                    jVar.d = new h(3);
                    this._speakerDao = jVar;
                }
                iVar = this._speakerDao;
            } catch (Throwable th) {
                throw th;
            }
        }
        return iVar;
    }

    @Override // org.nobody.multitts.db.AppDatabase
    public k voiceItemDao() {
        k kVar;
        if (this._voiceItemDao != null) {
            return this._voiceItemDao;
        }
        synchronized (this) {
            try {
                if (this._voiceItemDao == null) {
                    this._voiceItemDao = new d(this);
                }
                kVar = this._voiceItemDao;
            } catch (Throwable th) {
                throw th;
            }
        }
        return kVar;
    }

    @Override // org.nobody.multitts.db.AppDatabase
    public l voicePairDao() {
        l lVar;
        if (this._voicePairDao != null) {
            return this._voicePairDao;
        }
        synchronized (this) {
            try {
                if (this._voicePairDao == null) {
                    this._voicePairDao = new a(this);
                }
                lVar = this._voicePairDao;
            } catch (Throwable th) {
                throw th;
            }
        }
        return lVar;
    }

    @Override // androidx.room.C
    public D createOpenDelegate() {
        return new B6.a(this, "bb83daff97c5e6fbc95f6d04237b81d6", "773c4cccc2a0607534468064b32d3149");
    }
}
