package org.nobody.multitts.ui.other;

import h.m;

import A6.c;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import h7.d;
import h7.e;
import h7.h;
import java.io.File;
import a.a;
import a7.b;
import a7.j;


/* JADX INFO: loaded from: D:\mtts_work\dex\classes3.dex */
public class LogsActivity extends m {

    /* JADX INFO: renamed from: B, reason: collision with root package name */
    public static final /* synthetic */ int B = 0;

    /* JADX INFO: renamed from: A, reason: collision with root package name */
    public File A;

    /* JADX INFO: renamed from: y, reason: collision with root package name */
    public final b y = new b();

    /* JADX INFO: renamed from: z, reason: collision with root package name */
    public c z;

    @Override // h.m, b.l, F.i, android.app.Activity
    public final void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        c cVarInflate = A6.c.inflate(getLayoutInflater());
        this.z = cVarInflate;
        setContentView(cVarInflate.a);
        s(this.z.c);
        if (l() != null) {
            l().S(true);
            a aVarL = l();
            File fileI = h7.d.i(this);
            this.A = fileI;
            aVarL.Y(fileI != null ? fileI.getName() : getString(2131886149));
        }
        RecyclerView recyclerView = this.z.b;
        b bVar = this.y;
        recyclerView.setAdapter(bVar);
        recyclerView.setLayoutManager(new LinearLayoutManager(1));
        if (this.A != null) {
            h7.e.k().execute(new j(this, 1));
        }
        h7.h.c.a = bVar;
    }

    @Override // android.app.Activity
    public final boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(2131623937, menu);
        return true;
    }

    @Override // h.m, android.app.Activity
    public final void onDestroy() {
        super.onDestroy();
        this.z = null;
        h7.h.c.a = null;
    }

    @Override // android.app.Activity
    public final boolean onOptionsItemSelected(MenuItem menuItem) {
        int itemId = menuItem.getItemId();
        if (itemId == 16908332) {
            finish();
            return true;
        }
        if (itemId != 2131296315) {
            if (itemId != 2131296320) {
                return super.onOptionsItemSelected(menuItem);
            }
            h7.e.k().execute(new j(this, 0));
            return true;
        }
        File file = this.A;
        if (file != null && file.exists()) {
            this.A.delete();
        }
        b bVar = this.y;
        bVar.e.clear();
        bVar.i();
        return true;
    }
}
