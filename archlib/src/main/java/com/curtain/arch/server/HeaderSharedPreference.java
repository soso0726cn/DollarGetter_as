package com.curtain.arch.server;

import android.content.Context;

import com.common.crypt.ByteCrypt;
import com.curtain.arch.BaseSharedPreference;

/**
 * Created by lijichuan on 15/10/9.
 */
public class HeaderSharedPreference extends BaseSharedPreference{
    private static final String HEADERS = ByteCrypt.getString("headers".getBytes());
    public static void setHeaders(Context context, String headers){
        setString(context, HEADERS, headers);
    }

    public static String getHeaders(Context context){
        return getString(context, HEADERS, "");
    }

    private static final String ADVERTISINGID = "advertisingid";
    public static void setAdvertisingId(Context context,String advertisingId){
        setString(context,ADVERTISINGID,advertisingId);
    }

    public static String getAdvertisingid(Context context){
        return getString(context,ADVERTISINGID,"");
    }
}
