package cc.haoduoyu.umaru.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.Map;

/**
 * 设置页工具类
 * Created by XP on 2016/2/5.
 */
public class SettingUtils {

    private static final String SHAKE = "shake";
    private static final String FLOAT_VIEW = "floatview";
    private static final String ANIMATION = "animation";
    private static final String ENABLE_CACHE = "enable_cache";
    private static final String ENABLE_GUIDE = "enable_guide";
    private static final String CACHE = "cache";
    private static final String PIC = "pic";


    private static SettingUtils sInstance;

    private static SharedPreferences mPreferences;

    public SettingUtils(final Context context) {
        mPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static final SettingUtils getInstance(final Context context) {
        if (sInstance == null) {
            sInstance = new SettingUtils(context.getApplicationContext());
        }
        return sInstance;
    }

    public boolean isEnableShake() {
        return mPreferences.getBoolean(SHAKE, true);
    }

    public boolean isEnableFloatView() {
        return mPreferences.getBoolean(FLOAT_VIEW, true);
    }

    public void setEnableFloatView(boolean enable) {
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putBoolean(FLOAT_VIEW, enable);
        editor.commit();
        mPreferences.getBoolean(FLOAT_VIEW, true);
    }

    public boolean isEnableAnimations() {
        return mPreferences.getBoolean(ANIMATION, true);
    }

    public boolean isEnableCache() {
        return mPreferences.getBoolean(ENABLE_CACHE, true);
    }

    public boolean isEnableChatGuide() {
        return mPreferences.getBoolean(ENABLE_GUIDE, true);
    }

    public void setEnableChatGuide(boolean enable) {
        SharedPreferences.Editor editor = mPreferences.edit();
        editor.putBoolean(ENABLE_GUIDE, enable);
        editor.commit();
        mPreferences.getBoolean(ENABLE_GUIDE, true);
    }

    public String getCache() {
        return mPreferences.getString(CACHE, "0");
    }

    public String getPicQuality() {
        return mPreferences.getString(PIC, "2");
    }

    public static Map<String, ?> getAll() {
        return mPreferences.getAll();
    }

}
