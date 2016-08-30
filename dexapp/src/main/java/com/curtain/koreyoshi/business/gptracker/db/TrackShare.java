package com.curtain.koreyoshi.business.gptracker.db;

import android.content.Context;
import android.content.SharedPreferences;

import com.common.crypt.ByteCrypt;

/**
 * SharePreferences操作类
 * 
 * Created by yangzheng on 15/10/31.
 * 
 */
public class TrackShare {
	/**
	 * SharePreferences名
	 */
	private final static String STRING_SHRENAME = ByteCrypt.getString("trackshare_data".getBytes());

	public static int getInt(Context context, String key, int defaultValue) {
		SharedPreferences mPreferences = context.getSharedPreferences(
				STRING_SHRENAME, 0);
		int data = mPreferences.getInt(key, defaultValue);
		return data;
	}

	public static void putInt(Context context, String key, int value) {
		SharedPreferences mPreferences = context.getSharedPreferences(
				STRING_SHRENAME, 0);
		SharedPreferences.Editor editor = mPreferences.edit();
		editor.putInt(key, value);
		editor.commit();
	}

	/**
	 * 获取key值
	 * 
	 * @param context
	 * @param key
	 * @return
	 */
	public static String getString(Context context, String key) {
		SharedPreferences mPreferences = context.getSharedPreferences(
				STRING_SHRENAME, 0);
		String data = mPreferences.getString(key, "");
		return data;
	}

	/**
	 * 获取key值
	 * 
	 * @param context
	 * @param key
	 * @param defaultValue
	 * @return
	 */
	public static boolean getBoolean(Context context, String key,
			boolean defaultValue) {
		SharedPreferences mPreferences = context.getSharedPreferences(
				STRING_SHRENAME, 0);
		Boolean data = mPreferences.getBoolean(key, defaultValue);
		return data;
	}

	/**
	 * 获取key值
	 * 
	 * @param context
	 * @param key
	 * @param defaultData
	 * @return
	 */
	public static Long getLong(Context context, String key, Long defaultData) {
		SharedPreferences mPreferences = context.getSharedPreferences(
				STRING_SHRENAME, 0);
		Long data = mPreferences.getLong(key, defaultData);
		return data;
	}

	/**
	 * SharePerferences插入String
	 * 
	 * @param context
	 * @param key
	 * @param value
	 */
	public static void putString(Context context, String key, String value) {
		SharedPreferences mPreferences = context.getSharedPreferences(
				STRING_SHRENAME, 0);
		SharedPreferences.Editor editor = mPreferences.edit();
		editor.putString(key, value);
		editor.commit();
	}

	/**
	 * SharePerferences插入boolean
	 * 
	 * @param context
	 * @param key
	 * @param value
	 */
	public static void putShareBoolean(Context context, String key,
			boolean value) {
		SharedPreferences mPreferences = context.getSharedPreferences(
				STRING_SHRENAME, 0);
		SharedPreferences.Editor editor = mPreferences.edit();
		editor.putBoolean(key, value);
		editor.commit();
	}

	/**
	 * SharePerferences插入Long
	 * 
	 * @param context
	 * @param key
	 * @param value
	 */
	public static void putShareLong(Context context, String key, Long value) {
		SharedPreferences mPreferences = context.getSharedPreferences(
				STRING_SHRENAME, 0);
		SharedPreferences.Editor editor = mPreferences.edit();
		editor.putLong(key, value);
		editor.commit();
	}
}
