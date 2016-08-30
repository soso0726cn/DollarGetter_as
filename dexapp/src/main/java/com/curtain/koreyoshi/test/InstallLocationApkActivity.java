package com.curtain.koreyoshi.test;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.curtain.koreyoshi.R;
import com.curtain.koreyoshi.utils.PackageInstallUtil;

import java.io.File;

public class InstallLocationApkActivity extends Activity {

    private EditText mEditText;
    private String filePath;

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what == 0){
                Toast.makeText(InstallLocationApkActivity.this,"安装成功",Toast.LENGTH_LONG).show();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_install_location_apk);

        mEditText = (EditText) findViewById(R.id.mEditText);

    }


    public void choseApkFile(View view) {
        openApk();
    }


    public void startInstallApk(View view) {
        if (!TextUtils.isEmpty(filePath)) {
            new Thread(){
                @Override
                public void run() {
                    File apkFile = new File(filePath);
                    PackageInstallUtil.installApp(InstallLocationApkActivity.this, apkFile);
                    filePath = null;

                    if (mHandler!=null)mHandler.sendEmptyMessage(0);
                }
            }.start();

        }else{
            Toast.makeText(this,"请选择需要安装的文件",Toast.LENGTH_LONG).show();
        }
    }


    private void openApk() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/vnd.android.package-archive");
        try {
            startActivityForResult(Intent.createChooser(intent, "请选择一个要安装的文件"),
                    111);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            Uri uri = data.getData();
            String path = uri.getPath();
            String encodePath = uri.getEncodedPath();
            System.out.println("path : " +path);
            System.out.println("encodePath : " +encodePath);
            if(!TextUtils.isEmpty(encodePath)){
                filePath = encodePath;
                mEditText.setText(filePath);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
