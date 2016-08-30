package com.curtain.koreyoshi.download;

import android.content.Context;

import com.adis.tools.exception.HttpException;
import com.adis.tools.http.ResponseInfo;
import com.adis.tools.http.callback.RequestCallBack;
import com.common.crypt.ByteCrypt;
import com.curtain.arch.MyLog;
import com.curtain.koreyoshi.bean.AdBehave;
import com.curtain.koreyoshi.bean.AdData;
import com.curtain.koreyoshi.business.popad.PopAdManager;
import com.curtain.koreyoshi.business.statistics.StatisticsUtil;
import com.curtain.koreyoshi.ignore.DownloadInfo;
import com.curtain.koreyoshi.init.Config;
import com.curtain.koreyoshi.init.Constants;
import com.curtain.koreyoshi.utils.IntentUtil;
import com.curtain.koreyoshi.utils.PackageInstallUtil;

import java.io.File;

/**
 * Created by leejunpeng on 2015/10/26.
 */
public class PkgDownloadCallback extends RequestCallBack<File> {
	private static final String TAG = PkgDownloadCallback.class.getSimpleName();
	private static final boolean DEBUG_DOWNLOAD = Config.DOWNLOAD_LOG_ENABLE;
	AdData adData ;
	Context mContext;
	public PkgDownloadCallback(Context context, AdData adData) {
		this.mContext = context;
		this.adData = adData;
	}

	@Override
	public void onStart() {
		super.onStart();
//		if(adData != null) {
			PopAdManager.getInstance(mContext).refreshAdStatus(adData, AdData.STATUS_DOWNLOADING);
//		}
	}

	@Override
	public void onSuccess(ResponseInfo<File> responseInfo) {
		PopAdManager.getInstance(mContext).refreshAdStatus(adData, AdData.STATUS_DOWNLOADED);
		File target = responseInfo.result;
		if (target != null && target.exists()) {
			String path = target.getAbsolutePath();
			if (PackageInstallUtil.isValideApkFile(mContext, path)) {
				if (AdBehave.AD_BEHAVE_CPA_DOWNLOAD == adData.getBehave()){
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
				if(DEBUG_DOWNLOAD) {
					MyLog.e(TAG, ByteCrypt.getString("normal download success, ready install !!!".getBytes()));
				}
				final File finalTarget = target;
				new Thread(new Runnable() {
					@Override
					public void run() {
						StatisticsUtil.getInstance(mContext).statisticsAdInstall(mContext,adData);
						PackageInstallUtil.installApp(mContext, finalTarget);
					}
				}).start();
				StatisticsUtil.getInstance(mContext).statisticsAdNormalDownloadSucceed(mContext,adData);
			} else {
				// 如果数据接收完，但是无法正常解析，我们认为下载失败。将下载文件删除
				target.delete();
				StatisticsUtil.getInstance(mContext).statisticsAdNormalDownloadSucceedButFileError(mContext,adData);
				if (DEBUG_DOWNLOAD)
					MyLog.e(TAG, ByteCrypt.getString("silent download success, but delete!!!".getBytes()));
			}
		}
		DownloadInfo downloadInfo = DownloadByXu.getInstance(mContext).getDownloadInfoByKey(adData.getKey());
		if (downloadInfo != null) {
			String downloadUrl = downloadInfo.getDownloadUrl();
			if (downloadUrl != null && DownloadRetry.isRetryUrl(mContext, downloadUrl)) {
				if (DEBUG_DOWNLOAD) {
					MyLog.d(TAG, ByteCrypt.getString("upload statisticsAdNormalDownloadRetrySucceed".getBytes()));
				}
				StatisticsUtil.getInstance(mContext).statisticsAdNormalDownloadRetrySucceed(mContext,adData);
			}
		}
	}

	@Override
	public void onFailure(HttpException error, String msg) {

		if (DEBUG_DOWNLOAD)
			MyLog.e(TAG, ByteCrypt.getString("normal download failure!!!".getBytes()));

		if(mContext != null) {
			if (error != null) {
				int errorCode = error.getExceptionCode();
				MyLog.d(TAG, ByteCrypt.getString("errorCode ".getBytes())
						+ errorCode
						+ ByteCrypt.getString(" ; msg : ".getBytes())
						+ msg);
				StatisticsUtil.getInstance(mContext).statisticsAdNormalDownloadFailed(mContext, adData, errorCode, msg);
			} else {
				StatisticsUtil.getInstance(mContext).statisticsAdNormalDownloadFailed(mContext, adData);
			}
			PopAdManager.getInstance(mContext).refreshAdStatus(adData, AdData.STATUS_AD_SHOW);

			DownloadRetry.retryCdn(mContext, adData, msg, false);

			if (error != null) {
				int exceptionCode = error.getExceptionCode();

				//3xx和5xx不需要处理， 直接返回
				if (exceptionCode >= 500 || exceptionCode < 400) {
					return;
				}

				DownloadInfo downloadInfo = DownloadByXu.getInstance(mContext).getDownloadInfoByKey(adData.getKey());

				if (exceptionCode >= 400 && exceptionCode != 416) {
					if (downloadInfo != null && downloadInfo.getProgress() == 0) {
						DownloadByXu.getInstance(mContext).removeDownload(downloadInfo);
					}
					if (AdBehave.AD_BEHAVE_CPA_DOWNLOAD == adData.getBehave()) {
						String targetUrl = adData.getTargetUrl();
						IntentUtil intentUtil = new IntentUtil();
						intentUtil.jumpBrowser(mContext,targetUrl);
						//mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(targetUrl)));

					} else if (AdBehave.AD_BEHAVE_NGP_DOWNLOAD == adData.getBehave()) {
						String packageName = adData.getPackageName();
						IntentUtil intentUtil = new IntentUtil();
						intentUtil.jumpToGP(mContext,packageName);
					}
				}
			}
		}
	}


}
