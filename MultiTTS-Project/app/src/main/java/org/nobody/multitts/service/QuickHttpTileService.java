package org.nobody.multitts.service;

import android.content.Intent;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;
import com.google.common.base.Ascii;
import h7.e;
import o0.c;
import z6.a;

/* JADX INFO: loaded from: D:\mtts_work\dex\classes3.dex */
public class QuickHttpTileService extends TileService {
    @Override // android.service.quicksettings.TileService
    public final void onClick() {
        Tile qsTile = getQsTile();
        int state = qsTile.getState();
        // [R8 已移除的调用] 原字符串: "onClick: tile state="
        if (state == 1) {
            qsTile.setState(2);
            a.a0.f(true);
            if (!e.q(this)) {
                startService(new Intent(this, (Class<?>) ForwardService.class));
            }
        } else if (state == 2) {
            qsTile.setState(1);
            a.a0.f(false);
            if (e.q(this)) {
                stopService(new Intent(this, (Class<?>) ForwardService.class));
            }
        }
        qsTile.updateTile();
    }
}
