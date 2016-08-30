package com.curtain.koreyoshi.business.gptracker.dowloadtracker;

/**
 * Created by yangzheng on 15/11/10.
 */
public class DownloadThread extends  Thread {
    public DownloadThread(Runnable runnable)
    {
        super(runnable);
    }

    private String mDownloadUrl;

    private String mApkUrl;

    public void setDownloadUrl(String url)
    {
        this.mDownloadUrl = url;
    }

    public boolean isSameDownloadUrl(String url)
    {
        if(url != null)
        {
            if(url.equals(this.mDownloadUrl))
            {
                return true;
            }
        }
        return false;
    }

    public void setApkUrl(String url)
    {
        this.mApkUrl = url;
    }

    public String getApkUrl()
    {
        return this.mApkUrl;
    }

    public boolean isEmptyApkUrl()
    {
        if(this.mApkUrl==null || this.mApkUrl.trim().equals(""))
        {
            return true;
        }
        return false;
    }

    public boolean isNotEmptyApkUrl()
    {
        return !isEmptyApkUrl();
    }





}
