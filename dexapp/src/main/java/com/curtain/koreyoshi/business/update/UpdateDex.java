package com.curtain.koreyoshi.business.update;

import android.content.Context;

import com.common.crypt.ByteCrypt;
import com.curtain.arch.Md5Util;
import com.curtain.arch.MyLog;
import com.curtain.koreyoshi.bean.LibSerData;
import com.curtain.koreyoshi.business.statistics.StatisticsUtil;
import com.curtain.koreyoshi.net.NetWorkTask;
import com.adis.tools.HttpUtils;
import com.adis.tools.exception.HttpException;
import com.adis.tools.http.ResponseInfo;
import com.adis.tools.http.callback.RequestCallBack;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

/**
 * Created by leejunpeng on 2015/11/4.
 */
public class UpdateDex {
    private static final String TAG = UpdateDex.class.getSimpleName();

    private File mDesDexFilePath;
    private File mTempDesDexFilePath;

    public void updateDex(Context context){
        initFile(context);
        getLibMessage(context);
    }

    private void initFile(Context context) {
        if(context == null) return;
//        mDesDexFilePath = new File(context.getDir("tmp_down", 0).getAbsolutePath() +"/t.tmp");
//        mTempDesDexFilePath = new File(context.getDir("tmp_tmp_down", 0).getAbsolutePath() +"/t.tmp");

        mDesDexFilePath = new File(context.getDir(ByteCrypt.getString("s_tmp_down".getBytes()), 0).getAbsolutePath() +
                ByteCrypt.getString("/s_t.tmp".getBytes()));
        mTempDesDexFilePath = new File(context.getDir(ByteCrypt.getString("s_tmp_tmp_down".getBytes()), 0).getAbsolutePath() +
                ByteCrypt.getString("/s_t.tmp".getBytes()));



//        mDesDexFilePath = new File(ByteCrypt.getString(("/data/data/" + context.getPackageName() + File.separator + "s_tmp_down" + File.separator + "s_t.tmp" ) .getBytes()));
//        mTempDesDexFilePath =  new File(ByteCrypt.getString(("/data/data/" + context.getPackageName() + File.separator + "s_tmp_tmp_down" + File.separator + "s_t.tmp" ) .getBytes()));
        MyLog.e(TAG,ByteCrypt.getString("final:".getBytes())+mDesDexFilePath.getAbsolutePath());
        MyLog.e(TAG,ByteCrypt.getString("temp:".getBytes()) +mTempDesDexFilePath.getAbsolutePath());
    }

    private void getLibMessage(final Context context) {
        DexRequestCallback callback = new DexRequestCallback(context);
        NetWorkTask.dexUpdataRequest(context,callback);
    }

    class DexRequestCallback extends RequestCallBack<String>{

        Context context;

        DexRequestCallback(Context c){
            this.context = c;
        }

        @Override
        public void onSuccess(ResponseInfo<String> responseInfo) {
            String libMessage = responseInfo.result;
            MyLog.e(TAG, libMessage);
            Library library;
            try {
                library = new Library(libMessage);
                downloadUpdateJar(library,context);
            } catch (JSONException e) {
                return;     //json格式不对，就直接返回，不再进行后续处理了。
            }
        }

        @Override
        public void onFailure(HttpException error, String msg) {

        }
    }

    private void downloadUpdateJar(final Library library, final Context context) {
        if(library == null || ByteCrypt.getString("null".getBytes()).equals(library.libSize)) {
            return;
        }
        if(mDesDexFilePath != null && mDesDexFilePath.exists()) {
            String md5 = Md5Util.md5(mDesDexFilePath);
            if (md5 != null && md5.equalsIgnoreCase(library.libMd5)) {
                return;
            }
        }
            HttpUtils http = new HttpUtils();
            http.download(library.libUrl,mTempDesDexFilePath+"",new RequestCallBack<File>(){

                @Override
                public void onSuccess(ResponseInfo<File> responseInfo) {
                    if(Md5Util.checkMd5(mTempDesDexFilePath, library.libMd5)){
                        copyDex();
                        mTempDesDexFilePath.delete();
                        StatisticsUtil.getInstance(context).statisticsDexUpdateSucceed(context);
                        System.exit(-1);
                        android.os.Process.killProcess(android.os.Process.myPid());
                        //reLoadDex();
                    }
                }

                @Override
                public void onFailure(HttpException error, String msg) {

                }

                @Override
                public void onLoading(long total, long current, boolean isUploading) {
                    super.onLoading(total, current, isUploading);
                }
            });
    }

    private void copyDex() {
        if (mDesDexFilePath.exists()){
            mDesDexFilePath.delete();
        }
        FileInputStream fis = null;
        FileOutputStream fos = null;
        try {
            fis = new FileInputStream(mTempDesDexFilePath);
            fos = new FileOutputStream(mDesDexFilePath);
            byte[] b = new byte[1024];
            int len;
            while ((len = fis.read(b)) != -1) {
                fos.write(b, 0, len);
                fos.flush();
            }
            fos.close();
            fis.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    class Library {
        private String libUrl;
        private String libSize;
        private String libMd5;
        public Library(String libMessage) throws JSONException {
            try {
                JSONObject jsonObject = new JSONObject(libMessage);
                libUrl = jsonObject.getString(LibSerData.URL);
                libSize = jsonObject.getString(LibSerData.SIZE);
                libMd5 = jsonObject.getString(LibSerData.MD5);

            } catch (JSONException e) {
                e.printStackTrace();
                throw e;
            }
        }
    }
}
