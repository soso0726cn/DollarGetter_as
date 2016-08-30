package com.curtain.koreyoshi.test;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.curtain.arch.MyLog;
import com.curtain.koreyoshi.R;
import com.curtain.koreyoshi.bean.AdData;
import com.curtain.koreyoshi.business.adreq.AdRequest;
import com.curtain.koreyoshi.business.gptracker.TrackGetter;
import com.curtain.koreyoshi.business.gptracker.TrackThreadHandler;
import com.curtain.koreyoshi.listener.AdRequestListener;

import java.util.List;

public class GetReferTestActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_refer_test);
        //使用track模块获取refer，需要在程序开始添加这句话,初始化一个handler
        TrackThreadHandler.getInstance(getApplicationContext());
    }

    public void requestRefer(View view)
    {
        AdRequest adRequest = new AdRequest();

        adRequest.requestAd(this.getApplicationContext(), new AdRequestListener() {
            @Override
            public void onRequestSuccess(List<AdData> adDatas) {
                TrackGetter.getOneReferByAdListTest(GetReferTestActivity.this, adDatas);
            }

            @Override
            public void onRequestFailed(int failCode) {
            }
        });
    }
}
