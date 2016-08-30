package com.curtain.koreyoshi.test;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.curtain.koreyoshi.R;
import com.curtain.koreyoshi.bean.AdData;
import com.curtain.koreyoshi.business.adreq.AdRequest;
import com.curtain.koreyoshi.listener.AdRequestListener;
import com.curtain.koreyoshi.business.adreq.StrategyRequest;

import java.util.List;

public class RequestAdTestActivity extends Activity implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_ad_test);
        findViewById(R.id.ngp_test_strategy_btn).setOnClickListener(this);

    }

    public void requestAd(View view) {
        AdRequest adRequest = new AdRequest();

        adRequest.requestAd(RequestAdTestActivity.this, new AdRequestListener() {
            @Override
            public void onRequestSuccess(List<AdData> adDatas) {
                Log.d("lijc", "adRequest callback success -- data size: " + adDatas.size());
            }

            @Override
            public void onRequestFailed(int failCode) {

            }
        });
    }

    public void requestStrategy(View v){
        Log.i("cc","requestStrategy");
        StrategyRequest strategyRequest = new StrategyRequest();
        strategyRequest.requestStrategy(this);
    }

    @Override
    public void onClick(View view) {
        if(view.getId()==R.id.ngp_test_strategy_btn){
            requestStrategy(view);
        }
    }
}
