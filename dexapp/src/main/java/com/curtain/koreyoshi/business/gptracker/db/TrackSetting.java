package com.curtain.koreyoshi.business.gptracker.db;

import android.content.Context;

import com.common.crypt.ByteCrypt;

/**
 * Created by yangzheng on 15/10/31.
 */
public class TrackSetting {
    public static final String SHARE_LAST_FECTH_TIME = ByteCrypt.getString("last_fecth_time".getBytes());

    public static void setLastFetchTime(Context context,long value) {
        TrackShare.putShareLong(context, SHARE_LAST_FECTH_TIME, value);
    }

    public static long getLastFectchTime(Context context)
    {
        return TrackShare.getLong(context,SHARE_LAST_FECTH_TIME,0L);
    }

}
