package com.curtain.koreyoshi.business.controller;

import android.content.Context;

import com.curtain.koreyoshi.business.adreq.NameListRequest;
import com.curtain.koreyoshi.data.StrategySharedPreference;
import com.curtain.koreyoshi.init.Constants;

/**
 * Created by leejunpeng on 2016/5/5.
 */
public class NameListTask extends BaseTask{
    private static final String TAG = NameListTask.class.getSimpleName();

    public NameListTask(Context context) {
        super(context);
    }

    @Override
    void realDoIt() {
        NameListRequest request = new NameListRequest();
        request.requestNameList(context);
    }

    @Override
    boolean checkCondition() {
        long lastTime = StrategySharedPreference.getLastRequestListTime(context);
        long now = System.currentTimeMillis();
        if (now - lastTime < Constants.DAY){
            return false;
        }
        return true;
    }
}
