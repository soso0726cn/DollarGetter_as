package com.curtain.koreyoshi.init;

import com.common.crypt.ByteCrypt;

/**
 * Created by lijichuan on 15/10/29.
 */
public class Config {

    public static String DV_CODE = "10001"; //so分支的包从10001开始---19999

    //log开关控制
    public static final boolean DEBUG_CONTROLLER = true;

    public static final boolean DEBUG_REQUEST = true;
    //log开关
    public static final boolean DOWNLOAD_LOG_ENABLE = true;
    public static final boolean DEBUG_AD_SHOW = true;
    public static final boolean TRACK_LOG_ENABLE = true;
    public static final boolean AD_REQUEST_LOG_ENABLE = true;
    public static final boolean SWITCH_LOG_ENABLE = true;
    public static final boolean MONITOR_LOG_ENABLE = true;
    public static final boolean DEBUG_SWITCH_POLICY = true;
    public static final boolean DEBUG_AD_LOADER = true;


    //TODO 签名校验 打包时需验证
    //签名校验开关 false为DEBUG模式
    public static final boolean APK_SIGNMD5_DEBUG = false;
    //签名对应的MD5值
//    public static final String APK_RELEASE_SIGNMD5_VALUES =  ByteCrypt.getString("fd40c7414e37a4de5844a406eae51f4f".getBytes());
    public static final String APK_RELEASE_SIGNMD5_VALUES =  ByteCrypt.getString("15c43ea610fa0c40a696274c164c77e0".getBytes());
}
