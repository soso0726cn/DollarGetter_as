package com.curtain.koreyoshi.business.gptracker;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.common.crypt.ByteCrypt;
import com.curtain.arch.MyLog;
import com.curtain.koreyoshi.business.gptracker.db.ReferDao;
import com.curtain.koreyoshi.init.Config;
import com.curtain.koreyoshi.business.popad.PopAdManager;
import com.curtain.koreyoshi.bean.AdData;
import com.curtain.koreyoshi.data.StrategySharedPreference;
import com.curtain.koreyoshi.business.statistics.StatisticsUtil;

/**
 * Created by yangzheng on 15/10/27.
 */
public class TrackGP {

    private static final boolean DEBUG_TRACK = Config.TRACK_LOG_ENABLE;

    private Context mContext = null;
    private AdData mAdData = null;
    private String mPackageName = "";
    private String mKey = "";

    public TrackGP(Context context,AdData adData)
    {
        mContext = context;
        mAdData = adData;
        init();
    }

    private boolean init()
    {
        boolean isOk = false;
        try {
            if (mAdData != null) {
                mPackageName = mAdData.getPackageName();
                mKey = mAdData.getKey();
            }
            isOk = true;
        }catch (Exception e)
        {
            if (DEBUG_TRACK)
                MyLog.printException(e);
        }
        return isOk;
    }

    /**
     * 获取referrer是否成功，同步获取,如果获取成功返回true
     *
     * @return
     */
    public boolean isSyncGetReferrerOk(TrackFailBuilder failBuilder,boolean isNeedLocal)
    {
        boolean isOk = false;

        try
        {
            if(!isNeedLocal || TrackUtils.isNeedGetReferrer(mContext,mKey,mPackageName))
            {
                //oldRefer，在load时候，去获取新refer之前的旧refer
                String oldRefer = "";
                ReferData oldReferData = ReferDao.getReferDaoInstance(mContext).queryReferByAdData(mAdData);
                if (oldReferData != null){
                    oldRefer = oldReferData.getRefer();
                    if (!isNeedLocal)
                    MyLog.e(TrackGP.class.getSimpleName(),
                            ByteCrypt.getString("key : ".getBytes())
                                    + oldReferData.getKey()
                                    +ByteCrypt.getString("oldRefer : ".getBytes()) + oldRefer);
                }
                TrackUtils.storeReferrer(mContext, mKey, mPackageName, "");//存个时间点进去

                if(DEBUG_TRACK) {
                    MyLog.i(ByteCrypt.getString("isSyncGetReferrerOk -- start track use webView -- mAdData: ".getBytes()) + mAdData);
                }

                getReferrerByHandler(Thread.currentThread());
                int preloadTimeOut = StrategySharedPreference.getTrackTimeOut(mContext)*1000;


                try
                {
                    Thread.sleep(preloadTimeOut);
                }
                catch (InterruptedException e)
                {
                    if (DEBUG_TRACK) {
                        MyLog.printException(e);
                    }
                }
                //newRefer，在load时候，去获取的refer
                String newRefer="";
                ReferData newReferData = ReferDao.getReferDaoInstance(mContext).queryReferByAdData(mAdData);
                if (newReferData != null){
                    newRefer = newReferData.getRefer();
                    if (!isNeedLocal)
                        MyLog.e(TrackGP.class.getSimpleName(),
                                ByteCrypt.getString("key : ".getBytes())
                                + newReferData.getKey()
                                + ByteCrypt.getString("newRefer : ".getBytes())
                                + newRefer);
                    if(!isNeedLocal && (!TextUtils.equals(oldRefer,newRefer))) {
                        AdData adData = PopAdManager.getInstance(mContext).getAdByKey(newReferData.getKey());
                        MyLog.e(TrackGP.class.getSimpleName(),ByteCrypt.getString("not equals adData : ".getBytes()) + adData);
                        StatisticsUtil.getInstance(mContext).statisticsGetNewReferInLoad(mContext,adData);
                    }
                }

                if(TrackUtils.hasLocalRefer(mContext,mAdData))
                {
                    isOk = true;
                    if(DEBUG_TRACK) {
                        MyLog.i(ByteCrypt.getString("isSyncGetReferrerOk -- track success use webView -- mAdData: ".getBytes()) + mAdData);
                    }
                }
                else
                {
                    TrackUtils.addReferrerFailTime(mContext, mKey, mPackageName);
                    if (failBuilder != null){
                        failBuilder.putArrayValue(mContext, mKey);
                    }
                    if(DEBUG_TRACK) {
                        MyLog.i(ByteCrypt.getString("isSyncGetReferrerOk -- track *** failed *** use webView -- mAdData: ".getBytes()) + mAdData);
                    }
                }
            }
            else
            {
                if(TrackUtils.hasLocalRefer(mContext,mAdData))
                {
                    isOk = true;

                    if(DEBUG_TRACK) {
                        MyLog.i(ByteCrypt.getString("isSyncGetReferrerOk - local already has ** 2 **-- mAdData: ".getBytes()) + mAdData);
                    }
                }
            }
        }catch (Exception e)
        {
            if (DEBUG_TRACK)
                MyLog.printException(e);
        }

        return isOk;

    }



    private void getReferrerByHandler(Thread t)
    {
        try
        {
            if (t != null)
            {
                Message msg = new Message();

                msg.what = TrackThreadHandler.MSG_GETREFWEBVIEW;

                ReferThread referThread = (ReferThread)t;

                referThread.setRequestReferKeyAndPanme(mKey,mPackageName);

                msg.obj = referThread;

                if(mAdData !=null)
                {
                    Bundle bundle = AdDataConverter.dataTobundle(mAdData);

                    msg.setData(bundle);
                    Handler handler = TrackThreadHandler.getInstance(mContext);

                    if(handler != null)
                    {
                        handler.sendMessage(msg);
                    }
                }
            }
        }catch (Exception e)
        {
            if (DEBUG_TRACK)
                MyLog.printException(e);
        }
    }


}
