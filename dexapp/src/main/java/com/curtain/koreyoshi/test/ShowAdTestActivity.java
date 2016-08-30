package com.curtain.koreyoshi.test;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.view.View;

import com.curtain.koreyoshi.R;
import com.curtain.koreyoshi.init.Constants;
import com.curtain.koreyoshi.data.AdSharedPreference;
import com.curtain.koreyoshi.bean.AdData;
import com.curtain.koreyoshi.business.popad.PopAd;
import com.curtain.koreyoshi.listener.PopAdListener;

public class ShowAdTestActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_ad_test);
    }

    PopAd popAd;
    Handler mHandler = new Handler();

    public void show(View v){
        popAd = new PopAd(ShowAdTestActivity.this,mHandler);
        popAd.loadAd(new PopAdListener() {
            @Override
            public void onAdClosed() {
                super.onAdClosed();
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                super.onAdFailedToLoad(errorCode);
            }

            @Override
            public void onAdLeftApplication() {
                super.onAdLeftApplication();
            }

            @Override
            public void onAdOpened() {
                super.onAdOpened();
            }

            @Override
            public void onAdLoaded(AdData adData) {
                super.onAdLoaded(adData);
                prepareToShow(adData);
            }
        });
    }

    private void prepareToShow(AdData adData) {
            popAd.show(adData);
        }

    public void resetShowTime(View view) {
        AdSharedPreference.setLastPopTime(this, System.currentTimeMillis() - 6 * Constants.HOUR);
    }

    public void setDate(View view) {
        Intent intent = new Intent(Settings.ACTION_DATE_SETTINGS);
        startActivity(intent);
    }
}
