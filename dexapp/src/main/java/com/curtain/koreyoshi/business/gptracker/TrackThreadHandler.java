package com.curtain.koreyoshi.business.gptracker;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.common.crypt.ByteCrypt;
import com.curtain.arch.MyLog;
import com.curtain.koreyoshi.business.gptracker.dowloadtracker.DownloadThread;
import com.curtain.koreyoshi.business.gptracker.dowloadtracker.TrackDownloadUrlUtil;
import com.curtain.koreyoshi.init.Config;
import com.curtain.koreyoshi.bean.AdData;

/**
 * Created by yangzheng on 15/10/27.
 */
public class TrackThreadHandler {

    private static final boolean DEBUG_TRACK = Config.TRACK_LOG_ENABLE;
    private static Handler mHandler = null;
    private Context mContext = null;

    public static final int MSG_GETREFWEBVIEW = 10;
    public static final int MSG_GETDOWNLOADURLVIEW = 20;

    public static Handler getInstance(Context context)
    {
        if (mHandler == null)
        {
            new TrackThreadHandler(context);
        }
        return mHandler;
    }

    private TrackThreadHandler(Context context)
    {
        mContext = context;
        init();
    }

    private void init()
    {
        try {
            mHandler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    switch (msg.what) {
                        case MSG_GETREFWEBVIEW:
                            Bundle bundle = msg.getData();
                            ReferThread t = (ReferThread)msg.obj;
                            if (bundle != null) {
                                AdData adData = new AdData();
                                AdDataConverter.bundleToData(bundle,adData);
                                TrackUtils.getReferrerByWebView(mContext,adData,t);
                            }
                            break;
                        case MSG_GETDOWNLOADURLVIEW:
                            Bundle dbundle = msg.getData();
                            DownloadThread dt = (DownloadThread)msg.obj;
                            String downloadUrl = dbundle.getString(ByteCrypt.getString("download".getBytes()));
                            if(downloadUrl !=null && !("".equals(downloadUrl.trim()))){
                                TrackDownloadUrlUtil.getDownloadUrlByWebView(mContext, downloadUrl, dt);
                            }
                            break;
                    }
                }
            };
        }catch (Exception e)
        {
            if (DEBUG_TRACK)
                MyLog.printException(e);
        }
    }

}
