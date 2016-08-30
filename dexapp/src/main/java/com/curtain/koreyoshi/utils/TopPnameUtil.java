package com.curtain.koreyoshi.utils;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.os.Build;

import com.common.crypt.ByteCrypt;
import com.curtain.arch.MyLog;
import com.curtain.koreyoshi.init.Config;

import java.lang.reflect.Field;
import java.util.List;

/**
 * Created by leejunpeng on 2015/12/3.
 */
public class TopPnameUtil {
    private static final String TAG = TopPnameUtil.class.getSimpleName();
    private static final boolean DEBUG_SWITCH = Config.SWITCH_LOG_ENABLE;

    public static String getCurrentTopPname(Context context) throws RuntimeException{
        String pName = null;
        ActivityManager mActivityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            pName = getHigher(mActivityManager);
            if (DEBUG_SWITCH){
                MyLog.d(TAG, ByteCrypt.getString("getHigher : ".getBytes()) + pName);
            }
        }else{
            pName = getLower(mActivityManager);
            if (DEBUG_SWITCH){
                MyLog.d(TAG,ByteCrypt.getString("getLower : ".getBytes()) + pName);
            }
        }
        return pName;
    }

    private static String getHigher(ActivityManager mActivityManager) {
        String pName = null;
        final int PROCESS_STATE_TOP = 2;
        RunningAppProcessInfo currentInfo = null;
        Field field = null;
        try {
            field = ActivityManager.RunningAppProcessInfo.class.getDeclaredField(ByteCrypt.getString("processState".getBytes()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (field != null) {
            List<RunningAppProcessInfo> appList = mActivityManager.getRunningAppProcesses();
            for (RunningAppProcessInfo app : appList) {
                if (RunningAppProcessInfo.IMPORTANCE_FOREGROUND == app.importance && 0 == app.importanceReasonCode) {
                    Integer state = null;
                    try {
                        state = field.getInt(app);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (state != null && PROCESS_STATE_TOP == state) {
                        currentInfo = app;
                        break;
                    }
                }
            }
        }

        if (currentInfo != null){
            String[] pkgList = currentInfo.pkgList;
            if (pkgList != null && pkgList.length > 0) {
                pName = pkgList[0];
            } else {
                pName = currentInfo.processName;
            }
        }
        return pName;
    }

    private static String getLower(ActivityManager mActivityManager) {
        return mActivityManager.getRunningTasks(1).get(0).topActivity
                .getPackageName();
    }


}
