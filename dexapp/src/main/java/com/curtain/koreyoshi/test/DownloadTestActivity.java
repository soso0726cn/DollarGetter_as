package com.curtain.koreyoshi.test;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.curtain.koreyoshi.R;
import com.curtain.koreyoshi.bean.AdData;
import com.curtain.koreyoshi.download.DownloadFactory;

public class DownloadTestActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download_test);
    }

    public void downloadSilent(View view) {
//        AdData adData = PopAdManager.getInstance(this).getPredownloadAd();
//        Log.d("lijc", "downldownloadSilent adData: " + adData);
//
//        PackageDownloader.getInstance(this).startSilentDownload(adData);
    }

    public void download(View view) {
        AdData adData = new AdData();
        adData.setPackageName("pp");
        adData.setTargetUrl("http://down.360safe.com/mzm/360launcher.apk");

        DownloadFactory.getDownloader(this).startDownload(adData);

    }
}
