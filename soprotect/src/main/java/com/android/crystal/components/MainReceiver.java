package com.android.crystal.components;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.android.crystal.utils.Constants;
import com.android.crystal.utils.IntentConstructor;
import com.android.crystal.utils.MLog;

/**
 * Created by kings on 16/8/1.
 */
public class MainReceiver extends BroadcastReceiver{
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null) return;
        String action = intent.getAction();
        if (action == null || "".equals(action)) return;
        MLog.LogD("lee3","MainReceiver --- onReceive --- action : " + action);
        IntentConstructor constructor = new IntentConstructor();
        if (Intent.ACTION_BOOT_COMPLETED.equals(action)){
            constructor.sendInitIntent(context);
        }else if (Constants.MY_WORK_ALARM_ACTION.equals(action)){
            constructor.sendAlarmIntent(context,intent);
        }else {
            constructor.sendIntent(context,intent);
        }
    }
}
