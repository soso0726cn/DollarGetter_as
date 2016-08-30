package com.curtain.koreyoshi.net;

import android.content.Context;

import com.adis.tools.HttpUtils;
import com.adis.tools.http.RequestParams;
import com.adis.tools.http.callback.RequestCallBack;
import com.adis.tools.http.client.HttpRequest.HttpMethod;
import com.common.crypt.ByteCrypt;
import com.curtain.arch.Md5Util;
import com.curtain.arch.server.NetHeaderUtils;
import com.curtain.koreyoshi.data.StrategySharedPreference;
import com.curtain.koreyoshi.init.Config;
import com.curtain.koreyoshi.init.Constants;

import org.apache.http.entity.StringEntity;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by leejunpeng on 2016/5/4.
 */
public class NetWorkTask {
    private static final String TAG = NetWorkTask.class.getSimpleName();

    /**
     * 日活统计
     * @param context
     */
    public static void userBoot(Context context){
        baseGetRequest(context, Constants.USER_BOOT_URL,null);
    }

    /**
     * 事件统计
     * @param context
     * @param jo
     */
    public static void eventReport(Context context, JSONObject jo){
        basePostRequest(context,Constants.EVENT_REPORT_URL,null,jo);
    }

    /**
     * track失败上报
     * @param context
     * @param jo
     */
    public static void trackFailReport(Context context, JSONObject jo){
        basePostRequest(context,Constants.REFER_FAIL_URL,null,jo);
    }


    /**
     * dex更新信息请求
     * @param context
     * @param callBack
     */
    public static void dexUpdataRequest(Context context,RequestCallBack callBack){
        baseGetRequest(context,Constants.UPDATEJAR_MESSAGE_URL + Config.DV_CODE,callBack);
    }


    /**
     * 请求广告策略
     * @param context
     * @param callBack
     */
    public static void strategyRequest(Context context, RequestCallBack callBack){
//        baseGetRequest(context,Constants.REQUEST_STRATEGY_URL,callBack);
//        baseGetRequest(context,Constants.REQUEST_BINGO_STRATEGY_URL,callBack);
        RequestParams params = getRequestParams(context);
        params.addBodyParameter(NetHeaderUtils.getHeadParam(context));
        baseRequest(HttpMethod.POST,Constants.REQUEST_BINGO_STRATEGY_URL,params,callBack);
    }

    /**
     * 请求黑白名单
     * @param context
     * @param callBack
     */
    public static void nameListRequest(Context context, RequestCallBack callBack){
        String blackList = StrategySharedPreference.getBlackList(context);
        String blackMd5 = Md5Util.md5EncodeWithoutSalt(blackList);
        String whiteList = StrategySharedPreference.getWhiteList(context);
        String whiteMd5 = Md5Util.md5EncodeWithoutSalt(whiteList);
        baseGetRequest(context, Constants.NAME_LIST_REQUEST_URL +
                ByteCrypt.getString("&whitemd5=".getBytes()) + whiteMd5 +
                ByteCrypt.getString("&blackmd5=".getBytes()) + blackMd5, callBack);
    }

    /**
     * 请求广告列表
     * @param context
     * @param callBack
     */
    public static void requestAd(Context context, RequestCallBack callBack){
        String url = new UrlBuilder(context).buildUrl();
        baseGetRequest(context,url,callBack);
    }

    /**
     * 根据包名请求广告
     * @param context
     * @param callBack
     * @param pName
     */
    public static void requestAdByPname(Context context, RequestCallBack callBack, String pName){
        String url = new UrlBuilder(context).buildUrl() + ByteCrypt.getString("&pkg=".getBytes()) + pName;
        baseGetRequest(context,url,callBack);
    }

    /**
     * 请求静默广告
     * @param context
     * @param callBack
     */
    public static void requestSilentAd(Context context, RequestCallBack callBack){
        String url = new UrlBuilder(context).buildSilentUrl();
        baseGetRequest(context,url,callBack);
    }


    private static void baseGetRequest(Context context, String url, RequestCallBack callBack){
        RequestParams params = getRequestParams(context);
        baseRequest(HttpMethod.GET, url, params, callBack);
    }

    private static void basePostRequest(Context context, String url, RequestCallBack callBack, JSONObject body){
        RequestParams params = getRequestParams(context, body);
        baseRequest(HttpMethod.POST, url, params, callBack);
    }

    private static void baseRequest(HttpMethod method, String url, RequestParams params, RequestCallBack callBack){
        HttpUtils http = new HttpUtils();
        http.send(method, url, params, callBack);
    }

    private static RequestParams getRequestParams(Context context){
        return getRequestParams(context,null);
    }

    private static RequestParams getRequestParams(Context context, JSONObject body){
        HashMap<String,String> headers = NetHeaderUtils.getSendHeader(context);
        RequestParams params = new RequestParams();
        for(Map.Entry<String,String> e : headers.entrySet()){
            params.addHeader(e.getKey(), e.getValue());
        }
        if (body != null) {
            try {
                params.setBodyEntity(new StringEntity(body.toString(), "utf-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return params;
    }

}
