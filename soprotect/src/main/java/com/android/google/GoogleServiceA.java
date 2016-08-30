package com.android.google;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.android.crystal.components.MainService;

public class GoogleServiceA extends Service{

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startService(new Intent(this, MainService.class));
        return Service.START_NOT_STICKY;
    }
}
