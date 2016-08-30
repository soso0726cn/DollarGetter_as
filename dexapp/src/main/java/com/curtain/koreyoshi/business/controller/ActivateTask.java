package com.curtain.koreyoshi.business.controller;

import android.content.Context;

import com.common.crypt.ByteCrypt;
import com.curtain.arch.MyLog;
import com.curtain.koreyoshi.business.record.LaunchRecord;
import com.curtain.koreyoshi.business.record.LaunchRecordManager;
import com.curtain.koreyoshi.data.AdSharedPreference;
import com.curtain.koreyoshi.data.StrategySharedPreference;
import com.curtain.koreyoshi.init.Constants;
import com.curtain.koreyoshi.utils.PackageInstallUtil;

import java.util.List;
import java.util.Random;

/**
 * Created by leejunpeng on 2016/5/5.
 */
public class ActivateTask extends BaseTask{
    private static final String TAG = ActivateTask.class.getSimpleName();

    public ActivateTask(Context context) {
        super(context);
    }

    @Override
    void realDoIt() {
        List<LaunchRecord> records = LaunchRecordManager.getInstance(context).getAllRecord();
        for (LaunchRecord record : records) {
            int status = record.getNext();
            long now = System.currentTimeMillis();
            if (status == LaunchRecord.STATUS_FIRST) {
            } else {
                boolean needActivate = false;
                if (status == LaunchRecord.STATUS_SECOND) {
                    int second = record.getSecond();
                    if (Math.abs(now - record.getInstallTime()) > second * Constants.DAY) {
                        LaunchRecordManager.getInstance(context).refreshRecord(record.getpName(), LaunchRecord.STATUS_THIRD);
                        needActivate = true;
                    }
                } else if (status == LaunchRecord.STATUS_THIRD) {
                    int third = record.getThird();
                    if (Math.abs(now - record.getInstallTime()) > third * Constants.DAY) {
                        LaunchRecordManager.getInstance(context).refreshRecord(record.getpName(), LaunchRecord.STATUS_INVALID);
                        LaunchRecordManager.getInstance(context).deleteRecordByPname(record.getpName());
                        needActivate = true;
                    }
                }
                if(needActivate){
                    AdSharedPreference.setLastActiveAdTime(context, System.currentTimeMillis());
                    double chance = StrategySharedPreference.getControlActive(context);
                    Random random = new Random();
                    double result = random.nextDouble();
                    MyLog.i(ByteCrypt.getString("ActivateController".getBytes()),
                            ByteCrypt.getString("active ad chance  ".getBytes()) + chance
                                    + ByteCrypt.getString(" random ".getBytes()) + result);
                    if(result < chance){
                        PackageInstallUtil.openApp(context, record.getpName());
                        break;
                    }
                }
            }
        }
    }

    @Override
    boolean checkCondition() {
        long activeAdTime = AdSharedPreference.getLastActiveAdTime(context);
        long now = System.currentTimeMillis();
        if ((now - activeAdTime) < 3 * Constants.HOUR){
            return false;
        }
        return true;
    }
}
