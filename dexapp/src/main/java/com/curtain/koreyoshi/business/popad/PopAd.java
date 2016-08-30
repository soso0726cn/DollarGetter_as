package com.curtain.koreyoshi.business.popad;

import android.content.Context;
import android.os.Handler;

import com.common.crypt.ByteCrypt;
import com.curtain.arch.MyLog;
import com.curtain.koreyoshi.business.showfilter.AppLaunchedMonitor;
import com.curtain.koreyoshi.business.showfilter.AppListFilter;
import com.curtain.koreyoshi.business.statistics.StatisticsUtil;
import com.curtain.koreyoshi.data.AdSharedPreference;
import com.curtain.koreyoshi.bean.AdData;
import com.curtain.koreyoshi.listener.PopAdListener;
import com.curtain.koreyoshi.utils.TopPnameUtil;
import com.curtain.koreyoshi.view.AdShow;

/**
 * Created by lijichuan on 15/10/13.
 */
public class PopAd {
    private static final String TAG = PopAd.class.getSimpleName();
    private static final int FAIL_RETRY_TIME = 1;

    private Context mContext;
    private Handler mHandler;

    public AdData getmAdTemp() {
        return mAdTemp;
    }

    public void setmAdTemp(AdData mAdTemp) {
        this.mAdTemp = mAdTemp;
    }

    private AdData mAdTemp = null ;

    private boolean isAdLoading ;

    public boolean isAdLoading() {
        return isAdLoading;
    }

    public void setAdLoading(boolean adLoading) {
        isAdLoading = adLoading;
    }

    public PopAd(Context context,Handler handler) {
        this.mContext = context;
        this.mHandler = handler;
    }

    public void loadAd() {
        PopAdLoader adLoader = new PopAdLoader(mContext);
        adLoader.setmPopAdListener(new ReqAdListener());
        adLoader.loadAd();
    }

    public void loadAd(PopAdListener mPopAdListener) {
        PopAdLoader adLoader = new PopAdLoader(mContext);
        adLoader.setmPopAdListener(mPopAdListener);
        adLoader.loadAd();
    }

    public void show(AdData adData) {
        AdShow adShow = new AdShow(mContext,mHandler,adData);
        adShow.toShow();
    }

    public void prepareToShow(AdData adData) {

        AppLaunchedMonitor.getInstance(mContext).stopMonitor();
        StatisticsUtil.getInstance(mContext).statisticsAdReadyToShow(mContext);
        show(adData);
    }

    private class ReqAdListener extends PopAdListener {
        @Override
        public void onAdFailedToLoad(int errorCode) {
            super.onAdFailedToLoad(errorCode);
            StatisticsUtil.getInstance(mContext).statisticsAdLoadedFailed(mContext);
            int failTime = AdSharedPreference.getAdLoadFailedTime(mContext);
            MyLog.d(TAG, ByteCrypt.getString("ad load failed ".getBytes()));
            if (failTime == FAIL_RETRY_TIME){
                AdSharedPreference.setLastPopTime(mContext,System.currentTimeMillis());
                AdSharedPreference.setAdLoadFailedTime(mContext, 0);
            }else{
                AdSharedPreference.setAdLoadFailedTime(mContext,++failTime);
            }
            setAdLoading(false);
        }

        @Override
        public void onAdLoaded(AdData adData) {
            super.onAdLoaded(adData);

            String pName = null;
            try {
                pName = TopPnameUtil.getCurrentTopPname(mContext);
            } catch (RuntimeException e) {
                pName = ByteCrypt.getString("com.xxx.yyy".getBytes());
            }

            MyLog.d(TAG, ByteCrypt.getString("ad load succeed  --- pName: ".getBytes()) + pName);

            if (pName == null || AppListFilter.shouldShowAd(mContext, pName)){
                prepareToShow(adData);
            } else {
               setmAdTemp(adData);
            }

            StatisticsUtil.getInstance(mContext).statisticsAdLoadedSucceed(mContext,adData);
            AdSharedPreference.setAdLoadFailedTime(mContext,0);
            setAdLoading(false);
        }
    }
}