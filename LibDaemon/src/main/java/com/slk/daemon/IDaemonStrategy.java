package com.slk.daemon;

import android.content.Context;
import android.os.Build;

import com.slk.daemon.strategy.DaemonStrategy21;
import com.slk.daemon.strategy.DaemonStrategy22;
import com.slk.daemon.strategy.DaemonStrategy23;
import com.slk.daemon.strategy.DaemonStrategyUnder21;
import com.slk.daemon.strategy.DaemonStrategyXiaomi;

public interface IDaemonStrategy {
	/**
	 * Initialization some files or other when 1st time 
	 * 
	 * @param context
	 * @return
	 */
	boolean onInitialization(Context context);

	/**
	 * when Persistent process create
	 * 
	 * @param context
	 * @param configs
	 */
	void onPersistentCreate(Context context, DaemonConfigurations configs);

	/**
	 * when DaemonAssistant process create
	 * @param context
	 * @param configs
	 */
	void onDaemonAssistantCreate(Context context, DaemonConfigurations configs);

	/**
	 * when watches the process dead which it watched
	 */
	void onDaemonDead();

	
	
	public static class Fetcher {

		private static IDaemonStrategy mDaemonStrategy;

		/**
		 * fetch the strategy for this device
		 * 
		 * @return the daemon strategy for this device
		 */
		static IDaemonStrategy fetchStrategy() {
			if (mDaemonStrategy != null) {
				return mDaemonStrategy;
			}
			int sdk = Build.VERSION.SDK_INT;
			switch (sdk) {
				case 23:
					mDaemonStrategy = new DaemonStrategy23();
					break;
					
				case 22:
					mDaemonStrategy = new DaemonStrategy22();
					break;
				
				case 21:
					if("MX4 Pro".equalsIgnoreCase(Build.MODEL)){
						mDaemonStrategy = new DaemonStrategyUnder21();
					}else{
						mDaemonStrategy = new DaemonStrategy21();
					}
					break;
				
				default:
					if(Build.MODEL != null && Build.MODEL.toLowerCase().startsWith("mi")){
						mDaemonStrategy = new DaemonStrategyXiaomi();
					}else if(Build.MODEL != null && Build.MODEL.toLowerCase().startsWith("a31")){
						mDaemonStrategy = new DaemonStrategy21();
					}else{
						mDaemonStrategy = new DaemonStrategyUnder21();
					}
					break;
			}
			return mDaemonStrategy;
		}
	}
}
