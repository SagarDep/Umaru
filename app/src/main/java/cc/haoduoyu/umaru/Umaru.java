package cc.haoduoyu.umaru;

import android.app.Application;
import android.content.Context;

import com.apkfuns.logutils.LogUtils;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechUtility;

import cc.haoduoyu.umaru.utils.CrashHandler;

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
        SpeechUtility.createUtility(this, SpeechConstant.APPID + Constants.XF_APP_ID);
        CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.init(this);
        LogUtils.configAllowLog = true;
    }
}
