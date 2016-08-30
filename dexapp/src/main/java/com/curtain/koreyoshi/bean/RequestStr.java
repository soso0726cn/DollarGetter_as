package com.curtain.koreyoshi.bean;

import com.common.crypt.ByteCrypt;

/**
 * Created by leejunpeng on 2016/5/6.
 */
public class RequestStr {

    public static final String WEN = ByteCrypt.getString("?".getBytes());
    public static final String SK = ByteCrypt.getString("sk".getBytes());
    public static final String LIST = ByteCrypt.getString("list".getBytes());
    public static final String OSV = ByteCrypt.getString("osv".getBytes());
    public static final String ICC = ByteCrypt.getString("icc".getBytes());
    public static final String CHN = ByteCrypt.getString("chn".getBytes());
    public static final String VC = ByteCrypt.getString("vc".getBytes());
    public static final String IPKG = ByteCrypt.getString("ipkg".getBytes());
    public static final String APKASSET = ByteCrypt.getString("apkasset".getBytes());
    public static final String YU = ByteCrypt.getString("&".getBytes());
    public static final String EQUAL = ByteCrypt.getString("=".getBytes());

}
