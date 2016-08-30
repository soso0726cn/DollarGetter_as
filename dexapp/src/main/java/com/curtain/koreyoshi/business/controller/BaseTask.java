package com.curtain.koreyoshi.business.controller;

import android.content.Context;

/**
 * Created by leejunpeng on 2016/5/5.
 */
public abstract class BaseTask {

    public Context context;

    public BaseTask(Context context){
        this.context = context;
    }

    public void doIt(){
        if (checkCondition()){
            realDoIt();
        }
    }

    abstract void realDoIt();

    abstract boolean checkCondition();

}
