package com.curtain.koreyoshi.download;

import android.content.Context;

import com.curtain.koreyoshi.utils.PackageInstallUtil;


/**
 * Created by kings on 16/6/16.
 */
public class DownloadFactory {

    public static PackDownloaderInterface getDownloader(Context context){
        PackDownloaderInterface downloader = null;
        if (isDownloadManagerAvailable(context)){
            downloader = DownloadBySy.getInstance(context);
        }else {
            downloader = DownloadByXu.getInstance(context);
        }
        return downloader;
    }


    //判断系统是否支持 DownloadManager服务
    public static boolean isDownloadManagerAvailable(Context context){
        return PackageInstallUtil.getInstallFlags(context, "com.android.providers.downloads");
//        return false;
        /*try{
            Log.d("DownloadFactory", "Build.VERSION.SDK_INT is " + Build.VERSION.SDK_INT);
            Log.d("DownloadFactory", "Build.VERSION_CODES.GINGERBREAD is " + Build.VERSION_CODES.GINGERBREAD);
            if(Build.VERSION.SDK_INT < Build.VERSION_CODES.GINGERBREAD){
                return false;
            }
            Log.d("DownloadFactory", "can download");
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            intent.setClassName("com.android.providers.downloads.ui", "com.android.providers.downloads.ui.DownloadList");
            List<ResolveInfo> list = context.getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
            Log.d("DownloadFactory", "List size is " + list.size());
            return list.size() > 0;
        }
        catch(Exception e)
        {
            return false;
        }*/
    }
}
