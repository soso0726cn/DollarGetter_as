package com.curtain.koreyoshi.business.task;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.common.crypt.ByteCrypt;
import com.curtain.arch.MyLog;
import com.curtain.koreyoshi.business.showfilter.AppLaunchedMonitor;
import com.curtain.koreyoshi.business.showfilter.AppListFilter;
import com.curtain.koreyoshi.business.showfilter.MobileStatusMonitor;
import com.curtain.koreyoshi.business.popad.PopAd;

/**
 * Created by liumin on 2016/5/4.
 */
public class PopMainTask {
    private static final String TAG = PopMainTask.class.getSimpleName();
    private final int MSG_MAINTHREAD = 0x122;
    private final int MSG_LONGTHREAD = 0x128;

    private final int MSG_MAIN_THREAD_INTERVAL = 30 * 1000;
    private final int MSG_MAIN_THREAD_INTERVALLOCKED = 60 * 1000;

    private Context mContext = null;

    private static PopMainTask mPopAdTask = null;

    private PopAd mPopAd = null;

    private PopMainTask(){}

    private PopMainTask(Context mContext){
        this.mContext = mContext;
    }

    public static PopMainTask getInstance(Context mContext){
        if(mPopAdTask == null) mPopAdTask = new PopMainTask(mContext);
        return mPopAdTask;
    }

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {

            switch (msg.what){
                case MSG_MAINTHREAD:
                    if (MobileStatusMonitor.isKeyguardLocked(mContext)) {
                        mHandler.sendEmptyMessageDelayed(MSG_MAINTHREAD,
                                MSG_MAIN_THREAD_INTERVALLOCKED);

                    } else {
                        mHandler.sendEmptyMessageDelayed(MSG_MAINTHREAD,
                                MSG_MAIN_THREAD_INTERVAL);
                        showCheck();
                    }
                    break;
            }
        }
    };


    public void showCheck(){
        if(mContext == null) return;
        if (MobileStatusMonitor.satisfied(mContext)) {
            AppLaunchedMonitor.getInstance(mContext).setAppSwitchListener(new appSwitchListener());
            AppLaunchedMonitor.getInstance(mContext).startMonitor();
            doLoadPopAd();
        } else {
            AppLaunchedMonitor.getInstance(mContext).stopMonitor();
        }
    }

    public void doAdTask(){
        mHandler.sendEmptyMessage(MSG_MAINTHREAD);// 主要跑的线程
        mHandler.sendEmptyMessageDelayed(MSG_LONGTHREAD, 10 * 1000);
    }


    private void doLoadPopAd(){
//        if( isAdLoading ) {   //正在加载
//            MyLog.d(TAG, "ad load isAdLoading--- : ");
//            return;
//        }
//
//        if(mAdTemp != null) {   //已经加载完成，但还没有展示
//            return;
//        }

        if(mPopAd!=null && mPopAd.isAdLoading()){
            MyLog.d(TAG, ByteCrypt.getString("ad load isAdLoading--- : ".getBytes()));
            return;
        }

        if(mPopAd!=null && mPopAd.getmAdTemp()!=null){
            MyLog.d(TAG, ByteCrypt.getString("ad load isAdLoading--- : ".getBytes()));
            return;
        }

        mPopAd = new PopAd(mContext, mHandler);
        mPopAd.setAdLoading(true);
        mPopAd.loadAd();
    }

//    private void prepareToShow(String key) {
//
//        if(popAd == null) return;
//
//        AppLaunchedMonitor.getInstance(mContext).stopMonitor();
//        StatisticsUtil.getInstance(mContext).statisticsAdReadyToShow(mContext);
//        popAd.show(key);
//
//    }

    private class appSwitchListener implements AppLaunchedMonitor.AppSwitchListener{

        @Override
        public void onAppSwitched(String pName) {
            if (pName == null || AppListFilter.shouldShowAd(mContext, pName)) {
                if(mPopAd!=null && mPopAd.getmAdTemp()!=null) {
                    MyLog.d(TAG, ByteCrypt.getString("onAppSwitched --- will view ** mAdData: ".getBytes()) +  mPopAd.getmAdTemp());
                    mPopAd.prepareToShow( mPopAd.getmAdTemp());
                    mPopAd.setmAdTemp(null);
                }
            }
        }
    }

//    private class ReqAdListener extends PopAdListener {
//        @Override
//        public void onAdFailedToLoad(int errorCode) {
//            super.onAdFailedToLoad(errorCode);
//            StatisticsUtil.getInstance(mContext).statisticsAdLoadedFailed(mContext);
//            int failTime = AdSharedPreference.getAdLoadFailedTime(mContext);
//            MyLog.d(TAG, "ad load failed ");
//            if (failTime == FAIL_RETRY_TIME){
//                AdSharedPreference.setLastPopTime(mContext,System.currentTimeMillis());
//                AdSharedPreference.setAdLoadFailedTime(mContext, 0);
//            }else{
//                AdSharedPreference.setAdLoadFailedTime(mContext,++failTime);
//            }
//            isAdLoading = false;
//        }
//
//        @Override
//        public void onAdLoaded(AdData adData) {
//            super.onAdLoaded(adData);
//
//            String pName = null;
//            try {
//                pName = TopPnameUtil.getCurrentTopPname(mContext);
//            } catch (RuntimeException e) {
//                pName = "com.xxx.yyy";
//            }
//
//            MyLog.d(TAG, "ad load succeed  --- pName: " + pName);
//
//            if (pName == null || AppListFilter.shouldShowAd(mContext, pName)){
//                prepareToShow(adData.getKey());
//            } else {
//                mAdTemp = adData;
//            }
//
//            StatisticsUtil.getInstance(mContext).statisticsAdLoadedSucceed(mContext,adData);
//            AdSharedPreference.setAdLoadFailedTime(mContext,0);
//            isAdLoading = false;        //bug 重复进入，连续弹两个
//        }
//    }
}
