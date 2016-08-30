package com.curtain.koreyoshi.business.task;

import android.content.Context;

import com.curtain.koreyoshi.business.controller.ActivateTask;
import com.curtain.koreyoshi.business.controller.NameListTask;
import com.curtain.koreyoshi.business.controller.PreloadTask;
import com.curtain.koreyoshi.business.controller.RequestTask;
import com.curtain.koreyoshi.business.controller.ResumeTask;
import com.curtain.koreyoshi.business.controller.StrategyTask;
import com.curtain.koreyoshi.business.controller.UploadTask;
import com.curtain.koreyoshi.business.showfilter.MobileStatusMonitor;
import com.curtain.koreyoshi.business.gptracker.TrackGetter;
import com.curtain.koreyoshi.data.AdSharedPreference;
import com.curtain.koreyoshi.init.Constants;
import com.curtain.koreyoshi.utils.AdvertisingIdClient;
import com.curtain.koreyoshi.utils.NetConnectUtil;

/**
 * Created by leejunpeng on 2015/11/26.
 */
public class BackgroundTask {

    private static BackgroundTask mBackgroundTask;
    private BackgroundTask() {
    }

    public static BackgroundTask getInstance() {
        if(mBackgroundTask == null) {
            mBackgroundTask = new BackgroundTask();
        }
        return mBackgroundTask;
    }

    public void doBackgroundTask(Context mContext){

        if( !NetConnectUtil.isNetWorking(mContext) ) { //有网络的情况下，才处理任务; 没网络直接返回
            return;
        }

        //是否需要请求广告策略
        checkRequestStrategy(mContext);

        if (MobileStatusMonitor.showOpen(mContext)){
            //是否需要请求广告列表
            checkRequestAd(mContext);

            //是否需要获取refer
            checkGetRefer(mContext);

            //是否需要进行预下载
            checkPreload(mContext);

            //是否需要请求黑白名单
            checkRequestNameList(mContext);
        }

        //是否需要激活已安装的应用
        checkActivate(mContext);

        //是否需要上传日活事件
        checkUploadActive(mContext);

        //检查是否有需要恢复下载的任务
        checkResumeDownload(mContext);

        //检查是否需要更新gaid
        checkUpdateGaid(mContext);
    }

    private void checkUpdateGaid(Context mContext){
        if(mContext == null) return;
        long lastGaidTime = AdSharedPreference.getLastGaIdTime(mContext);
        long dayTime = 2 * Constants.DAY;
        long now = System.currentTimeMillis();
        boolean result = ((now - lastGaidTime) > dayTime);

        if(result){
            AdvertisingIdClient.getAdVertisingId(mContext);
        }

    }

    private void checkRequestNameList(Context mContext) {
        new NameListTask(mContext).doIt();
    }

    private void checkResumeDownload(Context mContext){
        new ResumeTask(mContext).doIt();
    }

    //上传日活事件
    private void checkUploadActive(Context mContext) {
        new UploadTask(mContext).doIt();
    }

    //检查预加载
    private void checkPreload(Context mContext) {
        new PreloadTask(mContext).doIt();
    }


    //检查激活
    private void checkActivate(final Context mContext) {
        new Thread(){
            @Override
            public void run() {
                new ActivateTask(mContext).doIt();
            }
        }.start();

    }

    //抓取refer
    private void checkGetRefer(Context mContext) {
        TrackGetter.preLoadRefer(mContext);
    }

    //请求广告策略
    private void checkRequestStrategy(Context mContext) {
        new StrategyTask(mContext).doIt();
    }

    //请求广告列表
    private void checkRequestAd(Context mContext) {
        new RequestTask(mContext).doIt();
    }

}
