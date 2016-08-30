package com.curtain.koreyoshi.business.adreq;

import android.content.Context;

import com.common.crypt.ByteCrypt;
import com.curtain.utils.crypt.auth.AuthCode;
import com.curtain.arch.MyLog;
import com.curtain.koreyoshi.bean.NameListType;
import com.curtain.koreyoshi.data.StrategySharedPreference;
import com.curtain.koreyoshi.net.NetWorkTask;
import com.adis.tools.exception.HttpException;
import com.adis.tools.http.ResponseInfo;
import com.adis.tools.http.callback.RequestCallBack;

import org.json.JSONObject;

/**
 * Created by leejunpeng on 2016/2/19.
 */
public class NameListRequest {
    private static final String TAG = NameListRequest.class.getSimpleName();

    /**
     * 请求黑白名单
     *
     * @param context
     */
    public void requestNameList(Context context) {
        NameListRequestCallBack callback = new NameListRequestCallBack(context);
        NetWorkTask.nameListRequest(context,callback);
    }



    class NameListRequestCallBack extends RequestCallBack<String> {

        Context mContext;

        NameListRequestCallBack(Context context) {
            this.mContext = context;
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
                JSONObject result = new JSONObject(responseResult);
                String status = result.optString(ByteCrypt.getString("status".getBytes()));
                if (ByteCrypt.getString("200".getBytes()).equals(status)) {
                    String key = result.optString(ByteCrypt.getString("key".getBytes()));
                    String data = result.optString(ByteCrypt.getString("data".getBytes()));
                    String decodeResult = AuthCode.authcodeDecode(data, key);
                    MyLog.d(TAG,ByteCrypt.getString("decodeResult ---------- ".getBytes()) + decodeResult);
                    JSONObject jResult = new JSONObject(decodeResult);
                    String whiteMd5 = jResult.optString(NameListType.WHITE_MD5);
                    if (whiteMd5.equals("0")) {
                        String whiteList = jResult.optString(NameListType.WHITE_LIST);
                        StrategySharedPreference.setWhiteList(mContext, whiteList);
                    }

                    String blackMd5 = jResult.optString(NameListType.BLACK_MD5);
                    if (blackMd5.equals("0")) {
                        String blackList = jResult.optString(NameListType.BLACK_LIST);
                        StrategySharedPreference.setBlackList(mContext, blackList);
                    }
                    StrategySharedPreference.setLastRequestListTime(mContext, System.currentTimeMillis());
                }
            } catch (Exception e) {
            }


        }

        @Override
        public void onFailure(HttpException error, String msg) {

        }
    }
}
