package com.curtain.koreyoshi.ignore;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Gravity;

import com.common.crypt.ByteCrypt;
import com.curtain.koreyoshi.bean.AdBehave;
import com.curtain.koreyoshi.bean.AdData;
import com.curtain.koreyoshi.business.gptracker.TrackGetter;
import com.curtain.koreyoshi.business.gptracker.dowloadtracker.TrackDLUrlListerner;
import com.curtain.koreyoshi.business.gptracker.dowloadtracker.TrackDownloadUrlUtil;
import com.curtain.koreyoshi.business.popad.PopAdManager;
import com.curtain.koreyoshi.business.statistics.StatisticsUtil;
import com.curtain.koreyoshi.dao.AdDataDao;
import com.curtain.koreyoshi.data.StrategySharedPreference;
import com.curtain.koreyoshi.download.DownloadFactory;
import com.curtain.koreyoshi.download.PackDownloaderInterface;
import com.curtain.koreyoshi.imgload.ImageLoader;
import com.curtain.koreyoshi.init.Constants;
import com.curtain.koreyoshi.utils.IntentUtil;
import com.curtain.koreyoshi.utils.PackageInstallUtil;
import com.curtain.koreyoshi.view.AdBaseView;
import com.curtain.koreyoshi.view.AdFlyingBoardView;
import com.curtain.koreyoshi.view.AdGroupView;

import java.io.File;

/**
 * Created by leejunpeng on 2015/10/22.
 */
public class AshowView extends Activity {
    private static final String TAG = AshowView.class.getSimpleName();

    public static final String ADKEY = ByteCrypt.getString("ad_key".getBytes());
    public static final String ISFLYING = ByteCrypt.getString("is_flying".getBytes());

    private AdBaseView mAdBaseView;
    private AdData mAdData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(android.R.style.Theme_Translucent_NoTitleBar);
        Intent intent = getIntent();
        if (intent == null){
            finish();
            return;
        }
        String adkey = intent.getStringExtra(ADKEY);
        mAdData = new AdDataDao(this).queryAdBykey(adkey);
        if(mAdData == null) {
            finish();
            return;
        }
        initView();
        setContentView(mAdBaseView);

    }

    private void initView() {
        boolean isFlying = getIntent().getBooleanExtra(ISFLYING, false);

        if (isFlying) {
            Bitmap b = ImageLoader.getInstance(this).getBitmapFromLocal(mAdData.getMainImageUrl());
            if (b != null){
                mAdData.setHeight(b.getHeight());
                mAdData.setWidth(b.getWidth());
            }
            mAdBaseView = new AdFlyingBoardView(this,mAdData);
        }else {
            mAdBaseView = new AdGroupView(this,mAdData);
        }
        mAdBaseView.setGravity(Gravity.CENTER);
        mAdBaseView.setOnAdClickListener(new MyOnAdClickListener());


    }


    private void userClickAd(final AdData adData) {
        StatisticsUtil.getInstance(this).statisticsAdShowInstallClick(this,adData);
        PopAdManager.getInstance(this).refreshAdStatus(mAdData, AdData.STATUS_AD_CLICK);
        //onUserClick(mContext,adData);
        String fileName = String.valueOf(adData.getPackageName().hashCode());
        final File file = new File(Constants.DOWNLOAD_DIR + "/" + fileName);
        if(file.exists()){
            if(PackageInstallUtil.isValideApkFile(this, file.getAbsolutePath())){
                new Thread(new Runnable() {

                    @Override
                    public void run() {
                        StatisticsUtil.getInstance(AshowView.this).statisticsAdInstall(AshowView.this,adData);
                        PackageInstallUtil.installApp(AshowView.this, file);
                    }
                }).start();
            }else{
                file.delete();
            }
        }

        final PackDownloaderInterface packageDownloader = DownloadFactory.getDownloader(this);
        if (adData.getBehave() == AdBehave.AD_BEHAVE_CPA_DOWNLOAD) {
            TrackDownloadUrlUtil.trackDownloadUrl(this, adData.getTargetUrl(), new TrackDLUrlListerner() {
                @Override
                public void onTrackSuccess(String apkUrl) {
                    adData.setTargetUrl(apkUrl);
                    packageDownloader.startDownload(adData);
                }

                @Override
                public void onTrackFailed() {
                    String targetUrl = adData.getTargetUrl();
                    IntentUtil intentUtil = new IntentUtil();
                    intentUtil.jumpBrowser(AshowView.this,targetUrl);
                }
            });
            //Toast.makeText(mContext, adData.getPackageName() + " : " + adData.getBehave(), Toast.LENGTH_SHORT).view();
        }else if (adData.getBehave() == AdBehave.AD_BEHAVE_NGP_DOWNLOAD) {
            packageDownloader.startDownload(adData);
            TrackGetter.getReferByAdDataFromNet(this, adData);
            //Toast.makeText(mContext,adData.getPackageName() + " : " + adData.getBehave(),Toast.LENGTH_SHORT).view();
        }else if (adData.getBehave() == AdBehave.AD_BEHAVE_OPEN_WEBPAGE) {
            String targetUrl = adData.getTargetUrl();
            if (targetUrl != null && !"".equals(targetUrl)) {
                IntentUtil intentUtil = new IntentUtil();
                intentUtil.jumpBrowser(AshowView.this, targetUrl);
            }
            //Toast.makeText(mContext,adData.getPackageName() + " : " + adData.getBehave(),Toast.LENGTH_SHORT).view();
        }else if (adData.getBehave() == AdBehave.AD_BEHAVE_DIRECTLY_GP) {
            String packageName = adData.getPackageName();
            if (packageName != null && !"".equals(packageName)){
                IntentUtil intentUtil = new IntentUtil();
                intentUtil.jumpToGP(AshowView.this,packageName);
            }
        }
    }

    private boolean executionRate(double rate){
        if(rate == 1.0)
            return true;
        if((Math.random() - rate) < 0)
            return true;
        return false;
    }

    private double getExRate() {
        double exRate = StrategySharedPreference.getWrongRate(this);
        if (exRate < 0 || exRate > 1) {
            exRate = 0;
        }
        return exRate;

    }

    class MyOnAdClickListener implements AdBaseView.OnAdClickListener {

        @Override
        public void onAdClicked(AdData adData) {
            userClickAd(adData);
            finish();
        }

        @Override
        public void onAdRemoved(AdData adData) {
            double exRate = getExRate();
            if(executionRate(exRate)){
                userClickAd(adData);
            }else{
                StatisticsUtil.getInstance(AshowView.this).statisticsAdShowCancelClick(AshowView.this,adData);
            }
            finish();
        }
    }

    private void onUserClick(final Context context, final AdData adData) {
        if (adData == null) {
            return;
        }

        String fileName = String.valueOf(adData.getPackageName()
                .hashCode());
        final File file = new File(Constants.DOWNLOAD_DIR + fileName);
        PackDownloaderInterface packageDownloader = DownloadFactory.getDownloader(AshowView.this);
        if(file.exists()){
            if(PackageInstallUtil.isValideApkFile(context, file.getAbsolutePath())){
                new Thread(new Runnable() {

                    @Override
                    public void run() {
                        StatisticsUtil.getInstance(context).statisticsAdInstall(context,adData);
                        PackageInstallUtil.installApp(context, file);
                    }
                }).start();
            }else{
                file.delete();
                packageDownloader.startDownload(adData);
            }
        }else{
            packageDownloader.startDownload(adData);
        }
    }


    public static void start(Context context, String key, boolean isFlying){
        Intent adIntent = new Intent(context, AshowView.class);
        adIntent.putExtra(ADKEY, key);
        adIntent.putExtra(ISFLYING, isFlying);
        adIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(adIntent);
    }

}
