package com.curtain.koreyoshi.business.record;

import android.content.Context;
import android.text.TextUtils;

import com.common.crypt.ByteCrypt;
import com.curtain.arch.MyLog;
import com.curtain.koreyoshi.business.record.db.ActiveDao;

import java.util.List;


public class LaunchRecordManager {

	private static LaunchRecordManager mInstance;
	private static ActiveDao mActiveDao;

	private LaunchRecordManager(Context context){
		mActiveDao = new ActiveDao(context);
	}

	public static LaunchRecordManager getInstance(Context context){
		if (mInstance == null){
			mInstance = new LaunchRecordManager(context);
		}
		return mInstance;
	}

	public void putRecord(LaunchRecord record){
		if(record == null){
			MyLog.d(ByteCrypt.getString("wmm".getBytes()),
					ByteCrypt.getString("LaunchRecord is null".getBytes()));
			return;
		}

		if(mActiveDao.isRecordExist(record)){
			MyLog.d(ByteCrypt.getString("wmm".getBytes()),
					ByteCrypt.getString("重复安装的应用".getBytes()));
			return;
		}
		MyLog.d(ByteCrypt.getString("wmm".getBytes()),
				ByteCrypt.getString("put ".getBytes())+record.getpName());
		mActiveDao.add(record);
	}
	
	public void refreshRecord(String pname, int nextStatus){
		LaunchRecord record = mActiveDao.getRecordByPname(pname);
		if (record == null){
			return;
		}
		record.setNext(nextStatus);
		mActiveDao.update(record);
	}


	public void deleteRecordByPname(String pname){
		MyLog.i(ByteCrypt.getString("wmm".getBytes()),
				ByteCrypt.getString("deleteRecordByPname : ".getBytes()) + pname);

		if (TextUtils.isEmpty(pname)){
			return;
		}

		mActiveDao.delete(pname);
	}

	public List<LaunchRecord> getAllRecord() {
		return mActiveDao.getAllRecords();
	}
}
