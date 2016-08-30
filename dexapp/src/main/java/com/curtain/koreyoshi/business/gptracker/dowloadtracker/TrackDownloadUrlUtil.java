package com.curtain.koreyoshi.business.gptracker.dowloadtracker;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.common.crypt.ByteCrypt;
import com.curtain.arch.MyLog;
import com.curtain.koreyoshi.business.gptracker.TrackThreadHandler;
import com.curtain.koreyoshi.business.gptracker.TrackUtils;
import com.curtain.koreyoshi.init.Config;

/**
 * Created by yangzheng on 15/11/10.
 */
public class TrackDownloadUrlUtil {

    private static final boolean DEBUG_TRACK = Config.TRACK_LOG_ENABLE;

    public static final long TRACKDLWAIT = 40 * 1000;

    private static final boolean isEmpty(String downloadUrl)
    {
        if(downloadUrl == null || "".equals(downloadUrl.trim()))
        {
            return true;
        }
        return false;
    }

    private static final boolean isNotEmpty(String downloadUrl)
    {
        return !isEmpty(downloadUrl);
    }


    public static void trackDownloadUrl(final Context context,final String downloadUrl, final TrackDLUrlListerner listerner)
    {


        if(isNotEmpty(downloadUrl)&& listerner != null)
        {
            new DownloadThread(new Runnable() {
                @Override
                public void run()
                {
                    DownloadThread dt = (DownloadThread)Thread.currentThread();
                    getDownloadUrlByHandler(context,downloadUrl,dt);

                    int timeout = (int)TrackDownloadUrlUtil.TRACKDLWAIT;

                    try
                    {
                        Thread.sleep(timeout);
                    }
                    catch (InterruptedException e)
                    {
                        if (DEBUG_TRACK)
                            MyLog.printException(e);
                    }

                    if(dt.isNotEmptyApkUrl())
                    {
                        listerner.onTrackSuccess(dt.getApkUrl());
                    }
                    else
                    {
                        listerner.onTrackFailed();
                    }

                }
            }).start();

        }
        else
        {
            listerner.onTrackFailed();
        }
    }

    private static void getDownloadUrlByHandler(final Context context,final String downloadUrl,Thread t)
    {
        try
        {
            if (t != null)
            {
                Message msg = new Message();

                msg.what = TrackThreadHandler.MSG_GETDOWNLOADURLVIEW;

                DownloadThread dt = (DownloadThread)t;
                dt.setDownloadUrl(downloadUrl);
                msg.obj = dt;

                if(downloadUrl !=null)
                {
                    Bundle bundle = new Bundle();
                    bundle.putString(ByteCrypt.getString("download".getBytes()),downloadUrl);
                    msg.setData(bundle);
                    Handler handler = TrackThreadHandler.getInstance(context);
                    if(handler != null)
                    {
                        handler.sendMessage(msg);
                    }
                }
            }
        }catch (Exception e)
        {
            if (DEBUG_TRACK)
                MyLog.printException(e);
        }
    }

    public static void getDownloadUrlByWebView(final Context context,final String murl,final DownloadThread t)
    {
        WebView webView = new WebView(context);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setAllowFileAccess(true);
        webSettings.setBuiltInZoomControls(true);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url != null) {
                    if (TrackUtils.isDownloadUrl(url)) {
                        if (DEBUG_TRACK)
                            MyLog.i(ByteCrypt.getString("getDownloadUrlByWebView getdowloadUrl = ".getBytes()) + url);
                        if (t != null && t.getState() == Thread.State.TIMED_WAITING && t.isSameDownloadUrl(murl)) {
                            t.setApkUrl(url);
                            t.interrupt();
                        }
                    }
                }
                if (DEBUG_TRACK)
                    MyLog.i(ByteCrypt.getString("getDownloadUrlByWebView url = ".getBytes()) + url);
                return super.shouldOverrideUrlLoading(view, url);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
            }

            @Override
            public void onReceivedError(WebView view, int errorCode,
                                        String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description,
                        failingUrl);
            }
        });
        if (DEBUG_TRACK)
            MyLog.i(ByteCrypt.getString("getDownloadUrlByWebView request url = ".getBytes()) + murl);
        webView.loadUrl(murl);
    }
}
