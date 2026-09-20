package org.nobody.multitts.util;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.io.File;
import z6.a;

/**
 * BGM 列表「播放试听」按钮（任务 #24，2026-09-19）。
 *
 * 装载方式：ASM 补丁（_preview/PatchPreview.java）注入 Y6.j.n(Lz1/k0;I)V 中
 * BGM 分支第一个 setText 调用点：
 *   dup2                      // 复制 [holder.v TextView, displayName String]
 *   ...原 setText...          // 消费一份
 *   invokestatic attach(TextView, String)  // 消费剩余一份
 *
 * 试听用独立 android.media.MediaPlayer，不触碰 BGM 的 ExoPlayer（H6.f）状态机，
 * 避免与看门狗/装载链互相干扰。再点一次同一首 = 停止。
 */
public final class MusicPreview {
    private static final String TAG = "MTTSPREVIEW";
    private static final Object BTN_TAG = "mtts_preview";
    private static MediaPlayer sPlayer;
    private static String sPlayingPath;

    private MusicPreview() {
    }

    /** ASM 注入点：栈序 [tv, displayName]。每次 bind 重建按钮（防复用后 displayName 陈旧）。 */
    public static void attach(TextView tv, String displayName) {
        try {
            Object p1 = tv.getParent();
            if (!(p1 instanceof View)) {
                return;
            }
            Object p2 = ((View) p1).getParent();
            if (!(p2 instanceof LinearLayout)) {
                return; // 结构不符（引擎列表分支/异常布局）直接跳过
            }
            LinearLayout row = (LinearLayout) p2;
            View old = row.findViewWithTag(BTN_TAG);
            if (old != null) {
                row.removeView(old);
            }
            Context ctx = tv.getContext();
            ImageButton btn = new ImageButton(ctx);
            btn.setTag(BTN_TAG);
            btn.setImageResource(android.R.drawable.ic_media_play);
            btn.setBackgroundResource(android.R.color.transparent);
            btn.setContentDescription("试听");
            int px = Math.round(ctx.getResources().getDisplayMetrics().density * 26f);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(px, px);
            lp.gravity = Gravity.END;
            lp.topMargin = 2;
            lp.bottomMargin = 2;
            btn.setLayoutParams(lp);
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public final void onClick(View v) {
                    MusicPreview.toggle(tv, displayName);
                }
            });
            row.addView(btn, 1); // 插在名称行与分隔线之间
        } catch (Throwable t) {
            Log.e(TAG, "attach fail", t);
        }
    }

    private static void toggle(TextView tv, String displayName) {
        String path = resolve(tv, displayName);
        if (path == null) {
            Log.w(TAG, "no file for " + displayName);
            return;
        }
        if (sPlayer != null && path.equals(sPlayingPath)) {
            stop(); // 再点一次同一首 = 停止
            Log.d(TAG, "preview stop " + path);
            return;
        }
        stop();
        try {
            MediaPlayer p = new MediaPlayer();
            p.setAudioAttributes(new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build());
            p.setDataSource(path);
            p.prepare(); // 本地文件，同步 prepare 足够
            p.start();
            sPlayer = p;
            sPlayingPath = path;
            Log.d(TAG, "preview start " + path);
        } catch (Throwable t) {
            Log.e(TAG, "preview fail " + path, t);
            stop();
        }
    }

    private static void stop() {
        if (sPlayer != null) {
            try {
                sPlayer.stop();
            } catch (Throwable ignored) {
            }
            try {
                sPlayer.release();
            } catch (Throwable ignored) {
            }
            sPlayer = null;
        }
        sPlayingPath = null;
    }

    /**
     * 按「去扩展名」在 music 目录反查文件。
     * 注意：z6.a.l 是【已选中】路径集合（非全量文件列表），不能作为反查源；
     * 全量列表来自对话框对 music 目录的扫描，这里直接扫同目录。
     */
    private static String resolve(TextView tv, String displayName) {
        if (displayName == null) {
            return null;
        }
        try {
            File dir = new File(tv.getContext().getExternalFilesDir((String) null), "music");
            File[] files = dir.listFiles();
            if (files == null) {
                return null;
            }
            for (File f : files) {
                String name = f.getName();
                int i = name.lastIndexOf('.');
                String base = i > 0 ? name.substring(0, i) : name;
                if (base.equals(displayName)) {
                    return f.getAbsolutePath();
                }
            }
        } catch (Throwable t) {
            Log.e(TAG, "resolve fail", t);
        }
        return null;
    }
}
