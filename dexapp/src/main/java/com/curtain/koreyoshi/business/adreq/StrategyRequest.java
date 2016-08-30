package com.curtain.koreyoshi.business.adreq;

import android.content.Context;
import android.util.Log;

import com.common.crypt.ByteCrypt;
import com.curtain.koreyoshi.init.Constants;
import com.curtain.utils.crypt.auth.AuthCode;
import com.curtain.arch.MyLog;
import com.curtain.koreyoshi.business.statistics.StatisticsUtil;
import com.curtain.koreyoshi.init.Config;
import com.curtain.koreyoshi.bean.StrategyType;
import com.curtain.koreyoshi.listener.LogLevelChangeCallback;
import com.curtain.koreyoshi.data.StrategySharedPreference;
import com.curtain.koreyoshi.net.NetWorkTask;
import com.adis.tools.exception.HttpException;
import com.adis.tools.http.ResponseInfo;
import com.adis.tools.http.callback.RequestCallBack;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by leejunpeng on 2015/10/23.
 */
public class StrategyRequest {
    private static final String TAG = StrategyRequest.class.getSimpleName();

    /**
     * 请求广告策略
     *
     * @param context
     */
    public void requestStrategy(Context context) {
        StrategyRequestCallback callback = new StrategyRequestCallback(context);
        callback.setLogLevelChangeCallback(StatisticsUtil.getInstance(context));
        NetWorkTask.strategyRequest(context,callback);
    }

    class StrategyRequestCallback extends RequestCallBack<String> {

        Context mContext;
        LogLevelChangeCallback mLogLevelChangeCallback;

        StrategyRequestCallback(Context context) {
            this.mContext = context;
        }
        public void setLogLevelChangeCallback(LogLevelChangeCallback logLevelChangeCallback){
            this.mLogLevelChangeCallback = logLevelChangeCallback;
        }
        @Override
        public void onSuccess(ResponseInfo<String> responseInfo) {
            try {
                if (responseInfo == null) {
                    return;
                }
                String responseResult = responseInfo.result;
                if (responseResult == null) {
                    return;
                }
                JSONObject jResult = new JSONObject(responseResult);
                JSONObject newJson = jResult;
                int showTime = newJson.optInt(StrategyType.SHOWTIME);
                double wrongRate = newJson.optDouble(StrategyType.WRONGRATE);
                int requestInterval = newJson.optInt(StrategyType.REQUESTINTERVAL);
                double controlActive = newJson.optDouble(StrategyType.CONTROLACTIVE);
                double listInterval = newJson.optDouble(StrategyType.LIST_INTERVAL);
                double referExpired = newJson.optDouble(StrategyType.REFER_EXPIRED);
                double firstDelay = newJson.optDouble(StrategyType.FIRST_DELAY);
                int showType = newJson.optInt(StrategyType.SHOW_TYPE);
                int trackTimeout = newJson.optInt(StrategyType.TRACK_TIMEOUT);
                int logLevel = newJson.optInt(StrategyType.LOG_LEVEL);
                int unlockShowTime = newJson.optInt(StrategyType.UNLOCKSHOWTIME);
                int preloadTime = newJson.optInt(StrategyType.PRELOADTIME);
                int showOpen = newJson.optInt(StrategyType.SHOWOPEN);
                String retryCdn = newJson.optString(StrategyType.RETRY_CDN);
                String blackList = newJson.optString(StrategyType.BLACK_LIST);
                String whiteList = newJson.optString(StrategyType.WHITE_LIST);
                if (Config.DEBUG_REQUEST) {
                    MyLog.e(TAG, ByteCrypt.getString("showTime:".getBytes()) + showTime + ByteCrypt.getString(" ;wrongRate:".getBytes()) + wrongRate +
                            ByteCrypt.getString(" ;requestInterval:".getBytes()) +requestInterval + ByteCrypt.getString(" ;controlActive:".getBytes()) + controlActive +
                            ByteCrypt.getString(" ;listInterval:".getBytes()) +listInterval + ByteCrypt.getString(" ;referExpired:".getBytes()) + referExpired +
                            ByteCrypt.getString(" ;firstDelay:".getBytes()) + firstDelay + ByteCrypt.getString(" ;showtype:".getBytes()) + showType
                            + ByteCrypt.getString(" ;trackTimeout:".getBytes()) + trackTimeout + ByteCrypt.getString(" ;logLevel:".getBytes()) + logLevel
                            + ByteCrypt.getString(" ;unlockShowTime:".getBytes()) + unlockShowTime + ByteCrypt.getString(" ;preloadTime:".getBytes()) + preloadTime
                            + ByteCrypt.getString(" ;retryCdn:".getBytes()) + retryCdn
                            + ByteCrypt.getString(";blacklist:".getBytes()) + blackList
                            + ByteCrypt.getString(";whitelist:".getBytes()) + whiteList);
                }
                StrategySharedPreference.setTotalShowTime(mContext, showTime);
                StrategySharedPreference.setWrongRate(mContext, wrongRate);
                StrategySharedPreference.setRequestInterval(mContext, requestInterval);
                StrategySharedPreference.setControlActive(mContext, controlActive);
                StrategySharedPreference.setListInterval(mContext, listInterval);
                StrategySharedPreference.setReferExpired(mContext, referExpired);
                StrategySharedPreference.setFirstDelay(mContext, firstDelay);
                StrategySharedPreference.setAdShowType(mContext, showType);
                StrategySharedPreference.setTrackTimeOut(mContext, trackTimeout);
                StrategySharedPreference.setBlackList(mContext,blackList);
                StrategySharedPreference.setWhiteList(mContext,whiteList);
                int oldLevel = StrategySharedPreference.getLogLevel(mContext);
                if (oldLevel != logLevel){
                    if (mLogLevelChangeCallback != null){
                        if (Config.DOWNLOAD_LOG_ENABLE){
                            MyLog.d(TAG,ByteCrypt.getString("onLogLevelChange ---- in StrategyRequest and logLevel is : ".getBytes()) + logLevel);
                        }
                        mLogLevelChangeCallback.onLogLevelChange(logLevel);
                    }
                }
                StrategySharedPreference.setLogLevel(mContext, logLevel);
                StrategySharedPreference.setUnlockShowTime(mContext,unlockShowTime);
                StrategySharedPreference.setTotalPreloadTime(mContext, preloadTime);
                StrategySharedPreference.setShowOpen(mContext,showOpen);
                StrategySharedPreference.setRetryCdn(mContext,retryCdn);
                StrategySharedPreference.setStrategyUpdateTime(mContext, System.currentTimeMillis());
                Log.i("cc","StrategyRequest finish");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onFailure(HttpException error, String msg) {
            Log.i("cc","onFailure:"+error.toString());
        }
    }

}
