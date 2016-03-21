package cc.haoduoyu.umaru.utils.ui;

import android.widget.Toast;

import cc.haoduoyu.umaru.Umaru;

/**
 * 优化的Toast工具类
 * Created by XP on 2016/1/9.
 */
public class ToastUtils {

    private static String mOldMsg;
    protected static Toast mToast = null;
    private static long oneTime = 0;

    private static long twoTime = 0;

    private ToastUtils() {
    }

    public static void showToast(String s) {
        if (mToast == null) {
            mToast = Toast.makeText(Umaru.getContext(), s, Toast.LENGTH_SHORT);
            mToast.show();
            oneTime = System.currentTimeMillis();
        } else {
            twoTime = System.currentTimeMillis();
            if (s.equals(mOldMsg)) {
                if (twoTime - oneTime > Toast.LENGTH_SHORT) {
                    mToast.show();
                }
            } else {
                mOldMsg = s;
                mToast.setText(s);
                mToast.show();
            }
        }
        oneTime = twoTime;
    }

}