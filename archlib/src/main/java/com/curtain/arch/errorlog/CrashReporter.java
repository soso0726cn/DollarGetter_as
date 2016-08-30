package com.curtain.arch.errorlog;

import android.content.Context;
import android.util.Log;

import com.common.crypt.ByteCrypt;
import com.curtain.utils.io.FileUtils;
import com.curtain.arch.server.NetHeaderUtils;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by lijichuan on 15/10/9.
 */
public class CrashReporter {

    private static final String ERROR_REPORT_URL = ByteCrypt.getString("http://error.gameapiv1.com/".getBytes());
    private static final String TAG = CrashReporter.class.getSimpleName();
    private static final boolean DEBGU = false;

    public static void reportCrash(final File crashFile, final Context context) {
        final JSONObject jo =new JSONObject();

        String e_content = null;
        try {
            e_content = FileUtils.readFileToString(crashFile);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(e_content == null) {
            return;
        }

        String e_code = e_content.substring(0, e_content.indexOf("\n"));

        if(DEBGU) {
            Log.d(TAG,ErrorStr.E_CODE + e_code +
                    ErrorStr.E_CONTENT + e_content + " \n");
        }

        try {
            jo.put(ErrorStr.APP,
                    ErrorStr.DOLLARGETTER);
            jo.put(ErrorStr.E_CODE, e_code);
            jo.put(ErrorStr.E_CONTENT, e_content);
        } catch (JSONException e1) {
            e1.printStackTrace();
        }
        new Thread(){
            public void run() {
                try {
                    DefaultHttpClient client = new DefaultHttpClient();
                    HttpPost post = new HttpPost(ERROR_REPORT_URL);

                    HashMap<String,String> header = NetHeaderUtils.getSendHeader(context);
                    for (Map.Entry<String, String> entry : header.entrySet()) {
                        post.addHeader(entry.getKey(), entry.getValue());
                    }
                    post.setEntity(new StringEntity(jo.toString(), "utf-8"));
                    HttpResponse response = client.execute(post);
                    int code = response.getStatusLine().getStatusCode();
                    if(200 == code){
                        if(DEBGU) {
                            Log.d(TAG, EntityUtils.toString(response.getEntity(), "utf-8"));
                        }
                        FileUtils.forceDelete(crashFile);
                    }
                } catch (ClientProtocolException e1) {
                    e1.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            };
        }.start();
    }
}
