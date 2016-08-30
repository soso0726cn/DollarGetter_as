package com.curtain.koreyoshi.business.task;

import android.content.Context;

import com.curtain.koreyoshi.bean.AdData;
import com.curtain.koreyoshi.business.gptracker.trackping.InstallTracker;
import com.curtain.koreyoshi.business.popad.PopAdManager;
import com.curtain.koreyoshi.download.DownloadBySy;
import com.curtain.koreyoshi.download.DownloadFactory;
import com.curtain.koreyoshi.download.DownloadRecord;
import com.curtain.koreyoshi.download.Downloads;
import com.curtain.koreyoshi.utils.PackageInstallUtil;

import java.util.List;

/**
 * Created by kings on 16/6/28.
 */
public class CreateTask {

    private static CreateTask mCreateTask;
    private CreateTask() {
    }

    public static CreateTask getInstance() {
        if(mCreateTask == null) {
            mCreateTask = new CreateTask();
        }
        return mCreateTask;
    }

    //同步下载状态
    public void syncDownloadStatus(Context context) {
        if (!DownloadFactory.isDownloadManagerAvailable(context)){
            return;
        }

        List<DownloadRecord> records = DownloadBySy.getInstance(context).queryDownloadRecords();
        for (DownloadRecord record : records){
            long id = record.get_id();
            AdData adData = PopAdManager.getInstance(context).getAdByDownloadId(id);
            if (adData != null){
                int statusInDownload = record.getStatus();
                int statusInAddata = adData.getStatus();
                if (Downloads.Impl.isStatusInformational(statusInDownload) && statusInAddata < AdData.STATUS_DOWNLOADING){
                    PopAdManager.getInstance(context).refreshAdStatus(adData,AdData.STATUS_DOWNLOADING);
                }else if (Downloads.Impl.isStatusSuccess(statusInDownload) && statusInAddata < AdData.STATUS_DOWNLOADED) {
                    String packageName = adData.getPackageName();
                    if (adData.getBehave() == 1){
                        String local_filename = record.getLocal_filename();
                        String ddlPname = PackageInstallUtil.getPname(context, local_filename);
                        if (!ddlPname.equals(packageName)){
                            PopAdManager.getInstance(context).updateAdPname(adData, ddlPname);
                            packageName = ddlPname;
                        }
                    }
                    boolean installFlags = PackageInstallUtil.getInstallFlags(context, packageName);
                    PopAdManager.getInstance(context).refreshAdStatus(adData,AdData.STATUS_DOWNLOADED);
                    if (installFlags) {
                        InstallTracker.getInstance().track(context, packageName);
                    }
                }
            }
        }
    }

}
