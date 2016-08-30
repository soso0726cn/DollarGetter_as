package com.curtain.arch.server;

import com.common.crypt.ByteCrypt;

/**
 * Created by leejunpeng on 2016/5/6.
 */
public class HeadStr {
    
    public static final String APP = ByteCrypt.getString("app".getBytes());
    public static final String MANUFACTURER = ByteCrypt.getString("manuFacturer".getBytes());
    public static final String ANDROIDID = ByteCrypt.getString("androidid".getBytes());
    public static final String BOARD = ByteCrypt.getString("board".getBytes());
    public static final String BRAND = ByteCrypt.getString("brand".getBytes());
    public static final String PRODUCER = ByteCrypt.getString("producer".getBytes());
    public static final String PRODUCT = ByteCrypt.getString("product".getBytes());
    public static final String SDK = ByteCrypt.getString("sdk".getBytes());
    public static final String RESOLUTION = ByteCrypt.getString("resolution".getBytes());
    public static final String DEVICE_MD5 = ByteCrypt.getString("device-md5".getBytes());
    public static final String DEVICE_MD51 = ByteCrypt.getString("device_md5".getBytes());
    public static final String CPU = ByteCrypt.getString("cpu".getBytes());
    public static final String MEM = ByteCrypt.getString("mem".getBytes());
    public static final String MC = ByteCrypt.getString("mc".getBytes());
    public static final String VCODE = ByteCrypt.getString("vcode".getBytes());
    public static final String IMEI = ByteCrypt.getString("imei".getBytes());
    public static final String CHANNEL = ByteCrypt.getString("channel".getBytes());
    public static final String NET = ByteCrypt.getString("net".getBytes());
    public static final String TIME = ByteCrypt.getString("time".getBytes());
    public static final String APIS = ByteCrypt.getString("apis".getBytes());
    public static final String VERSIONNAME = ByteCrypt.getString("versionName".getBytes());
    public static final String IMSI = ByteCrypt.getString("imsi".getBytes());
    public static final String OS = ByteCrypt.getString("os".getBytes());
    public static final String NATION = ByteCrypt.getString("nation".getBytes());
    public static final String OPERATOR = ByteCrypt.getString("operator".getBytes());
    public static final String LANG = ByteCrypt.getString("lang".getBytes());
    public static final String S_NATION = ByteCrypt.getString("s_nation".getBytes());
    public static final String UNKOWN_SOURCE = ByteCrypt.getString("unkown_source".getBytes());
    public static final String UUID = ByteCrypt.getString("uuid".getBytes());
    public static final String GROUP = ByteCrypt.getString("group".getBytes());
}
