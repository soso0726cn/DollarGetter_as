package com.curtain.koreyoshi.download;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;

import com.curtain.arch.MyLog;
import com.curtain.koreyoshi.bean.AdBehave;
import com.curtain.koreyoshi.bean.AdData;
import com.curtain.koreyoshi.business.popad.PopAdManager;
import com.curtain.koreyoshi.business.statistics.StatisticsUtil;
import com.curtain.koreyoshi.data.AdSharedPreference;
import com.curtain.koreyoshi.init.Constants;
import com.curtain.koreyoshi.utils.IntentUtil;
import com.curtain.koreyoshi.utils.NetConnectUtil;
import com.curtain.koreyoshi.utils.PackageInstallUtil;

import java.io.File;

/**
 * Created by leejunpeng on 2015/11/2.
 */
public class DownloadReceiver {
    private static final String TAG = DownloadReceiver.class.getSimpleName();
    private boolean DEBUG_DOWNLOAD = true;
    public void onReceive(final Context context ,Intent intent) {
        if(intent == null) return;
        String action = intent.getAction();
        if(action == null)  return;


        if(action.equals(Constants.DOWNLOAD_NOTIFICATION_CLICKED)) {    //下载通知栏点击
            handleClick(context, intent);
        } else if(action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) { //网络状态变化
            handleNetChange(context);
        }else if (action.equals(android.app.DownloadManager.ACTION_DOWNLOAD_COMPLETE)
                || action.equals(Constants.MY_DOWNLOAD_COMPLETE_ACTION)){//系统下载完成
            final long downloadId = intent.getLongExtra(android.app.DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            new Thread(){
                @Override
                public void run() {
                    handleDownloadComplete(context, downloadId);
                }
            }.start();

        }
    }

    private void handleNetChange(Context context) {
        if(NetConnectUtil.isWifiConnected(context)) {
            DownloadByXu.getInstance(context).resumeAllDownload();
        }
    }

    private void handleClick(Context context, Intent intent) {
        int notifyId = intent.getIntExtra(DownloadNotifier.NOTIFY_ID, -1);
        DownloadByXu.getInstance(context).resumeDownload(notifyId);
    }

    private void handleDownloadComplete(final Context context, long downloadId) {
        //根据downloadId从广告表中查出下载完成的广告，进行安装
        if (!DownloadFactory.isDownloadManagerAvailable(context)){
            return;
        }
        DownloadRecord record = DownloadBySy.getInstance(context).queryDownloadRecord(downloadId);
        final AdData adData = PopAdManager.getInstance(context).getAdByDownloadId(downloadId);
        if (record == null){
            return;
        }
        if (adData == null){
            return;
        }
        int status = record.getStatus();
        //TODO:下载失败也有会进这个广播，是不是不需要observer了
        if(Downloads.Impl.isStatusInformational(status)){
            //从准备开始到下载中
        }else if (Downloads.Impl.isStatusSuccess(status)){
            //下载成功
            handleSucceed(context, record, adData, status);
        }else if (Downloads.Impl.isStatusClientError(status)){
            //客户端错误4xx-5xx
            handleClientError(context, downloadId, adData, status);
        }else if (Downloads.Impl.isStatusServerError(status)){
            //服务端错误,TODO:重试条件？
        }


    }

    private void handleClientError(Context context, long downloadId, AdData adData, int status) {
        if (DownloadFactory.isDownloadManagerAvailable(context)){
            DownloadBySy.getInstance(context).removeDownload(downloadId);
        }
        if (0 != adData.getShowedTime()){
            if (AdBehave.AD_BEHAVE_CPA_DOWNLOAD == adData.getBehave()) {
                String targetUrl = adData.getTargetUrl();
                IntentUtil intentUtil = new IntentUtil();
                intentUtil.jumpBrowser(context,targetUrl);
                //context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(targetUrl)));

            } else if (AdBehave.AD_BEHAVE_NGP_DOWNLOAD == adData.getBehave()) {
                String packageName = adData.getPackageName();
                IntentUtil intentUtil = new IntentUtil();
                intentUtil.jumpToGP(context,packageName);
            }
            PopAdManager.getInstance(context).refreshAdStatus(adData, AdData.STATUS_AD_SHOW);
            StatisticsUtil.getInstance(context).statisticsAdNormalDownloadFailed(context, adData);
        } else {
            PopAdManager.getInstance(context).refreshAdStatus(adData, AdData.STATUS_INIT);
            StatisticsUtil.getInstance(context).statisticsAdPreDownloadFailed(context, adData);
        }
    }

    private void handleSucceed(final Context context, DownloadRecord record, final AdData adData, int status) {
        PopAdManager.getInstance(context).refreshAdStatus(adData, AdData.STATUS_DOWNLOADED);

        String path = record.getLocal_filename();
        if (path == null){
            return;
        }

        File target = new File(path);
        if (PackageInstallUtil.isValideApkFile(context, path)) {
            if (AdBehave.AD_BEHAVE_CPA_DOWNLOAD == adData.getBehave()){
                String ddlPname = PackageInstallUtil.getPname(context, path);
                if (!ddlPname.equals(adData.getPackageName())){
                    PopAdManager.getInstance(context).updateAdPname(adData, ddlPname);
                    File newName = new File(Constants.DOWNLOAD_DIR + String.valueOf(ddlPname.hashCode()));
                    target.renameTo(newName);
                    target = newName;
                }

            }
            if(DEBUG_DOWNLOAD) {
                MyLog.e(TAG, "normal download success, ready install !!!");
            }
            if (0 == adData.getShowedTime()){
                int key = -1;
                try {
                    key = Integer.valueOf(target.getName());
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
                if(key == -1){
                    return;
                }

                AdSharedPreference.addFileNameToList(context, target.getName());

                StatisticsUtil.getInstance(context).statisticsAdPreDownloadSucceed(context,adData);
                if (DEBUG_DOWNLOAD) {
                    MyLog.e(TAG, "silent download success!!!");
                }
            }else {
                final File finalTarget = target;
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        StatisticsUtil.getInstance(context).statisticsAdInstall(context, adData);
                        PackageInstallUtil.installApp(context, finalTarget);
                    }
                }).start();
                StatisticsUtil.getInstance(context).statisticsAdNormalDownloadSucceed(context, adData);
            }
        } else {
            // 如果数据接收完，但是无法正常解析，我们认为下载失败。将下载文件删除
            target.delete();
            if (0 == adData.getShowedTime()) {
                StatisticsUtil.getInstance(context).statisticsAdPreDownloadSucceedButFileError(context, adData);
            } else {
                StatisticsUtil.getInstance(context).statisticsAdNormalDownloadSucceedButFileError(context, adData);
            }
            if (DEBUG_DOWNLOAD)
                MyLog.e(TAG, "download success, but delete!!!");
        }

        String downloadUrl = record.getDownloadUrl();
        if (downloadUrl != null && DownloadRetry.isRetryUrl(context, downloadUrl)) {
            if (0 == adData.getShowedTime()) {
                StatisticsUtil.getInstance(context).statisticsAdPreDownloadRetrySucceed(context, adData);
            } else {
                StatisticsUtil.getInstance(context).statisticsAdNormalDownloadRetrySucceed(context, adData);
            }
            if (DEBUG_DOWNLOAD) {
                MyLog.d(TAG, "upload statisticsAdDownloadRetrySucceed");
            }
        }
    }


}
