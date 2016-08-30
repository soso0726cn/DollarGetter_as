package com.curtain.koreyoshi.business.task;

import android.app.Application;
import android.content.Context;

import com.curtain.arch.errorlog.CrashHandler;
import com.curtain.koreyoshi.business.controller.UpdateTask;
import com.curtain.koreyoshi.business.gptracker.TrackThreadHandler;
import com.curtain.koreyoshi.init.AppFolder;
import com.curtain.koreyoshi.init.Config;
import com.curtain.koreyoshi.init.Constants;
import com.curtain.koreyoshi.utils.AdvertisingIdClient;

/**
 * Created by liumin on 2016/5/4.
 */
public class TaskManager {

    private static TaskManager instances = null;
    private Context mContext = null;


    private TaskManager(){}

    public static TaskManager getInstance(Context mContext){
        if(instances == null) instances = new TaskManager(mContext);
        return instances;
    }


    public void doBackgroundTask(){
        if(mContext == null) return;
        BackgroundTask.getInstance().doBackgroundTask(mContext);
    }

    public void doUpdateTask(){
        if(mContext == null) return;
        new UpdateTask(mContext).doIt();
    }

    public void doAdTask(){
        if(mContext == null) return;
        PopMainTask.getInstance(mContext).showCheck();
    }

    public void doAllTask(){
        if(mContext == null) return;
        PopMainTask.getInstance(mContext).showCheck();
        BackgroundTask.getInstance().doBackgroundTask(mContext);
        new UpdateTask(mContext).doIt();
    }

    public void doCreateTask(){
        if (mContext == null) return;
        CreateTask.getInstance().syncDownloadStatus(mContext);
    }

    private TaskManager(Context mContext){
        if(mContext == null) return;

        if(mContext instanceof Application) {
            this.mContext = mContext;
        } else {
            try {
                this.mContext = mContext.getApplicationContext();
            } catch (Exception e) {
                this.mContext = mContext;
            }
        }

        initTask();
    }

    private void initTask(){

        initTrack();

        if (!Config.DOWNLOAD_LOG_ENABLE) {
            CrashHandler.getInstance().init(mContext);
            CrashHandler.setCrashReportDir(Constants.ERROR_DIR);
        }

        AppFolder.initFiles();

        AdvertisingIdClient.getAdVertisingId(mContext);

        PopMainTask.getInstance(mContext).doAdTask();
    }


    private void initTrack(){
        if(mContext == null) return;
        TrackThreadHandler.getInstance(mContext);
    }
}
