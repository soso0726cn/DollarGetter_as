package com.curtain.koreyoshi.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.common.crypt.ByteCrypt;
import com.curtain.arch.MyLog;

/**
 * Created by kings on 16/8/2.
 */
public class IntentUtil {
    private static final String TAG = IntentUtil.class.getSimpleName();


    /**
     * 跳gp
     * @param context
     * @param packageName
     */
    public void jumpToGP(Context context, String packageName){
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(ByteCrypt.getString("market://details?id=".getBytes()) + packageName));
            intent.setPackage(ByteCrypt.getString("com.android.vending".getBytes()));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            if (intent.resolveActivity(context.getPackageManager()) != null) {
                context.startActivity(intent);
            } else {
                MyLog.d("", ByteCrypt.getString("no market intent action view !!!".getBytes()));
                jumpToGPViaBrowser(context,packageName);
            }
        } catch (Exception e) {
            e.printStackTrace();
            jumpToGPViaBrowser(context,packageName);
        }
    }

    private void jumpToGPViaBrowser(Context context, String packageName){
        String url = ByteCrypt.getString("https://play.google.com/store/apps/details?id=".getBytes()) + packageName;
        jumpBrowser(context,url);
    }

    /**
     * 跳浏览器
     * @param context
     * @param targetUrl
     */
    public void jumpBrowser(Context context, String targetUrl){
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(targetUrl));
        //优先使用浏览器打开
        if(PackageInstallUtil.getInstallFlags(context, "com.android.browser")) {
            intent.setPackage("com.android.browser");
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (intent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(intent);
        } else {
            MyLog.d(TAG, "no browser intent action view !!!");
        }
    }




}
