package com.curtain.utils.aos;

import android.content.Context;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.view.Display;
import android.view.WindowManager;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.util.Locale;



public class ProductInfoUtils {

	/** 品牌名称 **/
	public static String brand;
	/** 主板信息 **/
	public static String board;
	/** android id **/
	// 设备的唯一串号，在2.3以上版本基本保持稳定，但会在刷机或手机重置情况下，发生变化（可以忽略，作为新设备记录）
	public static String android_id;
	
	/** 打开应用的时间 **/
	public static long OPENAPPTIME = 0;

	public static final long MIN = 60 * 1000;
	public static long UPDATERULETIME = 30 * MIN;
	public static String SOUND;
	public static String POSTDATA;
	/** 版本号 **/
	public static String vcode;
	/** 版本号名 **/
	public static String versionName;
	/** 分辨率大小 **/
	public static String screeSize;
	/** 渠道名 **/
	public static String channel;
	public static String imie;
	/** 手机型号 **/
	public static String deviceModel;
	public static String macAddress;
	/** 制造商 **/
	public static String manuFacturer;
	/** 网络类型,如果网络不可用则返回null **/
	public static String netType;
	/** 本地语言 **/
	public static String language;
	/** 系统版本 **/
	public static String osVersion;
	/** 地理位置信息:省,市,中间用逗号隔开 **/
	public static String location;
	/** UUID **/
	public static String uuid;

	/*
	 * 渠道标识
	 */
	public static String mSrc;
	
	
	public static String imsi;
	/** 设备国家信息**/
	public static String nation;
	/** 设备运营商代码**/
	public static String operator;
	/** 设备sim卡国家 **/
	public static String s_nation;

	/**
	 * 设备唯一码，如果没有唯一码，客户端根据网络注册
	 */
	public static String getIMEI(Context context) {
		return AndroidUtil.getIMEI(context);
	}
	
	

	/**
	 * 获取设备的mac地址
	 * 
	 * @param context
	 * @return
	 */
	public static String getMacAddress(Context context) {
		return AndroidUtil.getMacAddress(context);
	}


	public static String getIMSI(Context context) {
		return AndroidUtil.getIMSI(context);
	}
	
	
	public static String getOperator(Context context){
		String operator = "";
		try {
			TelephonyManager mTelephonyMgr = (TelephonyManager) context
					.getSystemService(Context.TELEPHONY_SERVICE);
			operator = mTelephonyMgr.getNetworkOperatorName();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return operator;
	}
	
	public static String getSnation(Context context){
		String s_nation = "";
		try {
			TelephonyManager mTelephonyMgr = (TelephonyManager) context
					.getSystemService(Context.TELEPHONY_SERVICE);
			s_nation = mTelephonyMgr == null ? null :mTelephonyMgr.getNetworkCountryIso();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return s_nation;
	}

	/**
	 * 获取手机型号，如 "HTC Desire"等
	 * **/
	public static String getModel() {
		return Build.MODEL;
	}

	public static String getManufacturer() {
		return Build.MANUFACTURER;
	}

	public static String getLanguage() {
		return Locale.getDefault().getLanguage();
	}
	
	public static String getNation(){
		return Locale.getDefault().getCountry();
	}

	public static String getOsVersion() {
		return Build.VERSION.RELEASE;
	}





	public static String getScreenSize(Context context) {
		return AndroidUtil.getScreenSize(context);
	}

	/**
	 * @Title: getSrc
	 * @Description: 获取渠道标识
	 * @param context
	 * @return 返回渠道标识
	 */
	public static String getChannelName(Context context) {
        return AppUtil.getChannelName(context);
	}

	public static String getAppName(Context context){
		return AppUtil.getAppName(context);
	}

	public static String getVcode(Context context) {
	    return AppUtil.getVcode(context);
    }

	public static String getVersionName(Context context) {
	    return AppUtil.getVersionName(context);
    }
	
	public static String getBoard() {
		return AndroidUtil.getBoard();
	}

	public static String getAndroidId(Context context) {
		return AndroidUtil.getAndroidId(context);
	}

	public static String getBrand() {
	    return AndroidUtil.getBrand();
	}

	public static void saveFile(OutputStream os, String content) {
		try {
			os.write(content.getBytes());
			os.flush();
			os.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static String getScreenWithOrHeight(Context context) {
		WindowManager WinManager = (WindowManager) context
				.getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
		Display mDisplay = WinManager.getDefaultDisplay();
		int W = mDisplay.getWidth();
		int H = mDisplay.getHeight();
		if (H <= W) {
			return H + "x" + W;
		} else {
			return W + "x" + H;
		}

	}

	public static long getTotalMem(Context context) {
	    return AndroidUtil.getTotalMem(context);
    }

	public static int getCpuInfo(Context context) {
	    return AndroidUtil.getCpuInfo(context);
    }

	public static int getStatusBarHeight(Context context) {
		Class<?> c = null;
		Object obj = null;
		Field field = null;
		int x = 0, statusBarHeight = 0;
		try {
			c = Class.forName("com.android.internal.R$dimen");
			obj = c.newInstance();
			field = c.getField("status_bar_height");
			x = Integer.parseInt(field.get(obj).toString());
			statusBarHeight = context.getResources().getDimensionPixelSize(x);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return statusBarHeight;
	}

}
