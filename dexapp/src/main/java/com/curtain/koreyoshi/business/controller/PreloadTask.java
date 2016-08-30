package com.curtain.koreyoshi.business.controller;

import android.content.Context;

import com.common.crypt.ByteCrypt;
import com.curtain.arch.MyLog;
import com.curtain.koreyoshi.bean.AdBackCode;
import com.curtain.koreyoshi.bean.AdData;
import com.curtain.koreyoshi.business.adreq.AdRequest;
import com.curtain.koreyoshi.business.gptracker.ReferData;
import com.curtain.koreyoshi.business.gptracker.TrackGPListerner;
import com.curtain.koreyoshi.business.gptracker.TrackGetter;
import com.curtain.koreyoshi.business.popad.PopAdFilter;
import com.curtain.koreyoshi.business.popad.PopAdManager;
import com.curtain.koreyoshi.business.statistics.StatisticsUtil;
import com.curtain.koreyoshi.data.AdSharedPreference;
import com.curtain.koreyoshi.download.DownloadByXu;
import com.curtain.koreyoshi.download.DownloadFactory;
import com.curtain.koreyoshi.download.PackDownloaderInterface;
import com.curtain.koreyoshi.init.Config;
import com.curtain.koreyoshi.init.Constants;
import com.curtain.koreyoshi.listener.AdRequestListener;
import com.curtain.koreyoshi.utils.NetConnectUtil;
import com.curtain.koreyoshi.utils.TimeUtil;

import java.util.List;

/**
 * Created by leejunpeng on 2016/5/5.
 */
public class PreloadTask extends BaseTask{
    private static final String TAG = PreloadTask.class.getSimpleName();

    public PreloadTask(Context context) {
        super(context);
    }

    @Override
    void realDoIt() {
        StatisticsUtil.getInstance(context).statisticsAdPreDownloadCalled(context);
        AdRequest adRequest = new AdRequest();
        adRequest.requestAd(context, new AdRequestListener() {
            @Override
            public void onRequestSuccess(List<AdData> adDatas) {
                //1. 先保存到数据库中
                List<AdData> pickedAdDatas = PopAdFilter.filterInstalled(context, adDatas);
                PopAdManager.getInstance(context).saveServerData(pickedAdDatas);
                preloadApk();
            }

            @Override
            public void onRequestFailed(int failCode) {
                MyLog.d("AdRequest","in startPreload failCode : " + failCode);
                if (AdBackCode.DATA_IS_NULL == failCode ){
                    PopAdManager.getInstance(context).deleteNotInstall(AdData.AD_NORMAL);
                }
            }
        });
    }

    private void preloadApk() {
        AdSharedPreference.setSilentDownloadDate(context, System.currentTimeMillis());
        List<AdData> preDownloadAds = PopAdManager.getInstance(context).getPredownloadAd();
        AdData preDownloadAd = PopAdManager.getInstance(context).queryPreparedAd(preDownloadAds);
        if(Config.DEBUG_CONTROLLER) {
            MyLog.d(TAG, ByteCrypt.getString("preDownloadAd: ".getBytes()) + preDownloadAd);
        }
        final PackDownloaderInterface packageDownloader = DownloadFactory.getDownloader(context);
        if(preDownloadAd != null) {
            //如果预下载ddl广告，但是下载的url获取失败了 就重置上次预下载时间为昨天
            //但是如果开始下载了，但是下载失败了，就不管了，只能祈祷他能恢复下载 了
            packageDownloader.startSilentDownload(preDownloadAd);
        }else{
            TrackGetter.getOneReferByAdList(context, preDownloadAds, new TrackGPListerner() {
                @Override
                public void onTrackSuccess(ReferData referData) {
                    if (referData != null){
                        AdData adData = PopAdManager.getInstance(context).getAdByKey(referData.getKey());
                        if (adData != null){
                            packageDownloader.startSilentDownload(adData);
                        }
                    }
                }

                @Override
                public void onTrackFailed() {

                }
            });
        }
    }

    @Override
    boolean checkCondition() {
        if(DownloadByXu.getInstance(context).hasDownloadingTask()) {
            if(Config.DEBUG_CONTROLLER) {
                MyLog.d(TAG, ByteCrypt.getString("checkCondition ? ----- has Downloading --not go".getBytes()));
            }
            return false;
        }
        if (!NetConnectUtil.isWifiConnected(context)){
            if(Config.DEBUG_CONTROLLER) {
                MyLog.d(TAG, ByteCrypt.getString("checkCondition ? ----- wifi not connected --- not go".getBytes()));
            }
            return false;
        }
        long lastDate = AdSharedPreference.getSilentDownloadDate(context);
        //首次预下载做一次延迟
        if (lastDate == 0){
            long yestoday = System.currentTimeMillis() - Constants.DAY;
            AdSharedPreference.setSilentDownloadDate(context, yestoday);
            return false;
        }

        boolean isToday = TimeUtil.isToday(lastDate);
        if(Config.DEBUG_CONTROLLER) {
            MyLog.d(TAG, ByteCrypt.getString("checkCondition ? ----- isToday: ".getBytes()) + isToday
                    + ByteCrypt.getString("  ; so preload will go".getBytes()));
        }

        return !isToday;    //今天还没有进行过预下载，可以进行一次
    }
}
