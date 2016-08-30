package com.curtain.koreyoshi.test;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;

import com.curtain.koreyoshi.business.task.TaskReceiver;
import com.curtain.koreyoshi.init.Constants;

public class TriggerReceiver extends BroadcastReceiver {
    public TriggerReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action == null || "".equals(action)) {
            return;
        }

        //亮屏或解锁，开机
        if (action.equals(Intent.ACTION_SCREEN_ON)
                || action.equals(Intent.ACTION_SCREEN_OFF)              //for stop monitor
                || action.equals(Intent.ACTION_USER_PRESENT)
                || action.equals(Intent.ACTION_BOOT_COMPLETED)) {
            gotoCheck(context, intent);
        }

        //网络连接状态变化
        if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
            gotoCheck(context, intent);
        }

        //自定义广播： 下载通知栏点击、包安装
        if (action.equals(Constants.DOWNLOAD_NOTIFICATION_CLICKED)
                || action.equals(Constants.MY_DOWNLOAD_COMPLETE_ACTION)
                ||action.equals(DownloadManager.ACTION_DOWNLOAD_COMPLETE)
                || action.equals(Constants.MY_PACKAGE_ADDED_ACTION)
                || action.equals(Intent.ACTION_PACKAGE_ADDED)
                || action.equals(Intent.ACTION_PACKAGE_REMOVED)) {
            gotoCheck(context, intent);
        }

    }

    private void gotoCheck(Context context, Intent intent) {
        intent.putExtra("action",intent.getAction());
        TaskReceiver.getInstance(context).onReceive(intent);
    }
}
