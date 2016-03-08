package cc.haoduoyu.umaru.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import cc.haoduoyu.umaru.R;

/**
 * Created by XP on 2016/1/29.
 */
public class ShareUtils {

    public static void shareImage(Context context, Uri uri, String title) {
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
        shareIntent.setType("image/jpeg");
        context.startActivity(Intent.createChooser(shareIntent, title));
    }


    public static void share(Context context, String extraText) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.share));
        intent.putExtra(Intent.EXTRA_TEXT, extraText + context.getString(R.string.share_via_umaru));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(
                Intent.createChooser(intent, context.getString(R.string.share)));
    }

    public static void sendCrash(Context context) {
        String uriStr = PreferencesUtils.getString(context, context.getString(R.string.crash_uri));
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.log));
        intent.putExtra(Intent.EXTRA_STREAM, Uri.parse(uriStr));
        context.startActivity(
                Intent.createChooser(intent, context.getString(R.string.send_log)));
    }
}
