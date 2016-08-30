package com.curtain.koreyoshi.test;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.curtain.koreyoshi.R;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {

    private ListView mListView;

    private List<String> names = new ArrayList<String>();
    private List<Intent> intents = new ArrayList<Intent>();

    private void init(Context context) {
        Intent intent = new Intent(this, TestService.class);
        startService(intent);

        names.add("请求广告列表");
        names.add("获取refer");
        names.add("下载apk");
        names.add("显示广告");
        names.add("数据库");
        names.add("更新Dex");
        names.add("安装本地APK");


        Intent request = new Intent(context, RequestAdTestActivity.class);
        Intent getRefer = new Intent(context, GetReferTestActivity.class);
        Intent downloadApk = new Intent(context, DownloadTestActivity.class);
        Intent showAd = new Intent(context, ShowAdTestActivity.class);
        Intent dao = new Intent(context, DaoTestActivity.class);
        Intent dexupdate = new Intent(context, UpdateDexActivity.class);
        Intent installApk = new Intent(context, InstallLocationApkActivity.class);

        intents.add(request);
        intents.add(getRefer);
        intents.add(downloadApk);
        intents.add(showAd);
        intents.add(dao);
        intents.add(dexupdate);
        intents.add(installApk);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.i("wmm", "MainActivity onCreate");
        init(this);

        mListView = (ListView) findViewById(R.id.list);


        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_expandable_list_item_1);

        for (String s: names) {
            adapter.add(s);
        }

        mListView.setAdapter(adapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                MainActivity.this.startActivity(intents.get(i));
            }
        });


    }
}
