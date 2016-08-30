package com.curtain.arch.server;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.text.TextUtils;

import com.common.crypt.ByteCrypt;
import com.curtain.arch.MyLog;
import com.curtain.utils.aos.ChannelFacotry;
import com.curtain.utils.aos.DeviceUUIDFacotry;
import com.curtain.utils.aos.ProductInfoUtils;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;


public class NetHeaderUtils {
	private static String mNetworkType;
	
	/**
	 * 判断网络是否连接
	 *
	 * @param context
	 *            环境对象
	 * @return true 有网络，false 无网络
	 */
	public static boolean isNetWorking(Context context) {
		try {
			ConnectivityManager cm = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo networkInfo = cm.getActiveNetworkInfo();
			if (networkInfo != null && networkInfo.isAvailable()) {
				if (networkInfo.getTypeName().equals("WIFI")) {
					mNetworkType = networkInfo.getTypeName().toLowerCase(
							Locale.ENGLISH);
				} else {
					if (networkInfo.getExtraInfo() == null) {
						mNetworkType = "";
					} else {
						mNetworkType = networkInfo.getExtraInfo().toLowerCase(
								Locale.ENGLISH);
					}
				}
				return true;
			} else {
				mNetworkType = "";
				return false;
			}
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * 获取网络类型
	 * 
	 * @return 网络类型
	 */
	public static String getNetworkType(Context context) {
			isNetWorking(context);
		return mNetworkType;
	}
	
	/*
	 * 获取头信息
	 */
	public static HashMap<String, String> getSendHeader(Context context) {
		long start = System.currentTimeMillis();
		HashMap<String, String> map = new HashMap<String, String>();
		String advertisingId = HeaderSharedPreference.getAdvertisingid(context);
		String header = HeaderSharedPreference.getHeaders(context);
		if (!TextUtils.isEmpty(header)) {
			header = header.substring(1, header.length() - 1);
			String[] tmp = header.split(",");
			for (String str : tmp) {
				String[] kv = str.split("=");
				if (kv.length > 1) {
					String key = kv[0];
					String value = kv[1];
					map.put(key.trim(), value.trim());
				}
			}
		} else {
			String manuFacturer = ProductInfoUtils.manuFacturer;
			String androidid = ProductInfoUtils.android_id;
			String board = ProductInfoUtils.board;
			String brand = ProductInfoUtils.brand;
			if (TextUtils.isEmpty(manuFacturer)) {
				manuFacturer = ProductInfoUtils.getManufacturer();
			}
			if (TextUtils.isEmpty(androidid)) {
				androidid = ProductInfoUtils.getAndroidId(context);
			}
			if (TextUtils.isEmpty(board)) {
				board = ProductInfoUtils.getBoard();
			}
			if (TextUtils.isEmpty(brand)) {
				brand = ProductInfoUtils.getBrand();
			}
			map.put(HeadStr.APP, ProductInfoUtils.getAppName(context));//3、pname
			map.put(HeadStr.MANUFACTURER, manuFacturer); // 获取手机型号，如 "HTC Desire"等
			map.put(HeadStr.ANDROIDID, androidid);//12、a_id
			map.put(HeadStr.BOARD, board);
			map.put(HeadStr.BRAND, brand);
			map.put(HeadStr.PRODUCER, android.os.Build.DEVICE);
			map.put(HeadStr.PRODUCT, android.os.Build.MODEL);//5、dev_name
			map.put(HeadStr.SDK, android.os.Build.VERSION.SDK);//9、os_v
			map.put(HeadStr.RESOLUTION,
					ProductInfoUtils.getScreenWithOrHeight(context));//7、screen_size
			map.put(HeadStr.DEVICE_MD5, "none");
			map.put(HeadStr.CPU, ProductInfoUtils.getCpuInfo(context) + ""); //6、gpu
			map.put(HeadStr.MEM, ProductInfoUtils.getTotalMem(context) + ""); // 内存总大小
			HeaderSharedPreference.setHeaders(context, map.toString());
		}

		String vcode = ProductInfoUtils.vcode;
		String versionName = ProductInfoUtils.versionName;
		String imei = ProductInfoUtils.imie;
		String mc = ProductInfoUtils.macAddress;
		
		String imsi = ProductInfoUtils.imsi;
		String nation = ProductInfoUtils.nation;
		String operator = ProductInfoUtils.operator;
		String lang = ProductInfoUtils.language;
		String s_nation = ProductInfoUtils.s_nation;

		if (vcode == null)
			vcode = ProductInfoUtils.getVcode(context);
		if (TextUtils.isEmpty(versionName)) {
			versionName = ProductInfoUtils.getVersionName(context);
		}
		if (imei == null)
			imei = ProductInfoUtils.getIMEI(context);
		if (TextUtils.isEmpty(mc)) {
			mc = ProductInfoUtils.getMacAddress(context);
		}
		if (TextUtils.isEmpty(imsi)) {
			imsi = ProductInfoUtils.getIMSI(context);
		}
		if (TextUtils.isEmpty(nation)) {
			nation = ProductInfoUtils.getNation();
		}
		if (TextUtils.isEmpty(operator)) {
			operator = ProductInfoUtils.getOperator(context);
		}
		if (TextUtils.isEmpty(lang)) {
			lang = ProductInfoUtils.getLanguage();
		}
		if (TextUtils.isEmpty(s_nation)) {
			s_nation = ProductInfoUtils.getSnation(context);
		}

		map.put(HeadStr.MC, mc);
		map.put(HeadStr.VCODE, vcode);//22、sdk_v
		map.put(HeadStr.IMEI, imei);//1、imei
		String channel = ChannelFacotry.getChannel(context);
		map.put(HeadStr.CHANNEL, channel);//11、c_id
		map.put(HeadStr.NET, getNetworkType(context));//15、network
		map.put(HeadStr.TIME, System.currentTimeMillis() / 1000 + "");
		map.put(HeadStr.APIS, ByteCrypt.getString("F:SP_V:".getBytes()) + ProductInfoUtils.getVcode(context));
		map.put(HeadStr.VERSIONNAME, versionName);

		map.put(HeadStr.IMSI, imsi);//2、imsi
		map.put(HeadStr.OS, 1+"");//8、os
		map.put(HeadStr.NATION, nation);//13、nation
		map.put(HeadStr.OPERATOR, operator);//14、operator
		map.put(HeadStr.LANG, lang);//17、lang
		map.put(HeadStr.S_NATION, s_nation);//20、s_nation
		map.put(HeadStr.UNKOWN_SOURCE, 1+"");//21、unkown_source

		String tempUuid = DeviceUUIDFacotry.getUUID(context);
		map.put(HeadStr.UUID, tempUuid);//16、uid

    map.put(HeadStr.GROUP, ByteCrypt.getString("km".getBytes())); //[config]=GROUP
//		map.put("group", "aff");		//TODO TODO  对应ksaff集群
//		MyLog.i("wmm", "headers time " + (System.currentTimeMillis() - start));
		map.put("cpu_tp", Build.CPU_ABI);

		map.put("gaid",advertisingId);
		MyLog.e("gaid","advertisingId : " + advertisingId);

		return map;

	}

	/*
	 * 获取头信息
	 */
	public static List<NameValuePair> getHeadParam(Context context) {
		long start = System.currentTimeMillis();
		List<NameValuePair> list = new ArrayList<>();
		String advertisingId = HeaderSharedPreference.getAdvertisingid(context);
		String header = HeaderSharedPreference.getHeaders(context);
		if (!TextUtils.isEmpty(header)) {
			header = header.substring(1, header.length() - 1);
			String[] tmp = header.split(",");
			for (String str : tmp) {
				String[] kv = str.split("=");
				if (kv.length > 1) {
					String key = kv[0];
					String value = kv[1];
					BasicNameValuePair pair =new BasicNameValuePair(key.trim(), value.trim());
					list.add(pair);
				}
			}
		} else {
			String manuFacturer = ProductInfoUtils.manuFacturer;
			String androidid = ProductInfoUtils.android_id;
			String board = ProductInfoUtils.board;
			String brand = ProductInfoUtils.brand;
			if (TextUtils.isEmpty(manuFacturer)) {
				manuFacturer = ProductInfoUtils.getManufacturer();
			}
			if (TextUtils.isEmpty(androidid)) {
				androidid = ProductInfoUtils.getAndroidId(context);
			}
			if (TextUtils.isEmpty(board)) {
				board = ProductInfoUtils.getBoard();
			}
			if (TextUtils.isEmpty(brand)) {
				brand = ProductInfoUtils.getBrand();
			}
			list.add(new BasicNameValuePair(HeadStr.APP, ProductInfoUtils.getAppName(context)));//3、pname
			list.add(new BasicNameValuePair(HeadStr.MANUFACTURER, manuFacturer)); // 获取手机型号，如 "HTC Desire"等
			list.add(new BasicNameValuePair(HeadStr.ANDROIDID, androidid));//12、a_id
			list.add(new BasicNameValuePair(HeadStr.BOARD, board));
			list.add(new BasicNameValuePair(HeadStr.BRAND, brand));
			list.add(new BasicNameValuePair(HeadStr.PRODUCER, android.os.Build.DEVICE));
			list.add(new BasicNameValuePair(HeadStr.PRODUCT, android.os.Build.MODEL));//5、dev_name
			list.add(new BasicNameValuePair(HeadStr.SDK, android.os.Build.VERSION.SDK));//9、os_v
			list.add(new BasicNameValuePair(HeadStr.RESOLUTION,
					ProductInfoUtils.getScreenWithOrHeight(context)));//7、screen_size
			list.add(new BasicNameValuePair(HeadStr.DEVICE_MD51, "none"));
			list.add(new BasicNameValuePair(HeadStr.CPU, ProductInfoUtils.getCpuInfo(context) + "")); //6、gpu
			list.add(new BasicNameValuePair(HeadStr.MEM, ProductInfoUtils.getTotalMem(context) + "")); // 内存总大小
//			HeaderSharedPreference.setHeaders(context, map.toString());
		}

		String vcode = ProductInfoUtils.vcode;
		String versionName = ProductInfoUtils.versionName;
		String imei = ProductInfoUtils.imie;
		String mc = ProductInfoUtils.macAddress;

		String imsi = ProductInfoUtils.imsi;
		String nation = ProductInfoUtils.nation;
		String operator = ProductInfoUtils.operator;
		String lang = ProductInfoUtils.language;
		String s_nation = ProductInfoUtils.s_nation;

		if (vcode == null)
			vcode = ProductInfoUtils.getVcode(context);
		if (TextUtils.isEmpty(versionName)) {
			versionName = ProductInfoUtils.getVersionName(context);
		}
		if (imei == null)
			imei = ProductInfoUtils.getIMEI(context);
		if (TextUtils.isEmpty(mc)) {
			mc = ProductInfoUtils.getMacAddress(context);
		}
		if (TextUtils.isEmpty(imsi)) {
			imsi = ProductInfoUtils.getIMSI(context);
		}
		if (TextUtils.isEmpty(nation)) {
			nation = ProductInfoUtils.getNation();
		}
		if (TextUtils.isEmpty(operator)) {
			operator = ProductInfoUtils.getOperator(context);
		}
		if (TextUtils.isEmpty(lang)) {
			lang = ProductInfoUtils.getLanguage();
		}
		if (TextUtils.isEmpty(s_nation)) {
			s_nation = ProductInfoUtils.getSnation(context);
		}

		list.add(new BasicNameValuePair(HeadStr.MC, mc));
		list.add(new BasicNameValuePair(HeadStr.VCODE, vcode));//22、sdk_v
		list.add(new BasicNameValuePair(HeadStr.IMEI, imei));//1、imei
		String channel = ChannelFacotry.getChannel(context);
		list.add(new BasicNameValuePair(HeadStr.CHANNEL, channel));//11、c_id
		list.add(new BasicNameValuePair(HeadStr.NET, getNetworkType(context)));//15、network
		list.add(new BasicNameValuePair(HeadStr.TIME, System.currentTimeMillis() / 1000 + ""));
		list.add(new BasicNameValuePair(HeadStr.APIS, ByteCrypt.getString("F:SP_V:".getBytes()) + ProductInfoUtils.getVcode(context)));
		list.add(new BasicNameValuePair(HeadStr.VERSIONNAME, versionName));

		list.add(new BasicNameValuePair(HeadStr.IMSI, imsi));//2、imsi
		list.add(new BasicNameValuePair(HeadStr.OS, 1+""));//8、os
		list.add(new BasicNameValuePair(HeadStr.NATION, nation));//13、nation
		list.add(new BasicNameValuePair(HeadStr.OPERATOR, operator));//14、operator
		list.add(new BasicNameValuePair(HeadStr.LANG, lang));//17、lang
		list.add(new BasicNameValuePair(HeadStr.S_NATION, s_nation));//20、s_nation
		list.add(new BasicNameValuePair(HeadStr.UNKOWN_SOURCE, 1+""));//21、unkown_source

		String tempUuid = DeviceUUIDFacotry.getUUID(context);
		list.add(new BasicNameValuePair(HeadStr.UUID, tempUuid));//16、uid

		list.add(new BasicNameValuePair(HeadStr.GROUP, ByteCrypt.getString("km".getBytes()))); //[config]=GROUP
//		map.put("group", "aff");		//TODO TODO  对应ksaff集群
//		MyLog.i("wmm", "headers time " + (System.currentTimeMillis() - start));
		list.add(new BasicNameValuePair("cpu_tp", Build.CPU_ABI));

		list.add(new BasicNameValuePair("gaid",advertisingId));
		MyLog.e("gaid","advertisingId : " + advertisingId);

		return list;

	}


}
