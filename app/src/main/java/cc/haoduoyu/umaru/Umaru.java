package cc.haoduoyu.umaru;

import android.app.Application;
import android.content.Context;

import cc.haoduoyu.umaru.player.PlayerController;

/**
 * Created by XP on 2016/1/10.
 */
public class Umaru extends Application {
    private static Context mContext;
    public static Context getContext() {
        return mContext;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
        PlayerController.startService(this);
    }
}
