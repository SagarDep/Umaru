package cc.haoduoyu.umaru.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.media.MediaMetadataRetriever;
import android.media.audiofx.AudioEffect;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.telephony.PhoneNumberUtils;
import android.text.format.Time;
import android.view.Display;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.apkfuns.logutils.LogUtils;

import java.io.IOException;
import java.util.Calendar;

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
     * 判断网络是否可用
     *
     * @param context
     */
    public static Boolean isNetworkReachable(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo current = cm.getActiveNetworkInfo();
        if (current == null) {
            return false;
        }
        return (current.isAvailable());
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

    /**
     * 得到Material标准色
     * http://www.google.com/design/spec/style/color.html#color-color-palette
     *
     * @return
     */
    public static int getRandomMaterialColor() {
        int[] colors = new int[]{
                //RED 400,500,600
                Color.parseColor("#EF5350"), Color.parseColor("#F44336"), Color.parseColor("#E53935"),
                //PINK
                Color.parseColor("#EC407A"), Color.parseColor("#E91E63"), Color.parseColor("#D81B60"),
                //PURPLE
                Color.parseColor("#AB47BC"), Color.parseColor("#9C27B0"), Color.parseColor("#8E24AA"),
                //DEEP PURPLE
                Color.parseColor("#7E57C2"), Color.parseColor("#673AB7"), Color.parseColor("#5E35B1"),
                //INDIGO
                Color.parseColor("#5C6BC0"), Color.parseColor("#3F51B5"), Color.parseColor("#3949AB"),
                //BLUE
                Color.parseColor("#42A5F5"), Color.parseColor("#2196F3"), Color.parseColor("#1E88E5"),
                //Light Blue
                Color.parseColor("#29B6F6"), Color.parseColor("#03A9F4"), Color.parseColor("#039BE5"),
                //Cyan
                Color.parseColor("#26C6DA"), Color.parseColor("#00BCD4"), Color.parseColor("#00ACC1"),
                //Teal
                Color.parseColor("#26A69A"), Color.parseColor("#009688"), Color.parseColor("#00897B"),
                //Green
                Color.parseColor("#66BB6A"), Color.parseColor("#4CAF50"), Color.parseColor("#43A047"),
                //Light Green
                Color.parseColor("#9CCC65"), Color.parseColor("#8BC34A"), Color.parseColor("#7CB342"),
                //Lime
                Color.parseColor("#D4E157"), Color.parseColor("#CDDC39"), Color.parseColor("#C0CA33"),
                //Yellow
                Color.parseColor("#FFEE58"), Color.parseColor("#FFEB3B"), Color.parseColor("#FDD835"),
                //Amber
                Color.parseColor("#FFCA28"), Color.parseColor("#FFC107"), Color.parseColor("#FFB300"),
                //Orange
                Color.parseColor("#FFA726"), Color.parseColor("#FF9800"), Color.parseColor("#FB8C00"),
                //Deep Orange
                Color.parseColor("#FF7043"), Color.parseColor("#FF5722"), Color.parseColor("#F4511E"),
                //Brown
                Color.parseColor("#8D6E63"), Color.parseColor("#795548"), Color.parseColor("#6D4C41"),
                //Grey
                Color.parseColor("#BDBDBD"), Color.parseColor("#9E9E9E"), Color.parseColor("#757575"),
                //Blue Grey
                Color.parseColor("#78909C"), Color.parseColor("#607D8B"), Color.parseColor("#546E7A"),
        };
        return colors[(int) (Math.random() * colors.length)];
    }

    /**
     * 是否是白天
     *
     * @return
     */
    public static boolean isDay() {
        Time time = new Time();
        time.setToNow();
        LogUtils.d(time.hour);
        if (6 <= time.hour && time.hour <= 18) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 拨打电话
     *
     * @param context
     * @param number
     */
    public static void dial(Context context, String number) {
        Uri uri = Uri.parse("tel:" + number);
        LogUtils.d(uri);
        Intent intent = new Intent(Intent.ACTION_DIAL, uri);
        context.startActivity(intent);
    }

    public static void sms(Context context, String number) {
        Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:" + number));
        intent.putExtra("sms_body", "");
        context.startActivity(intent);
    }

    public static void copyToClipBoard(Context context, String text, String success) {
        ClipData clipData = ClipData.newPlainText("umaru", text);
        ClipboardManager manager = (ClipboardManager) context.getSystemService(
                Context.CLIPBOARD_SERVICE);
        manager.setPrimaryClip(clipData);
        Toast.makeText(context, success, Toast.LENGTH_SHORT).show();
    }

    /**
     * 取得媒体文件信息，主要是得到专辑封面
     */
    public static Bitmap getSongPic(Song song) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();

        try {
            retriever.setDataSource(song.getSongData());
            byte[] stream = retriever.getEmbeddedPicture();
            if (stream != null) return BitmapFactory.decodeByteArray(stream, 0, stream.length);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
