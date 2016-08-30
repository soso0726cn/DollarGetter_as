package com.curtain.koreyoshi.data;

import android.content.Context;

import com.common.crypt.ByteCrypt;
import com.curtain.arch.BaseSharedPreference;
import com.curtain.koreyoshi.init.Constants;

/**
 * Created by leejunpeng on 2015/10/23.
 */
public class StrategySharedPreference extends BaseSharedPreference {
    private static final String TAG = StrategySharedPreference.class.getSimpleName();
    public static int CUSTOMIZED_FIRST_DELAY=3;
    private static final class Config {
        private static final int DEFAULT_ORIGIN_SHOW_TIME = 2;
        private static final int DEFAULT_UNION_SHOW_TIME = 2;
        private static final int DEFAULT_TOTAL_SHOW_TIME = 4;
        private static /*final*/ int DEFAULT_FIRST_DELAY_HOUR = /*3*/CUSTOMIZED_FIRST_DELAY;
        private static final int DEFAULT_SHOW_TYPE = 1;
        private static final int DEFAULT_TRACK__TIMEOUT = 40;
    public static final int DEFAULT_LOG_LEVEL = 3;//[config]=LOG_LEVEL
        public static final int DEFAULT_SHOW_OPEN = 1;
    }
//--------------------  从服务器获取的字段 start --------------
    /**
     *  广告开关 0关1开
     */
    private static final String SHOW_OPEN = ByteCrypt.getString("show_open".getBytes());
    public static void setShowOpen(Context context, int level){
        setInt(context, SHOW_OPEN, level);
    }
    public static int getShowOpen(Context context){
        return getInt(context, SHOW_OPEN, Config.DEFAULT_SHOW_OPEN);
    }

    /**
     * 广告展现次数
     */
    private static final String TOTAL_SHOWTIME = ByteCrypt.getString("showTime".getBytes());
    public static void setTotalShowTime(Context context, int showTime){
        setInt(context, TOTAL_SHOWTIME, showTime);
    }
    public static int getShowTime(Context context){
        return getInt(context, TOTAL_SHOWTIME, Config.DEFAULT_TOTAL_SHOW_TIME);
    }

    /**
     * 广告解锁需要展现次数
     */
    private static final String UNLOCK_SHOWTIME = ByteCrypt.getString("unlockshowTime".getBytes());
    public static void setUnlockShowTime(Context context, int showTime){
        setInt(context, UNLOCK_SHOWTIME, showTime);
    }
    public static int getUnlockShowTime(Context context){
        return getInt(context, UNLOCK_SHOWTIME, 0);
    }

    /**
     * 预下载次数
     */
    private static final String TOTAL_PRELOADTIME = ByteCrypt.getString("preloadTime".getBytes());
    public static void setTotalPreloadTime(Context context, int preloadTime){
        setInt(context, TOTAL_PRELOADTIME, preloadTime);
    }
    public static int getTotalPreloadTime(Context context){
        return getInt(context, TOTAL_PRELOADTIME, 0);
    }

    /**
     * track超时时长
     */
    private static final String TRACK_TIMEOUT = ByteCrypt.getString("track_timeout".getBytes());
    public static void setTrackTimeOut(Context context, int trackTimeout){
        setInt(context, TRACK_TIMEOUT, trackTimeout);
    }
    public static int getTrackTimeOut(Context context){
        return getInt(context, TRACK_TIMEOUT, Config.DEFAULT_TRACK__TIMEOUT);
    }

    /**
     * 误点率
     */
    private static final String WRONGRATE = ByteCrypt.getString("wrongRate".getBytes());
    public static void setWrongRate(Context context, double wrongRate){
        setDouble(context, WRONGRATE, wrongRate);
    }
    public static double getWrongRate(Context context){
        return getDouble(context, WRONGRATE, 0);
    }

    /**
     * 请求展现广告间隔
     */
    private static final String REQUESTINTERVAL = ByteCrypt.getString("requestInterval".getBytes());
    public static void setRequestInterval(Context context, int requestInterval){
        setDouble(context, REQUESTINTERVAL, requestInterval);
    }
    public static double getRequestInterval(Context context){
        return getDouble(context, REQUESTINTERVAL, 0);
    }


    /**
     * 请求refer间隔
     * @param context
     * @return
     */
    public static double getReferInterval(Context context){
        double listInterval = getDouble(context, LISTINTERVAL, 0);
        double referInterval = listInterval - 1 > 0 ? (listInterval - 1) : listInterval;
        return referInterval;
    }

    /**
     * 激活控制
     */
    private static final String CONTROLACTIVE = ByteCrypt.getString("controlActive".getBytes());
    public static void setControlActive(Context context, double controlActive){
        setDouble(context, CONTROLACTIVE, controlActive);
    }
    public static double getControlActive(Context context){
        return getDouble(context, CONTROLACTIVE, 0);
    }

    /**
     * 请求广告列表间隔
     */
    private static final String LISTINTERVAL = ByteCrypt.getString("listInterval".getBytes());
    public static void setListInterval(Context context, double listInterval){
        setDouble(context, LISTINTERVAL, listInterval);
    }
    public static double getListInterval(Context context){
        return getDouble(context, LISTINTERVAL, 0);
    }

    /**
     * refer有效时长
     * 单位： 小时
     */
    private static final String REFEREXPIRED = ByteCrypt.getString("referExpired".getBytes());
    public static void setReferExpired(Context context, double referExpired){
        setDouble(context, REFEREXPIRED, referExpired);
    }
    public static double getReferExpired(Context context){
        return getDouble(context, REFEREXPIRED, 0);
    }

    /**
     * 首次安装弹广告的延期时长
     * 单位： 小时
     */
    private static final String FIRSTDELAY = ByteCrypt.getString("firstDelay".getBytes());
    public static void setFirstDelay(Context context, double firstDelay){
        setDouble(context, FIRSTDELAY, firstDelay);
    }
    public static double getFirstDelay(Context context){
        return getDouble(context, FIRSTDELAY, Config.DEFAULT_FIRST_DELAY_HOUR);
    }

    /**
     * 网盟广告展示次数
     */
    private static final String SHOW_TIME_UNION = ByteCrypt.getString("show_time_union".getBytes());
    public static void setUnionShowTime(Context context, int times){
        setInt(context, SHOW_TIME_UNION, times);
    }
    public static int getUnionShowTime(Context context){
        return getInt(context, SHOW_TIME_UNION, Config.DEFAULT_UNION_SHOW_TIME);
    }

    /**
     *  网盟原生展示次数
     */
    private static final String SHOW_TIME_ORIGIN = ByteCrypt.getString("show_time_origin".getBytes());
    public static void setOriginShowTime(Context context, int times){
        setInt(context, SHOW_TIME_ORIGIN,times);
    }
    public static int getOriginShowTime(Context context){
        return getInt(context, SHOW_TIME_ORIGIN, Config.DEFAULT_ORIGIN_SHOW_TIME);
    }


    /**
     *  广告展现形式：1为window 2为activity
     */
    private static final String AD_SHOW_TYPE = ByteCrypt.getString("ad_show_type".getBytes());
    public static void setAdShowType(Context context, int type){
        setInt(context, AD_SHOW_TYPE,type);
    }
    public static int getAdShowType(Context context){
        return getInt(context, AD_SHOW_TYPE, Config.DEFAULT_SHOW_TYPE);
    }

    /**
     *  日志上报等级
     */
    private static final String LOG_LEVEL = ByteCrypt.getString("log_level".getBytes());
    public static void setLogLevel(Context context, int level){
        setInt(context, LOG_LEVEL,level);
    }
    public static int getLogLevel(Context context){
        return getInt(context, LOG_LEVEL, Config.DEFAULT_LOG_LEVEL);
    }

    /**
     * 白名单
     */
    final static String WHITE_LIST=ByteCrypt.getString("white_list".getBytes());
    public static void setWhiteList(Context context,String list){
        setString(context, WHITE_LIST, list);
    }
    public static String getWhiteList(Context context){
        return getString(context, WHITE_LIST, "");
    }

    /**
     * 黑名单
     */
    final static String BLACK_LIST=ByteCrypt.getString("black_list".getBytes());
    public static void setBlackList(Context context,String list){
        setString(context, BLACK_LIST, list);
    }
    public static String getBlackList(Context context){
        return getString(context, BLACK_LIST, "");
    }
	
	   /**
     *  重试cdn
     */
    private static final String RETRY_CDN = ByteCrypt.getString("retry_cdn".getBytes());
    public static void setRetryCdn(Context context, String retryCdn){
        setString(context, RETRY_CDN, retryCdn);
    }
    public static String getRetryCdn(Context context){
        return getString(context, RETRY_CDN, "");
    }


//--------------------  从服务器获取的字段 end --------------


//--------------------  客户端存入的时间戳 start --------------

    final static String LAST_REQUEST_LIST_TIME = ByteCrypt.getString("last_request_list_time".getBytes());
    public static void setLastRequestListTime(Context context, long time) {
        setLong(context,LAST_REQUEST_LIST_TIME,time);
    }
    public static long getLastRequestListTime(Context context) {
        return getLong(context,LAST_REQUEST_LIST_TIME,0);
    }


    /**
     * 检查弹窗权限的时间
     * 间隔10天一次
     */
    private static final String LASTCHECKALERTTIME = ByteCrypt.getString("lastcheckalerttime".getBytes());
    public static void setLastCheckAlertTime(Context context, long checktime) {
        setLong(context, LASTCHECKALERTTIME, checktime);
    }
    public static long getLastCheckAlertTime(Context context){
        return getLong(context, LASTCHECKALERTTIME, 0);
    }

    /**
     * 广告策略更新的时间
     */
    private static final String STRATEGYUPDATETIME = ByteCrypt.getString("strategyupdatetime".getBytes());
    public static void setStrategyUpdateTime(Context context, long strategyupdatetime){
        setLong(context, STRATEGYUPDATETIME, strategyupdatetime);
    }
    public static long getStrategyUpdateTime(Context context){
        return getLong(context, STRATEGYUPDATETIME, 0);
    }

    /**
     * 广告策略更新的时间
     */
    private static final String REQUEST_COMPLETED_TIME = ByteCrypt.getString("request_completed_time".getBytes());
    public static void setLastRequestAdTime(Context context, long time){
        setLong(context, REQUEST_COMPLETED_TIME, time);
    }
    public static long getLastRequestAdTime(Context context){
        return getLong(context, REQUEST_COMPLETED_TIME, 0);
    }



    //广告请求重试次数
    private static final String AD_RETRY_TIMES = ByteCrypt.getString("ad_retry_times".getBytes());
    public static void setRetryTimes(Context context, int retryTimes){
        setInt(context, AD_RETRY_TIMES, retryTimes);
    }
    public static int getRetryTimes(Context context){
        return getInt(context, AD_RETRY_TIMES, 0);
    }

//--------------------  客户端存入的时间戳 end --------------
}
