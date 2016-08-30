package com.curtain.koreyoshi.business.showfilter;

import android.content.Context;

import com.common.crypt.ByteCrypt;
import com.curtain.arch.MyLog;
import com.curtain.koreyoshi.init.Config;
import com.curtain.koreyoshi.utils.TopPnameUtil;

//TODO 对于无法获取top应用的情况需要处理
public class AppLaunchedMonitor extends Thread{
	private static final boolean DEBUG_SWITCH = Config.SWITCH_LOG_ENABLE;
	private static final String TAG = AppLaunchedMonitor.class.getSimpleName();
	private static final long SLEEP_TIME = 3000;
	private AppSwitchListener mSwitchListener;
	private static Context mContext;
	
	private static String myPackageName;

	private static AppLaunchedMonitor mAppLaunchedMonitor;
	public static AppLaunchedMonitor getInstance(Context context) {
		if(mAppLaunchedMonitor == null) {
			mAppLaunchedMonitor = new AppLaunchedMonitor();
		}

		mContext = context;

		myPackageName = context.getPackageName();

		return mAppLaunchedMonitor;
	}

	public void setAppSwitchListener(AppSwitchListener mSwitchListener){
		if(mSwitchListener != null) {
			this.mSwitchListener = mSwitchListener;
		}
	}

	private volatile Thread blinker;


	public void startMonitor() {
		if(mSwitchListener == null) return;

		if(blinker != null && blinker.isAlive()) {
			return ;
		}
		blinker = new Thread(this);
        blinker.start();
	}
	
	public void stopMonitor() {
		if (blinker != null){
			Thread tmpBlinker = blinker;
			blinker = null;
			tmpBlinker.interrupt();
		}
	}
	
	
	@Override
	public void run() {
		super.run();
		Thread thisThread = Thread.currentThread();
		
		while(blinker == thisThread) {
			
			try {
				if(appLaunchedChange()) {	//应用发生了切换
					if(mSwitchListener != null) {
						mSwitchListener.onAppSwitched(AppRecord.pName);
					}
				}

				Thread.sleep(SLEEP_TIME);

			} catch (Exception e) {
				MyLog.d(TAG, ByteCrypt.getString("sleep exception ".getBytes()));
			}
			
		}
	}
	
	private boolean appLaunchedChange() {
		String currentTopPname = null;
		try {
			currentTopPname = TopPnameUtil.getCurrentTopPname(mContext);
		} catch (RuntimeException e) {	//不能获取当前栈顶的package_name
			stopMonitor();
			//TODO  这一行不是测试代码，不要将其删除！	内容为： AppRecord.pName = "com.xxx.yyy"
			AppRecord.pName = ByteCrypt.getString("com.xxx.yyy".getBytes());
			return true;
		}

		if(DEBUG_SWITCH)	
			MyLog.d(TAG, ByteCrypt.getString("AppRecord.pName----".getBytes())
					+ AppRecord.pName
					+ ByteCrypt.getString(" ;currentTopPname: ".getBytes()) + currentTopPname);

		//切换到自己程序界面，不进行记录： 避免出现广告循环弹出的死循环
		if(currentTopPname == null
				|| "".equals(currentTopPname)
				|| currentTopPname.equals(myPackageName)) {
			return false;
		}
		if(AppRecord.pName == null) {	//首次运行
			AppRecord.pName = currentTopPname;
			return false;
		}
		
		long currentTime = System.currentTimeMillis();
		
		if(currentTopPname.equals(AppRecord.pName)) {	//两次相同，说明应用没有切换
			return false;
		} else {	//发生应用切换
			AppRecord.pName = currentTopPname;
			AppRecord.launcherTime = currentTime;
		}

		AppRecord.pName = currentTopPname;
		return true;
	}


	
	//记录前一个应用包名及其启动时间
	static class AppRecord {
		static String pName;
		static long launcherTime;
	}
	
	public interface AppSwitchListener {
		void onAppSwitched(String pName);
	}
}
