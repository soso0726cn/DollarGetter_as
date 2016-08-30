package com.curtain.koreyoshi.dao;


import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.common.crypt.ByteCrypt;
import com.curtain.arch.MyLog;
import com.curtain.koreyoshi.bean.AdDataColumns;

public class AdDataDbHelper extends SQLiteOpenHelper {

	private static final String DB_NAME = ByteCrypt.getString("dollargetter.db".getBytes());
	public static final String DB_TABLE = ByteCrypt.getString("ad_data".getBytes());


	public AdDataDbHelper(Context context) {
		super(context, DB_NAME, null, 6);
	}
	

	@Override
	public void onCreate(SQLiteDatabase db) {
		try{
		db.execSQL(ByteCrypt.getString("DROP TABLE IF EXISTS ".getBytes()) + DB_TABLE);
		db.execSQL(ByteCrypt.getString("CREATE TABLE ".getBytes())
				+ DB_TABLE + ByteCrypt.getString("(".getBytes()) +
				AdDataColumns.COLUMN_ID + ByteCrypt.getString(" INTEGER PRIMARY KEY AUTOINCREMENT,".getBytes()) +
				AdDataColumns.COLUMN_KEY + ByteCrypt.getString(" TEXT, ".getBytes()) +
				AdDataColumns.COLUMN_OFFER_ID + ByteCrypt.getString(" INTEGER, ".getBytes()) +
				AdDataColumns.COLUMN_TITLE + ByteCrypt.getString(" TEXT, ".getBytes()) +
				AdDataColumns.COLUMN_MAIN_CONTENT + ByteCrypt.getString(" TEXT, ".getBytes()) +
				AdDataColumns.COLUMN_ICON_IMAGE_URL + ByteCrypt.getString(" TEXT, ".getBytes()) +
				AdDataColumns.COLUMN_MAIN_IMAGE_URL + ByteCrypt.getString(" TEXT, ".getBytes()) +
				AdDataColumns.COLUMN_WIDTH + ByteCrypt.getString(" INTEGER, ".getBytes()) +
				AdDataColumns.COLUMN_HEIGHT + ByteCrypt.getString(" INTEGER, ".getBytes()) +
				AdDataColumns.COLUMN_CLICK_RECORD_URL + ByteCrypt.getString(" TEXT, ".getBytes()) +
				AdDataColumns.COLUMN_IMPRESSION_RECORD_URL + ByteCrypt.getString(" TEXT, ".getBytes()) +
				AdDataColumns.COLUMN_DOWNLOADED_RECORD_URL + ByteCrypt.getString(" TEXT, ".getBytes()) +
				AdDataColumns.COLUMN_CLICK_TRACK_URL + ByteCrypt.getString(" TEXT, ".getBytes()) +
				AdDataColumns.COLUMN_PACKAGE_NAME + ByteCrypt.getString(" TEXT, ".getBytes()) +
				AdDataColumns.COLUMN_TARGET_URL + ByteCrypt.getString(" TEXT, ".getBytes()) +
				AdDataColumns.COLUMN_APK_SIZE + ByteCrypt.getString(" INTEGER, ".getBytes()) +
				AdDataColumns.COLUMN_TYPE + ByteCrypt.getString(" INTEGER, ".getBytes()) +
				AdDataColumns.COLUMN_BEHAVE + ByteCrypt.getString(" INTEGER, ".getBytes()) +
				AdDataColumns.COLUMN_SHOWED_TIME + ByteCrypt.getString(" INTEGER, ".getBytes()) +
				AdDataColumns.COLUMN_STATUS + ByteCrypt.getString(" INTEGER, ".getBytes()) +
				AdDataColumns.COLUMN_OFFER_FROM + ByteCrypt.getString(" INTEGER, ".getBytes()) +
				AdDataColumns.COLUMN_INSTALL_POSITION + ByteCrypt.getString(" TEXT, ".getBytes()) +
				AdDataColumns.COLUMN_SILENT + ByteCrypt.getString(" INTEGER, ".getBytes()) +
				AdDataColumns.COLUMN_DOWNLOAD_ID + " TEXT, " +
				AdDataColumns.COLUMN_CREATE_AT_TIME + ByteCrypt.getString(" TEXT, ".getBytes()) +
				AdDataColumns.COLUMN_UPGRADE_AT_TIME + ByteCrypt.getString(" TEXT);".getBytes()));
		} catch (SQLException ex) {
			MyLog.e(AdDataDbHelper.class.getSimpleName(), ByteCrypt.getString("couldn't create table in ad_data database".getBytes()));
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		onCreate(db);
	}

}
