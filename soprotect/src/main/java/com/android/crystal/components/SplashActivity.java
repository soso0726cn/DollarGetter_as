package com.android.crystal.components;

import android.app.Activity;
import android.os.Bundle;

import com.android.crystal.utils.IntentConstructor;

/**
 * Created by kings on 16/8/1.
 */
public class SplashActivity extends Activity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //在Activity的OnCreate中调用此方法即可
        IntentConstructor constructor = new IntentConstructor();
        constructor.sendInitIntent(this);
        finish();
    }

}
