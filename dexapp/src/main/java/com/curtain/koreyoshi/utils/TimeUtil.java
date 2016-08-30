package com.curtain.koreyoshi.utils;

import com.curtain.koreyoshi.init.Constants;

import java.util.Calendar;

/**
 * Created by leejunpeng on 2015/11/2.
 */
public class TimeUtil {
    private static final String TAG = TimeUtil.class.getSimpleName();


    public static boolean isToday(long time){
        /*
        StackTraceElement[] stackTrace = new Exception().getStackTrace();
        for(StackTraceElement e: stackTrace) {
            Log.d("save", "isToday" + e.getClassName() + " \t" + e.getMethodName() + " \t" + e.getLineNumber());
        }
        */
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new java.util.Date());
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        long minTimeInMillis = calendar.getTimeInMillis();
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        long maxTimeInMillis = calendar.getTimeInMillis();
        return (time > minTimeInMillis && time < maxTimeInMillis);
    }

    public static boolean isYesterday(long time){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new java.util.Date());
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        long minTimeInMillis = calendar.getTimeInMillis() - Constants.DAY;
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        long maxTimeInMillis = calendar.getTimeInMillis() - Constants.DAY;
        return (time > minTimeInMillis && time < maxTimeInMillis);
    }
}
