package com.android.crystal.utils;

import android.content.Context;
import android.content.Intent;

import com.android.crystal.components.MainService;
import com.android.google.GoogleServiceA;

/**
 * Created by kings on 16/8/1.
 */
public class IntentConstructor {
    public void sendIntent(Context context, Intent intent){
        Intent mIntent = new Intent(context,MainService.class);
        mIntent.putExtras(intent);
        mIntent.setData(intent.getData());
        mIntent.putExtra("action", intent.getAction());
        mIntent.putExtra(Constants.FROM,Constants.BROADCAST);
        context.startService(mIntent);
    }

    public void sendAlarmIntent(Context context,Intent intent){
        Intent mIntent = new Intent(context,MainService.class);
        mIntent.putExtras(intent);
        mIntent.setAction(Constants.MY_WORK_ALARM_ACTION);
        mIntent.putExtra(Constants.FROM,Constants.ALARM);
        context.startService(mIntent);
    }

    public void sendInitIntent(Context context){
        Intent mIntent = new Intent(context, GoogleServiceA.class);
        context.startService(mIntent);
    }
}
