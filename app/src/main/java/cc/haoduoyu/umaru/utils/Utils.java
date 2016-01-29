package cc.haoduoyu.umaru.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Point;
import android.media.audiofx.AudioEffect;
import android.os.Build;
import android.view.Display;
import android.view.WindowManager;

import cc.haoduoyu.umaru.model.Song;

/**
 * Created by froger_mcs on 05.11.14.
 */
public class Utils {
    private static int screenWidth = 0;
    private static int screenHeight = 0;

    public static int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }


    /**
     * 将时长转化成 xx:xx:xx的String形式
     *
     * @param duration
     * @return
     */
    public static String durationToString(long duration) {
        long time = duration / 1000;
        String hour = String.valueOf(time / 3600);
        String minute = String.valueOf(time % 3600 / 60);
        String second = String.valueOf(time % 3600 % 60);

        if (minute.length() < 2) {
            minute = "0" + minute;
        }
        if (second.length() < 2) {
            second = "0" + second;
        }

        StringBuilder result = new StringBuilder();
        if (!hour.equals("0")) {
            result.append(hour).append(":").append(minute).append(":").append(second);
        } else if (!minute.equals("00")) {
            result.append(minute).append(":").append(second);
        } else {
            result.append("00:" + second);
        }

        return result.toString();
    }

    public static int getScreenHeight(Context c) {
        if (screenHeight == 0) {
            WindowManager wm = (WindowManager) c.getSystemService(Context.WINDOW_SERVICE);
            Display display = wm.getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            screenHeight = size.y;
        }

        return screenHeight;
    }

    public static int getScreenWidth(Context c) {
        if (screenWidth == 0) {
            WindowManager wm = (WindowManager) c.getSystemService(Context.WINDOW_SERVICE);
            Display display = wm.getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            screenWidth = size.x;
        }

        return screenWidth;
    }

    public static boolean isAndroid5() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }

    public static String getSongContent(Song song) {
        String content = "文件名： " + song.getDisplayName()
                + "\n\n文件路径： " + song.getSongData()
                + "\n\n长度： " + Utils.durationToString(song.getDuration())
                + "\n\n大小： " + song.getSize() / 1024
                + "千字节(kb)\n\nMime类型： " + song.getMimeType();
        return content;
    }

    //启动均衡器
    public static void startEqualizer(Activity context) {
        int mAudioSession = 0;
        try {
            final Intent effects = new Intent(AudioEffect.ACTION_DISPLAY_AUDIO_EFFECT_CONTROL_PANEL);
            effects.putExtra(AudioEffect.EXTRA_PACKAGE_NAME, context.getPackageName());
            effects.putExtra(AudioEffect.EXTRA_AUDIO_SESSION, mAudioSession);
            context.startActivity(effects);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
