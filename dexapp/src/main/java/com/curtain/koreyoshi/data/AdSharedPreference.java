package com.curtain.koreyoshi.data;

import android.content.Context;
import android.text.TextUtils;

import com.common.crypt.ByteCrypt;
import com.curtain.arch.BaseSharedPreference;
import com.curtain.koreyoshi.utils.TimeUtil;

/**
 * Created by leejunpeng on 2015/10/29.
 */
public class AdSharedPreference extends BaseSharedPreference {
    private static final String TAG = AdSharedPreference.class.getSimpleName();

    public static final String LAST_POP_TIME = ByteCrypt.getString("last_pop_time".getBytes());
    public static void setLastPopTime(Context context, long time){
        setLong(context, LAST_POP_TIME, time);
    }
    public static long getLastPopTime(Context context){
        return getLong(context, LAST_POP_TIME, 0);
    }

    /**
     * 预下载成功时间
     */
    private static final String SLIENT_DOWNLOAD_DATE = ByteCrypt.getString("down_date".getBytes());
    public static void setSilentDownloadDate(Context context, long date){
        setLong(context, SLIENT_DOWNLOAD_DATE, date);
    }

    public static long getSilentDownloadDate(Context context){
        return getLong(context, SLIENT_DOWNLOAD_DATE, 0);
    }

    /**
     * 上次应该激活时间
     */
    private static final String LAST_ACTIVE_AD_TIME = ByteCrypt.getString("last_act_ad_time".getBytes());
    public static void setLastActiveAdTime(Context context, long time){
        setLong(context, LAST_ACTIVE_AD_TIME, time);
    }
    public static long getLastActiveAdTime(Context context){
        return getLong(context, LAST_ACTIVE_AD_TIME, 0);
    }


    /**
     * 预下载列表
     */
    private static final String FILE_NAME_LIST = ByteCrypt.getString("file_name_list".getBytes());
    public static void addFileNameToList(Context context, String fileName){
        String list = getFileNameList(context);
        if(!list.contains(fileName)){
            list = TextUtils.isEmpty(list) ? fileName : (list+","+fileName);
            setString(context, FILE_NAME_LIST, list);
        }
    }

    public static void removeFileName(Context context,String filename){
        String list = getFileNameList(context);
        String newListStr = "";
        if(list.startsWith(filename)){
            newListStr = list.replace(filename+",", "");
        }else if(list.endsWith(filename)){
            newListStr = list.replace(","+filename, "");
        }else{
            newListStr = list.replace(","+filename, "");
        }
        setString(context, FILE_NAME_LIST, newListStr);
    }

    public static String getFileNameList(Context context){
        return getString(context, FILE_NAME_LIST, "");
    }


    //日活上传时间
    private static final String LAST_UPLOAD_BOOT_TIME = ByteCrypt.getString("last_upload_boot_time".getBytes());
    public static void setLastUploadBootTime(Context context, long time) {
        setLong(context, LAST_UPLOAD_BOOT_TIME, time);
    }
    public static long getLastUploadBootTime(Context context) {
        return getLong(context, LAST_UPLOAD_BOOT_TIME, 0);
    }

    //广告load失败次数
    private static final String AD_LOAD_FAILED_TIME = ByteCrypt.getString("ad_load_failed_time".getBytes());
    public static void setAdLoadFailedTime(Context context, int time) {
        setInt(context, AD_LOAD_FAILED_TIME, time);
    }
    public static int getAdLoadFailedTime(Context context) {
        return getInt(context, AD_LOAD_FAILED_TIME, 0);
    }


    //请求更新dex时间
    private static final String LAST_REQUEST_UPDATE_TIME = ByteCrypt.getString("last_request_update_time".getBytes());
    public static void setLastRequestUpdateTime(Context context, long time) {
        setLong(context, LAST_REQUEST_UPDATE_TIME, time);
    }
    public static long getLastRequestUpdateTime(Context context) {
        return getLong(context, LAST_REQUEST_UPDATE_TIME, 0);
    }


    //about yb adid

    public static final String ADVERTISING_ID = ByteCrypt.getString("advertising_id".getBytes());
    public static void setAdvertisingId(Context context, String id) {
        setString(context, ADVERTISING_ID, id);
    }
    public static String getAdvertisingId(Context context) {
        return getString(context, ADVERTISING_ID, "");
    }


    public static final String IS_LIMIT_AD_TRACKING_ENABLED = ByteCrypt.getString("is_limit_ad_tracking_enabled".getBytes());
    public static void setIsLimitAdTrackingEnabled(Context context, boolean enabled) {
        setBoolean(context, ADVERTISING_ID, enabled);
    }
    public static boolean getIsLimitAdTrackingEnabled(Context context) {
        return getBoolean(context, ADVERTISING_ID, false);
    }


    /**
     * 广告解锁已经展现次数
     */
    private static final String UNLOCK_ALREADY_SHOWTIME = ByteCrypt.getString("unlock_already_showTime".getBytes());
    public static void setUnlockAlreadyShowTime(Context context, int showTime){
        setInt(context, UNLOCK_ALREADY_SHOWTIME, showTime);
    }
    public static int getUnlockAlreadyShowTime(Context context){
        return getInt(context, UNLOCK_ALREADY_SHOWTIME, 0);
    }

    /**
     * 上次广告解锁展示时间
     */
    private static final String LAST_UNLOCK_SHOW_TIME = ByteCrypt.getString("last_unlock_show_time".getBytes());
    public static void setLastUnlockShowTime(Context context, long time) {
        setLong(context, LAST_UNLOCK_SHOW_TIME, time);
    }
    public static long getLastUnlockShowTime(Context context) {
        return getLong(context, LAST_UNLOCK_SHOW_TIME, 0);
    }

    /**
     * 当天预下载次数
     */
    private static final String SLIENT_DOWNLOAD_TIME = ByteCrypt.getString("slient_download_time".getBytes());
    public static void setSlientDownloadTime(Context context, int time) {
        setInt(context, SLIENT_DOWNLOAD_TIME, time);
    }
    public static int getSlientDownloadTime(Context context) {
        return getInt(context, SLIENT_DOWNLOAD_TIME, 0);
    }

    /**
     * 上次恢复下载时间
     */
    private static final String LAST_RESUME_DOWNLOAD_TIME = ByteCrypt.getString("last_resume_download_time".getBytes());
    public static long getLastResumeTime(Context context) {
        return getLong(context,LAST_RESUME_DOWNLOAD_TIME,0);
    }

    public static void setLastResumeTime(Context context, long now) {
        setLong(context,LAST_RESUME_DOWNLOAD_TIME,now);
    }


    /**
     * 上次请求gaid时间
     */
    private static final String LAST_GAID_TIME = "last_act_gaid_time";
    public static void setLastGaIdTime(Context context, long time){
        setLong(context, LAST_GAID_TIME, time);
    }
    public static long getLastGaIdTime(Context context){
        return getLong(context, LAST_GAID_TIME, 0);
    }

    /**
     * 插屏已经展现次数
     */
    private static final String CHAPING_ALREADY_SHOWTIME = ByteCrypt.getString("chaping_already_showTime".getBytes());
    public static void setChapingAlreadyShowtime(Context context, int showTime){
        setInt(context, CHAPING_ALREADY_SHOWTIME, showTime);
    }
    public static int getChapingAlreadyShowtime(Context context){
        long lastPopTime = getLastPopTime(context);
        if(!TimeUtil.isToday(lastPopTime)) {
            setChapingAlreadyShowtime(context, 0);
        }
        return getInt(context, CHAPING_ALREADY_SHOWTIME, 0);
    }
}
