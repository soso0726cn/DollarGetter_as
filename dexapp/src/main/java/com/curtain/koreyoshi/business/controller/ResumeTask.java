package com.curtain.koreyoshi.business.controller;

import android.content.Context;

import com.curtain.koreyoshi.data.AdSharedPreference;
import com.curtain.koreyoshi.download.DownloadByXu;
import com.curtain.koreyoshi.init.Constants;
import com.curtain.koreyoshi.utils.NetConnectUtil;

/**
 * Created by leejunpeng on 2016/5/5.
 */
public class ResumeTask extends BaseTask{
    private static final String TAG = ResumeTask.class.getSimpleName();
    private static final long RESUME_INTERNAL = 10 * Constants.MINUTE;

    public ResumeTask(Context context) {
        super(context);
    }

    @Override
    void realDoIt() {
        DownloadByXu.getInstance(context).resumeAllDownload();
    }

    @Override
    boolean checkCondition() {
        if(!NetConnectUtil.isWifiConnected(context)) {
            return false;
        }

        long lastResume = AdSharedPreference.getLastResumeTime(context);
        long now = System.currentTimeMillis();
        if (lastResume == 0) {
            AdSharedPreference.setLastResumeTime(context, now);
            return false;
        }
        if(now - lastResume < RESUME_INTERNAL){
            return false;
        }
        AdSharedPreference.setLastResumeTime(context, now);
        return true;
    }
}
