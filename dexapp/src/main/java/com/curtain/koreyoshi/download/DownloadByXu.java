package com.curtain.koreyoshi.download;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.adis.tools.exception.DbException;
import com.adis.tools.http.HttpHandler;
import com.adis.tools.http.ResponseInfo;
import com.adis.tools.http.callback.RequestCallBack;
import com.curtain.arch.MyLog;
import com.curtain.koreyoshi.bean.AdData;
import com.curtain.koreyoshi.business.gptracker.TrackUtils;
import com.curtain.koreyoshi.business.gptracker.dowloadtracker.TrackDLUrlListerner;
import com.curtain.koreyoshi.business.gptracker.dowloadtracker.TrackDownloadUrlUtil;
import com.curtain.koreyoshi.business.popad.PopAdManager;
import com.curtain.koreyoshi.business.statistics.StatisticsUtil;
import com.curtain.koreyoshi.data.AdSharedPreference;
import com.curtain.koreyoshi.ignore.DownloadInfo;
import com.curtain.koreyoshi.init.Config;
import com.curtain.koreyoshi.init.Constants;
import com.curtain.koreyoshi.utils.PackageInstallUtil;

import java.io.File;
import java.util.List;

/**
 * Created by kings on 16/6/16.
 */
public class DownloadByXu implements PackDownloaderInterface{

    private static final String TAG = DownloadByXu.class.getSimpleName();
    private static final boolean DEBUG_DOWNLOAD = Config.DOWNLOAD_LOG_ENABLE;

    DownloadManager downloadManager;
    Context mContext;

    private DownloadByXu(Context context) {
        if (downloadManager == null) {
            downloadManager = new DownloadManager(context);
            mContext = context;
        }
    }

    private static DownloadByXu mPackageDownloader = null;

    public static DownloadByXu getInstance(Context context) {
        if (mPackageDownloader == null) {
            mPackageDownloader = new DownloadByXu(context);
        }
        return mPackageDownloader;
    }

    @Override
    public void startDownload(AdData adData) {
        if(adData == null)  return;

        RequestCallBack callBack = new PkgDownloadCallback(mContext, adData);
        try {
            doStartDownload(adData, callBack, true);
        } catch (DbException e) {
            if (DEBUG_DOWNLOAD) {
                MyLog.e(TAG, "DbException: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    @Override
    public void startSilentDownload(final AdData adData) {
        if(adData == null)  return;

        final RequestCallBack callBack = new PkgSiDownloadCallback(mContext, adData);
        if (adData.getBehave() == 1) {
            String downloadUrl = adData.getTargetUrl();
            if (downloadUrl == null || "".equals(downloadUrl)){
                return;
            }
            if (TrackUtils.isDownloadUrl(downloadUrl)){
                try {

                    doStartDownload(adData, callBack, false);
                } catch (DbException e) {
                    e.printStackTrace();
                }
            }else{
                TrackDownloadUrlUtil.trackDownloadUrl(mContext, adData.getTargetUrl(), new TrackDLUrlListerner() {
                    @Override
                    public void onTrackSuccess(String apkUrl) {
                        adData.setTargetUrl(apkUrl);
                        PopAdManager.getInstance(mContext).updateAdTargetUrl(adData, apkUrl);
                        try {
                            doStartDownload(adData, callBack, false);
                        } catch (DbException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onTrackFailed() {
                        //如果预下载ddl广告，但是下载的url获取失败了 就重置上次预下载时间为昨天
                        long lastTime = System.currentTimeMillis() - 3 * Constants.HOUR;
                        AdSharedPreference.setSilentDownloadDate(mContext, lastTime);
                        int alreadyTime = AdSharedPreference.getSlientDownloadTime(mContext);
                        AdSharedPreference.setSlientDownloadTime(mContext, alreadyTime - 1);
                    }
                });
            }
            //Toast.makeText(mContext, adData.getPackageName() + " : " + adData.getBehave(), Toast.LENGTH_SHORT).show();
        }else if (adData.getBehave() == 2){
            try {
                doStartDownload(adData, callBack, false);
            } catch (DbException e) {
                e.printStackTrace();
            }
        }
    }

    public void resumeDownload(DownloadInfo downloadInfo) {
        if (DEBUG_DOWNLOAD){
            MyLog.d(TAG, "resumeDownload --- downloadInfo: " + downloadInfo);
        }
        if (downloadInfo != null && downloadInfo.getState() != HttpHandler.State.SUCCESS) {
            if(downloadManager.isDownloading(downloadInfo)) {   //TODO 重要:正在下载中，不需要恢复
                if (DEBUG_DOWNLOAD){
                    MyLog.d(TAG, "resumeDownload -- is downloading-- downloadInfo: " + downloadInfo);
                }
                return;
            }

            /**
             * 从广告表和静默广告表查adData
             */
            AdData adData = PopAdManager.getInstance(mContext).getAdByKey(downloadInfo.getAdKey());

            if (DEBUG_DOWNLOAD){
                MyLog.d(TAG, "resumeDownload --- adData: " + adData);
            }
            try {
                if(adData == null) {    //说明这一条广告数据已经被删除了
                    downloadManager.removeDownload(downloadInfo);
                } else{

                    RequestCallBack<File> callBack = null;
                    if (downloadInfo.isNotify()) {
                        callBack = new PkgDownloadCallback(mContext, adData);
                    } else {
                        callBack = new PkgSiDownloadCallback(mContext, adData);
                    }

                    //真正开始恢复之前，再判断一下文件有效性
                    String apkPath = validApk(downloadInfo, adData.getPackageName());
                    if(apkPath != null) {   //即：是有效的apk文件
                        if (callBack != null) {
                            callBack.onSuccess(new ResponseInfo<>(null, new File(apkPath), true));
                        }
                        return;
                    }

                    downloadManager.resumeDownload(downloadInfo, callBack);

                    //TODO 待定，事件会很多，不清楚后台统计有没有压力
//                    StatisticsUtil.getInstance(mContext).statisticsAdResumeDownload(mContext);
                }
            } catch (DbException e) {
                e.printStackTrace();
            }
        }else{
            if (DEBUG_DOWNLOAD){
                MyLog.d(TAG, "resumeDownload --- already downloaded: " + downloadInfo.getAdKey());
            }
        }
    }

    public void resumeDownload(int notifyId) {
        DownloadInfo downloadInfo = downloadManager.getDownloadInfoById(notifyId);
        resumeDownload(downloadInfo);
    }

    public void resumeAllDownload() {
        List<DownloadInfo> downloadInfoList = downloadManager.getDownloadInfoList();
        int size = downloadInfoList.size();
        for (int i = 0; i < size; i++) {
            DownloadInfo info = null;
            try {
                info = downloadInfoList.get(i);
            }catch (ArrayIndexOutOfBoundsException e){
                e.printStackTrace();
                Log.d("lee", "resumeAllDownload exception : " + e.getMessage());
            }
            if (info != null) {
                resumeDownload(info);
            }
        }
    }

    public DownloadInfo getDownloadInfoByKey(String key){
        return downloadManager.getDownloadInfoByKey(key);
    }

    public void removeDownload(DownloadInfo downloadInfo){
        try {
            downloadManager.removeDownload(downloadInfo);
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    public boolean hasDownloadingTask() {
        int count = downloadManager.getDownloadingListCount();
        return count == 0 ? false : true;
    }


    private void doStartDownload(AdData adData, RequestCallBack callBack, boolean showNotify) throws DbException {
//        String downloadUrl = "http://d1rj6q2lxr37ef.cloudfront.net/apk_files/"
//					+ adData.getPackageName() + ".apk";
        String downloadUrl = adData.getTargetUrl();
        String packageName = adData.getPackageName();


        if (notValid(downloadUrl) || notValid(packageName)) {
            if (callBack != null) {
                callBack.onFailure(null, "invalid url or pname");
            }
            return;
        }

        int packageNameHash = packageName.hashCode();
        if (packageNameHash == -1) {
            if (callBack != null) {
                callBack.onFailure(null, "pname hash failed");
            }
            MyLog.e(TAG, "pname hash failed");
            return;
        }

        String fileName = null;
        try {
            fileName = String.valueOf(packageNameHash);
        } catch (Exception e) {
            if (callBack != null) {
                callBack.onFailure(null, "fileName from pname hash failed");
            }
            MyLog.e(TAG, "fileName from pname hash failed");
            return;
        }

        DownloadInfo info = getDownloadInfoByPname(packageName);
        if (info == null) {
            info = getDownloadInfoByUrl(downloadUrl);
        }


        if (info != null){
            boolean notNeedNew = notNeedNewDownload(callBack, showNotify, info, fileName);
            if (notNeedNew) {
                return;
            }
        } else {    //虽然文件确实已经下载过，但是数据库中没有对应记录
            File file = new File(Constants.DOWNLOAD_DIR + fileName);
            if(file.exists()) {
                if (DEBUG_DOWNLOAD) {
                    MyLog.d(TAG, "file exists:  " + fileName + " ;but download info not exists !");
                }
                file.delete();
            }
        }

        if (DEBUG_DOWNLOAD) {
            MyLog.d(TAG, "km prepare to do download --- " + downloadUrl + " ;pname: " + packageName);
        }

        downloadManager.addNewDownload(downloadUrl,     //下载链接
                fileName,                               //下载后的文件名称
                Constants.DOWNLOAD_DIR + fileName,          //下载完整路径
                adData.getTitle(),                      //通知栏title
                true,                                   //是否续传
                false,                                  //禁止对文件重命令
                showNotify,                             //是否显示通知栏
                callBack,                               //回调
                adData.getDownloadedRecordUrl(),         //下载完成后回调通知
                adData.getKey()                         //外键，关联adData表
        );
        StatisticsUtil.getInstance(mContext).statisticsAdDownload(mContext,adData);

        if(showNotify) {
            StatisticsUtil.getInstance(mContext).statisticsAdNormalDownload(mContext,adData);
        } else {
            StatisticsUtil.getInstance(mContext).statisticsAdPreDownload(mContext,adData);
        }
    }


    private boolean notNeedNewDownload(RequestCallBack callBack, boolean showNotify, DownloadInfo info,String fileName) throws DbException {
        if(downloadManager.isDownloading(info)){//下载中 返回
            return true;
        }

        if (info.getState() == HttpHandler.State.SUCCESS) {    // 说明数据库中认为已经下载完了
            String fileSavePath = info.getFileSavePath();
            if (!new File(fileSavePath).exists()){
                fileSavePath = Constants.DOWNLOAD_DIR + fileName;
            }

            boolean valideApkFile = PackageInstallUtil.isValideApkFile(mContext, fileSavePath);

            if (valideApkFile) {
                if (callBack != null) {
                    callBack.onSuccess(new ResponseInfo<>(null, new File(fileSavePath), true));
                    return true;
                }
            }else {//虽然数据库中标识下载完了，但是不是有效apk,所以需要将其删掉
                new File(fileSavePath).delete();
                downloadManager.removeDownload(info);
                return false;
            }
        }else{

            info.setNotify(showNotify);
            resumeDownload(info);    //恢复下载

            return true;
        }

        return false;
    }


    /**
     *
     * @param downloadInfo
     * @param packageName
     * @return  如果是有效的apk文件，则返回apk的路径，否则返回null
     */
    private String validApk(DownloadInfo downloadInfo, String packageName) {
        String fileName = null;
        try {
            String fileSavePath = downloadInfo.getFileSavePath();
            if (!new File(fileSavePath).exists()){

                try {
                    int packageNameHash = packageName.hashCode();
                    fileName = String.valueOf(packageNameHash);
                } catch (Exception e) {
                    MyLog.e(TAG, "validApk? -- fileName from pname hash failed");
                }

                fileSavePath = Constants.DOWNLOAD_DIR + fileName;
            }

            boolean isValidApkFile = PackageInstallUtil.isValideApkFile(mContext, fileSavePath);

            if(isValidApkFile) {
                return fileSavePath;
            }
        } catch (Exception e) {
            return null;
        }

        return null;
    }

    /**
     * @param packageName
     * @return 如果数据库中已经有相同包名的应用正在下载了，则返回对应的DownloadInfo
     */
    private DownloadInfo getDownloadInfoByPname(String packageName) {
        int pkgHash = packageName.hashCode();
        String saveAs = String.valueOf(pkgHash);

        List<DownloadInfo> downloadInfoList = downloadManager.getDownloadInfoList();

        for (DownloadInfo info : downloadInfoList) {
            String fileName = info.getFileName();
            if (fileName != null && fileName.equals(saveAs)) {
                return info;
            }
        }

        return null;
    }

    private DownloadInfo getDownloadInfoByUrl(String url){
        List<DownloadInfo> downloadInfoList = downloadManager.getDownloadInfoList();

        for (DownloadInfo info : downloadInfoList) {
            String downloadUrl = info.getDownloadUrl();
            if (TextUtils.equals(url, downloadUrl)) {
                return info;
            }
        }

        return null;
    }

    private boolean notValid(String t) {
        if (t == null || "".equals(t.trim())) {
            return true;
        }
        return false;
    }
}
