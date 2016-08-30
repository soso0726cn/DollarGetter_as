package com.curtain.koreyoshi.download;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;

import com.common.crypt.ByteCrypt;
import com.curtain.arch.MyLog;
import com.curtain.koreyoshi.ignore.DownloadInfo;
import com.curtain.koreyoshi.init.Config;
import com.curtain.koreyoshi.init.Constants;
import com.curtain.koreyoshi.bean.AdBehave;
import com.curtain.koreyoshi.bean.AdData;
import com.curtain.koreyoshi.data.StrategySharedPreference;
import com.curtain.koreyoshi.business.statistics.StatisticsUtil;
import com.curtain.koreyoshi.utils.NetConnectUtil;

/**
 * Created by leejunpeng on 2016/3/8.
 */
public class DownloadRetry {
    private static final String TAG = DownloadRetry.class.getSimpleName();

    public static void retryCdn(Context context, AdData adData, String msg, boolean isSi) {
        DownloadInfo downloadInfo = DownloadByXu.getInstance(context).getDownloadInfoByKey(adData.getKey());
        if (downloadInfo != null) {
            String downloadUrl = downloadInfo.getDownloadUrl();
            String mainCdn = getUriAuthority(downloadUrl);
            String retryCdn = StrategySharedPreference.getRetryCdn(context);
            if (canRetry(context, adData, msg, mainCdn, retryCdn)) {
                if (Config.DOWNLOAD_LOG_ENABLE) {
                    MyLog.d(TAG, ByteCrypt.getString("can retry ----connect time out!".getBytes()));
                }

                String retryUrl = downloadUrl.replace(mainCdn, retryCdn);
                if (!TextUtils.isEmpty(retryUrl)) {
                    //静默任务先不重试
                    modifyDownloadInfo(context, downloadInfo, retryUrl);
                    if (isSi) {
                        StatisticsUtil.getInstance(context).statisticsAdPreDownloadRetry(context,adData);
                    } else {
                        StatisticsUtil.getInstance(context).statisticsAdNormalDownloadRetry(context,adData);
                    }
                }
            }
        }
    }

    private static void modifyDownloadInfo(Context context, DownloadInfo downloadInfo, String retryUrl) {
        if (downloadInfo.getProgress() == 0){
            if (Config.DOWNLOAD_LOG_ENABLE){
                MyLog.d(TAG, ByteCrypt.getString("modify downloadInfo ----- ".getBytes()));
            }
            downloadInfo.setDownloadUrl(retryUrl);
            DownloadByXu.getInstance(context).resumeDownload(downloadInfo);
        }
    }

    private static boolean canRetry(Context context, AdData adData, String msg, String mainCdn,String retryCdn){
        if (context == null || adData == null || TextUtils.isEmpty(msg) || TextUtils.isEmpty(mainCdn) || TextUtils.isEmpty(retryCdn)){
            if (Config.DOWNLOAD_LOG_ENABLE){
                MyLog.d(TAG,ByteCrypt.getString("can't retry ----data is null!".getBytes()));
            }
            return false;
        }
        if (!NetConnectUtil.isNetWorking(context)){
            if (Config.DOWNLOAD_LOG_ENABLE){
                MyLog.d(TAG,ByteCrypt.getString("can't retry ----network is not connected!".getBytes()));
            }
            return false;
        }
        if (!(AdBehave.AD_BEHAVE_NGP_DOWNLOAD == adData.getBehave())){
            if (Config.DOWNLOAD_LOG_ENABLE){
                MyLog.d(TAG,ByteCrypt.getString("can't retry ----data is not gp".getBytes()));
            }
            return false;
        }
        if (TextUtils.equals(mainCdn, retryCdn)){
            if (Config.DOWNLOAD_LOG_ENABLE){
                MyLog.d(TAG,ByteCrypt.getString("can't retry ----retry cdn is same".getBytes()));
            }
            return false;
        }
        return (msg.contains(Constants.CONNECT_TIMEOUT_EX) || msg.contains(Constants.UNKNOW_HOST_EX));
    }


    private static String getUriAuthority(String url) {
        String mainCdn = null;
        if (url != null){
            Uri uri = Uri.parse(url);
            if (uri != null) {
                mainCdn = uri.getAuthority();
                if (Config.DOWNLOAD_LOG_ENABLE){
                    MyLog.d(TAG,ByteCrypt.getString("maincdn : ".getBytes()) + mainCdn);
                }
            }
        }
        return mainCdn;
    }

    public static boolean isRetryUrl(Context context, String url) {
        boolean result = false;
        String retryCdn = StrategySharedPreference.getRetryCdn(context);
        String urlCdn = getUriAuthority(url);
        if (Config.DOWNLOAD_LOG_ENABLE){
            MyLog.d(TAG,ByteCrypt.getString("retryCdn : ".getBytes()) + retryCdn + ByteCrypt.getString(" ; urlCdn : ".getBytes()) + urlCdn);
        }
        if (!TextUtils.isEmpty(retryCdn) && !TextUtils.isEmpty(urlCdn) && TextUtils.equals(urlCdn, retryCdn)){
            result = true;
        }
        return result;
    }
}
