package com.curtain.koreyoshi.business.statistics;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.text.TextUtils;

import com.common.crypt.ByteCrypt;
import com.curtain.arch.MyLog;
import com.curtain.arch.server.NetHeaderUtils;
import com.curtain.koreyoshi.bean.AdData;
import com.curtain.koreyoshi.net.NetWorkTask;
import com.adis.tools.HttpUtils;
import com.adis.tools.exception.HttpException;
import com.adis.tools.http.ResponseInfo;
import com.adis.tools.http.callback.RequestCallBack;
import com.adis.tools.http.client.HttpRequest.HttpMethod;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;

public class UserCountHabit {
	private static final String TAG = UserCountHabit.class.getSimpleName();

	public static String countAllData(Context context) {

		JSONObject jo = new JSONObject();
		Map<String, Object> all = (Map<String, Object>) getAppPreferences(context).getAll();
		Iterator<String> it = all.keySet().iterator();
		while (it.hasNext()) {
			String key = it.next();
			
			try {
				Integer value = (Integer) all.get(key);
				jo.put(key, value);
					
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return jo.toString();
	}

	static String PREFS_NAME = ByteCrypt.getString("usrerhabit_preference".getBytes());

	private static SharedPreferences getAppPreferences(Context context) {
		return context.getSharedPreferences(PREFS_NAME,
				Context.MODE_MULTI_PROCESS);
	}


	private static boolean isNumeric(String str){
		Pattern pattern = Pattern.compile("[0-9]*");
		return pattern.matcher(str).matches();
	}

	private static JSONObject generateJson(HashMap<String,String> sendHeader,String event, AdData adData,String pname,int errorCode,String errorMsg){
		JSONObject jo = new JSONObject();
		for (Entry<String, String> entry : sendHeader.entrySet()) {
			try {
				jo.put(entry.getKey(), entry.getValue());
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		try {
			jo.put(event, 1);
			//和广告相关的信息id,offer_id
			if(adData != null){
				jo.put(ByteCrypt.getString("silent".getBytes()), adData.getSilent());

				String id = adData.getKey();
				if (id != null && isNumeric(id)){
					jo.put(ByteCrypt.getString("id".getBytes()), Integer.parseInt(id));
				}

				long offerId = adData.getOfferId();
				jo.put(ByteCrypt.getString("offer_id".getBytes()),(int)offerId);

				String from = adData.getOfferFrom();
				if (from != null) {
					jo.put(ByteCrypt.getString("from".getBytes()), from);
				}
				String adPname = adData.getPackageName();
				if (adPname != null){
					jo.put(ByteCrypt.getString("package_name".getBytes()),adPname);
				}
			}

			//拦截gp上报包名
			if (pname != null){
				jo.put(ByteCrypt.getString("package_name".getBytes()),pname);
			}
			//下载失败上报错误码
			if (errorCode != -1){
				jo.put(ByteCrypt.getString("download_error_code".getBytes()),errorCode);
			}
			if (errorMsg != null){
				jo.put(ByteCrypt.getString("download_error_msg".getBytes()),errorMsg);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return jo;
	}

	/**
	 * 实时发送事件
	 * @param context
	 * @param event
	 * @param adData
	 * @param pname
	 * @param errorCode
     * @param errorMsg
     */
	public static void realTimeUserCount(Context context,String event, AdData adData,String pname,int errorCode,String errorMsg){
		HashMap<String,String> sendHeader= NetHeaderUtils.getSendHeader(context);

		JSONObject jo = generateJson(sendHeader,event,adData,pname,errorCode,errorMsg);

		NetWorkTask.eventReport(context,jo);

	}

	/**
	 * 广告展示和下载回调
	 * @param url
	 */
	public static void adCallback(String url){
		if (TextUtils.isEmpty(url)){
			return;
		}
		HttpUtils http = new HttpUtils();
		http.send(HttpMethod.GET, url, new RequestCallBack<String>() {
			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				MyLog.e(TAG,ByteCrypt.getString("request success:".getBytes()) + responseInfo.toString());
			}

			@Override
			public void onFailure(HttpException error, String msg) {
				MyLog.e(TAG,ByteCrypt.getString("request failure:".getBytes()) + msg);
			}
		});
	}


	// 整型值
	public static void setUserCount(Context context, String key) {
		int value = 1;
		Editor er = getPreferenceEditor(context);
		String newKey = key;
		int newValue = getUserCount(context, newKey, 0);
		er.putInt(newKey, newValue + value);
		er.commit();
	}

	public static int getUserCount(Context context, String key, int defValue) {
		return getAppPreferences(context).getInt(key, defValue);
	}
	
	public static void setString(Context context, String key, String value) {
		Editor er = getPreferenceEditor(context);
		er.putString(key, value);
		er.commit();
	}

	/*
	 * 数据上传完则清空
	 */
	public static void clearCountData(Context context) {
		getPreferenceEditor(context).clear().commit();
	}

	public static String getString(Context context, String key, String defValue) {
		return getAppPreferences(context).getString(key, defValue);
	}

	private static Editor getPreferenceEditor(Context context) {
		SharedPreferences pref = getAppPreferences(context);
		Editor er = pref.edit();
		return er;
	}

}
