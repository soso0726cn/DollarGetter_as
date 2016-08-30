package com.curtain.koreyoshi.ignore;

import com.common.crypt.ByteCrypt;
import com.adis.tools.db.annotation.Transient;
import com.adis.tools.http.HttpHandler;

import java.io.File;

/**
 * Author: wyouflf
 * Date: 13-11-10
 * Time: 下午8:11
 */
public class DownloadInfo {

    public DownloadInfo() {
    }

    private long id;

    @Transient
    private HttpHandler<File> handler;

    private HttpHandler.State state;

    private String downloadUrl;

    private String fileName;

    private String fileSavePath;

    private long progress;

    private long fileLength;

    private boolean autoResume;

    private boolean autoRename;

    private String downloadSuccessUrl;

    private boolean notify;

    private String notifyTitle;

    private String adKey;

    public String getAdKey() {
        return adKey;
    }

    public void setAdKey(String adKey) {
        this.adKey = adKey;
    }

    public String getDownloadSuccessUrl() {
        return downloadSuccessUrl;
    }

    public void setDownloadSuccessUrl(String downloadSuccessUrl) {
        this.downloadSuccessUrl = downloadSuccessUrl;
    }

    public boolean isNotify() {
        return notify;
    }

    public void setNotify(boolean notify) {
        this.notify = notify;
    }

    public String getNotifyTitle() {
        return notifyTitle;
    }

    public void setNotifyTitle(String notifyTitle) {
        this.notifyTitle = notifyTitle;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public HttpHandler<File> getHandler() {
        return handler;
    }

    public void setHandler(HttpHandler<File> handler) {
        this.handler = handler;
    }

    public HttpHandler.State getState() {
        return state;
    }

    public void setState(HttpHandler.State state) {
        this.state = state;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileSavePath() {
        return fileSavePath;
    }

    public void setFileSavePath(String fileSavePath) {
        this.fileSavePath = fileSavePath;
    }

    public long getProgress() {
        return progress;
    }

    public void setProgress(long progress) {
        this.progress = progress;
    }

    public long getFileLength() {
        return fileLength;
    }

    public void setFileLength(long fileLength) {
        this.fileLength = fileLength;
    }

    public boolean isAutoResume() {
        return autoResume;
    }

    public void setAutoResume(boolean autoResume) {
        this.autoResume = autoResume;
    }

    public boolean isAutoRename() {
        return autoRename;
    }

    public void setAutoRename(boolean autoRename) {
        this.autoRename = autoRename;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DownloadInfo)) return false;

        DownloadInfo that = (DownloadInfo) o;

        if (id != that.id) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return (int) (id ^ (id >>> 32));
    }

    @Override
    public String toString() {
        return ByteCrypt.getString("DownloadInfo{".getBytes()) +
                ByteCrypt.getString("downloadUrl='".getBytes()) + downloadUrl + '\'' +
                ByteCrypt.getString(", id=".getBytes()) + id +
                ByteCrypt.getString(", state=".getBytes()) + state +
                ByteCrypt.getString(", adKey='".getBytes()) + adKey + '\'' +
                ByteCrypt.getString(", notifyTitle='".getBytes()) + notifyTitle + '\'' +
                ByteCrypt.getString(", fileName='".getBytes()) + fileName + '\'' +
                ByteCrypt.getString(", fileSavePath='".getBytes()) + fileSavePath + '\'' +
                ByteCrypt.getString(", progress=".getBytes()) + progress +
                ByteCrypt.getString(", fileLength=".getBytes()) + fileLength +
                ByteCrypt.getString(", notify=".getBytes()) + notify +
                '}';
    }
}
