package com.curtain.koreyoshi.business.gptracker;

import android.content.Context;
import android.telephony.TelephonyManager;

import com.common.crypt.ByteCrypt;
import com.curtain.arch.MyLog;
import com.curtain.koreyoshi.init.Config;
import com.curtain.koreyoshi.net.NetWorkTask;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by yangzheng on 15/11/2.
 */
public class TrackFailBuilder
{

    private static final boolean DEBUG_TRACK = Config.TRACK_LOG_ENABLE;


    public TrackFailBuilder()
    {
        init();
    }

    private void init()
    {
        jsonArray = new JSONArray();
    }

    private void initIfNecessary()
    {
        if(jsonArray == null)
        {
            init();
        }
    }

    private JSONArray jsonArray;

    public void putArrayValue(Context context,String key)
    {
        initIfNecessary();
        try {
            jsonArray.put(key);
        } catch (Exception e) {
            if (DEBUG_TRACK)
                MyLog.printException(e);
            e.printStackTrace();
        }
    }

    public void send(Context context, int silent){
        StackTraceElement[] stackTrace = new Exception().getStackTrace();
        for(StackTraceElement e: stackTrace) {
            MyLog.d(TrackFailBuilder.class.getSimpleName(), e.getClassName() + " \t" + e.getMethodName() + " \t" + e.getLineNumber());
        }

        initIfNecessary();

        JSONObject json = new JSONObject();

        try
        {
            if(jsonArray.length() == 0)
            {
                return;
            }

            String iso = getIsoCountryCode(context);

            json.put(ByteCrypt.getString("iso".getBytes()),iso == null ? "":iso);
            json.put(ByteCrypt.getString("failRefer".getBytes()), jsonArray);
            json.put(ByteCrypt.getString("silent".getBytes()),silent);
        }
        catch (Exception e)
        {
            if (DEBUG_TRACK)
                MyLog.printException(e);
            e.printStackTrace();
        }

        if (DEBUG_TRACK) {
            MyLog.i(ByteCrypt.getString("sending thread: ".getBytes())
                    + Thread.currentThread().getName()
                    + ByteCrypt.getString(" ;id: ".getBytes())
                    + Thread.currentThread().getId());

            MyLog.i(ByteCrypt.getString("sending ---- failed time: ".getBytes()) + json.toString());

        }

        NetWorkTask.trackFailReport(context,json);
    }

    private static String getIsoCountryCode(Context context) {
        TelephonyManager mTelephonyManager = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);

        return mTelephonyManager == null ? null : mTelephonyManager.getNetworkCountryIso();
    }


}
