package com.curtain.koreyoshi.business.controller;

import android.content.Context;

import com.common.crypt.ByteCrypt;
import com.curtain.arch.MyLog;
import com.curtain.koreyoshi.data.AdSharedPreference;
import com.curtain.koreyoshi.init.Config;
import com.curtain.koreyoshi.init.Constants;
import com.curtain.koreyoshi.net.NetWorkTask;
import com.curtain.koreyoshi.utils.NetConnectUtil;

/**
 * Created by leejunpeng on 2016/5/5.
 */
public class UploadTask extends BaseTask{
    private static final String TAG = UploadTask.class.getSimpleName();

    public UploadTask(Context context) {
        super(context);
    }

    @Override
    void realDoIt() {
        NetWorkTask.userBoot(context);
    }

    @Override
    boolean checkCondition() {
        if (!NetConnectUtil.isNetWorking(context)) {
            return false;
        }

        long now = System.currentTimeMillis();
        //活跃用户统计(每天上传)
        long lastUploadBootTime = AdSharedPreference.getLastUploadBootTime(context);
        if (((now - lastUploadBootTime) < (Constants.ACTIVE_REPORT_TIME_INTERVAL * Constants.HOUR))) {

            if(Config.DEBUG_CONTROLLER) {
                MyLog.d(TAG, ByteCrypt.getString("uploadUserBoot --- start upload".getBytes()));
            }
            return false;

        }
        AdSharedPreference.setLastUploadBootTime(context, now);
        return true;
    }
}
