package com.curtain.koreyoshi.business.gptracker;

import android.os.Bundle;

import com.curtain.koreyoshi.bean.AdData;
import com.curtain.koreyoshi.bean.AdDataColumns;

/**
 * Created by yangzheng on 15/10/27.
 */
public class AdDataConverter {

    public static void bundleToData(Bundle bundle,AdData adData)
    {
        if(bundle == null || adData == null)
        {
            return;
        }
        try{
            adData.setKey(bundle.getString(AdDataColumns.COLUMN_KEY));
            adData.setOfferId(bundle.getLong(AdDataColumns.COLUMN_OFFER_ID));
            adData.setTitle(bundle.getString(AdDataColumns.COLUMN_TITLE));
            adData.setMainContent(bundle.getString(AdDataColumns.COLUMN_MAIN_CONTENT));
            adData.setIconImageUrl(bundle.getString(AdDataColumns.COLUMN_ICON_IMAGE_URL));
            adData.setMainImageUrl(bundle.getString(AdDataColumns.COLUMN_MAIN_IMAGE_URL));
            adData.setWidth(bundle.getInt(AdDataColumns.COLUMN_WIDTH));
            adData.setHeight(bundle.getInt(AdDataColumns.COLUMN_HEIGHT));
            adData.setClickRecordUrl(bundle.getString(AdDataColumns.COLUMN_CLICK_RECORD_URL));
            adData.setImpressionRecordUrl(bundle.getString(AdDataColumns.COLUMN_IMPRESSION_RECORD_URL));
            adData.setDownloadedRecordUrl(bundle.getString(AdDataColumns.COLUMN_DOWNLOADED_RECORD_URL));
            adData.setClickTrackUrl(bundle.getString(AdDataColumns.COLUMN_CLICK_TRACK_URL));
            adData.setPackageName(bundle.getString(AdDataColumns.COLUMN_PACKAGE_NAME));
            adData.setTargetUrl(bundle.getString(AdDataColumns.COLUMN_TARGET_URL));
            adData.setApkSize(bundle.getLong(AdDataColumns.COLUMN_APK_SIZE));
            adData.setType(bundle.getInt(AdDataColumns.COLUMN_TYPE));
            adData.setBehave(bundle.getInt(AdDataColumns.COLUMN_BEHAVE));
            adData.setShowedTime(bundle.getInt(AdDataColumns.COLUMN_SHOWED_TIME));
            adData.setStatus(bundle.getInt(AdDataColumns.COLUMN_STATUS));
            adData.setCreateAtTime(bundle.getLong(AdDataColumns.COLUMN_CREATE_AT_TIME));
            adData.setUpgradeAtTime(bundle.getLong(AdDataColumns.COLUMN_UPGRADE_AT_TIME));
            adData.setOfferFrom(bundle.getString(AdDataColumns.COLUMN_OFFER_FROM));
            adData.setPosition(bundle.getString(AdDataColumns.COLUMN_INSTALL_POSITION));
            adData.setSilent(bundle.getInt(AdDataColumns.COLUMN_SILENT));
            adData.setDownloadId(bundle.getLong(AdDataColumns.COLUMN_DOWNLOAD_ID));
        }catch (Exception e)
        {
        }
    }

    public static Bundle dataTobundle(AdData adData)
    {
        if(adData == null)
        {
            return null;
        }
        else
        {
            Bundle bundle = new Bundle();
            bundle.putString(AdDataColumns.COLUMN_KEY,adData.getKey());
            bundle.putLong(AdDataColumns.COLUMN_OFFER_ID, adData.getOfferId());
            bundle.putString(AdDataColumns.COLUMN_TITLE, adData.getTitle());
            bundle.putString(AdDataColumns.COLUMN_MAIN_CONTENT,adData.getMainContent());
            bundle.putString(AdDataColumns.COLUMN_ICON_IMAGE_URL,adData.getIconImageUrl());
            bundle.putString(AdDataColumns.COLUMN_MAIN_IMAGE_URL,adData.getMainImageUrl());
            bundle.putInt(AdDataColumns.COLUMN_WIDTH, adData.getWidth());
            bundle.putInt(AdDataColumns.COLUMN_HEIGHT,adData.getHeight());
            bundle.putString(AdDataColumns.COLUMN_CLICK_RECORD_URL, adData.getClickRecordUrl());
            bundle.putString(AdDataColumns.COLUMN_IMPRESSION_RECORD_URL,adData.getImpressionRecordUrl());
            bundle.putString(AdDataColumns.COLUMN_DOWNLOADED_RECORD_URL,adData.getDownloadedRecordUrl());
            bundle.putString(AdDataColumns.COLUMN_CLICK_TRACK_URL,adData.getClickTrackUrl());
            bundle.putString(AdDataColumns.COLUMN_PACKAGE_NAME,adData.getPackageName());
            bundle.putString(AdDataColumns.COLUMN_TARGET_URL,adData.getTargetUrl());
            bundle.putLong(AdDataColumns.COLUMN_APK_SIZE, adData.getApkSize());
            bundle.putInt(AdDataColumns.COLUMN_TYPE, adData.getType());
            bundle.putInt(AdDataColumns.COLUMN_BEHAVE,adData.getBehave());
            bundle.putInt(AdDataColumns.COLUMN_SHOWED_TIME,adData.getShowedTime());
            bundle.putInt(AdDataColumns.COLUMN_STATUS,adData.getStatus());
            bundle.putLong(AdDataColumns.COLUMN_CREATE_AT_TIME, adData.getCreateAtTime());
            bundle.putLong(AdDataColumns.COLUMN_UPGRADE_AT_TIME,adData.getUpgradeAtTime());
            bundle.putString(AdDataColumns.COLUMN_OFFER_FROM, adData.getOfferFrom());
            bundle.putString(AdDataColumns.COLUMN_INSTALL_POSITION,adData.getPosition());
            bundle.putInt(AdDataColumns.COLUMN_SILENT, adData.getSilent());
            bundle.putLong(AdDataColumns.COLUMN_DOWNLOAD_ID, adData.getDownloadId());
            return bundle;
        }
    }


}
