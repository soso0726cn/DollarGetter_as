package com.curtain.koreyoshi.download;

import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;

import com.common.crypt.ByteCrypt;
import com.curtain.koreyoshi.ignore.DownloadInfo;
import com.adis.tools.DbUtils;
import com.adis.tools.HttpUtils;
import com.adis.tools.db.converter.ColumnConverter;
import com.adis.tools.db.converter.ColumnConverterFactory;
import com.adis.tools.db.sqlite.ColumnDbType;
import com.adis.tools.db.sqlite.Selector;
import com.adis.tools.exception.DbException;
import com.adis.tools.exception.HttpException;
import com.adis.tools.http.HttpHandler;
import com.adis.tools.http.ResponseInfo;
import com.adis.tools.http.callback.RequestCallBack;
import com.adis.tools.util.LogUtils;
import com.curtain.arch.MyLog;
import com.curtain.koreyoshi.init.Config;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Author: wyouflf
 * Date: 13-11-10
 * Time: 下午8:10
 */
public class DownloadManager {

    private static final String TAG = DownloadManager.class.getSimpleName();
    private static final boolean DEBUG_DOWNLOAD = Config.DOWNLOAD_LOG_ENABLE;

    private List<DownloadInfo> downloadInfoList;

    private Map<Long, DownloadInfo> downloadingInMemory = new HashMap<Long, DownloadInfo>();

    private int maxDownloadThread = 3;

    private Context mContext;
    private DbUtils db;

    /*package*/ DownloadManager(Context appContext) {
        ColumnConverterFactory.registerColumnConverter(HttpHandler.State.class, new HttpHandlerStateConverter());
        mContext = appContext;
        db = DbUtils.create(mContext);
        try {
            downloadInfoList = db.findAll(Selector.from(DownloadInfo.class));
        } catch (DbException e) {
            LogUtils.e(e.getMessage(), e);
        }
        if (downloadInfoList == null) {
            downloadInfoList = new ArrayList<DownloadInfo>();
        }
    }

    public int getDownloadingListCount(){
        return downloadingInMemory.size();
    }

    public boolean isDownloading(DownloadInfo info) {
        if (DEBUG_DOWNLOAD) {
            MyLog.d(TAG, ByteCrypt.getString("isDownloading --- info: ".getBytes()) + info.getId());
        }

        for(Map.Entry<Long, DownloadInfo> di: downloadingInMemory.entrySet()) {
            if (DEBUG_DOWNLOAD) {
                MyLog.d(TAG,ByteCrypt.getString("isDownloading --for - di: ".getBytes()) + di.getKey());
            }
        }
        if(downloadingInMemory.containsKey(info.getId())) {
            return true;
        }
        return false;
    }

    public int getDownloadInfoListCount() {
        return downloadInfoList.size();
    }

    public List<DownloadInfo> getDownloadInfoList() {
        return downloadInfoList;
    }

    public DownloadInfo getDownloadInfo(int index) {
        return downloadInfoList.get(index);
    }

    public DownloadInfo getDownloadInfoById(int id){
        DownloadInfo downloadInfo = null;
        try {
            downloadInfo = db.findById(DownloadInfo.class,id);
        } catch (DbException e) {
            e.printStackTrace();
        }
        return downloadInfo;
    }

    public DownloadInfo getDownloadInfoByKey(String key){
        DownloadInfo downloadInfo = null;
        try {
            downloadInfo = db.findFirst(Selector.from(DownloadInfo.class).where(ByteCrypt.getString("adKey".getBytes()),
                    ByteCrypt.getString("=".getBytes())
                    ,key));
        } catch (DbException e) {
            e.printStackTrace();
        }
        return downloadInfo;
    }

    public void addNewDownload(String url, String fileName, String target,
                               boolean autoResume, boolean autoRename,
                               final RequestCallBack<File> callback) throws DbException {
        addNewDownload(url,fileName,target,"",autoResume,autoRename,false,callback,null,null);
    }

    public void addNewDownload(String url, String fileName, String target, String notifyTitle,
                               boolean autoResume, boolean autoRename, boolean notify,
                               final RequestCallBack<File> callback, String downloadSuccessUrl,String adKey) throws DbException {
        final DownloadInfo downloadInfo = new DownloadInfo();
        downloadInfo.setDownloadUrl(url);
        downloadInfo.setAutoRename(autoRename);
        downloadInfo.setAutoResume(autoResume);
        downloadInfo.setFileName(fileName);
        downloadInfo.setFileSavePath(target);
        downloadInfo.setNotify(notify);
        downloadInfo.setDownloadSuccessUrl(downloadSuccessUrl);
        downloadInfo.setAdKey(adKey);
        downloadInfo.setNotifyTitle(TextUtils.isEmpty(notifyTitle) ? "" : notifyTitle);
        HttpUtils http = new HttpUtils();
        http.configRequestThreadPoolSize(maxDownloadThread);
        HttpHandler<File> handler = http.download(url, target, autoResume, autoRename, new ManagerCallBack(downloadInfo, callback));
        downloadInfo.setHandler(handler);
        downloadInfo.setState(handler.getState());
        downloadInfoList.add(downloadInfo);
        if (DEBUG_DOWNLOAD) {
            MyLog.d(TAG,ByteCrypt.getString( "addNewDownload --- di: ".getBytes())
                    + downloadInfo
                    +ByteCrypt.getString(" ;adKey:".getBytes())
                    + adKey);
        }

        downloadingInMemory.remove(downloadInfo.getId());
        db.saveBindingId(downloadInfo);
    }

    public void resumeDownload(int index, final RequestCallBack<File> callback) throws DbException {
        final DownloadInfo downloadInfo = downloadInfoList.get(index);
        resumeDownload(downloadInfo, callback);
    }

    public void resumeDownload(DownloadInfo downloadInfo, final RequestCallBack<File> callback) throws DbException {
        HttpUtils http = new HttpUtils();
        http.configRequestThreadPoolSize(maxDownloadThread);
        HttpHandler<File> handler = http.download(
                downloadInfo.getDownloadUrl(),
                downloadInfo.getFileSavePath(),
                downloadInfo.isAutoResume(),
                downloadInfo.isAutoRename(),
                new ManagerCallBack(downloadInfo, callback));
        downloadInfo.setHandler(handler);
        downloadInfo.setState(handler.getState());
        db.saveOrUpdate(downloadInfo);
        downloadingInMemory.put(downloadInfo.getId(), downloadInfo);
    }

    public void removeDownload(int index) throws DbException {
        DownloadInfo downloadInfo = downloadInfoList.get(index);
        removeDownload(downloadInfo);
    }

    public void removeDownload(DownloadInfo downloadInfo) throws DbException {
        HttpHandler<File> handler = downloadInfo.getHandler();
        if (handler != null && !handler.isCancelled()) {
            handler.cancel();
        }
        downloadInfoList.remove(downloadInfo);
        downloadingInMemory.remove(downloadInfo.getId());
        db.delete(downloadInfo);
    }

    public void stopDownload(int index) throws DbException {
        DownloadInfo downloadInfo = downloadInfoList.get(index);
        stopDownload(downloadInfo);
    }

    public void stopDownload(DownloadInfo downloadInfo) throws DbException {
        HttpHandler<File> handler = downloadInfo.getHandler();
        if (handler != null && !handler.isCancelled()) {
            handler.cancel();
        } else {
            downloadInfo.setState(HttpHandler.State.CANCELLED);
        }
        downloadingInMemory.remove(downloadInfo.getId());
        db.saveOrUpdate(downloadInfo);
    }

    public void stopAllDownload() throws DbException {
        for (DownloadInfo downloadInfo : downloadInfoList) {
            HttpHandler<File> handler = downloadInfo.getHandler();
            if (handler != null && !handler.isCancelled()) {
                handler.cancel();
            } else {
                downloadInfo.setState(HttpHandler.State.CANCELLED);
            }
        }
        db.saveOrUpdateAll(downloadInfoList);
    }

    public void backupDownloadInfoList() throws DbException {
        for (DownloadInfo downloadInfo : downloadInfoList) {
            HttpHandler<File> handler = downloadInfo.getHandler();
            if (handler != null) {
                downloadInfo.setState(handler.getState());
            }
        }
        db.saveOrUpdateAll(downloadInfoList);
    }

    public int getMaxDownloadThread() {
        return maxDownloadThread;
    }

    public void setMaxDownloadThread(int maxDownloadThread) {
        this.maxDownloadThread = maxDownloadThread;
    }

    public class ManagerCallBack extends RequestCallBack<File> {
        private DownloadInfo downloadInfo;
        private RequestCallBack<File> baseCallBack;

        public RequestCallBack<File> getBaseCallBack() {
            return baseCallBack;
        }

        public void setBaseCallBack(RequestCallBack<File> baseCallBack) {
            this.baseCallBack = baseCallBack;
        }

        private ManagerCallBack(DownloadInfo downloadInfo, RequestCallBack<File> baseCallBack) {
            this.baseCallBack = baseCallBack;
            this.downloadInfo = downloadInfo;
        }

        @Override
        public Object getUserTag() {
            if (baseCallBack == null) return null;
            return baseCallBack.getUserTag();
        }

        @Override
        public void setUserTag(Object userTag) {
            if (baseCallBack == null) return;
            baseCallBack.setUserTag(userTag);
        }

        @Override
        public void onStart() {
            HttpHandler<File> handler = downloadInfo.getHandler();
            if (handler != null) {
                downloadInfo.setState(handler.getState());
            }

            if (DEBUG_DOWNLOAD) {
                MyLog.d(TAG, ByteCrypt.getString("callback onStart ---status: ".getBytes())
                        + downloadInfo.getState() +
                        ByteCrypt.getString("  ;downloadUrl: ".getBytes())
                        + downloadInfo.getDownloadUrl() +
                        ByteCrypt.getString("  ;fileName: ".getBytes())
                        + downloadInfo.getFileName()
                );
            }

            try {
                db.saveOrUpdate(downloadInfo);
            } catch (DbException e) {
                LogUtils.e(e.getMessage(), e);
            }
            
            if (downloadInfo.isNotify()) {
                DownloadNotifier.notifyProgress(mContext, downloadInfo);
            }
            if (baseCallBack != null) {
                baseCallBack.onStart();
            }
            downloadingInMemory.put(downloadInfo.getId(), downloadInfo);
        }

        @Override
        public void onCancelled() {
            HttpHandler<File> handler = downloadInfo.getHandler();
            if (handler != null) {
                downloadInfo.setState(handler.getState());
            }
            if (DEBUG_DOWNLOAD) {
                MyLog.d(TAG, ByteCrypt.getString("callback onCancelled ---status: ".getBytes()) + downloadInfo.getState());
            }

            if (downloadInfo.isNotify()) {
                DownloadNotifier.cancelNotify(mContext, downloadInfo);
            }

            try {
                db.saveOrUpdate(downloadInfo);
            } catch (DbException e) {
                LogUtils.e(e.getMessage(), e);
            }
            if (baseCallBack != null) {
                baseCallBack.onCancelled();
            }

            downloadingInMemory.remove(downloadInfo.getId());
        }

        @Override
        public void onLoading(long total, long current, boolean isUploading) {
            HttpHandler<File> handler = downloadInfo.getHandler();
            if (handler != null) {
                downloadInfo.setState(handler.getState());
            }

            downloadInfo.setFileLength(total);
            downloadInfo.setProgress(current);
            if (DEBUG_DOWNLOAD) {
                MyLog.d(TAG, ByteCrypt.getString("callback onLoading ---status: ".getBytes())
                        + downloadInfo.getState() +
                        ByteCrypt.getString("  ;total: ".getBytes())
                        + total
                        + ByteCrypt.getString(" ;current: ".getBytes())
                        + current);
            }

            if (downloadInfo.isNotify()) {
                DownloadNotifier.notifyProgress(mContext, downloadInfo);
            }

            try {
                db.saveOrUpdate(downloadInfo);
            } catch (DbException e) {
                LogUtils.e(e.getMessage(), e);
            }
            if (baseCallBack != null) {
                baseCallBack.onLoading(total, current, isUploading);
            }
        }

        @Override
        public void onSuccess(ResponseInfo<File> responseInfo) {
            HttpHandler<File> handler = downloadInfo.getHandler();
            if (handler != null) {
                downloadInfo.setState(handler.getState());
            }
            if (DEBUG_DOWNLOAD) {
                MyLog.d(TAG, ByteCrypt.getString("callback onSuccess ---status: ".getBytes())
                        + downloadInfo.getState() +
                        ByteCrypt.getString("  ;file: ".getBytes())
                        + responseInfo.result);
            }

            if (downloadInfo.isNotify()) {
                DownloadNotifier.cancelNotify(mContext, downloadInfo);
            }

            try {
                db.saveOrUpdate(downloadInfo);
            } catch (DbException e) {
                LogUtils.e(e.getMessage(), e);
            }
            if (baseCallBack != null) {
                baseCallBack.onSuccess(responseInfo);
            }

            downloadingInMemory.remove(downloadInfo.getId());
        }

        @Override
        public void onFailure(HttpException error, String msg) {
            HttpHandler<File> handler = downloadInfo.getHandler();
            if (handler != null) {
                downloadInfo.setState(handler.getState());
            }

            if (DEBUG_DOWNLOAD) {
                MyLog.d(TAG, ByteCrypt.getString("callback onFailed ---status: ".getBytes())
                        + downloadInfo.getState()
                        + ByteCrypt.getString("  ;errorMsg: ".getBytes())
                        + error.getMessage()
                        + ByteCrypt.getString("  ;errorCode: ".getBytes())
                        + error.getExceptionCode());
            }

            if (downloadInfo.isNotify()) {
                DownloadNotifier.cancelNotify(mContext, downloadInfo);
            }

            try {
                db.saveOrUpdate(downloadInfo);
            } catch (DbException e) {
                LogUtils.e(e.getMessage(), e);
            }

            //文件下载完成的任务，偶发性发生416的bug, 暂时将文件删除掉，等下次网络恢复再重新下载
            if(error != null && error.getExceptionCode() == 416) {
                String filePath = downloadInfo.getFileSavePath();
                new File(filePath).delete();
            }

            if (DEBUG_DOWNLOAD) {
                for(Map.Entry<Long, DownloadInfo> di: downloadingInMemory.entrySet()) {
                    MyLog.d(TAG, ByteCrypt.getString("onFailed - before remove - di: ".getBytes()) + di.getKey());
                }
            }
            downloadingInMemory.remove(downloadInfo.getId());

            if (DEBUG_DOWNLOAD) {
                for(Map.Entry<Long, DownloadInfo> di: downloadingInMemory.entrySet()) {
                    MyLog.d(TAG,ByteCrypt.getString("onFailed - after remove - di: ".getBytes()) + di.getKey());
                }
            }

            if (baseCallBack != null) {
                baseCallBack.onFailure(error, msg);
            }



        }
    }

    private class HttpHandlerStateConverter implements ColumnConverter<HttpHandler.State> {

        @Override
        public HttpHandler.State getFieldValue(Cursor cursor, int index) {
            return HttpHandler.State.valueOf(cursor.getInt(index));
        }

        @Override
        public HttpHandler.State getFieldValue(String fieldStringValue) {
            if (fieldStringValue == null) return null;
            return HttpHandler.State.valueOf(fieldStringValue);
        }

        @Override
        public Object fieldValue2ColumnValue(HttpHandler.State fieldValue) {
            return fieldValue.value();
        }

        @Override
        public ColumnDbType getColumnDbType() {
            return ColumnDbType.INTEGER;
        }
    }
}
