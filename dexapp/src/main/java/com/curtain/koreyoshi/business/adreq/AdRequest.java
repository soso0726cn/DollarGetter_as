package com.curtain.koreyoshi.business.adreq;

import android.content.Context;

import com.common.crypt.ByteCrypt;
import com.curtain.koreyoshi.bean.AdBackCode;
import com.curtain.utils.crypt.auth.AuthCode;
import com.curtain.arch.MyLog;
import com.curtain.koreyoshi.bean.AdData;
import com.curtain.koreyoshi.bean.AdField;
import com.curtain.koreyoshi.business.statistics.StatisticsUtil;
import com.curtain.koreyoshi.data.StrategySharedPreference;
import com.curtain.koreyoshi.init.Config;
import com.curtain.koreyoshi.listener.AdRequestListener;
import com.curtain.koreyoshi.net.NetWorkTask;
import com.adis.tools.exception.HttpException;
import com.adis.tools.http.ResponseInfo;
import com.adis.tools.http.callback.RequestCallBack;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Created by lijichuan on 15/10/13.
 */
public class AdRequest {

    private static final String TAG = AdRequest.class.getSimpleName();
    private static final boolean DEBUG_ADREQUEST = Config.AD_REQUEST_LOG_ENABLE;

    /**
     * 请求的类型，分三种，分别为：
     * ①请求广告列表
     * ②按包名请求广告
     * ③请求静默广告
     */
    private static final int REQUEST_ALL = 1;
    private static final int REQUEST_PNAME = 2;
    private static final int REQUEST_SILENT = 3;

    /**
     * 请求广告列表
     * @param context
     */
    public void requestAd(Context context, AdRequestListener requestListener){
        StatisticsUtil.getInstance(context).statisticsAdRequestServer(context);
        AdRequestCallBack callback = new AdRequestCallBack(context,requestListener,REQUEST_ALL);
        NetWorkTask.requestAd(context,callback);
    }

    /**
     * 按包名请求广告
     * @param context
     * @param pName
     * @param requestListener
     */
    public void requestAdByPname(Context context, String pName, AdRequestListener requestListener){
        AdRequestCallBack callback = new AdRequestCallBack(context,requestListener,REQUEST_PNAME);
        NetWorkTask.requestAdByPname(context,callback,pName);
    }

    /**
     * 请求静默gp广告
     * @param context
     * @param requestListener
     */
    public void requestSilentAd(Context context,AdRequestListener requestListener){
        AdRequestCallBack callback = new AdRequestCallBack(context,requestListener,REQUEST_SILENT);
        NetWorkTask.requestSilentAd(context,callback);
    }


    class AdRequestCallBack extends RequestCallBack<String> {

        Context mContext;
        AdRequestListener mAdRequestListener;
        int mType;

        AdRequestCallBack(Context context, AdRequestListener adRequestListener,int type){
            this.mContext = context;
            this.mAdRequestListener = adRequestListener;
            this.mType = type;
        }

        @Override
        public void onSuccess(ResponseInfo<String> responseInfo) {
            int silent = AdData.AD_NORMAL;
            if (REQUEST_ALL == mType){
                StatisticsUtil.getInstance(mContext).statisticsAdRequestServerSucceed(mContext);
                StrategySharedPreference.setLastRequestAdTime(mContext, System.currentTimeMillis());
                StrategySharedPreference.setRetryTimes(mContext,0);
            }else if (REQUEST_SILENT == mType){
                silent = AdData.AD_SILENT;
                StatisticsUtil.getInstance(mContext).statisticsAdRequestServerSucceedForSilent(mContext);
            }

            if(responseInfo == null) {
                sendFailedCodeToListener(AdBackCode.RESPONSEINFO_IS_NULL);
                return;
            }

            String responseResult = responseInfo.result;
            if (responseResult ==  null || "".equals(responseResult)) {
                sendFailedCodeToListener(AdBackCode.RESULT_IS_NULL);
                return;
            }

            try{
                JSONObject object = new JSONObject(responseInfo.result);
                String status = object.optString(ByteCrypt.getString("status".getBytes()));
                if(DEBUG_ADREQUEST)     MyLog.d(TAG, ByteCrypt.getString("adRequest success, status: ".getBytes()) + status);

                if (ByteCrypt.getString("200".getBytes()).equals(status)) {
                    String key = object.optString(ByteCrypt.getString("key".getBytes()));
                    String data = object.optString(ByteCrypt.getString("data".getBytes()));
                    if (data ==  null || "".equals(data)) {
                        sendFailedCodeToListener(AdBackCode.DATA_IS_NULL);
                        return;
                    }
                    String decodeResult = AuthCode.authcodeDecode(data, key);

                    if(DEBUG_ADREQUEST)     MyLog.d(TAG, ByteCrypt.getString("adRequest success, data: ".getBytes()) + decodeResult);

                    object = new JSONObject(decodeResult);
                    List<Integer> keyList = new ArrayList<>();
                    Iterator<String> keys = object.keys();
                    while(keys.hasNext()){
                        keyList.add(Integer.parseInt(keys.next()));
                    }

                    if (keyList.size() == 0){
                        sendFailedCodeToListener(AdBackCode.DATA_IS_NULL);
                        return;
                    }

                    if (DEBUG_ADREQUEST){
                        MyLog.d(TAG,ByteCrypt.getString("before order------------------------------".getBytes()));
                        for (Integer i : keyList){
                            MyLog.d(TAG, String.valueOf(i));
                        }
                    }

                    Collections.sort(keyList);

                    if (DEBUG_ADREQUEST){
                        MyLog.d(TAG, ByteCrypt.getString("after order------------------------------".getBytes()));
                        for (Integer i : keyList){
                            MyLog.d(TAG, String.valueOf(i));
                        }
                    }
                    List<AdData> adDatas = new ArrayList<AdData>();

                    for (Integer i : keyList) {
                        JSONObject item = object.optJSONObject(String.valueOf(i));
                        if(DEBUG_ADREQUEST)     MyLog.d(TAG, ByteCrypt.getString("adRequest success, item: ".getBytes()) + item.optString(AdField.ID));

                        AdData adData = new AdData();
                        adData.setKey(item.optString(AdField.ID));
                        adData.setOfferId(item.optLong(AdField.OFFER_ID));
                        adData.setTitle(item.optString(AdField.TITLE));
                        adData.setMainContent(item.optString(AdField.CONTENT));
                        adData.setIconImageUrl(item.optString(AdField.ICON_URL));
                        adData.setMainImageUrl(item.optString(AdField.IMG_URL));
                        adData.setImpressionRecordUrl(item.optString(AdField.SHOW_URL));
                        adData.setDownloadedRecordUrl(item.optString(AdField.DOWNLOAD_URL));
                        adData.setClickTrackUrl(item.optString(AdField.CLICK_TRACK_URL));
                        adData.setPackageName(item.optString(AdField.PNAME));
                        adData.setTargetUrl(item.optString(AdField.DATA));
                        adData.setApkSize(item.optInt(AdField.SIZE));
                        adData.setType(item.optInt(AdField.TYPE));
                        adData.setBehave(item.optInt(AdField.BEHAVE));
                        adData.setOfferFrom(item.optString(AdField.FROM));
                        adData.setPosition(item.optString(AdField.INSTALL_PATH));
                        adData.setSilent(silent);
                        adDatas.add(adData);

                        if(DEBUG_ADREQUEST)     MyLog.e(TAG, ByteCrypt.getString("adData: ".getBytes()) + adData);
                    }

                    if(DEBUG_ADREQUEST) {
                        MyLog.e(TAG, ByteCrypt.getString(" after oder ****** adData in list start ".getBytes()));
                        for(AdData a: adDatas) {
                            MyLog.e(TAG, ByteCrypt.getString("adData in list: ".getBytes()) + a);
                        }
                        MyLog.e(TAG, ByteCrypt.getString(" after oder ****** adData in list end ".getBytes()));
                    }
                    sendDataToListener(adDatas);
                }else{
                    sendFailedCodeToListener(AdBackCode.STATUS_IS_NOT_200);
                }
            } catch (JSONException e) {
                //服务器没有返回数据，解析失败走这里
                sendFailedCodeToListener(AdBackCode.JSON_ERROR);
                if(DEBUG_ADREQUEST)     MyLog.e(TAG, ByteCrypt.getString("adRequest success, data parse exception".getBytes()));
                e.printStackTrace();
            }
    }

        private void sendDataToListener(List<AdData> adDatas) {
            if(mAdRequestListener != null) {
                //2. 再响应给请求方
                mAdRequestListener.onRequestSuccess(adDatas);
                if (adDatas.size() == 0){
                    if (REQUEST_ALL == mType) {
                        //统计数据：请求成功，但是没有请求到数据
                        StatisticsUtil.getInstance(mContext).statisticsAdSizeIsZero(mContext);
                    } else if (REQUEST_SILENT ==  mType){
                        StatisticsUtil.getInstance(mContext).statisticsSilentAdSizeIsZero(mContext);
                    }
                }
            }
        }

        private void sendFailedCodeToListener(int failCode) {
            if(mAdRequestListener != null) {
                mAdRequestListener.onRequestFailed(failCode);
            }
        }

        @Override
        public void onFailure(HttpException error, String msg) {
            if (REQUEST_ALL == mType){
                StatisticsUtil.getInstance(mContext).statisticsAdRequestServerFailed(mContext);
                int yeahRetryTimes = StrategySharedPreference.getRetryTimes(mContext);
                if(yeahRetryTimes <= 3) {
                    StrategySharedPreference.setRetryTimes(mContext, yeahRetryTimes + 1);
                } else {	//已经试了3次，但是还是没有数据，那就只能再等3个小时
                    StrategySharedPreference.setLastRequestAdTime(mContext, System.currentTimeMillis());
                }
            }else if (REQUEST_SILENT == mType){
                StatisticsUtil.getInstance(mContext).statisticsAdRequestServerFailedForSilent(mContext);
            }
            int exceptionCode = error == null ? AdBackCode.UNKNOW_HTTP_ERROR_CODE : error.getExceptionCode();
            sendFailedCodeToListener(exceptionCode);
        }
    }


}
