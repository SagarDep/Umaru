package cc.haoduoyu.umaru;

import android.app.Application;
import android.content.Context;
import android.support.v7.app.AppCompatDelegate;

import com.apkfuns.logutils.LogUtils;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechUtility;

import cc.haoduoyu.umaru.utils.CrashHandler;
import cc.haoduoyu.umaru.utils.PreferencesUtils;
import cc.haoduoyu.umaru.utils.Utils;

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

        //讯飞
        SpeechUtility.createUtility(this, SpeechConstant.APPID + Constants.XF_APP_ID);

        //CrashHandler
        CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.init(this);

//        LeakCanary.install(this);

        //Log
        LogUtils.configAllowLog = true;

        String processName = Utils.getProcessName(getApplicationContext(),
                android.os.Process.myPid());
        LogUtils.d("processName: " + processName);

        setTheme();
    }

    private void setTheme() {
        if (PreferencesUtils.getBoolean(this, getString(R.string.night_yes), false)) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            LogUtils.d("setDefaultNightMode MODE_NIGHT_YES");
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            LogUtils.d("setDefaultNightMode MODE_NIGHT_NO");
        }
    }


}
