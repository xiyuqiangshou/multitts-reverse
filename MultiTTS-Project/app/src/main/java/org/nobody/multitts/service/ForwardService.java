package org.nobody.multitts.service;


import G6.b;
import R6.d;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.IBinder;
import android.os.PowerManager;
import android.service.quicksettings.TileService;
import android.widget.Toast;
import com.google.common.base.Ascii;
import com.google.common.primitives.SignedBytes;
import h7.e;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import org.nobody.multitts.ui.main.MainActivity;
import k5.i;
import o0.c;
import z6.a;

/* JADX INFO: loaded from: D:\mtts_work\dex\classes3.dex */
public class ForwardService extends Service implements d {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public PowerManager.WakeLock b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public WifiManager.WifiLock c;

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    public Notification.BigTextStyle d;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public Notification.Builder e;

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public b a = null;

    /* JADX INFO: renamed from: f, reason: collision with root package name */
    public final a f = z6.a.a0;

    public final void a() {
        PowerManager.WakeLock wakeLockNewWakeLock = ((PowerManager) getSystemService("power")).newWakeLock(536870913, ForwardService.class.getName());
        this.b = wakeLockNewWakeLock;
        wakeLockNewWakeLock.acquire(1200000L);
    }

    public final void b() {
        WifiManager.WifiLock wifiLockCreateWifiLock = ((WifiManager) getApplicationContext().getSystemService("wifi")).createWifiLock(3, ForwardService.class.getName());
        this.c = wifiLockCreateWifiLock;
        wifiLockCreateWifiLock.acquire();
        int i = Build.VERSION.SDK_INT;
        if (i >= 26) {
            G0.h.k();
            NotificationChannel notificationChannelA = G0.h.a("MultiTTS_Forward", getString(2131886282));
            notificationChannelA.setLockscreenVisibility(0);
            ((NotificationManager) getSystemService("notification")).createNotificationChannel(notificationChannelA);
        }
        this.d = new Notification.BigTextStyle();
        Notification.Builder contentText = new Notification.Builder(this).setSmallIcon(2131231000).setOnlyAlertOnce(true).setVibrate(null).setSound(null).setLights(0, 0, 0).setContentIntent(PendingIntent.getActivity(this, 0, new Intent(this, (Class<?>) MainActivity.class), i >= 31 ? 67108864 : 0)).setContentTitle(getString(2131886437)).setContentText(getString(2131886387));
        this.e = contentText;
        if (i >= 26) {
            contentText.setChannelId("MultiTTS_Forward");
        }
        Notification notificationBuild = this.e.build();
        if (i < 33) {
            startForeground(20, notificationBuild);
        } else {
            startForeground(20, notificationBuild, 2);
        }
    }

    @Override // R6.d
    public final void f(String str, String str2) {
        a aVar = this.f;
        if (aVar.w) {
            PowerManager.WakeLock wakeLock = this.b;
            if (wakeLock == null) {
                a();
            } else if (!wakeLock.isHeld()) {
                this.b.acquire(1200000L);
            }
        }
        if (aVar.o) {
            if (this.e == null) {
                b();
            }
            this.e.setContentTitle(str);
            this.e.setStyle(this.d.bigText(str2));
            if (Build.VERSION.SDK_INT < 33) {
                startForeground(20, this.e.build());
            } else {
                startForeground(20, this.e.build(), 2);
            }
        }
    }

    @Override // android.app.Service
    public final IBinder onBind(Intent intent) {
        return null;
    }

    @Override // android.app.Service
    public final void onCreate() {
        a aVar = this.f;
        super.onCreate();
        b bVar = new b(this, this);
        this.a = bVar;
        try {
            bVar.f();
            if (aVar.o) {
                b();
            }
            Toast.makeText(this, getString(2131886388), 1).show();
            if (aVar.w) {
                a();
            }
            if (Build.VERSION.SDK_INT >= 24) {
                TileService.requestListeningState(this, new ComponentName(this, (Class<?>) QuickHttpTileService.class));
            }
        } catch (IOException e8) {
            h7.e.e("ForwardService", "onCreate: ", e8);
        }
    }

    @Override // android.app.Service
    public final void onDestroy() {
        super.onDestroy();
        b bVar = this.a;
        if (bVar != null) {
            try {
                i.d(bVar.b);
                J6.a aVar = bVar.d;
                aVar.getClass();
                for (k5.a aVar2 : new ArrayList<k5.a>((List<k5.a>) aVar.c)) {
                    i.d(aVar2.a);
                    i.d(aVar2.b);
                }
                Thread thread = bVar.c;
                if (thread != null) {
                    thread.join();
                }
            } catch (Exception e8) {
                i.f.log(Level.SEVERE, "Could not stop all connections", (Throwable) e8);
            }
            bVar.i.a();
        }
        PowerManager.WakeLock wakeLock = this.b;
        if (wakeLock != null) {
            wakeLock.release();
        }
        WifiManager.WifiLock wifiLock = this.c;
        if (wifiLock != null && wifiLock.isHeld()) {
            this.c.release();
        }
        stopForeground(true);
        Toast.makeText(this, getString(2131886389), 1).show();
        if (Build.VERSION.SDK_INT >= 24) {
            TileService.requestListeningState(this, new ComponentName(this, (Class<?>) QuickHttpTileService.class));
        }
    }

    @Override // android.app.Service
    public final int onStartCommand(Intent intent, int i, int i8) {
        return 0;
    }
}
