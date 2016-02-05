/*
 * Copyright (c) 2014, 青岛司通科技有限公司 All rights reserved.
 * File Name：SharedPreUtils.java
 * Version：V1.0
 * Author：zhaokaiqiang
 * Date：2014-9-4
 */
package cc.haoduoyu.umaru.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import java.util.Map;


/**
 * Created by XP on 2016/1/9.
 */
public class PreferencesUtils {

    /**
     * 全局shared preference的名称
     */
    private static final String SHARED_PREFERANCE_NAME = "umaru_pref";

    public static void setInteger(Context context, String key, int value) {
        SharedPreferences sp = context.getSharedPreferences(
                SHARED_PREFERANCE_NAME, Context.MODE_PRIVATE);
        Editor editor = sp.edit();
        editor.putInt(key, value);
        editor.commit();
    }

    public static int getInteger(Context context, String key, int defaultValue) {
        SharedPreferences sp = context.getSharedPreferences(
                SHARED_PREFERANCE_NAME, Context.MODE_PRIVATE);
        return sp.getInt(key, defaultValue);
    }

    public static void setString(Context context, String key, String value) {
        SharedPreferences sp = context.getSharedPreferences(
                SHARED_PREFERANCE_NAME, Context.MODE_PRIVATE);
        Editor editor = sp.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public static String getString(Context context, String key) {
        SharedPreferences sp = context.getSharedPreferences(
                SHARED_PREFERANCE_NAME, Context.MODE_PRIVATE);
        return sp.getString(key, "");
    }

    public static void setBoolean(Context context, String key, boolean value) {
        SharedPreferences sp = context.getSharedPreferences(
                SHARED_PREFERANCE_NAME, Context.MODE_PRIVATE);
        Editor editor = sp.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    public static boolean getBoolean(Context context, String key,
                                     boolean defaultValue) {
        SharedPreferences sp = context.getSharedPreferences(
                SHARED_PREFERANCE_NAME, Context.MODE_PRIVATE);
        return sp.getBoolean(key, defaultValue);
    }

    public static Map<String, ?> getAll(Context context) {
        return context.getSharedPreferences(SHARED_PREFERANCE_NAME,
                Activity.MODE_PRIVATE).getAll();
    }
}
