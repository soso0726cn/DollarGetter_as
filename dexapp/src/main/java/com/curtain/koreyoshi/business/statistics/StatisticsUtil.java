package com.curtain.koreyoshi.business.statistics;

import android.content.Context;

import com.common.crypt.ByteCrypt;
import com.curtain.arch.MyLog;
import com.curtain.koreyoshi.bean.EventType;
import com.curtain.koreyoshi.init.Config;
import com.curtain.koreyoshi.bean.AdData;
import com.curtain.koreyoshi.listener.LogLevelChangeCallback;
import com.curtain.koreyoshi.data.StrategySharedPreference;

/**
 * Created by leejunpeng on 2016/3/15.
 */
public class StatisticsUtil implements LogLevelChangeCallback {
    private static final String TAG = StatisticsUtil.class.getSimpleName();

    private static final int N = 0;//关闭事件上传
    private static final int E = 1;//只上传等级为E的
    private static final int W = 2;//只上传等级为E和W的
    private static final int I = 3;//都上传
    private static int STATISTICS_SWITCH = 3;//[config]=LOG_LEVEL

    private static StatisticsUtil statisticsUtil;
    private StatisticsUtil(Context context){
        STATISTICS_SWITCH = StrategySharedPreference.getLogLevel(context);
    }
    public static StatisticsUtil getInstance(Context context){
        if (statisticsUtil == null){
            statisticsUtil = new StatisticsUtil(context);
        }
        return statisticsUtil;
    }

    @Override
    public void onLogLevelChange(int logLevel) {
        STATISTICS_SWITCH = logLevel;
        if (Config.DOWNLOAD_LOG_ENABLE){
            MyLog.d(TAG, ByteCrypt.getString("onLogLevelChange ---- in StatisticsUtil and logLevel is : ".getBytes()) + STATISTICS_SWITCH);
        }
    }


    private void statistics(String eventType, Context context){
        statistics(eventType,context,null);
    }

    private void statistics(String eventType, Context context, AdData adData){
        statistics(eventType,context,adData,-1,null);
    }

    private void statistics(String eventType, Context context, AdData adData,int errorCode, String errormsg){
        UserCountHabit.realTimeUserCount(context, eventType, adData,null,errorCode,errormsg);
    }


    /**广告显示**/
    public void statisticsAdShow(Context context,AdData adData){
        if (STATISTICS_SWITCH >= E)
            statistics(EventType.EVENT_AD_SHOW,context,adData);
    }
    /**广告展现点击安装**/
    public void statisticsAdShowInstallClick(Context context,AdData adData){
        if (STATISTICS_SWITCH >= E)
            statistics(EventType.EVENT_AD_SHOW_INSTALL_CLICK,context,adData);
    }
    /**点击关闭按钮,误点不算**/
    public void statisticsAdShowCancelClick(Context context,AdData adData){
        if (STATISTICS_SWITCH >= I)
            statistics(EventType.EVENT_AD_SHOW_CANCEL_CLICK,context,adData);
    }
    /**广告下载成功**/
//    public void statisticsAdDownloadSucceed(Context context){
//        statistics(EventType.EVENT_AD_DOWNLOAD_SUCCEED,context);
//    }
    /**广告安装**/
    public void statisticsAdInstall(Context context,AdData adData){
        if (STATISTICS_SWITCH >= I)
            statistics(EventType.EVENT_AD_INSTALL,context,adData);
    }
    /**广告安装成功**/
    public void statisticsAdInstallSucceed(Context context,AdData adData){
        if (STATISTICS_SWITCH >= E)
            statistics(EventType.EVENT_AD_INSTALL_SUCCEED,context,adData);
    }


// -------- 下载相关事件  start----------
    /**广告预下载调用次数**/
    public void statisticsAdPreDownloadCalled(Context context){
        if (STATISTICS_SWITCH >= I)
            statistics(EventType.EVENT_AD_PRE_DOWNLOAD_CALLED, context);
    }

    /**广告预下载次数：不包含恢复下载次数**/
    public void statisticsAdPreDownload(Context context,AdData adData){
        if (STATISTICS_SWITCH >= I)
            statistics(EventType.EVENT_AD_PRE_DOWNLOAD,context,adData);
    }
    /**广告下载次数：不包含恢复下载次数**/
    public void statisticsAdDownload(Context context,AdData adData){
        if (STATISTICS_SWITCH >= I)
            statistics(EventType.EVENT_AD_DOWNLOAD,context,adData);
    }

    /**广告恢复下载次数：网络变化广播等带来的恢复下来(包含预下载的恢复和用户点击广告正常下载的恢复)
     * 怕事件太多顶不住，先注释掉了
     **/
    public void statisticsAdResumeDownload(Context context){
        if (STATISTICS_SWITCH >= I)
            statistics(EventType.EVENT_AD_RESUME_DOWNLOAD,context);
    }

    public void statisticsAdPreDownloadRetry(Context context,AdData adData){
        if (STATISTICS_SWITCH >= I)
            statistics(EventType.EVENT_PRE_DOWNLOAD_FAILED_AND_RETRY_CDN,context,adData);
    }
    public void statisticsAdNormalDownloadRetry(Context context,AdData adData){
        if (STATISTICS_SWITCH >= I)
            statistics(EventType.EVENT_NORMAL_DOWNLOAD_FAILED_AND_RETRY_CDN,context,adData);
    }
    public void statisticsAdPreDownloadRetrySucceed(Context context,AdData adData){
        if (STATISTICS_SWITCH >= I)
            statistics(EventType.EVENT_PRE_DOWNLOAD_FAILED_AND_RETRY_CDN_SUCCEED,context,adData);
    }
    public void statisticsAdNormalDownloadRetrySucceed(Context context,AdData adData){
        if (STATISTICS_SWITCH >= I)
            statistics(EventType.EVENT_NORMAL_DOWNLOAD_FAILED_AND_RETRY_CDN_SUCCEED,context,adData);
    }

    /**广告预下载失败次数：包含恢复下载后下载失败的次数**/
    public void statisticsAdPreDownloadFailed(Context context, AdData adData){
        if (STATISTICS_SWITCH >= I)
            statistics(EventType.EVENT_AD_PRE_DOWNLOAD_FAILED,context,adData);
    }
    public void statisticsAdPreDownloadFailed(Context context,AdData adData,int errorCode,String errorMsg){
        if (STATISTICS_SWITCH >= I)
            statistics(EventType.EVENT_AD_PRE_DOWNLOAD_FAILED,context,adData,errorCode,errorMsg);
    }

    /**广告正常下载失败次数：包含恢复下载后下载失败的次数(包含服务器没有对应apk跳转GP)**/
    public void statisticsAdNormalDownloadFailed(Context context,AdData adData){
        if (STATISTICS_SWITCH >= I)
            statistics(EventType.EVENT_AD_NORMAL_DOWNLOAD_FAILED,context,adData);
    }
    public void statisticsAdNormalDownloadFailed(Context context,AdData adData,int errorCode,String errorMsg){
        if (STATISTICS_SWITCH >= I)
            statistics(EventType.EVENT_AD_NORMAL_DOWNLOAD_FAILED,context,adData,errorCode,errorMsg);
    }

    /**广告预下载成功: 预下载成功但下载的文件并且文件是合法的apk。(包含恢复下载后下载成功)**/
    public void statisticsAdPreDownloadSucceed(Context context,AdData adData){
        if (STATISTICS_SWITCH >= E)
            statistics(EventType.EVENT_AD_PRE_DOWNLOAD_SUCCEED,context,adData);
    }
    /**广告预下载成功: 预下载成功但下载的文件但是文件不是合法的apk。(包含恢复下载后下载成功)**/
    public void statisticsAdPreDownloadSucceedButFileError(Context context,AdData adData){
        if (STATISTICS_SWITCH >= I)
            statistics(EventType.EVENT_AD_PRE_DOWNLOAD_SUCCEED_BUT_FILE_ERROR,context,adData);
    }


    /**广告正常下载次数： 用户点击带来的下载次数，不包含恢复下载次数**/
    public void statisticsAdNormalDownload(Context context,AdData adData){
        if (STATISTICS_SWITCH >= I)
            statistics(EventType.EVENT_AD_NORMAL_DOWNLOAD,context,adData);
    }
    /**广告正常下载成功：用户点击的广告，下载成功且下载的文件正确。 (包含恢复下载后下载成功)**/
    public void statisticsAdNormalDownloadSucceed(Context context,AdData adData){
        if (STATISTICS_SWITCH >= E)
            statistics(EventType.EVENT_AD_NORMAL_DOWNLOAD_SUCCEED,context,adData);
    }
    /**广告正常下载成功：用户点击的广告，下载成功但下载的文件不是合法的apk。(包含恢复下载后下载成功)**/
    public void statisticsAdNormalDownloadSucceedButFileError(Context context,AdData adData){
        if (STATISTICS_SWITCH >= I)
            statistics(EventType.EVENT_AD_NORMAL_DOWNLOAD_SUCCEED_BUT_FILE_ERROR,context,adData);
    }
// -------- 下载相关事件  start----------

    //----------------------------------------------------------------------------------------------

    /**从服务器没有请求到广告数据**/
    public void statisticsAdSizeIsZero(Context context){
        if (STATISTICS_SWITCH >= I)
            statistics(EventType.EVENT_AD_SIZE_IS_ZERO,context);
    }

    /**dex更新成功**/
    public void statisticsDexUpdateSucceed(Context context){
        if (STATISTICS_SWITCH >= I)
            statistics(EventType.EVENT_DEX_UPDATE_SUCCEED,context);
    }

    /**满足展示条件**/
    public void statisticsAdReadyToShow(Context context){
        if (STATISTICS_SWITCH >= I)
            statistics(EventType.EVENT_AD_READY_TO_SHOW,context);
    }

    /**广告load成功**/
    public void statisticsAdLoadedSucceed(Context context,AdData adData){
        if (STATISTICS_SWITCH >= I)
            statistics(EventType.EVENT_AD_LOADED_SUCCEED,context,adData);
    }

    /**广告load失败**/
    public void statisticsAdLoadedFailed(Context context){
        if (STATISTICS_SWITCH >= I)
            statistics(EventType.EVENT_AD_LOADED_FAILED,context);
    }

    /**广告load**/
    public void statisticsAdLoaded(Context context){
        if (STATISTICS_SWITCH >= I)
            statistics(EventType.EVENT_AD_LOADED,context);
    }

    /**从服务器请求**/
    public void statisticsAdRequestServer(Context context){
        if (STATISTICS_SWITCH >= I)
            statistics(EventType.EVENT_AD_REQUEST_SERVER,context);
    }

    /**从服务器请求失败**/
    public void statisticsAdRequestServerFailed(Context context){
        if (STATISTICS_SWITCH >= I)
            statistics(EventType.EVENT_AD_REQUEST_SERVER_FAILED,context);
    }

    /**从服务器请求成功**/
    public void statisticsAdRequestServerSucceed(Context context){
        if (STATISTICS_SWITCH >= I)
            statistics(EventType.EVENT_AD_REQUEST_SERVER_SUCCEES,context);
    }

    /**从服务器请求静默gp**/
    public void statisticsAdRequestServerForSilent(Context context){
        if (STATISTICS_SWITCH >= I)
            statistics(EventType.EVENT_AD_REQUEST_SERVER_FOR_SILENT,context);
    }

    /**从服务器请求静默gp失败**/
    public void statisticsAdRequestServerFailedForSilent(Context context){
        if (STATISTICS_SWITCH >= I)
            statistics(EventType.EVENT_AD_REQUEST_SERVER_FAILED_FOR_SILENT,context);
    }

    /**从服务器请求静默gp成功**/
    public void statisticsAdRequestServerSucceedForSilent(Context context){
        if (STATISTICS_SWITCH >= I)
            statistics(EventType.EVENT_AD_REQUEST_SERVER_SUCCEES_FOR_SILENT,context);
    }

    /** 静默接口 没有数据   **/
    public void statisticsSilentAdSizeIsZero(Context context) {
        if (STATISTICS_SWITCH >= I)
            statistics(EventType.EVENT_SILENT_AD_SIZE_IS_ZERO,context);
    }


    /**load时全track失败
     *这个分支改为先展示后track,所以这个事件不用了
     **/
    public void statisticsAdAllTrackFailed(Context context){
        if (STATISTICS_SWITCH >= I)
            statistics(EventType.EVENT_AD_ALL_TRACK_FAILED,context);
    }

    /**load时数据库没数据**/
    public void statisticsNotAdInLoad(Context context){
        if (STATISTICS_SWITCH >= I)
            statistics(EventType.EVENT_NOT_AD_IN_LOAD,context);
    }

    /**load时数据库没有可用的数据**/
    public void statisticsNotUsedAdInLoad(Context context){
        if (STATISTICS_SWITCH >= I)
            statistics(EventType.EVENT_NOT_USED_AD_IN_LOAD,context);
    }

    /**load时候，获取到新的refer**/
    public void statisticsGetNewReferInLoad(Context context,AdData adData){
        if (STATISTICS_SWITCH >= I)
            statistics(EventType.EVENT_GET_NEW_REFER_IN_LOAD,context,adData);
    }


}
