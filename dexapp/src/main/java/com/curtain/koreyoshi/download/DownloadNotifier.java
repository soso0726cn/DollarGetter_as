package com.curtain.koreyoshi.download;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.widget.RemoteViews;

import com.common.crypt.ByteCrypt;
import com.curtain.arch.MyLog;
import com.curtain.koreyoshi.ignore.DownloadInfo;
import com.curtain.koreyoshi.init.Config;
import com.curtain.koreyoshi.init.Constants;

import java.lang.reflect.Method;

/**
 * Created by lijichuan on 15/10/31.
 */
public class DownloadNotifier {

    public static final String NOTIFY_ID = ByteCrypt.getString("notify_id".getBytes());
    private static final boolean DEBUG_DOWNLOAD = Config.DOWNLOAD_LOG_ENABLE;

    private static final String TAG = DownloadNotifier.class.getSimpleName();

    private static NotificationManager mNotificationManager;
    private static Notification.Builder builder;

    private static NotificationManager getNotificationManager(Context context) {
        if (mNotificationManager == null) {
            mNotificationManager = (NotificationManager) context
                    .getSystemService(android.content.Context.NOTIFICATION_SERVICE);
        }
        return mNotificationManager;
    }

    private static Notification.Builder getNotificatinBuilder(
            Context context) {
        if (builder == null) {
            builder = new Notification.Builder(context);
        }
        return builder;
    }

    public static void cancelNotify(Context context, DownloadInfo downloadInfo) {
        int id = (int) downloadInfo.getId();
        if(DEBUG_DOWNLOAD) {
            MyLog.d(TAG, ByteCrypt.getString("cancelNotify --".getBytes())
                    + ByteCrypt.getString(" ;id: ".getBytes())
                    + id);

            StackTraceElement[] stackTrace = new Exception().getStackTrace();
            for(StackTraceElement e: stackTrace) {
                MyLog.d(TAG, e.getClassName() + " \t" + e.getMethodName() + " \t" + e.getLineNumber());
            }

        }

        getNotificationManager(context).cancel(id);
    }

    public static void notifyProgress(Context context, DownloadInfo downloadInfo) {
        Notification notify = null;

        int notifyId = (int)downloadInfo.getId();
        String notifyTitle = downloadInfo.getNotifyTitle();
        long total = downloadInfo.getFileLength();
        long current = downloadInfo.getProgress();
        int progress = total == 0 ? 0 : (int) (current * 100 / total);

        if(DEBUG_DOWNLOAD) {
            MyLog.d(TAG, ByteCrypt.getString("notifyProgress --".getBytes())
                    + ByteCrypt.getString(" ;id: ".getBytes())
                    + notifyId
                    + ByteCrypt.getString(" progress: ".getBytes())
                    + progress);
        }

		//TODO
        int icon = android.R.drawable.stat_sys_download;
        if(icon == 0){
            icon = android.R.drawable.stat_sys_download_done;
            if (icon == 0){
                icon = android.R.drawable.ic_media_ff;
                if (icon == 0) {
                    if (DEBUG_DOWNLOAD) {
                        MyLog.d(TAG, ByteCrypt.getString("resource icon id is 0, won't view the notification".getBytes()));
                    }
                    return;
                }
            }
        }
        Intent intent = new Intent(Constants.DOWNLOAD_NOTIFICATION_CLICKED);
        intent.putExtra(NOTIFY_ID,notifyId);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, notifyId, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.ICE_CREAM_SANDWICH){
            try{
                notify = buildNotificationHigher(context, downloadInfo, notifyTitle, progress, icon, pendingIntent);
            } catch(Throwable e){
                if(DEBUG_DOWNLOAD) {
                    MyLog.d(TAG, ByteCrypt.getString("buildNotificationHigher get error ===============================".getBytes()));
                    e.printStackTrace();

                }
                try{
                    notify = buildNotificationLower(context, downloadInfo, notifyTitle, progress, icon, pendingIntent);
                }catch (Throwable e1){
                    if(DEBUG_DOWNLOAD) {
                        MyLog.d(TAG, ByteCrypt.getString("buildNotificationLower=============== get error after buildNotificationHigher===============================".getBytes()));
                        e1.printStackTrace();

                    }
                }
            }

        } else {
            try{
                notify = buildNotificationLower(context, downloadInfo, notifyTitle, progress, icon, pendingIntent);
            }catch (Throwable e){
                if(DEBUG_DOWNLOAD) {
                    MyLog.d(TAG, ByteCrypt.getString("buildNotificationLower=============== get error ===============================".getBytes()));
                    e.printStackTrace();

                }
            }
        }

        if (notify == null) return;

        if(DEBUG_DOWNLOAD) {
            MyLog.d(TAG, ByteCrypt.getString("buildNotification is ok ,is very great ,god!!!!! ===============================notify: ".getBytes()) + notify);
        }
//        notify.flags = notify.flags|Notification.FLAG_AUTO_CANCEL;

        getNotificationManager(context).notify(notifyId, notify);
    }

    private static Notification buildNotificationLower(Context context, DownloadInfo downloadInfo, String notifyTitle, int progress, int icon, PendingIntent pendingIntent) {
        Notification notify;
        notify = new Notification();
//        notify.flags = Notification.DEFAULT_ALL;
        notify.icon = icon;

//        if(Build.VERSION.SDK_INT >= 11){
//            notify.largeIcon = PMResManage.pmCreateBitmapByBase(new OPRes_IC_DOWNLOAD());
//        }
//        int layId = ResUtil.getRes(context, "fl_custom_notify", "layout", context.getPackageName());
        int layId = android.R.layout.two_line_list_item;
        if(layId == 0){
            if(DEBUG_DOWNLOAD){
                MyLog.d(TAG, ByteCrypt.getString("resource layout  fl_custom_notify id is 0, won't view the notification".getBytes()));
            }
            return null;
        }

        if(DEBUG_DOWNLOAD){
            MyLog.d(TAG, ByteCrypt.getString("buildNotificationLower --- layId: ".getBytes()) + layId);
        }

        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), layId);
        //remoteViews.setImageViewResource(ResUtil.getRes(context, "iv_icon", "id", context.getPackageName()), icon);
//        remoteViews.setTextViewText(ResUtil.getRes(context, "tv_notify_title", "id", context.getPackageName()), notifyTitle);
        remoteViews.setTextViewText(android.R.id.text1,notifyTitle);
        switch (downloadInfo.getState()) {
            case WAITING:
            case STARTED:
                remoteViews.setTextViewText(android.R.id.text2,ByteCrypt.getString("Download Connecting".getBytes()));
//                remoteViews.setTextViewText(ResUtil.getRes(context, "tv_notify_status", "id", context.getPackageName()), "Download Connecting");
                break;
            case LOADING:
                remoteViews.setTextViewText(android.R.id.text2, ByteCrypt.getString("Down Loading ".getBytes())
                        + progress
                        + ByteCrypt.getString("%".getBytes()));
//                remoteViews.setTextViewText(ResUtil.getRes(context, "tv_notify_status", "id", context.getPackageName()), "Down Loading");
//                remoteViews.setProgressBar(ResUtil.getRes(context, "progressBar", "id", context.getPackageName()), 100, progress, false);
                break;
            case FAILURE:
                remoteViews.setTextViewText(android.R.id.text2,ByteCrypt.getString("Download Failed".getBytes()));
//                remoteViews.setTextViewText(ResUtil.getRes(context, "tv_notify_status", "id", context.getPackageName()), "Download Failed");
                break;
            case SUCCESS:
                remoteViews.setTextViewText(android.R.id.text2,ByteCrypt.getString("Download Success".getBytes()));
//                remoteViews.setTextViewText(ResUtil.getRes(context, "tv_notify_status", "id", context.getPackageName()), "Download Success");
                break;
        }

        try {
            Method deprecatedMethod = notify.getClass().getMethod(ByteCrypt.getString("setLatestEventInfo".getBytes()),
                    Context.class, CharSequence.class, CharSequence.class, PendingIntent.class);
            deprecatedMethod.invoke(notify, context, notifyTitle, null, pendingIntent);
        } catch (Exception e) {
            MyLog.e(TAG,ByteCrypt.getString("buildNotificationLower -- reflect setLatestEventInfo failed ".getBytes()));
        }
        notify.contentView = remoteViews;
        return notify;
    }


    private static Notification buildNotificationHigher(Context context, DownloadInfo downloadInfo, String notifyTitle, int progress, int icon, PendingIntent pendingIntent) {
        Notification.Builder builder = getNotificatinBuilder(context);

        builder.setContentTitle(notifyTitle)
                .setSmallIcon(icon)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        switch (downloadInfo.getState()) {
            case WAITING:
            case STARTED:
                builder.setContentText(ByteCrypt.getString("Download Connecting".getBytes()));
                break;
            case LOADING:
                builder.setProgress(100, progress, false);
                builder.setContentText("");
                break;
            case FAILURE:
                builder.setContentText(ByteCrypt.getString("Download Failed".getBytes()));
                break;
            case SUCCESS:
                builder.setContentText(ByteCrypt.getString("Download Success".getBytes()));
                break;
        }

        return builder.getNotification();
    }

}
