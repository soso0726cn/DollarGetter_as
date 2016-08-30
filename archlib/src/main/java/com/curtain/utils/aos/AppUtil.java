package com.curtain.utils.aos;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.os.Bundle;

import com.common.crypt.ByteCrypt;
import com.curtain.utils.io.IOUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by wmmeng on 16/4/4.
 */
public class AppUtil {


    private static ApplicationInfo getAppInfoByMETA(Context context) throws PackageManager
            .NameNotFoundException {
        PackageManager pm = context.getPackageManager();
        return pm.getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
    }

    public static String getAppName(Context context){
        String app = "";
        try {
            ApplicationInfo info = getAppInfoByMETA(context);
            if(info != null){
                Bundle datas = info.metaData;
                if(datas != null){
                    String meta = ByteCrypt.getString("KINGS_APP_NAME".getBytes());
                    app = datas.getString(meta);
                }
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return app;
    }



    /**
     * @Title: getSrc
     * @Description: 获取渠道标识
     * @param context
     * @return 返回渠道标识
     */
    public static String getChannelName(Context context) {
        String mSrc = "";

        if(mSrc == null){
            mSrc = getChannel(context);
        }

        if (mSrc == null) {
            mSrc = getChannelFromAssest(context);
        }
        if (mSrc == null) {
            mSrc = "sp_kingsgame";
        }
        return mSrc;

    }

    private static String getChannelFromAssest(Context context){
        AssetManager assetManager = context.getAssets();
        String channel = "";
        String f = "config.txt";
        InputStream inputStream = null;
        BufferedReader d = null;
        try {
            String[] files = assetManager.list("");
            if(files !=null){
                for (String file : files) {
                    if(file.contains(f)){
                        inputStream = assetManager.open(file);
                        d = new BufferedReader(
                                new InputStreamReader(inputStream));

                        channel = d.readLine().trim();
                        d.close();
                    }
                }
            }


            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();

        }finally {
            IOUtils.closeQuietly(d);
            IOUtils.closeQuietly(inputStream);
        }
        return channel;
//
//        String[] files = null;
//        try {
//            files = assetManager.list("");
//        } catch (IOException e) {
//
//        }
//
//        if (files != null) {
//            for (String file : files) {
//                //config.txt
//                if (file.contains("")) {
//                    InputStream inputStream = null;
//                    try {
//                        inputStream = assetManager.open(file);
//
//                        BufferedReader d = new BufferedReader(
//                                new InputStreamReader(inputStream));
//
//                        mSrc = d.readLine().trim();
//
//                        d.close();
//                        inputStream.close();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//
//                    }
//                    break;
//                }
//            }
//        }
    }

    private static String getChannel(Context context){
        try {
            ApplicationInfo applicationInfo = getAppInfoByMETA(context);
            if(applicationInfo != null){
                Bundle metaData = applicationInfo.metaData;
                if(metaData != null){
                    //SELF_CHANNEL_NAME
                    String meta = "SELF_CHANNEL_NAME";
                    return metaData.getString(meta);
                }
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return "";
    }


    public static String getVcode(Context context) {
//		try {
//			PackageInfo packageInfo = context.getPackageManager()
//					.getPackageInfo(context.getPackageName(), 0);
//			return String.valueOf(packageInfo.versionCode);
//		} catch (NameNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
        return "1";
    }

    public static String getVersionName(Context context) {
//		try {
//			PackageInfo packageInfo = context.getPackageManager()
//					.getPackageInfo(context.getPackageName(), 0);
//			return packageInfo.versionName;
//		} catch (NameNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
        return "1.0";
    }
}
