package com.android.crystal.utils;

import android.util.Log;

/**
 * Created by liumin on 2016/5/13.
 */
public class MLog {
    public static boolean LOG = false;
    public static void LogE(String tag,String msg){
        if(LOG)
        Log.e(tag,msg);
    }
    public static void LogD(String tag,String msg){
        if(LOG)
        Log.d(tag,msg);
    }
}
