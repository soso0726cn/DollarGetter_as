package com.curtain.koreyoshi.business.popad;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;

import com.common.crypt.ByteCrypt;
import com.curtain.arch.MyLog;
import com.curtain.koreyoshi.bean.AdBackCode;
import com.curtain.koreyoshi.bean.AdBehave;
import com.curtain.koreyoshi.bean.AdData;
import com.curtain.koreyoshi.business.adreq.AdRequest;
import com.curtain.koreyoshi.business.gptracker.ReferData;
import com.curtain.koreyoshi.business.gptracker.TrackGetter;
import com.curtain.koreyoshi.business.gptracker.db.ReferDao;
import com.curtain.koreyoshi.business.statistics.StatisticsUtil;
import com.curtain.koreyoshi.dao.AdDataDao;
import com.curtain.koreyoshi.data.StrategySharedPreference;
import com.curtain.koreyoshi.imgload.ImageLoader;
import com.curtain.koreyoshi.init.Config;
import com.curtain.koreyoshi.init.Constants;
import com.curtain.koreyoshi.listener.AdRequestListener;
import com.curtain.koreyoshi.listener.PopAdListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lijichuan on 15/10/13.
 */
public class PopAdLoader {
    private static final boolean DEBUG_AD_LOADER = Config.DEBUG_AD_LOADER;
    private static final String TAG = PopAdLoader.class.getSimpleName();

    private Context mContext;
    private PopAdListener mPopAdListener;
    private AdDataDao mAdDataDao;
    private ReferDao mReferDao;


    public PopAdLoader(Context context, PopAdListener popAdListener) {
        this.mContext = context;
        this.mPopAdListener = popAdListener;
        this.mAdDataDao = new AdDataDao(context);
        this.mReferDao = new ReferDao(context);
    }

    public PopAdLoader(Context context) {
        this.mContext = context;
        this.mAdDataDao = new AdDataDao(context);
        this.mReferDao = new ReferDao(context);
    }

    public void setmPopAdListener(PopAdListener popAdListener){
        this.mPopAdListener = popAdListener;
    }
    /**
     * 加载一条广告
     * @return
     */
    public void loadAd() {
        MyLog.e(TAG, ByteCrypt.getString("load start ---thread :".getBytes()) + Thread.currentThread().getName());
        StatisticsUtil.getInstance(mContext).statisticsAdLoaded(mContext);
        requestAdRealTime();
    }

    private void requestAdRealTime(){
        AdRequest adRequest = new AdRequest();
        adRequest.requestAd(mContext, new AdRequestListener() {
            @Override
            public void onRequestSuccess(List<AdData> adDatas) {
                //1. 先保存到数据库中
                List<AdData> pickedAdDatas = PopAdFilter.filterInstalled(mContext, adDatas);
                PopAdManager.getInstance(mContext).saveServerData(pickedAdDatas);
                //3. 预请求refer
                TrackGetter.preLoadRefer(mContext);
                getLocalAd();
            }

            @Override
            public void onRequestFailed(int failCode) {
                MyLog.d("AdRequest","in ad loader failCode : " + failCode);
                if (AdBackCode.DATA_IS_NULL == failCode){
                    PopAdManager.getInstance(mContext).deleteNotInstall(AdData.AD_NORMAL);
                }
                getLocalAd();
            }
        });
    }

    private void getLocalAd() {
        List<AdData> adDatas = mAdDataDao.queryOrderedAd();
        if (adDatas != null && adDatas.size() != 0) {
            List<AdData> pickedAdDatas = PopAdFilter.filterInstalled(mContext, adDatas);
            if (DEBUG_AD_LOADER)
                MyLog.d(TAG, ByteCrypt.getString("loadAd --- adDatas: ".getBytes())
                        + adDatas.size()
                        + ByteCrypt.getString(" ;pickedAdDatas: ".getBytes())
                        + pickedAdDatas.size());
            if (pickedAdDatas != null && pickedAdDatas.size() > 0) {
                //必须可以返回一条广告了
                AdData adData = getSuitableAd(pickedAdDatas);
                if (DEBUG_AD_LOADER) {
                    MyLog.d(TAG, ByteCrypt.getString("preparedAd --- ".getBytes()) + adData);
                }
                preloadPicture(adData);
            } else {
                mPopAdListener.onAdFailedToLoad(0);
            }
        }else {
            mPopAdListener.onAdFailedToLoad(0);
        }
    }

    /**
     * 取一条用于展示的广告
     * 优先取有效的：behave=1的，或者有refer的
     * 如果没有有效的，就返回第一条
     * @param adDatas 不为null且size大于0
     * @return 不会为null
     */
    private AdData getSuitableAd(List<AdData> adDatas) {
        if (adDatas ==null || adDatas.size() == 0)
            return null;
        long now = System.currentTimeMillis();
        long referExpired = (long) (StrategySharedPreference.getReferExpired(mContext) * Constants.HOUR);


        List<AdData> ddlList = new ArrayList<>();
        List<AdData> openwebList = new ArrayList<>();
        List<AdData> directGpList = new ArrayList<>();

        //优先显示refer获取成功的offer
        for (AdData adData : adDatas){
            if (AdBehave.AD_BEHAVE_DIRECTLY_GP == adData.getBehave()){
                if(DEBUG_AD_LOADER) {
                    MyLog.d(TAG,ByteCrypt.getString("behave ==6 --- first----- adData：".getBytes()) + adData.getKey());
                }
                directGpList.add(adData);
                continue;
            }

            if (AdBehave.AD_BEHAVE_CPA_DOWNLOAD == adData.getBehave()) {
                if(DEBUG_AD_LOADER) {
                    MyLog.d(TAG,ByteCrypt.getString("behave ==1 --- first----- adData：".getBytes()) + adData.getKey());
                }
                ddlList.add(adData);
                continue;
            }

            if (AdBehave.AD_BEHAVE_OPEN_WEBPAGE == adData.getBehave()) {
                if(DEBUG_AD_LOADER) {
                    MyLog.d(TAG,"behave ==5 --- first----- adData：" + adData.getKey());
                }
                openwebList.add(adData);
                continue;
            }

            //以下只考虑ngp的，防止再加上behave为789的
            if(AdBehave.AD_BEHAVE_NGP_DOWNLOAD != adData.getBehave()){
                continue;
            }

            //TODO 即使有refer的广告，也只能优先展示1次； 展示过之后，与其他offer平等对待
            if(adData.getShowedTime() != 0 ) {
                continue;
            }

            ReferData referData = mReferDao.queryReferByAdData(adData);
            if (referData == null) {
                if(DEBUG_AD_LOADER) {
                    MyLog.d(TAG,ByteCrypt.getString("referData == null ** continue ** adData：".getBytes()) + adData.getKey());
                }
                continue;
            }
            if (TextUtils.isEmpty(referData.getRefer())) {
                if(DEBUG_AD_LOADER) {
                    MyLog.d(TAG,ByteCrypt.getString("referData == empty ** continue ** adData：".getBytes()) + adData.getKey());
                }
                continue;
            }
            if (referIsValid(referData, now, referExpired)){
                if(DEBUG_AD_LOADER) {
                    MyLog.d(TAG,ByteCrypt.getString("referData *** good ** will view  ** adData：".getBytes()) + adData.getKey());
                }

                return adData;
            } else {
                if(DEBUG_AD_LOADER) {
                    MyLog.d(TAG,ByteCrypt.getString("referData not valid ** continue ** adData：".getBytes()) + adData.getKey());
                }
            }
        }

        //2.直接调gp类型的广告
        for (AdData adData : directGpList){
            if (adData != null && adData.getShowedTime() == 0){
                if(DEBUG_AD_LOADER) {
                    MyLog.d(TAG,ByteCrypt.getString("behave ==6 --- second----- adData：".getBytes()) + adData.getKey());
                }
                return adData;
            }
        }

        //3. 所有ddl类型的广告
        for(AdData adData: ddlList) {
            if(adData != null && adData.getShowedTime() == 0) {
                if(DEBUG_AD_LOADER) {
                    MyLog.d(TAG,ByteCrypt.getString("behave ==1 --- second----- adData：".getBytes()) + adData.getKey());
                }
                return adData;
            }
        }

        //4. 所有打开浏览器的“订阅类广告”
        for(AdData adData: openwebList) {
            if(adData != null && adData.getShowedTime() == 0) {
                if(DEBUG_AD_LOADER) {
                    MyLog.d(TAG,"behave ==5 --- second----- adData：" + adData.getKey());
                }
                return adData;
            }
        }


        if(DEBUG_AD_LOADER) {
            MyLog.d(TAG,ByteCrypt.getString("referData not valid but must return ** adData：".getBytes()) + adDatas.get(0).getKey());
        }
        return adDatas.get(0);
    }

    /**
     * 判断refer是否过期
     * @param referData
     * @param now
     * @param referExpired
     * @return
     */
    private boolean referIsValid(ReferData referData,long now,long referExpired) {
        long trackedTime = referData.getWhenTracked();
        return now - trackedTime < referExpired;
    }

    private void preloadPicture(final AdData adData) {
        if (adData != null) {
            MyLog.e(TAG, ByteCrypt.getString("preloadPicture ---thread :".getBytes()) + Thread.currentThread().getName());
            //请求插屏大图
            String mainImageUrl = adData.getMainImageUrl();
            if (!TextUtils.isEmpty(mainImageUrl)){
                ImageLoader.getInstance(mContext).downloadPhoto(mainImageUrl, null, new ImageLoader.LoaderCallBack() {
                    @Override
                    public void loaderSuccess(Bitmap bitmap) {
                        MyLog.e(TAG, ByteCrypt.getString("preloadPicture loaderSuccess---".getBytes()));
                        mPopAdListener.onAdLoaded(adData);
                    }

                    @Override
                    public void loaderFailure() {
                        //请求icon图标
                        mPopAdListener.onAdLoaded(adData);

                    }
                });
            }else {
                mPopAdListener.onAdLoaded(adData);
            }

            ImageLoader.getInstance(mContext).queuePhoto(adData.getIconImageUrl(), null);
        }else{
            mPopAdListener.onAdFailedToLoad(0);
        }
    }




}
