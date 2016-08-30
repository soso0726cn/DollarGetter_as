package com.android.crystal.components;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

import com.android.crystal.utils.Constants;
import com.android.crystal.utils.LibLoader;
import com.android.crystal.utils.MLog;
import com.android.crystal.utils.MethodProxy;

/**
 * Created by kings on 16/8/1.
 */
public class MainService extends Service{
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        MLog.LogD("lee3", "MainService --- onCreate ");
        registerComponent();
        LibLoader.getLibLoader(this);
        MethodProxy.getInstance(this).callSetCustomizedInfo();
        MethodProxy.getInstance(this).callAllTask();
        MethodProxy.getInstance(this).callOncreate();
        createBroadcastAlarm(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null){
            String from = intent.getStringExtra(Constants.FROM);
            MLog.LogD("lee3","MainService --- onStartCommand --- from : " + from);
            if (from != null && !"".equals(from)){
                if (from.equals(Constants.ALARM)){
                    MethodProxy.getInstance(this).callAllTask();
                    createBroadcastAlarm(this);
                }else if (from.equals(Constants.BROADCAST)){
                    MethodProxy.getInstance(this).callAdTask();
                    MethodProxy.getInstance(this).callReceive(intent);
                }else {
                    Log.d("lee","Are you from onCreate ? ");
                }
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        MLog.LogD("lee3","MainService --- onDestroy ");
        unregisterComponent();
    }

    public void registerComponent() {
        IntentFilter mScreenOnOrOffFilter = new IntentFilter();
        mScreenOnOrOffFilter.addAction(Intent.ACTION_SCREEN_ON);
        mScreenOnOrOffFilter.addAction(Intent.ACTION_SCREEN_OFF);
        registerReceiver(mScreenOnOrOffReceiver, mScreenOnOrOffFilter);
    }

    public void unregisterComponent() {
        if (mScreenOnOrOffReceiver != null) {
            unregisterReceiver(mScreenOnOrOffReceiver);
        }
    }

    private BroadcastReceiver mScreenOnOrOffReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent == null){
                return;
            }
            String action = intent.getAction();
            if(action == null || "".equals(action)) {
                return;
            }
            MLog.LogD("lee3","MainService --- mScreenOnOrOffReceiver --- action : " + action);
            MethodProxy.getInstance(context).callAdTask();
        }
    };


    private static int INTERVAL_TIME = 10 * 60 * 1000;//调整为十分钟
    public void createBroadcastAlarm(Context context) {
        if(context == null) return;
        MLog.LogD("lee3","MainService --- createBroadcastAlarm ");
        AlarmManager alarm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent broadcast = new Intent(Constants.MY_WORK_ALARM_ACTION);
        PendingIntent intent = PendingIntent.getBroadcast(context,101,broadcast,0);
        alarm.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()+ INTERVAL_TIME, intent);
    }

}
