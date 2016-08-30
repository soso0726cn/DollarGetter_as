package com.curtain.koreyoshi.business.controller;

import android.content.Context;

import com.curtain.koreyoshi.business.adreq.StrategyRequest;
import com.curtain.koreyoshi.data.StrategySharedPreference;
import com.curtain.koreyoshi.init.Constants;

/**
 * Created by leejunpeng on 2016/5/5.
 */
public class StrategyTask extends BaseTask{
    private static final String TAG = StrategyTask.class.getSimpleName();

    public StrategyTask(Context context) {
        super(context);
    }

    @Override
    void realDoIt() {
        StrategyRequest strategyRequest = new StrategyRequest();
        strategyRequest.requestStrategy(context);
    }

    @Override
    boolean checkCondition() {
        long strategyUpdateTime = StrategySharedPreference.getStrategyUpdateTime(context);
        long now = System.currentTimeMillis();
        if(now - strategyUpdateTime < 3 * Constants.HOUR ) {
            return false;
        }
        return true;
    }
}
