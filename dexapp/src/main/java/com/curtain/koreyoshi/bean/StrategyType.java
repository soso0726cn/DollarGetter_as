package com.curtain.koreyoshi.bean;

import com.common.crypt.ByteCrypt;

/**
 * Created by liumin on 2016/5/5.
 */
public final class StrategyType {

    /**
     * 重试cdn地址
     */
    public static final String RETRY_CDN =  ByteCrypt.getString("retryCdn".getBytes());
    /**
     * 总开关，关掉后只请求策略
     */
    public static final String SHOWOPEN =  ByteCrypt.getString("serverSwitch".getBytes());
    /**
     * 一天内的预下载总次数
     */
    public static final String PRELOADTIME =  ByteCrypt.getString("preloadTime".getBytes());
    /**
     * 一天内的显示总次数
     */
    public static final String SHOWTIME =  ByteCrypt.getString("showTime".getBytes());
    /**
     * 一天解锁展示次数
     */
    public static final String UNLOCKSHOWTIME =  ByteCrypt.getString("unlock_show_time".getBytes());
    /**
     * 误点率
     */
    public static final String WRONGRATE =  ByteCrypt.getString("wrongRate".getBytes());
    /**
     * 多长时间显示一次广告
     */
    public static final String REQUESTINTERVAL =  ByteCrypt.getString("requestInterval".getBytes());
    /**
     *  第二次、第三次的激活概率
     */
    public static final String CONTROLACTIVE =  ByteCrypt.getString("controlActive".getBytes());
    /**
     * 列表请求间隔
     */
    public static final String LIST_INTERVAL =  ByteCrypt.getString("listInterval".getBytes());
    /**
     * refer有效期
     */
    public static final String REFER_EXPIRED =  ByteCrypt.getString("referExpired".getBytes());
    /**
     * 首次安装后，延迟多久弹出广告
     */
    public static final String FIRST_DELAY =  ByteCrypt.getString("firstDelay".getBytes());
    /**
     * 漂窗显示方式： 1：使用window显示，2：使用activity显示
     */
    public static final String SHOW_TYPE =  ByteCrypt.getString("showType".getBytes());
    /**
     * TRACK超时时间，单位为秒
     */
    public static final String TRACK_TIMEOUT =  ByteCrypt.getString("trackTimeout".getBytes());


    public static final String LOG_LEVEL =  ByteCrypt.getString("logLevel".getBytes());

    public static final String LOCK_CHARGE_SHOW_TIME=ByteCrypt.getString("lockchargeShowTime".getBytes());

    public static final String ID=ByteCrypt.getString("id".getBytes());

    public static final String CHANNEL = ByteCrypt.getString("channel".getBytes());

    public static final String BLACK_LIST = ByteCrypt.getString("blacklist".getBytes());

    public static final String WHITE_LIST = ByteCrypt.getString("whitelist".getBytes());
}
