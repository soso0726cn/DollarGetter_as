package com.curtain.koreyoshi.view;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Handler;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.WindowManager;

import com.common.crypt.ByteCrypt;
import com.curtain.arch.MyLog;
import com.curtain.koreyoshi.bean.AdBehave;
import com.curtain.koreyoshi.bean.AdData;
import com.curtain.koreyoshi.business.gptracker.TrackGetter;
import com.curtain.koreyoshi.business.gptracker.TrackUtils;
import com.curtain.koreyoshi.business.gptracker.dowloadtracker.TrackDLUrlListerner;
import com.curtain.koreyoshi.business.gptracker.dowloadtracker.TrackDownloadUrlUtil;
import com.curtain.koreyoshi.business.popad.PopAdManager;
import com.curtain.koreyoshi.business.statistics.StatisticsUtil;
import com.curtain.koreyoshi.data.AdSharedPreference;
import com.curtain.koreyoshi.data.StrategySharedPreference;
import com.curtain.koreyoshi.download.DownloadFactory;
import com.curtain.koreyoshi.download.PackDownloaderInterface;
import com.curtain.koreyoshi.ignore.AshowView;
import com.curtain.koreyoshi.imgload.ImageLoader;
import com.curtain.koreyoshi.utils.IntentUtil;

/**
 * Created by leejunpeng on 2015/11/2.
 */
public class AdShow {
    private static final String TAG = AdShow.class.getSimpleName();

    private AdData mAdData;
    private Context mContext;
    private Handler mHandler;

    public AdShow(Context context,Handler handler,AdData adData){
        this.mContext = context;
        this.mHandler = handler;
        this.mAdData = adData;
    }

    public void toShow(){
        if (mAdData !=null) {
            boolean hasPic = checkIfHasPic();
            if (hasPic){
                doShow(true);
            }else {
                doShow(false);
            }
        }
    }

    /**
     * 检查是否有图片
     * 进而选择展示describe还是big pic
     */
    private boolean checkIfHasPic() {
        boolean result = false;
        ImageLoader imageLoader = ImageLoader.getInstance(mContext);
        String mainImageUrl = mAdData.getMainImageUrl();
        if (!TextUtils.isEmpty(mainImageUrl) && !mainImageUrl.endsWith(ByteCrypt.getString("gif".getBytes()))) {
            Bitmap b = imageLoader.getBitmapFromLocal(mainImageUrl);
            if (b != null){
                mAdData.setHeight(b.getHeight());
                mAdData.setWidth(b.getWidth());
                result = true;
            }
        }
        return result;
    }


    /**
     * 展示广告
     * @param isFlying
     */
    private void doShow(boolean isFlying) {
        int adShowType = StrategySharedPreference.getAdShowType(mContext);
        switch (adShowType){
            case 1:
                adShowAlertWindow(isFlying);
                break;
            case 2:
                AshowView.start(mContext, mAdData.getKey(), isFlying);
                break;
        }
        StatisticsUtil.getInstance(mContext).statisticsAdShow(mContext,mAdData);
        AdSharedPreference.setLastPopTime(mContext, System.currentTimeMillis());
        PopAdManager.getInstance(mContext).refreshAdStatus(mAdData, AdData.STATUS_AD_SHOW);
        int chapingAlreadyShowtime = AdSharedPreference.getChapingAlreadyShowtime(mContext);
        AdSharedPreference.setChapingAlreadyShowtime(mContext, ++chapingAlreadyShowtime );
    }


    private void adShowAlertWindow(final boolean isFlying) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                alertWindow(isFlying);
            }
        });
    }


    private AdBaseView mAdBaseView;
    private WindowManager mWinManager;
    private WindowManager.LayoutParams mLp;



    public void alertWindow(boolean isFlying){
        if (mAdData == null)
            return;
        try {
            if(mAdBaseView != null) {
                mWinManager.removeView(mAdBaseView);
            }
        } catch (Exception e) {

            e.printStackTrace();
        }
        mWinManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        if (isFlying) {
            mAdBaseView = new AdFlyingBoardView(mContext,mAdData);
        }else {
//            mAdBaseView = new AdGroupView(mContext,mAdData);
            mAdBaseView = new AdDescInterstitialView(mContext,mAdData);
        }
        mAdBaseView.setGravity(Gravity.CENTER);
        mAdBaseView.setOnAdClickListener(new MyOnAdClickListener());
        mLp = new WindowManager.LayoutParams();
        mLp.type = WindowManager.LayoutParams.TYPE_SYSTEM_ERROR;
        mLp.format = PixelFormat.TRANSLUCENT;
        mLp.flags = WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            mLp.flags |= WindowManager.LayoutParams.FLAG_SECURE;
        }
        mLp.gravity = Gravity.CENTER;
        mLp.screenOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
        mWinManager.addView(mAdBaseView, mLp);
    }

    class MyOnAdClickListener implements AdBaseView.OnAdClickListener {

        @Override
        public void onAdClicked(AdData adData) {
            userClickAd(adData);
            removeAdView();
        }

        @Override
        public void onAdRemoved(AdData adData) {
            double exRate = getExRate();
            if(executionRate(exRate)){
                userClickAd(adData);
            }else{
                StatisticsUtil.getInstance(mContext).statisticsAdShowCancelClick(mContext,adData);
            }
            removeAdView();
        }
    }


    private void userClickAd(final AdData adData) {
        StatisticsUtil.getInstance(mContext).statisticsAdShowInstallClick(mContext,adData);
        PopAdManager.getInstance(mContext).refreshAdStatus(mAdData, AdData.STATUS_AD_CLICK);
        final PackDownloaderInterface packageDownloader = DownloadFactory.getDownloader(mContext);
        int behave = adData.getBehave();
        if (behave == AdBehave.AD_BEHAVE_CPA_DOWNLOAD) {
            String downloadUrl = adData.getTargetUrl();
            if (downloadUrl == null || "".equals(downloadUrl)){
                return;
            }
            if (TrackUtils.isDownloadUrl(downloadUrl)){
                packageDownloader.startDownload(adData);
            }else{
                TrackDownloadUrlUtil.trackDownloadUrl(mContext, downloadUrl, new TrackDLUrlListerner() {
                    @Override
                    public void onTrackSuccess(String apkUrl) {
                        MyLog.e(TAG,"trackDownloadUrl success" + apkUrl);
                        adData.setTargetUrl(apkUrl);
                        PopAdManager.getInstance(mContext).updateAdTargetUrl(adData, apkUrl);
                        packageDownloader.startDownload(adData);
                    }

                    @Override
                    public void onTrackFailed() {
                        String targetUrl = adData.getTargetUrl();
                        if (targetUrl != null && !"".equals(targetUrl)) {
                            IntentUtil intentUtil = new IntentUtil();
                            intentUtil.jumpBrowser(mContext, targetUrl);
                        }
                    }
                });
            }
            //Toast.makeText(mContext, adData.getPackageName() + " : " + adData.getBehave(), Toast.LENGTH_SHORT).view();
        }else if (behave == AdBehave.AD_BEHAVE_NGP_DOWNLOAD) {
            packageDownloader.startDownload(adData);
            TrackGetter.getReferByAdDataFromNet(mContext, adData);
            //Toast.makeText(mContext,adData.getPackageName() + " : " + adData.getBehave(),Toast.LENGTH_SHORT).view();
        }else if (behave == AdBehave.AD_BEHAVE_OPEN_WEBPAGE) {
            String targetUrl = adData.getTargetUrl();
            if (targetUrl != null && !"".equals(targetUrl)) {
                IntentUtil intentUtil = new IntentUtil();
                intentUtil.jumpBrowser(mContext, targetUrl);
            }
            //Toast.makeText(mContext,adData.getPackageName() + " : " + adData.getBehave(),Toast.LENGTH_SHORT).view();
        }else if (behave == AdBehave.AD_BEHAVE_DIRECTLY_GP){
            String packageName = adData.getPackageName();
            if (packageName != null && !"".equals(packageName)){
                IntentUtil intentUtil = new IntentUtil();
                intentUtil.jumpToGP(mContext,packageName);
            }
        }
    }

    private void removeAdView() {
        try {
            if (mAdBaseView != null) {
                mWinManager.removeView(mAdBaseView);
                mAdBaseView = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean executionRate(double rate){
        return ((rate == 1.0) || ((Math.random() - rate) < 0));
    }

    private double getExRate() {
        double exRate = StrategySharedPreference.getWrongRate(mContext);
        if (exRate < 0 || exRate > 1) {
            exRate = 0;
        }
        return exRate;

    }

}
