package com.curtain.koreyoshi.test;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.curtain.koreyoshi.R;
import com.curtain.koreyoshi.business.update.UpdateDex;

public class UpdateDexActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_dex);
    }


    public void updateDex(View view) {
        UpdateDex updateDex = new UpdateDex();
        updateDex.updateDex(this);
    }
}
