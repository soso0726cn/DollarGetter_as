package com.curtain.koreyoshi.business.popad;

import android.content.Context;
import android.text.TextUtils;

import com.common.crypt.ByteCrypt;
import com.curtain.arch.MyLog;
import com.curtain.koreyoshi.data.StrategySharedPreference;
import com.curtain.koreyoshi.business.gptracker.ReferData;
import com.curtain.koreyoshi.business.gptracker.db.ReferDao;
import com.curtain.koreyoshi.init.Config;
import com.curtain.koreyoshi.init.Constants;
import com.curtain.koreyoshi.bean.AdData;
import com.curtain.koreyoshi.dao.AdDataDao;
import com.curtain.koreyoshi.utils.TimeUtil;

import java.util.List;

/**
 * Created by leejunpeng on 2015/11/2.
 */
public class PopAdManager {
    private static final String TAG = PopAdManager.class.getSimpleName();
    private static final boolean DEBUG_AD_MANAGER = Config.DEBUG_AD_LOADER;

    private static PopAdManager mInstance;
    private static AdDataDao mAdDataDao;
    private Context mContext;

    private PopAdManager(Context context){
        this.mContext = context;
        mAdDataDao = new AdDataDao(context);
    }

    public static PopAdManager getInstance(Context context){
        if (mInstance == null){
            mInstance = new PopAdManager(context);
        }
        return mInstance;
    }


    //更新广告状态
    public synchronized void refreshAdStatus(AdData adData,int status){
        if (adData != null){
            if (adData.getSilent() == 0) {
                if (!TimeUtil.isToday(adData.getUpgradeAtTime())) {
                    adData.setShowedTime(0);
                }
                if (status == AdData.STATUS_AD_SHOW && adData.getStatus() != AdData.STATUS_DOWNLOADING) {
                    int showTime = adData.getShowedTime();
                    adData.setShowedTime(showTime + 1);
                }
                if (adData.getStatus() < status || adData.getStatus() == AdData.STATUS_DOWNLOADING) {
                    adData.setStatus(status);
                }
            }
            mAdDataDao.update(adData);
        }

    }

    //更新下载id
    public synchronized void updateDownloadId(AdData adData,long id){
        if (adData != null){
            adData.setDownloadId(id);
            mAdDataDao.update(adData);
        }
    }

    public synchronized List<AdData> queryDownloadData(){
        return mAdDataDao.queryDownloadAd();
    }

    //更新广告包名
    public synchronized void updateAdPname(AdData adData,String pname){
        if (adData != null){
            adData.setPackageName(pname);
            mAdDataDao.update(adData);
        }

    }
    //更新targetUrl
    public synchronized void updateAdTargetUrl(AdData adData,String url){
        if (adData != null){
            adData.setTargetUrl(url);
            mAdDataDao.update(adData);
        }

    }

    //获取当天展示条数
    public synchronized int getShowedTime(){
        int time = 0;
        List<Integer> showTimes = mAdDataDao.queryShowTimes();
        for (int i = 0;i < showTimes.size();i++){
            time += showTimes.get(i);
        }
        return time;
    }

    public synchronized AdData getAdByDownloadId(long downloadId){
        return mAdDataDao.queryAdByDownloadId(downloadId);
    }


    //获取预下载的广告
    public synchronized List<AdData> getPredownloadAd(){
        return mAdDataDao.queryPreloadAd();
    }

    /**
     * 先从数据库中读取，有现在加载好的就直接返回
     * @param adDatas
     * @return
     */
    public AdData queryPreparedAd(List<AdData> adDatas) {
        if (adDatas ==null || adDatas.size() == 0)
            return null;
        long now = System.currentTimeMillis();
        long referExpired = (long) (StrategySharedPreference.getReferExpired(mContext) * Constants.HOUR);
        ReferDao mReferDao = new ReferDao(mContext);
        for (AdData adData : adDatas){
            if (1 == adData.getBehave()) {
                continue;
            }
            //1.优先返回gp有refer的供预下载
            ReferData referData = mReferDao.queryReferByAdData(adData);
            if (referData == null)
                continue;
            if (TextUtils.isEmpty(referData.getRefer()))
                continue;
            if (referIsValid(referData,now,referExpired)){
                return adData;
            }
        }
        return null;
    }

    private boolean referIsValid(ReferData referData,long now,long referExpired) {
        long trackedTime = referData.getWhenTracked();
        return now - trackedTime < referExpired;
    }


    public synchronized void saveServerData(List<AdData> adDatas){
        List<AdData> newDataList = adDatas;
        List<AdData> oldDataList = mAdDataDao.queryAll(AdData.AD_NORMAL);

        List<AdData> targetDataList = PopAdFilter.filterAdData2Save(mContext, newDataList, oldDataList);

        deleteAll(AdData.AD_NORMAL);

        if(DEBUG_AD_MANAGER) {
            MyLog.d(TAG, "\n\nold--- a: " + oldDataList == null ? "0" : "" + oldDataList.size());

            for(AdData a: oldDataList) {
                MyLog.d(TAG, ByteCrypt.getString("old--- a: ".getBytes()) + a);
            }
        }



        if(DEBUG_AD_MANAGER) {
            MyLog.d(TAG, "\n\nold--- a: " + newDataList == null? "0" : "" +newDataList.size());
            for(AdData a: newDataList) {
                MyLog.d(TAG, ByteCrypt.getString("new--- a: ".getBytes()) + a);
            }
        }

        if(DEBUG_AD_MANAGER) {
            MyLog.d(TAG, "\n\nold--- a: " + targetDataList == null? "0" : "" +targetDataList.size());
            for(AdData a: targetDataList) {
                MyLog.d(TAG, ByteCrypt.getString("target--- a: ".getBytes()) + a);
            }
            MyLog.d(TAG, "\n\n");
        }
        mAdDataDao.addList(targetDataList);
    }


    private synchronized void deleteAll(int silent) {
        List<AdData> data = mAdDataDao.queryAll(silent);
        if(data == null)
            return;
        for (int i = 0; i < data.size(); i++) {
            AdData item = data.get(i);
            mAdDataDao.delete(item);
        }
    }

    public synchronized void deleteNotInstall(int silent){
        List<AdData> data = mAdDataDao.queryAll(silent);
        if(data == null)
            return;
        for (int i = 0; i < data.size(); i++) {
            AdData item = data.get(i);
            if (item != null && AdData.STATUS_INSTALLED != item.getStatus()) {
                mAdDataDao.delete(item);
            }
        }
    }




    public synchronized AdData getAdByPname(String pName){
        return mAdDataDao.getAdByPname(pName);
    }
    public synchronized AdData getAdByKey(String key){
        return mAdDataDao.queryAdBykey(key);
    }


}
