package com.curtain.koreyoshi.business.showfilter;

import android.app.KeyguardManager;
import android.content.Context;
import android.hardware.display.DisplayManager;
import android.os.Build;
import android.os.PowerManager;
import android.util.Log;
import android.view.Display;
import android.widget.Toast;

import com.common.crypt.ByteCrypt;
import com.curtain.arch.MyLog;
import com.curtain.koreyoshi.init.Config;
import com.curtain.koreyoshi.business.popad.PopAdManager;
import com.curtain.koreyoshi.data.AdSharedPreference;
import com.curtain.koreyoshi.data.StrategySharedPreference;
import com.curtain.koreyoshi.init.Constants;
import com.curtain.koreyoshi.utils.NetConnectUtil;

import java.lang.reflect.Method;
import java.util.Date;

/**
 * Created by lijichuan on 15/11/2.
 */
public class MobileStatusMonitor {
    private static final boolean DEBUG_MONITOR = Config.MONITOR_LOG_ENABLE;
//    private static final String TAG = MobileStatusMonitor.class.getSimpleName();
    private static final String TAG = "cc";


    public static boolean satisfied(Context context) {

        boolean satisfied = checkCondition(context);

        if(DEBUG_MONITOR) {
            MyLog.d(TAG, ByteCrypt.getString("satisfied: ".getBytes()) + satisfied);
        }

        return satisfied;
    }

    //1. 亮屏解锁状态；	2. 网络连接状态；	3. 时间时隔满足
    private static boolean checkCondition(Context context) {
        if (!showOpen(context)){
            if(DEBUG_MONITOR)	{
                MyLog.d(TAG, ByteCrypt.getString("switch is close!!!!!".getBytes()));
                Toast.makeText(context, ByteCrypt.getString("总开关关闭了".getBytes()), Toast.LENGTH_SHORT).show();
            }
            return false;
        }
        //判断是否处于解锁状态	TODO  兼容性验证
        if(isKeyguardLocked(context)){
            if(DEBUG_MONITOR)	MyLog.d(TAG, ByteCrypt.getString("keyguard locked".getBytes()));
            return false;
        }

        if( !isScreenOn(context) ) {
            if(DEBUG_MONITOR)	MyLog.d(TAG, ByteCrypt.getString("screen off ".getBytes()));
            return false;
        }

        //判断网络是否处于连接状态
        if( !NetConnectUtil.isNetWorking(context) ) {
            if(DEBUG_MONITOR)	{
                MyLog.d(TAG, ByteCrypt.getString("net not connected".getBytes()));
                Toast.makeText(context, ByteCrypt.getString("KS: wifi未连接".getBytes()), Toast.LENGTH_SHORT).show();
            }
            return false;
        }

        //判断总次数是否已满足
        if(!checkPopCount(context)) {
            if(DEBUG_MONITOR) {
                MyLog.d(TAG, ByteCrypt.getString("total time not satisfied".getBytes()));
                Toast.makeText(context, ByteCrypt.getString("KS: 当日总次数已达标".getBytes()), Toast.LENGTH_SHORT).show();
            }
            return false;
        }


        //判断时间间隔是否足够
        if(!checkTimeInterval(context)) {
            if(DEBUG_MONITOR) {
                MyLog.d(TAG, ByteCrypt.getString("time interval not satisfied".getBytes()));
                Toast.makeText(context, ByteCrypt.getString("KS: 与上次显示时间间隔太短".getBytes()), Toast.LENGTH_SHORT).show();
            }
            return false;
        }


        return true;
    }

    private static boolean checkTimeInterval(Context context) {
        long lastPopTime = AdSharedPreference.getLastPopTime(context);
        long now = System.currentTimeMillis();
        long interval = (long) (StrategySharedPreference.getRequestInterval(context) * Constants.HOUR);
        if (lastPopTime == 0){
            long lastRequestStrategy = StrategySharedPreference.getStrategyUpdateTime(context);
            if (lastRequestStrategy != 0) {
                long firstDelay = (long) (StrategySharedPreference.getFirstDelay(context) * Constants.HOUR);
                long nextShowTime = now + firstDelay - interval;
                if (DEBUG_MONITOR) MyLog.d(TAG, ByteCrypt.getString("first time delay to: ".getBytes()) + new Date(nextShowTime));
                AdSharedPreference.setLastPopTime(context, nextShowTime);
                Log.i("cc","firstDelay:"+(firstDelay));
                Log.i("cc","nextShowTime:"+(nextShowTime));
            }
            return false;
        }
        if(DEBUG_MONITOR)
            MyLog.d(TAG, ByteCrypt.getString("now: ".getBytes())
                    + new Date(now)
                    + ByteCrypt.getString(" ;lastPopTime: ".getBytes())
                    + new Date(lastPopTime)
                    + ByteCrypt.getString(" ;interval: ".getBytes())
                    + interval/Constants.HOUR);

        Log.i("cc","now:"+now);
        Log.i("cc","lastPopTime:"+lastPopTime);
        Log.i("cc","now - lastPopTime:"+(now - lastPopTime));
        Log.i("cc","interval:"+interval);
        return ((now - lastPopTime) > interval);
    }

    //TODO adManager
    private static boolean checkPopCount(Context context) {
        int needTime = StrategySharedPreference.getShowTime(context);
//        int alreadyTime = PopAdManager.getInstance(context).getShowedTime();
        int alreadyTime = AdSharedPreference.getChapingAlreadyShowtime(context);
        if(DEBUG_MONITOR) {
            MyLog.d(TAG,ByteCrypt.getString("check count -- needTime: ".getBytes())
                    + needTime
                    + ByteCrypt.getString(" ;alreadyTime: ".getBytes())
                    + alreadyTime);
        }
        return (alreadyTime < needTime);
    }

    private static boolean isScreenOn(Context context) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            try {
                // If you use API20 or more:
                DisplayManager dm = (DisplayManager) context.getSystemService(Context.DISPLAY_SERVICE);
                for (Display display : dm.getDisplays()) {
                    Class displayClass = Display.class;
                    Method[] declaredMethods = displayClass.getDeclaredMethods();
                    for (Method method : declaredMethods) {
                        String name = method.getName();
                        Class<?>[] parameterTypes = method.getParameterTypes();

                        if(name != null && ByteCrypt.getString("getState".getBytes()).equals(name)
                                && parameterTypes.length == 0) {
                            if(DEBUG_MONITOR) MyLog.d(TAG, ByteCrypt.getString("by display : ".getBytes()) + display.getState());
                            if (display.getState() != Display.STATE_OFF) {
                                return true;
                            }
                        }
                    }
                }
            } catch (Exception e) {
                //ignore		//国产手机由于rom的原因，很多Jerry_bean及以上的手机都没有这个api
            }
        }

        // If you use less than API20:
        try {
            PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            if (powerManager.isScreenOn()) {
                return true;
            }
        }catch (Exception e){
            return true;
        }

        return false;
    }

    public static boolean showOpen(Context context){
        int open = StrategySharedPreference.getShowOpen(context);
        Log.i("cc","showOpen:"+open);
        return (open > 0);
    }


    /**
     * 屏幕是否锁屏状态
     *
     * @param context
     * @return
     */
    public static boolean isKeyguardLocked(Context context) {
        boolean isLocked = false;
        try {
            KeyguardManager mKeyguardManager = (KeyguardManager) context
                    .getSystemService(Context.KEYGUARD_SERVICE);
            isLocked = mKeyguardManager.inKeyguardRestrictedInputMode();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return isLocked;
    }
}
