package com.curtain.koreyoshi.business.showfilter;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;

import com.common.crypt.ByteCrypt;
import com.curtain.arch.MyLog;
import com.curtain.koreyoshi.data.StrategySharedPreference;
import com.curtain.koreyoshi.init.Config;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lijichuan on 15/11/2.
 */
public class AppListFilter {
    private static final boolean DEBUG_SWITCH_POLICY = Config.DEBUG_SWITCH_POLICY;
    private static final String TAG = /*AppListFilter.class.getSimpleName()*/"cc";
//    private static AppListFilter mAppListFilter;
//    private AppListFilter(){}
//
//    public static AppListFilter getInstance() {
//        if(mAppListFilter == null) {
//            mAppListFilter = new AppListFilter();
//        }
//        return mAppListFilter;
//    }

    public static boolean shouldShowAd(Context context, String pName) {
        return checkFilter(context, pName);
    }

    //检查应用切换是否被过滤
    private static boolean checkFilter(Context context,String pName){
        //在白名单就一定可以弹
        if (isInWhiteList(context,pName)){
            if(DEBUG_SWITCH_POLICY) {
                MyLog.d(TAG, " view -- white list  --");
            }
            return true;
        }
        //过滤掉一般系统应用
        if(pName.contains(ByteCrypt.getString("phone".getBytes())) || pName.contains(ByteCrypt.getString("camera".getBytes()))
                || pName.contains(ByteCrypt.getString("clock".getBytes()))
                || pName.contains(ByteCrypt.getString("dialer".getBytes()))){
            if(DEBUG_SWITCH_POLICY) {
                MyLog.d(TAG, ByteCrypt.getString("not view -- system app -- phone or camera or clock or dialer".getBytes()));
            }
            return false;
        }
        //过滤掉桌面
        if( isLauncherApp(context, pName) ) {
            if(DEBUG_SWITCH_POLICY) {
                MyLog.d(TAG, ByteCrypt.getString("not view -- launcher --".getBytes()));
            }
            return false;
        }
        //过滤掉systemui和自己
        if(isSystemUiOrMyself(context,pName)){
            if(DEBUG_SWITCH_POLICY) {
                MyLog.d(TAG, ByteCrypt.getString("not view -- self or systemui or settings --".getBytes()));
            }
            return false;
        }
        //过滤黑名单
        if (isInBlackList(context,pName)){
            if(DEBUG_SWITCH_POLICY) {
                MyLog.d(TAG, "not view -- black list  --");
            }
            return false;
        }

        return true;
    }

    private static boolean isInWhiteList(Context context, String pName) {
        String whiteList = StrategySharedPreference.getWhiteList(context);
        return (whiteList.contains(pName));
    }

    private static boolean isInBlackList(Context context, String pName) {
        String blackList = StrategySharedPreference.getBlackList(context);
        return (blackList.contains(pName));
    }

    private static boolean isLauncherApp(Context context, String pName) {
        List<String> allTheLauncher = getAllTheLauncher(context);

        for(String launcher: allTheLauncher) {
            if(pName != null && pName.equals(launcher)) {
                return true;
            }
        }

        return false;
    }

    private static List<String> getAllTheLauncher(Context context) {
        List<String> names = null;
        PackageManager pkgMgt = context.getPackageManager();
        Intent it = new Intent(Intent.ACTION_MAIN);
        it.addCategory(Intent.CATEGORY_HOME);
        List<ResolveInfo> ra = pkgMgt.queryIntentActivities(it, 0);
        if (ra.size() != 0) {
            names = new ArrayList<>();
        }
        for (int i = 0; i < ra.size(); i++) {
            String packageName = ra.get(i).activityInfo.packageName;
            names.add(packageName);
        }
        return names;
    }

    private static boolean isSystemUiOrMyself(Context context,String pName) {
        if(pName.equalsIgnoreCase(context.getPackageName()) || pName.equalsIgnoreCase(ByteCrypt.getString("com.android.systemui".getBytes())))
            return true;
        else if(pName.equals(ByteCrypt.getString("com.android.settings".getBytes()))) {
            return true;
        }
        return false;
    }

}
