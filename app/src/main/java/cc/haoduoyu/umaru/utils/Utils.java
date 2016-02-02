package cc.haoduoyu.umaru.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Point;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.media.audiofx.AudioEffect;
import android.media.audiofx.BassBoost;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.Settings;
import android.view.Display;
import android.view.WindowManager;

import java.io.IOException;

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

    public static boolean isAndroid6() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }

    public static String getSongContent(Song song) {
        String content = "文件名： " + song.getDisplayName()
                + "\n\n文件路径： " + song.getSongData()
                + "\n\n长度： " + Utils.durationToString(song.getDuration())
                + "\n\n大小： " + song.getSize() / 1024
                + "千字节\n\nMime类型： " + song.getMimeType();
//                + "\n\n比特率： " + getRate(song.getSongData(), MediaFormat.KEY_BIT_RATE)
//                + " kb/s\n\n采样率： " + getRate(song.getSongData(), MediaFormat.KEY_SAMPLE_RATE)
//                + " Hz\n\n通道数： " + getRate(song.getSongData(), MediaFormat.KEY_CHANNEL_COUNT);
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

    //得到各类型Rate,API23闪退
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public static String getRate(String songData, String type) {
        String i = null;
        MediaExtractor mex = new MediaExtractor();
        try {
            mex.setDataSource(songData);
        } catch (IOException e) {
            e.printStackTrace();
        }
        MediaFormat mf = mex.getTrackFormat(0);
        int bitRate = mf.getInteger(MediaFormat.KEY_BIT_RATE);
        int sampleRate = mf.getInteger(MediaFormat.KEY_SAMPLE_RATE);
        int channelCount = mf.getInteger(MediaFormat.KEY_CHANNEL_COUNT);
        String mime = mf.getString(MediaFormat.KEY_MIME);
        switch (type) {
            case MediaFormat.KEY_BIT_RATE:
                i = String.valueOf(bitRate / 1000);
                break;
            case MediaFormat.KEY_SAMPLE_RATE:
                i = String.valueOf(sampleRate);
                break;
            case MediaFormat.KEY_CHANNEL_COUNT:
                i = String.valueOf(channelCount);
                break;
            case MediaFormat.KEY_MIME:
                i = mime;
                break;
        }
        return i;
    }

    /**
     * 打开应用程序设置
     *
     * @param activity
     */
    public static void openAppSettings(Activity activity) {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_SETTINGS);
        activity.startActivity(intent);
    }
}
