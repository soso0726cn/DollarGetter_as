package com.slk.daemon;

import android.content.Context;

public interface IDaemonClient {
	/**
	 * override this method by {@link android.app.Application}</br></br>
	 * ****************************************************************</br>
	 * <b>DO super.attchBaseContext() first !</b></br>
	 * ****************************************************************</br>
	 * 
	 * @param base
	 */
	void onAttachBaseContext(Context base);
}
