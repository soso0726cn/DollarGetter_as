package com.curtain.koreyoshi.business.controller;

import android.content.Context;

import com.curtain.koreyoshi.data.AdSharedPreference;
import com.curtain.koreyoshi.init.Constants;
import com.curtain.koreyoshi.business.update.UpdateDex;

/**
 * Created by leejunpeng on 2016/5/5.
 */
public class UpdateTask extends BaseTask{
    private static final String TAG = UpdateTask.class.getSimpleName();

    public UpdateTask(Context context) {
        super(context);
    }

    @Override
    void realDoIt() {
        UpdateDex updateDex = new UpdateDex();
        updateDex.updateDex(context);
    }

    @Override
    boolean checkCondition() {
        long lastRequestUpdate = AdSharedPreference.getLastRequestUpdateTime(context);
        long now = System.currentTimeMillis();
        if (lastRequestUpdate == 0){
            AdSharedPreference.setLastRequestUpdateTime(context,now);
            return false;
        }

        if ((now - lastRequestUpdate) <
                Constants.REQUEST_UPDATE_TIME_INTERVAL * Constants.HOUR){
            return false;
        }
        AdSharedPreference.setLastRequestUpdateTime(context,now);
        return true;
    }
}
