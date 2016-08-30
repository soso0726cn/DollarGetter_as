package com.curtain.koreyoshi.bean;

import com.common.crypt.ByteCrypt;

/**
 * Created by leejunpeng on 2016/5/6.
 */
public class LibSerData {

    public static final String URL = ByteCrypt.getString("url".getBytes());
    public static final String SIZE = ByteCrypt.getString("size".getBytes());
    public static final String MD5 = ByteCrypt.getString("md5".getBytes());
}
