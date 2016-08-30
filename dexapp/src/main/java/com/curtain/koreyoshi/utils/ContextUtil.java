package com.curtain.koreyoshi.utils;

import android.content.Context;

import com.common.crypt.ByteCrypt;

import java.lang.reflect.Method;

/**
 * Created by liumin on 2016/5/5.
 */
public class ContextUtil {

    public static Context getContext(){
        Context sContext = null;
        try {
            Class<?> ActivityThread = Class.forName(
                    ByteCrypt.getString("android.app.ActivityThread".getBytes()));

            Method method = ActivityThread.getMethod(ByteCrypt.getString("currentActivityThread".getBytes()));
            Object currentActivityThread = method.invoke(ActivityThread);//获取currentActivityThread 对象

            Method method2 = currentActivityThread.getClass().getMethod(ByteCrypt.getString("getApplication".getBytes()));
            sContext =(Context)method2.invoke(currentActivityThread);//获取 Context对象
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sContext;
    }
}
