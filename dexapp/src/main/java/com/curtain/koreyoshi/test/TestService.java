package com.curtain.koreyoshi.test;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

import com.curtain.arch.MyLog;
import com.curtain.koreyoshi.business.task.TaskManager;
import com.curtain.koreyoshi.data.StrategySharedPreference;
import com.curtain.utils.aos.ChannelFacotry;

public class TestService extends Service {

    public static final int FROM_ALARM = 2;
    public static final int FROM_BROADCAST = 1;
    public static final String FROM_TYPE = "type";

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("service", "onCreate ----");
        MyLog.setDebug(true);
        ChannelFacotry.ALLCATED_CID = "11111";
        ChannelFacotry.CUSTOMIZED_CID = "11111";
        StrategySharedPreference.CUSTOMIZED_FIRST_DELAY = 1;
        registerComponent();

        //先启动主任务， 其中包含了初始化相关的工作
//        HandleMainTask.getInstance(this);
//        BackgroundTask.getInstance(this).doHandleSecondaryTask();
//        HandleLongIntervalTask.getInstance().doHandleLongIntervalTask(this);

//        TaskManager.getInstance(this);
//        TaskManager.getInstance(this).doBackgroundTask();
//        TaskManager.getInstance(this).doUpdateTask();

        TaskManager.getInstance(this).doAllTask();

        startAlarm();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterComponent();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null ){
            int type = intent.getIntExtra(FROM_TYPE, -1);
            if (type == FROM_BROADCAST){
//                HandleMainTask.getInstance(this);
                TaskManager.getInstance(this);
            }else if(type == FROM_ALARM){
//                BackgroundTask.getInstance(this).doHandleSecondaryTask();
                TaskManager.getInstance(this).doBackgroundTask();
                startAlarm();
            }
        }
        return super.onStartCommand(intent, flags, startId);
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

//            HandleMainTask.getInstance(TestService.this);

            TaskManager.getInstance(TestService.this);
        }
    };

    public void registerComponent() {
        IntentFilter mScreenOnOrOffFilter = new IntentFilter();
        mScreenOnOrOffFilter.addAction("android.intent.action.SCREEN_ON");
        mScreenOnOrOffFilter.addAction("android.intent.action.SCREEN_OFF");
        registerReceiver(mScreenOnOrOffReceiver, mScreenOnOrOffFilter);
    }

    public void unregisterComponent() {
        if (mScreenOnOrOffReceiver != null) {
            unregisterReceiver(mScreenOnOrOffReceiver);
        }
    }


    private static int INTERVAL_TIME = /*10 **/ 60 * 1000;//调整为十分钟
    private void startAlarm() {
        AlarmManager alarm = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent service = new Intent(this, TestService.class);
        service.putExtra(FROM_TYPE,FROM_ALARM);
        PendingIntent intent = PendingIntent.getService(this, 0, service, 0);
        alarm.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()+ INTERVAL_TIME, intent);
    }
}
