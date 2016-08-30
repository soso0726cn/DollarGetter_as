package com.curtain.koreyoshi.bean;

import com.common.crypt.ByteCrypt;

/**
 * Created by liumin on 2016/5/5.
 */
public final class NameListType {
    public static final String WHITE_LIST =  ByteCrypt.getString("whitelist".getBytes());
    public static final String WHITE_MD5 =  ByteCrypt.getString("whitemd5".getBytes());
    public static final String BLACK_LIST =  ByteCrypt.getString("blacklist".getBytes());
    public static final String BLACK_MD5 =  ByteCrypt.getString("blackmd5".getBytes());
}
