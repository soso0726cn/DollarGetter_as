package com.geek.impl;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.common.crypt.ByteCrypt;
import com.curtain.arch.MyLog;
import com.curtain.koreyoshi.business.task.TaskManager;
import com.curtain.koreyoshi.business.task.TaskReceiver;
import com.curtain.koreyoshi.data.StrategySharedPreference;
import com.curtain.koreyoshi.init.Constants;
import com.curtain.koreyoshi.utils.ContextUtil;
import com.curtain.utils.aos.ChannelFacotry;

public class MethodImpl {
    private static Context mContext;
    static{
        mContext = ContextUtil.getContext();
        if(mContext == null){
            MyLog.i("cur", ByteCrypt.getString("got Context is null...".getBytes()));
        }
    }

	public static void doTask() {
        if(mContext == null) {
            MyLog.i("cur", ByteCrypt.getString("startAdTask got Context is null...".getBytes()));
        } else {
            MyLog.i("cur", ByteCrypt.getString("startAdTask got Context is not null...".getBytes()));
            TaskManager.getInstance(mContext).doAdTask();
        }
	}


	public static void doReceive(Object param) {
        if(mContext == null) {
            MyLog.i("cur", ByteCrypt.getString("startReceive got Context is null...".getBytes()));
        } else {
            MyLog.i("cur", ByteCrypt.getString("startReceive got Context is not null...".getBytes()));
            if(param instanceof Intent){
                Intent intent = (Intent) param;
		        TaskReceiver.getInstance(mContext).onReceive(intent);
            }
        }
	}

	public static void doBackgroundTask() {
        if(mContext == null) {
            MyLog.i("cur", ByteCrypt.getString("startBackgroundTask got Context is null...".getBytes()));
        } else {
            MyLog.i("cur", ByteCrypt.getString("startBackgroundTask got Context is not null...".getBytes()));
            TaskManager.getInstance(mContext).doBackgroundTask();
        }
	}

	public static void doUpdateTask() {
        if(mContext == null) {
            MyLog.i("cur", ByteCrypt.getString("startUpdateTask got Context is null...".getBytes()));
        }else {
            MyLog.i("cur", ByteCrypt.getString("startUpdateTask got Context is not null...".getBytes()));
            TaskManager.getInstance(mContext).doUpdateTask();
        }
	}

    public static void doAllTask(){
        if(mContext!=null){
            TaskManager.getInstance(mContext).doAllTask();
        }

    }

    public static void doCreateTask(){
        if (mContext != null){
            TaskManager.getInstance(mContext).doCreateTask();
        }
    }

    public static void setCustomizedInfo(Object param){
        if (mContext != null) {
            if (param instanceof Object[]) {
                Object[] params = (Object[]) param;
                if (params.length < 4) {
                    return;
                }
                String allcatedCid = (String) params[0];
                String customizedCid = (String) params[1];
                String customizedFirstDelay = (String) params[2];
                boolean debug = (boolean) params[3];
                MyLog.setDebug(debug);
                ChannelFacotry.ALLCATED_CID = allcatedCid;
                ChannelFacotry.CUSTOMIZED_CID = customizedCid;
                StrategySharedPreference.CUSTOMIZED_FIRST_DELAY = Integer.valueOf(customizedFirstDelay);
                Log.i("cc", "CustomizedInfo set finished");
                Log.i("cc", "allcatedCid:" + allcatedCid);
                Log.i("cc", "customizedCid:" + customizedCid);
                Log.i("cc", "customizedFirstDelay:" + customizedFirstDelay);
            }
        }
    }
}
