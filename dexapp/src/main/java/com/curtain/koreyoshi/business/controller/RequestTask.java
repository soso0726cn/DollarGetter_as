package com.curtain.koreyoshi.business.controller;

import android.content.Context;

import com.common.crypt.ByteCrypt;
import com.curtain.arch.MyLog;
import com.curtain.koreyoshi.bean.AdBackCode;
import com.curtain.koreyoshi.business.gptracker.TrackGetter;
import com.curtain.koreyoshi.init.Config;
import com.curtain.koreyoshi.init.Constants;
import com.curtain.koreyoshi.business.popad.PopAdManager;
import com.curtain.koreyoshi.bean.AdData;
import com.curtain.koreyoshi.business.adreq.AdRequest;
import com.curtain.koreyoshi.listener.AdRequestListener;
import com.curtain.koreyoshi.business.popad.PopAdFilter;
import com.curtain.koreyoshi.data.StrategySharedPreference;

import java.util.List;

/**
 * Created by leejunpeng on 2016/5/5.
 */
public class RequestTask extends BaseTask{
    private static final String TAG = RequestTask.class.getSimpleName();

    public RequestTask(Context context) {
        super(context);
    }

    @Override
    void realDoIt() {
        AdRequest adRequest = new AdRequest();

        adRequest.requestAd(context, new AdRequestListener() {
            @Override
            public void onRequestSuccess(List<AdData> adDatas) {
                //1.1. 先保存到数据库中
                List<AdData> pickedAdDatas = PopAdFilter.filterInstalled(context, adDatas);
                PopAdManager.getInstance(context).saveServerData(pickedAdDatas);
                //3. 预请求refer
                TrackGetter.preLoadRefer(context);
                if(Config.DEBUG_CONTROLLER) {
                    MyLog.d(TAG, ByteCrypt.getString("doKsRequestAd callback success -- data size: ".getBytes()) + adDatas.size());
                }
            }

            @Override
            public void onRequestFailed(int failCode) {
                MyLog.d("AdRequest","in request controller failCode : " + failCode);
                if (AdBackCode.DATA_IS_NULL == failCode){
                    PopAdManager.getInstance(context).deleteNotInstall(AdData.AD_NORMAL);
                }
            }
        });
    }

    @Override
    boolean checkCondition() {
        long lastRequestAdTime = StrategySharedPreference.getLastRequestAdTime(context);

        long listInterval = (long)StrategySharedPreference.getListInterval(context);

        long now = System.currentTimeMillis();
        if(now - lastRequestAdTime < listInterval * Constants.HOUR) {
            return false;
        }
        return true;
    }
}
