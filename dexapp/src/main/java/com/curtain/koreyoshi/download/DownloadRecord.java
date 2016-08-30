package com.curtain.koreyoshi.download;

/**
 * Created by kings on 16/6/14.
 */
public class DownloadRecord {



    /*
    _id : 1005
    local_filename : /storage/emulated/0/storage/emulated/0/Sprite/backup/1779912238-9
    mediaprovider_uri : null
    destination : 4
    title : 1779912238-9
    description : Downloading
    uri : http://gp.apiv7.com/apk/googleplay/com.ebay.mobile.apk?d=20160614
    status : 192
    hint : file:///storage/emulated/0/storage/emulated/0/Sprite/backup/1779912238
    media_type : application/vnd.android.package-archive
    total_size : 13625541
    last_modified_timestamp : 1435473034480
    bytes_so_far : 2253118
    local_uri : file:///storage/emulated/0/storage/emulated/0/Sprite/backup/1779912238-9
    reason : placeholder
    fileName : 1779912238
    downloadUrl : http://gp.apiv7.com/apk/googleplay/com.ebay.mobile.apk?d=20160614
    */


    private long _id;
    private String local_filename;
    private String title;
    private int status;
    private String downloadUrl;

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public long get_id() {
        return _id;
    }

    public void set_id(long _id) {
        this._id = _id;
    }

    public String getLocal_filename() {
        return local_filename;
    }

    public void setLocal_filename(String local_filename) {
        this.local_filename = local_filename;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }


    @Override
    public String toString() {
        return "DownloadRecord{" +
                "_id=" + _id +
                ", local_filename='" + local_filename + '\'' +
                ", title='" + title + '\'' +
                ", status=" + status +
                ", downloadUrl='" + downloadUrl + '\'' +
                '}';
    }
}
