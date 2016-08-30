package com.curtain.koreyoshi.business.task;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;

import com.common.crypt.ByteCrypt;
import com.curtain.koreyoshi.business.record.PackageReceiver;
import com.curtain.koreyoshi.download.DownloadReceiver;
import com.curtain.koreyoshi.init.Constants;

/**
 * Created by lijichuan on 15/11/2.
 */
public class TaskReceiver {
    private static Context mContext;

    private static TaskReceiver handleReceiver ;
    private TaskReceiver() {}

    public static TaskReceiver getInstance(Context context) {
        if(handleReceiver == null) {
            handleReceiver = new TaskReceiver();
            mContext = context;
        }
        return handleReceiver;
    }

    public void onReceive(Intent fromIntent) {
        Intent intent = fromIntent;
        if (intent == null){
            return;
        }
        intent.setAction(fromIntent.getStringExtra(ByteCrypt.getString("action".getBytes())));

        String action = intent.getAction();
        if(action == null || "".equals(action)) {
            return;
        }

        if (action.equals(Constants.DOWNLOAD_NOTIFICATION_CLICKED)
                || action.equals(ConnectivityManager.CONNECTIVITY_ACTION)
                ||action.equals(DownloadManager.ACTION_DOWNLOAD_COMPLETE)
                || action.equals(Constants.MY_DOWNLOAD_COMPLETE_ACTION)){
            new DownloadReceiver().onReceive(mContext,intent);
        }else if (action.equals(Intent.ACTION_PACKAGE_ADDED)
                || action.equals(Constants.MY_PACKAGE_ADDED_ACTION)
                || action.equals(Constants.PINKU_PACKAGE_ADDED_ACTION)
                || action.equals(Intent.ACTION_PACKAGE_REMOVED)){
            new PackageReceiver().onReceive(mContext, intent);
        }



    }
}
