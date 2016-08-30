package com.curtain.koreyoshi.business.record;

import android.content.Context;
import android.content.Intent;

import com.common.crypt.ByteCrypt;
import com.curtain.koreyoshi.business.gptracker.trackping.InstallTracker;
import com.curtain.koreyoshi.business.gptracker.trackping.TrackDataManager;
import com.curtain.koreyoshi.init.Constants;
import com.curtain.koreyoshi.business.popad.PopAdManager;
import com.curtain.koreyoshi.bean.AdData;


/**
 * Created by leejunpeng on 2015/11/2.
 */
public class PackageReceiver {
    private static final String TAG = PackageReceiver.class.getSimpleName();

    public void onReceive(Context context,Intent intent){
        String action = intent.getAction();
        if(Intent.ACTION_PACKAGE_ADDED.equals(action)) {
            String packageName = intent.getData().getSchemeSpecificPart();
            addPackage(context, packageName);

        }else if(Constants.MY_PACKAGE_ADDED_ACTION.equals(action)){
            String packageName = intent.getStringExtra(ByteCrypt.getString("package".getBytes()));
            addPackage(context, packageName);
        }else if(Constants.PINKU_PACKAGE_ADDED_ACTION.equals(action)){
            boolean isInstallSuccess = intent.getBooleanExtra(ByteCrypt.getString("installSuccess".getBytes()),false);
            if (isInstallSuccess){
                String packageName = intent.getStringExtra(ByteCrypt.getString("pack".getBytes()));
               addPackage(context, packageName);
            }
        }else if (Intent.ACTION_PACKAGE_REMOVED.equals(action)) {
            String packageName = intent.getData().getSchemeSpecificPart();
            LaunchRecordManager.getInstance(context).deleteRecordByPname(packageName);
        }
    }


    private void addPackage(Context context, String packageName){
        AdData adData = PopAdManager.getInstance(context).getAdByPname(packageName);
        if(adData != null || TrackDataManager.getInstance(context).contains(packageName)){
            InstallTracker.getInstance().track(context,packageName);
        }
    }


}
