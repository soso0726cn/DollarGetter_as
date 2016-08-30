package com.slk.daemon;

import android.content.Context;

public class NativeDaemonBase {
	/**
	 * used for native
	 */
	protected 	Context			mContext;
	
    public NativeDaemonBase(Context context){
        this.mContext = context;
    }

    /**
     * native call back
     */
	protected void onDaemonDead(){
		IDaemonStrategy.Fetcher.fetchStrategy().onDaemonDead();
    }
    
}
