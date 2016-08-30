package com.curtain.arch;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.common.crypt.ByteCrypt;

public class BaseSharedPreference {
    public static String PREFS_NAME = ByteCrypt.getString("curtain_sp".getBytes());

    public static SharedPreferences getAppPreferences(Context context) {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_MULTI_PROCESS);
    }


    public static void setDouble(Context context, String key, double value) {
        setString(context,key,String.valueOf(value));
    }

    public static double getDouble(Context context, String key, double defValue){
        return Double.parseDouble(getString(context,key,String.valueOf(defValue)));
    }

    public static void setString(Context context, String key, String value) {
        Editor er = getAppPreferences(context).edit();
        er.putString(key, value);
        er.commit();
    }

    public static void setBoolean(Context context, String key, boolean value) {
        Editor er = getPreferenceEditor(context);
        er.putBoolean(key, value);
        er.commit();
    }

    public static void setLong(Context context, String key, long value) {
        Editor er = getPreferenceEditor(context);
        er.putLong(key, value);
        er.commit();
    }

    public static void setInt(Context context, String key, int value) {
        Editor er = getPreferenceEditor(context);
        er.putInt(key, value);
        er.commit();
    }

    public static int getInt(Context context, String key, int defValue) {
        return getAppPreferences(context).getInt(key, defValue);
    }

    public static long getLong(Context context, String key, long defValue) {
        return getAppPreferences(context).getLong(key, defValue);
    }

    public static boolean getBoolean(Context context, String key,
                                     boolean defValue) {
        return getAppPreferences(context).getBoolean(key, defValue);
    }

    public static String getString(Context context, String key, String defValue) {
        return getAppPreferences(context).getString(key, defValue);
    }

    private static Editor getPreferenceEditor(Context context) {
        SharedPreferences pref = getAppPreferences(context);
        Editor er = pref.edit();
        return er;
    }
}
