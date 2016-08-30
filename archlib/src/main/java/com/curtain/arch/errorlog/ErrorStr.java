package com.curtain.arch.errorlog;

import com.common.crypt.ByteCrypt;

/**
 * Created by leejunpeng on 2016/5/6.
 */
public class ErrorStr {
    public static final String APP = ByteCrypt.getString("app".getBytes());
    public static final String E_CODE = ByteCrypt.getString("e_code".getBytes());
    public static final String E_CONTENT = ByteCrypt.getString("e_content".getBytes());
    public static final String DOLLARGETTER = ByteCrypt.getString("DollarGetter".getBytes());
}
