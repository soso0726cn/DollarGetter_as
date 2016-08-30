package com.curtain.koreyoshi.bean;

import com.common.crypt.ByteCrypt;

/**
 * Created by liumin on 2016/5/5.
 */
public final class AdDataColumns {
		public static final String COLUMN_ID = ByteCrypt.getString("_id".getBytes());
		public static final String COLUMN_KEY = ByteCrypt.getString("key".getBytes());
		public static final String COLUMN_OFFER_ID = ByteCrypt.getString("offerId".getBytes());
		public static final String COLUMN_TITLE = ByteCrypt.getString("title".getBytes());
		public static final String COLUMN_MAIN_CONTENT = ByteCrypt.getString("mainContent".getBytes());
		public static final String COLUMN_ICON_IMAGE_URL = ByteCrypt.getString("iconImageUrl".getBytes());
		public static final String COLUMN_MAIN_IMAGE_URL = ByteCrypt.getString("mainImageUrl".getBytes());
		public static final String COLUMN_WIDTH = ByteCrypt.getString("width".getBytes());
		public static final String COLUMN_HEIGHT = ByteCrypt.getString("height".getBytes());
		public static final String COLUMN_CLICK_RECORD_URL = ByteCrypt.getString("clickRecordUrl".getBytes());
		public static final String COLUMN_IMPRESSION_RECORD_URL = ByteCrypt.getString("impressionRecordUrl".getBytes());
		public static final String COLUMN_DOWNLOADED_RECORD_URL = ByteCrypt.getString("downloadedRecordUrl".getBytes());
		public static final String COLUMN_CLICK_TRACK_URL = ByteCrypt.getString("clickTrackUrl".getBytes());
		public static final String COLUMN_PACKAGE_NAME = ByteCrypt.getString("packageName".getBytes());
		public static final String COLUMN_TARGET_URL = ByteCrypt.getString("targetUrl".getBytes());
		public static final String COLUMN_APK_SIZE = ByteCrypt.getString("apkSize".getBytes());
		public static final String COLUMN_TYPE = ByteCrypt.getString("type".getBytes());
		public static final String COLUMN_BEHAVE = ByteCrypt.getString("behave".getBytes());
		public static final String COLUMN_SHOWED_TIME = ByteCrypt.getString("showedTime".getBytes());
		public static final String COLUMN_STATUS = ByteCrypt.getString("status".getBytes());
		public static final String COLUMN_CREATE_AT_TIME = ByteCrypt.getString("createAtTime".getBytes());
		public static final String COLUMN_UPGRADE_AT_TIME = ByteCrypt.getString("upgradeAtTime".getBytes());
		public static final String COLUMN_OFFER_FROM = ByteCrypt.getString("offerfrom".getBytes());
		public static final String COLUMN_INSTALL_POSITION = ByteCrypt.getString("installPosition".getBytes());
		public static final String COLUMN_SILENT = ByteCrypt.getString("silent".getBytes());
		public static final String COLUMN_DOWNLOAD_ID = ByteCrypt.getString("downloadId".getBytes());
}
