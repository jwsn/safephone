package com.seaice.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by seaice on 2016/3/3.
 */
public class PrefUtil {
    private static final String SP_CONFIG = "config";

    public static void setBooleanPref(Context ctx, String key, boolean flag) {
        SharedPreferences sp = ctx.getSharedPreferences(SP_CONFIG, ctx.MODE_PRIVATE);
        sp.edit().putBoolean(key, flag).commit();
    }

    public static boolean getBooleanPref(Context ctx, String key) {
        SharedPreferences sp = ctx.getSharedPreferences(SP_CONFIG, ctx.MODE_PRIVATE);
        return sp.getBoolean(key, false);
    }

    public static void setStringPref(Context ctx, String key, String value) {
        SharedPreferences sp = ctx.getSharedPreferences(SP_CONFIG, ctx.MODE_PRIVATE);
        sp.edit().putString(key, value).commit();
    }

    public static String getStringPref(Context ctx, String key) {
        SharedPreferences sp = ctx.getSharedPreferences(SP_CONFIG, ctx.MODE_PRIVATE);
        return sp.getString(key, null);
    }

    public static void removePref(Context ctx, String key) {
        SharedPreferences sp = ctx.getSharedPreferences(SP_CONFIG, ctx.MODE_PRIVATE);
        sp.edit().remove(key).commit();
    }

    public static void setIntPref(Context ctx, String key, int value) {
        SharedPreferences sp = ctx.getSharedPreferences(SP_CONFIG, ctx.MODE_PRIVATE);
        sp.edit().putInt(key, value).commit();
    }

    public static int getIntPref(Context ctx, String key) {
        SharedPreferences sp = ctx.getSharedPreferences(SP_CONFIG, ctx.MODE_PRIVATE);
        return sp.getInt(key, 0);
    }
}
