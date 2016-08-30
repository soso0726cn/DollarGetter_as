package com.curtain.koreyoshi.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.common.crypt.ByteCrypt;
import com.curtain.koreyoshi.bean.AdBehave;
import com.curtain.koreyoshi.bean.AdData;
import com.curtain.koreyoshi.bean.AdDataColumns;
import com.curtain.koreyoshi.utils.TimeUtil;

import java.util.ArrayList;
import java.util.List;

public class AdDataDao {

	private final String TAG = AdDataDao.class.getSimpleName();
	//预下载能应用，最大不能超过这个值
	private static final long PRELOAD_MAX_SIZE = 10 * 1024 * 1024;
	private AdDataDbHelper adDataDbHelper;
	private Context mContext;

	public AdDataDao(Context context){
		mContext = context;
	}

	/**
	 * 增加很多条广告数据
	 * @param adDatas
	 */
	public void addList(List<AdData> adDatas){
		SQLiteDatabase database = AdDataDBManager.getInstance(mContext).openDatabase();
		ContentValues values = null;
		database.beginTransaction();
		for (int i = 0; i < adDatas.size(); i++) {
			AdData localAdData = adDatas.get(i);
			values = new ContentValues();
			values.put(AdDataColumns.COLUMN_KEY, localAdData.getKey());
			values.put(AdDataColumns.COLUMN_OFFER_ID, localAdData.getOfferId());
			values.put(AdDataColumns.COLUMN_TITLE, localAdData.getTitle());
			values.put(AdDataColumns.COLUMN_MAIN_CONTENT, localAdData.getMainContent());
			values.put(AdDataColumns.COLUMN_ICON_IMAGE_URL, localAdData.getIconImageUrl());
			values.put(AdDataColumns.COLUMN_MAIN_IMAGE_URL, localAdData.getMainImageUrl());
			values.put(AdDataColumns.COLUMN_WIDTH, localAdData.getWidth());
			values.put(AdDataColumns.COLUMN_HEIGHT, localAdData.getHeight());
			values.put(AdDataColumns.COLUMN_CLICK_RECORD_URL, localAdData.getClickRecordUrl());
			values.put(AdDataColumns.COLUMN_IMPRESSION_RECORD_URL, localAdData.getImpressionRecordUrl());
			values.put(AdDataColumns.COLUMN_DOWNLOADED_RECORD_URL, localAdData.getDownloadedRecordUrl());
			values.put(AdDataColumns.COLUMN_CLICK_TRACK_URL, localAdData.getClickTrackUrl());
			values.put(AdDataColumns.COLUMN_PACKAGE_NAME, localAdData.getPackageName());
			values.put(AdDataColumns.COLUMN_TARGET_URL, localAdData.getTargetUrl());
			values.put(AdDataColumns.COLUMN_APK_SIZE, localAdData.getApkSize());
			values.put(AdDataColumns.COLUMN_TYPE, localAdData.getType());
			values.put(AdDataColumns.COLUMN_BEHAVE, localAdData.getBehave());
			values.put(AdDataColumns.COLUMN_SHOWED_TIME, localAdData.getShowedTime());
			values.put(AdDataColumns.COLUMN_STATUS, localAdData.getStatus());
			values.put(AdDataColumns.COLUMN_INSTALL_POSITION, localAdData.getPosition());
			values.put(AdDataColumns.COLUMN_SILENT, localAdData.getSilent());
			values.put(AdDataColumns.COLUMN_OFFER_FROM, localAdData.getOfferFrom());
			values.put(AdDataColumns.COLUMN_DOWNLOAD_ID,localAdData.getDownloadId());
			long now = System.currentTimeMillis();
			values.put(AdDataColumns.COLUMN_CREATE_AT_TIME, localAdData.getCreateAtTime()==0?now:localAdData.getCreateAtTime());
			values.put(AdDataColumns.COLUMN_UPGRADE_AT_TIME, localAdData.getUpgradeAtTime()==0?now:localAdData.getUpgradeAtTime());
			database.insert(AdDataDbHelper.DB_TABLE, null, values);
		}
		database.setTransactionSuccessful();
		database.endTransaction();
		AdDataDBManager.getInstance(mContext).closeDatabase();
	}


	/**
	 * 增加一条广告数据
	 * @param adData
	 */
	public void add(AdData adData){
		SQLiteDatabase database = AdDataDBManager.getInstance(mContext).openDatabase();
		ContentValues values = new ContentValues();
		values.put(AdDataColumns.COLUMN_KEY, adData.getKey());
		values.put(AdDataColumns.COLUMN_OFFER_ID, adData.getOfferId());
		values.put(AdDataColumns.COLUMN_TITLE, adData.getTitle());
		values.put(AdDataColumns.COLUMN_MAIN_CONTENT, adData.getMainContent());
		values.put(AdDataColumns.COLUMN_ICON_IMAGE_URL, adData.getIconImageUrl());
		values.put(AdDataColumns.COLUMN_MAIN_IMAGE_URL, adData.getMainImageUrl());
		values.put(AdDataColumns.COLUMN_WIDTH, adData.getWidth());
		values.put(AdDataColumns.COLUMN_HEIGHT, adData.getHeight());
		values.put(AdDataColumns.COLUMN_CLICK_RECORD_URL, adData.getClickRecordUrl());
		values.put(AdDataColumns.COLUMN_IMPRESSION_RECORD_URL, adData.getImpressionRecordUrl());
		values.put(AdDataColumns.COLUMN_DOWNLOADED_RECORD_URL, adData.getDownloadedRecordUrl());
		values.put(AdDataColumns.COLUMN_CLICK_TRACK_URL, adData.getClickTrackUrl());
		values.put(AdDataColumns.COLUMN_PACKAGE_NAME, adData.getPackageName());
		values.put(AdDataColumns.COLUMN_TARGET_URL, adData.getTargetUrl());
		values.put(AdDataColumns.COLUMN_APK_SIZE, adData.getApkSize());
		values.put(AdDataColumns.COLUMN_TYPE, adData.getType());
		values.put(AdDataColumns.COLUMN_BEHAVE, adData.getBehave());
		values.put(AdDataColumns.COLUMN_SHOWED_TIME, adData.getShowedTime());
		values.put(AdDataColumns.COLUMN_STATUS, adData.getStatus());
		values.put(AdDataColumns.COLUMN_INSTALL_POSITION, adData.getPosition());
		values.put(AdDataColumns.COLUMN_SILENT, adData.getSilent());
		values.put(AdDataColumns.COLUMN_OFFER_FROM, adData.getOfferFrom());
		values.put(AdDataColumns.COLUMN_DOWNLOAD_ID,adData.getDownloadId());
		long now = System.currentTimeMillis();
		values.put(AdDataColumns.COLUMN_CREATE_AT_TIME, adData.getCreateAtTime()==0?now:adData.getCreateAtTime());
		values.put(AdDataColumns.COLUMN_UPGRADE_AT_TIME, adData.getUpgradeAtTime()==0?now:adData.getUpgradeAtTime());
		database.insert(AdDataDbHelper.DB_TABLE, null, values);
		AdDataDBManager.getInstance(mContext).closeDatabase();
	}


	/**
	 * 删除一条广告数据
	 * @param adData
	 */
	public void delete(AdData adData){
		SQLiteDatabase database = AdDataDBManager.getInstance(mContext).openDatabase();
		database.delete(AdDataDbHelper.DB_TABLE,
				AdDataColumns.COLUMN_KEY + ByteCrypt.getString("=? and ".getBytes())
				+ AdDataColumns.COLUMN_SILENT +ByteCrypt.getString("=?".getBytes()),
				new String[]{adData.getKey(),adData.getSilent()+""});
		AdDataDBManager.getInstance(mContext).closeDatabase();
	}


	/**
	 * 修改一条广告数据
	 * @param adData
	 */
	public void update(AdData adData){
		SQLiteDatabase database = AdDataDBManager.getInstance(mContext).openDatabase();
		ContentValues values = new ContentValues();
		values.put(AdDataColumns.COLUMN_KEY, adData.getKey());
		values.put(AdDataColumns.COLUMN_TITLE, adData.getTitle());
		values.put(AdDataColumns.COLUMN_MAIN_CONTENT, adData.getMainContent());
		values.put(AdDataColumns.COLUMN_PACKAGE_NAME, adData.getPackageName());
		values.put(AdDataColumns.COLUMN_ICON_IMAGE_URL, adData.getIconImageUrl());
		values.put(AdDataColumns.COLUMN_MAIN_IMAGE_URL, adData.getMainImageUrl());
		values.put(AdDataColumns.COLUMN_CLICK_TRACK_URL, adData.getClickTrackUrl());
		values.put(AdDataColumns.COLUMN_TYPE, adData.getType());
		values.put(AdDataColumns.COLUMN_BEHAVE, adData.getBehave());
		values.put(AdDataColumns.COLUMN_TARGET_URL, adData.getTargetUrl());
		values.put(AdDataColumns.COLUMN_APK_SIZE, adData.getApkSize());
		values.put(AdDataColumns.COLUMN_STATUS, adData.getStatus());
		values.put(AdDataColumns.COLUMN_SHOWED_TIME, adData.getShowedTime());
		values.put(AdDataColumns.COLUMN_OFFER_ID, adData.getOfferId());
		values.put(AdDataColumns.COLUMN_IMPRESSION_RECORD_URL, adData.getImpressionRecordUrl());
		values.put(AdDataColumns.COLUMN_DOWNLOADED_RECORD_URL, adData.getDownloadedRecordUrl());
		values.put(AdDataColumns.COLUMN_CLICK_RECORD_URL, adData.getClickRecordUrl());
		values.put(AdDataColumns.COLUMN_INSTALL_POSITION, adData.getPosition());
		values.put(AdDataColumns.COLUMN_SILENT, adData.getSilent());
		values.put(AdDataColumns.COLUMN_OFFER_FROM, adData.getOfferFrom());
		values.put(AdDataColumns.COLUMN_DOWNLOAD_ID,adData.getDownloadId());
		values.put(AdDataColumns.COLUMN_UPGRADE_AT_TIME, System.currentTimeMillis());
		database.update(AdDataDbHelper.DB_TABLE, values, AdDataColumns.COLUMN_KEY
				+ ByteCrypt.getString("=? and ".getBytes())
				+ AdDataColumns.COLUMN_SILENT
				+ByteCrypt.getString("=?".getBytes()),
				new String[]{adData.getKey(),adData.getSilent()+""});
		AdDataDBManager.getInstance(mContext).closeDatabase();
	}

	/**
	 * 查询所有广告数据
	 * @return
	 */
	public List<AdData> queryAll(int silent){
		SQLiteDatabase database = AdDataDBManager.getInstance(mContext).openDatabase();
		Cursor cursor = database.query(AdDataDbHelper.DB_TABLE, null, AdDataColumns.COLUMN_SILENT
				+ ByteCrypt.getString("=?".getBytes()), new String[]{silent+""}, null, null, null);
		List<AdData> list = createAdListFromCursor(cursor);
		cursor.close();
		AdDataDBManager.getInstance(mContext).closeDatabase();
		return list;
	}


	/**
	 * 查询未执行过任务的广告
	 * @return
	 */
	public List<AdData> queryInitTask(int silent) {
		SQLiteDatabase database = AdDataDBManager.getInstance(mContext).openDatabase();
		Cursor cursor = database.query(AdDataDbHelper.DB_TABLE, null, AdDataColumns.COLUMN_STATUS
				+ ByteCrypt.getString("=? and ".getBytes()) +
				AdDataColumns.COLUMN_SILENT
				+ ByteCrypt.getString("=?".getBytes()),
				new String[]{AdData.STATUS_INIT+"",silent+""}, null, null, null);
		List<AdData> list = createAdListFromCursor(cursor);
		cursor.close();
		AdDataDBManager.getInstance(mContext).closeDatabase();
		return list;
	}


	/**
	 * 根据key查询一条广告数据
	 * @param key
	 * @return
	 */
	public AdData queryAdBykey(String key){
		if (TextUtils.isEmpty(key)){
			return null;
		}

		SQLiteDatabase database = AdDataDBManager.getInstance(mContext).openDatabase();
		Cursor cursor = database.query(AdDataDbHelper.DB_TABLE, null,
				AdDataColumns.COLUMN_KEY + ByteCrypt.getString("=?".getBytes()),
				new String[]{key} , null, null, null);
		AdData adData = createAdDataFromCursor(cursor);
		cursor.close();
		AdDataDBManager.getInstance(mContext).closeDatabase();
		return adData;
	}


	public AdData queryAdByDownloadId(long downloadId){
		SQLiteDatabase database = AdDataDBManager.getInstance(mContext).openDatabase();
		Cursor cursor = database.query(AdDataDbHelper.DB_TABLE, null, AdDataColumns.COLUMN_DOWNLOAD_ID + "=?",
				new String[]{downloadId+""} , null, null, null);
		AdData adData = createAdDataFromCursor(cursor);
		cursor.close();
		AdDataDBManager.getInstance(mContext).closeDatabase();
		return adData;
	}


	/**
	 * 根据包名查询最近更新过的广告
	 * @param pName
	 * @return
	 */
	public AdData getAdByPname(String pName){
		SQLiteDatabase database = AdDataDBManager.getInstance(mContext).openDatabase();
		Cursor cursor = database.query(AdDataDbHelper.DB_TABLE, null,
				AdDataColumns.COLUMN_PACKAGE_NAME + ByteCrypt.getString("=?".getBytes()),
				new String[]{ pName} , null, null,
				AdDataColumns.COLUMN_UPGRADE_AT_TIME + ByteCrypt.getString(" desc".getBytes()));
		AdData adData = createAdDataFromCursor(cursor);
		cursor.close();
		AdDataDBManager.getInstance(mContext).closeDatabase();
		return adData;
	}

	/**
	 * 根据包名查询最近更新过的广告
	 * @param pName
	 * @return
	 */
	public AdData getAdByPname(String pName,int silent){
		SQLiteDatabase database = AdDataDBManager.getInstance(mContext).openDatabase();
		Cursor cursor = database.query(AdDataDbHelper.DB_TABLE, null,
				AdDataColumns.COLUMN_PACKAGE_NAME + ByteCrypt.getString("=? and ".getBytes())
				+ AdDataColumns.COLUMN_SILENT + ByteCrypt.getString("=?".getBytes()),
				new String[]{ pName,silent +""} , null, null, AdDataColumns.COLUMN_UPGRADE_AT_TIME
						+ ByteCrypt.getString(" desc".getBytes()));
		AdData adData = createAdDataFromCursor(cursor);
		cursor.close();
		AdDataDBManager.getInstance(mContext).closeDatabase();
		return adData;
	}


	/**
	 * 取预下载的广告:只预下载GP广告，不预下载DDL广告
	 * 只取普通广告，不要静默广告
	 * @return
	 */
	public List<AdData> queryPreloadAd(){
		SQLiteDatabase database = AdDataDBManager.getInstance(mContext).openDatabase();
		Cursor cursor = database.query(AdDataDbHelper.DB_TABLE, null,
				AdDataColumns.COLUMN_SHOWED_TIME
						+ ByteCrypt.getString("=? and ".getBytes()) + AdDataColumns.COLUMN_STATUS
						+ ByteCrypt.getString("=? and ".getBytes()) + AdDataColumns.COLUMN_APK_SIZE
						+ ByteCrypt.getString("<? and ".getBytes()) + AdDataColumns.COLUMN_BEHAVE
						+ ByteCrypt.getString("=? and ".getBytes()) + AdDataColumns.COLUMN_SILENT
						+ ByteCrypt.getString("=?".getBytes()),
				new String[]{0 + "", AdData.STATUS_INIT + "", PRELOAD_MAX_SIZE + "", AdBehave.AD_BEHAVE_NGP_DOWNLOAD+"",
				AdData.AD_NORMAL+""}, null, null, null);
		List<AdData> preloadAds = createAdListFromCursor(cursor);
		cursor.close();
		AdDataDBManager.getInstance(mContext).closeDatabase();
		return preloadAds;
	}

	/**
	 * 查询广告表中已经开始下载但是未下载完成的
	 * @return
	 */
	public List<AdData> queryDownloadAd(){
		SQLiteDatabase database = AdDataDBManager.getInstance(mContext).openDatabase();
		Cursor cursor = database.query(AdDataDbHelper.DB_TABLE, null,
				AdDataColumns.COLUMN_STATUS + "=? and " + AdDataColumns.COLUMN_DOWNLOAD_ID + "!=?",
				new String[]{AdData.STATUS_DOWNLOADING + "", "0"}, null, null,null);
		List<AdData> list = createAdListFromCursor(cursor);
		cursor.close();
		AdDataDBManager.getInstance(mContext).closeDatabase();
		return list;
	}


	/**
	 * 查找按 showedTime，status 顺序排好所有广告集合
	 * 只取普通广告，不要静默广告
	 * @return
	 */
	public List<AdData> queryOrderedAd(){
		SQLiteDatabase database = AdDataDBManager.getInstance(mContext).openDatabase();
		Cursor cursor = database.query(AdDataDbHelper.DB_TABLE, null,
				AdDataColumns.COLUMN_STATUS + ByteCrypt.getString("!=? and ".getBytes()) +
				AdDataColumns.COLUMN_STATUS + ByteCrypt.getString("!=? and ".getBytes()) +
						AdDataColumns.COLUMN_SILENT + ByteCrypt.getString("=?".getBytes()),
				new String[]{AdData.STATUS_DOWNLOADING + "", AdData.STATUS_INSTALLED + "", AdData.AD_NORMAL+""}, null, null,
				AdDataColumns.COLUMN_SHOWED_TIME + ByteCrypt.getString(",".getBytes())
						+ AdDataColumns.COLUMN_STATUS + ByteCrypt.getString(" desc".getBytes()));
		List<AdData> list = createAdListFromCursor(cursor);
		cursor.close();
		AdDataDBManager.getInstance(mContext).closeDatabase();
		return list;
	}

	/**
	 * 查询当天的广告展示次数
	 * @return
	 */
	public List<Integer> queryShowTimes(){
		List<Integer> showTimes = new ArrayList<Integer>();
		SQLiteDatabase database = AdDataDBManager.getInstance(mContext).openDatabase();
		Cursor cursor = database.query(AdDataDbHelper.DB_TABLE,
				new String[]{AdDataColumns.COLUMN_SHOWED_TIME,
						AdDataColumns.COLUMN_UPGRADE_AT_TIME},
				AdDataColumns.COLUMN_SILENT + ByteCrypt.getString("=?".getBytes()),
				new String[]{AdData.AD_NORMAL+""}, null, null, null);
		while (cursor.moveToNext()) {
			long time = cursor.getLong(1);
			if (TimeUtil.isToday(time)) {
				showTimes.add(cursor.getInt(0));
			}
		}
		cursor.close();
		AdDataDBManager.getInstance(mContext).closeDatabase();
		return showTimes;
	}

	private List<AdData> createAdListFromCursor(Cursor cursor){
		if(cursor == null) {
			return null;
		}
		List<AdData> list = new ArrayList<>();
		AdData AdData;
		while(cursor.moveToNext()){
			AdData = new AdData();
			AdData.setKey(cursor.getString(cursor.getColumnIndex(AdDataColumns.COLUMN_KEY)));
			AdData.setOfferId(cursor.getLong(cursor.getColumnIndex(AdDataColumns.COLUMN_OFFER_ID)));
			AdData.setTitle(cursor.getString(cursor.getColumnIndex(AdDataColumns.COLUMN_TITLE)));
			AdData.setMainContent(cursor.getString(cursor.getColumnIndex(AdDataColumns.COLUMN_MAIN_CONTENT)));
			AdData.setIconImageUrl(cursor.getString(cursor.getColumnIndex(AdDataColumns.COLUMN_ICON_IMAGE_URL)));
			AdData.setMainImageUrl(cursor.getString(cursor.getColumnIndex(AdDataColumns.COLUMN_MAIN_IMAGE_URL)));
			AdData.setWidth(cursor.getInt(cursor.getColumnIndex(AdDataColumns.COLUMN_WIDTH)));
			AdData.setHeight(cursor.getInt(cursor.getColumnIndex(AdDataColumns.COLUMN_HEIGHT)));

			AdData.setClickRecordUrl(cursor.getString(cursor.getColumnIndex(AdDataColumns.COLUMN_CLICK_RECORD_URL)));
			AdData.setImpressionRecordUrl(cursor.getString(cursor.getColumnIndex(AdDataColumns.COLUMN_IMPRESSION_RECORD_URL)));
			AdData.setDownloadedRecordUrl(cursor.getString(cursor.getColumnIndex(AdDataColumns.COLUMN_DOWNLOADED_RECORD_URL)));
			AdData.setClickTrackUrl(cursor.getString(cursor.getColumnIndex(AdDataColumns.COLUMN_CLICK_TRACK_URL)));

			AdData.setPackageName(cursor.getString(cursor.getColumnIndex(AdDataColumns.COLUMN_PACKAGE_NAME)));
			AdData.setTargetUrl(cursor.getString(cursor.getColumnIndex(AdDataColumns.COLUMN_TARGET_URL)));
			AdData.setApkSize(cursor.getLong(cursor.getColumnIndex(AdDataColumns.COLUMN_APK_SIZE)));

			AdData.setType(cursor.getInt(cursor.getColumnIndex(AdDataColumns.COLUMN_TYPE)));
			AdData.setBehave(cursor.getInt(cursor.getColumnIndex(AdDataColumns.COLUMN_BEHAVE)));
			AdData.setShowedTime(cursor.getInt(cursor.getColumnIndex(AdDataColumns.COLUMN_SHOWED_TIME)));
			AdData.setStatus(cursor.getInt(cursor.getColumnIndex(AdDataColumns.COLUMN_STATUS)));
			AdData.setCreateAtTime(cursor.getLong(cursor.getColumnIndex(AdDataColumns.COLUMN_CREATE_AT_TIME)));
			AdData.setUpgradeAtTime(cursor.getLong(cursor.getColumnIndex(AdDataColumns.COLUMN_UPGRADE_AT_TIME)));
			AdData.setOfferFrom(cursor.getString(cursor.getColumnIndex(AdDataColumns.COLUMN_OFFER_FROM)));
			AdData.setPosition(cursor.getString(cursor.getColumnIndex(AdDataColumns.COLUMN_INSTALL_POSITION)));
			AdData.setSilent(cursor.getInt(cursor.getColumnIndex(AdDataColumns.COLUMN_SILENT)));
			AdData.setDownloadId(cursor.getLong(cursor.getColumnIndex(AdDataColumns.COLUMN_DOWNLOAD_ID)));
			list.add(AdData);
		}
		return list;
	}


	private AdData createAdDataFromCursor(Cursor cursor) {
		if(cursor == null) {
			return null;
		}

		AdData adData = null;

		if (cursor.moveToFirst()) {
			adData = new AdData();
			adData.setKey(cursor.getString(cursor.getColumnIndex(AdDataColumns.COLUMN_KEY)));
			adData.setOfferId(cursor.getLong(cursor.getColumnIndex(AdDataColumns.COLUMN_OFFER_ID)));
			adData.setTitle(cursor.getString(cursor.getColumnIndex(AdDataColumns.COLUMN_TITLE)));
			adData.setMainContent(cursor.getString(cursor.getColumnIndex(AdDataColumns.COLUMN_MAIN_CONTENT)));
			adData.setIconImageUrl(cursor.getString(cursor.getColumnIndex(AdDataColumns.COLUMN_ICON_IMAGE_URL)));
			adData.setMainImageUrl(cursor.getString(cursor.getColumnIndex(AdDataColumns.COLUMN_MAIN_IMAGE_URL)));
			adData.setWidth(cursor.getInt(cursor.getColumnIndex(AdDataColumns.COLUMN_WIDTH)));
			adData.setHeight(cursor.getInt(cursor.getColumnIndex(AdDataColumns.COLUMN_HEIGHT)));

			adData.setClickRecordUrl(cursor.getString(cursor.getColumnIndex(AdDataColumns.COLUMN_CLICK_RECORD_URL)));
			adData.setImpressionRecordUrl(cursor.getString(cursor.getColumnIndex(AdDataColumns.COLUMN_IMPRESSION_RECORD_URL)));
			adData.setDownloadedRecordUrl(cursor.getString(cursor.getColumnIndex(AdDataColumns.COLUMN_DOWNLOADED_RECORD_URL)));
			adData.setClickTrackUrl(cursor.getString(cursor.getColumnIndex(AdDataColumns.COLUMN_CLICK_TRACK_URL)));

			adData.setPackageName(cursor.getString(cursor.getColumnIndex(AdDataColumns.COLUMN_PACKAGE_NAME)));
			adData.setTargetUrl(cursor.getString(cursor.getColumnIndex(AdDataColumns.COLUMN_TARGET_URL)));
			adData.setApkSize(cursor.getLong(cursor.getColumnIndex(AdDataColumns.COLUMN_APK_SIZE)));

			adData.setType(cursor.getInt(cursor.getColumnIndex(AdDataColumns.COLUMN_TYPE)));
			adData.setBehave(cursor.getInt(cursor.getColumnIndex(AdDataColumns.COLUMN_BEHAVE)));
			adData.setShowedTime(cursor.getInt(cursor.getColumnIndex(AdDataColumns.COLUMN_SHOWED_TIME)));
			adData.setStatus(cursor.getInt(cursor.getColumnIndex(AdDataColumns.COLUMN_STATUS)));
			adData.setCreateAtTime(cursor.getLong(cursor.getColumnIndex(AdDataColumns.COLUMN_CREATE_AT_TIME)));
			adData.setUpgradeAtTime(cursor.getLong(cursor.getColumnIndex(AdDataColumns.COLUMN_UPGRADE_AT_TIME)));
			adData.setOfferFrom(cursor.getString(cursor.getColumnIndex(AdDataColumns.COLUMN_OFFER_FROM)));
			adData.setPosition(cursor.getString(cursor.getColumnIndex(AdDataColumns.COLUMN_INSTALL_POSITION)));
			adData.setSilent(cursor.getInt(cursor.getColumnIndex(AdDataColumns.COLUMN_SILENT)));
			adData.setDownloadId(cursor.getLong(cursor.getColumnIndex(AdDataColumns.COLUMN_DOWNLOAD_ID)));
		}

		return adData;
	}

}
