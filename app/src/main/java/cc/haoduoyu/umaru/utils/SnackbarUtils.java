package cc.haoduoyu.umaru.utils;

import android.support.design.widget.Snackbar;
import android.view.View;

/**
 * Snackbar工具类
 * Created by XP on 2016/1/9.
 */
public class SnackbarUtils {

    private SnackbarUtils() {
    }

    public static void showIndefinite(View view, CharSequence text) {
        show(view, text, Snackbar.LENGTH_INDEFINITE);
    }

    public static void showLong(View view, CharSequence text) {
        show(view, text, Snackbar.LENGTH_LONG);
    }

    public static void showShort(View view, CharSequence text) {
        show(view, text, Snackbar.LENGTH_SHORT);
    }

    public static void show(View view, CharSequence text, int duration) {
        Snackbar.make(view, text, duration).show();
    }

    public static void showSnackBackWithAction(View view, CharSequence text, CharSequence btnText) {

        Snackbar.make(view, text, Snackbar.LENGTH_LONG).setAction(btnText, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Snackbar.make(v, "Great!", Snackbar.LENGTH_LONG).show();
            }
        }).show();
    }
}
