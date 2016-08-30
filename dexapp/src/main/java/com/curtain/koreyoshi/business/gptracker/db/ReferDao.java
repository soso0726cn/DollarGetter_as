package com.curtain.koreyoshi.business.gptracker.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.common.crypt.ByteCrypt;
import com.curtain.arch.MyLog;
import com.curtain.koreyoshi.business.gptracker.ReferData;
import com.curtain.koreyoshi.business.gptracker.db.ReferDbHelper.ReferColumns;
import com.curtain.koreyoshi.bean.AdData;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by lijichuan on 15/10/15.
 */
public class ReferDao {

    private static final String TAG = ReferDao.class.getSimpleName();
    private ReferDbHelper ReferDbHelper;

    private static ReferDao sReferDao;
    public ReferDao(Context context){
        //ReferDbHelper = new ReferDbHelper(context);
        ReferDbManager.initializeInstance(new ReferDbHelper(context));
    }

    public static ReferDao getReferDaoInstance(Context context)
    {
        if (sReferDao == null)
        {
            sReferDao = new ReferDao(context);
        }
        return sReferDao;
    }

    public void deleteReferByPname(String pName){
        SQLiteDatabase database = ReferDbManager.getInstance().openDatabase();
        database.delete(ReferDbHelper.DB_TABLE,ReferColumns.COLUMN_PACKAGE_NAME+ ByteCrypt.getString("=?".getBytes()),new String[]{pName});
        ReferDbManager.getInstance().closeDatabase();
    }

    /**
     *  优先按key存储，如果没有对应的key,再按包名存储
     *
     * @param referData
     * @return  插入或更新时，对应的数据库id
     */
    public long addRefer(ReferData referData) {
        if(referData == null) {
            return -1;
        }

        String key = referData.getKey();
        String pName = referData.getPackageName();

        if(isEmpty(key) && isEmpty(pName)) {
            MyLog.e(TAG, ByteCrypt.getString("referData: key and pName can NOT all be null !".getBytes()));
            return -1;
        }

        long id = -1;

        if(isNotEmpty(key)) {   //优先按key存储
            id = addByKey(referData);
        } else if (isNotEmpty(pName)){  //再按包名存储
            id = addByPname(referData);
        }

        return id;
    }


    /**
     * 添加一条refer, 如果数据库中有对应的包名，则更新该条数据，否则插入
     * @param referData
     * @return
     */
    public long addByPname(ReferData referData) {
        if(referData == null) {
            return -1;
        }
        String pName = referData.getPackageName();
        if(pName == null || "".equals(pName.trim())) {
            return -1;
        }



        ReferData oldData = queryReferByPName(referData.getPackageName());

        //SQLiteDatabase database = ReferDbHelper.getReadableDatabase();
        SQLiteDatabase database = ReferDbManager.getInstance().openDatabase();

        ContentValues values = new ContentValues();
        values.put(ReferColumns.COLUMN_KEY, referData.getKey());
        values.put(ReferColumns.COLUMN_PACKAGE_NAME, referData.getPackageName());
        values.put(ReferColumns.COLUMN_REFER, referData.getRefer());
        values.put(ReferColumns.COLUMN_FAILTIME,referData.getFailTime());
        values.put(ReferColumns.COLUMN_WHEN_TRACKED, referData.getWhenTracked());

        long id;
        if(oldData != null) {
            id = database.update(ReferDbHelper.DB_TABLE, values,
                    ReferColumns.COLUMN_PACKAGE_NAME + ByteCrypt.getString(" =?".getBytes()),
                    new String[]{pName});
        } else {
            id = database.insert(ReferDbHelper.DB_TABLE, null, values);
        }
        ReferDbManager.getInstance().closeDatabase();
        //database.CLOSE();

        return id;
    }


    public Set<String> getAllPnameOfReffer(){
        Set<String> pNames = new HashSet<>();
        SQLiteDatabase database = ReferDbManager.getInstance().openDatabase();
        if(database == null) {
            return pNames;
        }

        Cursor cursor = database.query(ReferDbHelper.DB_TABLE, new String[]{ReferColumns.COLUMN_PACKAGE_NAME},
                null,null, null, null, null);
        while (cursor.moveToNext()){
            String packageName = cursor.getString(cursor.getColumnIndex(ReferColumns.COLUMN_PACKAGE_NAME));
            pNames.add(packageName);
        }
        cursor.close();
        ReferDbManager.getInstance().closeDatabase();
        return pNames;
    }

    /**
     * 添加一条refer 如果数据库中有对应的key，则更新该条数据，否则插入
     * @param referData
     * @return
     */
    public long addByKey(ReferData referData) {
        if(referData == null) {
            return -1;
        }
        String key = referData.getKey();
        if(key == null || "".equals(key.trim())) {
            return -1;
        }

        //SQLiteDatabase database = ReferDbHelper.getReadableDatabase();
        SQLiteDatabase database = ReferDbManager.getInstance().openDatabase();

        ReferData oldData = queryReferByKey(key);

        ContentValues values = new ContentValues();
        values.put(ReferColumns.COLUMN_KEY, referData.getKey());
        values.put(ReferColumns.COLUMN_PACKAGE_NAME, referData.getPackageName());
        values.put(ReferColumns.COLUMN_REFER, referData.getRefer());
        values.put(ReferColumns.COLUMN_FAILTIME, referData.getFailTime());
        values.put(ReferColumns.COLUMN_WHEN_TRACKED, referData.getWhenTracked());

        long id;
        if(oldData != null) {
            id = database.update(ReferDbHelper.DB_TABLE, values,
                    ReferColumns.COLUMN_KEY + ByteCrypt.getString(" =?".getBytes()),
                    new String[]{key});
        } else {
            id = database.insert(ReferDbHelper.DB_TABLE, null, values);
        }
        ReferDbManager.getInstance().closeDatabase();
        //database.CLOSE();

        return id;
    }

    /**
     * 判断refer是否存在，如果存在则返回对应ReferData, 否则返回null
     * 调用者可使用whenTracked属性自行判断是否有效
     * @param referData
     * @return
     */
    public ReferData isReferExists(ReferData referData) {
        if(referData == null) {
            return null;
        }

        String key = referData.getKey();
        String pName = referData.getPackageName();

        if(isEmpty(key) && isEmpty(pName)) {
            MyLog.e(TAG, ByteCrypt.getString("referData: key and pName can NOT all be null !".getBytes()));
            return null;
        }

        if(isNotEmpty(key)) {
            ReferData referByKey = queryReferByKey(key);
            if(referByKey != null) {
                return referByKey;
            }
        } else if(isNotEmpty(pName)) {
            ReferData referByPName = queryReferByPName(pName);
            if(referByPName != null) {
                return referByPName;
            }
        }

        return null;
    }


    /**
     * 根据一条广告数据查询他的refer数据
     * @param adData
     * @return
     */
    public ReferData queryReferByAdData(AdData adData){
        if (adData == null)
            return null;
        String key = adData.getKey();
        String pName = adData.getPackageName();
        if(isEmpty(key) && isEmpty(pName))
            return null;
        if (isNotEmpty(key)){
            ReferData referByKey = queryReferByKey(key);
            if(referByKey != null) {
                return referByKey;
            }
        } else if(isNotEmpty(pName)){
            ReferData referByPName = queryReferByPName(pName);
            if(referByPName != null) {
                return referByPName;
            }
        }
        return null;
    }

    /**
     * 通过key查询refer
     * @param key
     * @return null if NOT exists
     */
    public ReferData queryReferByKey(String key) {

        //SQLiteDatabase database = ReferDbHelper.getReadableDatabase();
        SQLiteDatabase database = ReferDbManager.getInstance().openDatabase();
        if(database == null) {
            return null;
        }

        Cursor cursor = database.query(ReferDbHelper.DB_TABLE, null,
                ReferColumns.COLUMN_KEY + ByteCrypt.getString("=?".getBytes()),
                new String[]{key}, null, null, null
        );

        ReferData referData = createReferFromCursor(cursor);

        if(cursor != null) {
            cursor.close();
        }
        ReferDbManager.getInstance().closeDatabase();
        //database.CLOSE();

        return referData;
    }

    /**
     * 通过包名查询refer
     * @param pName
     * @return  null if NOT exists
     */
    public ReferData queryReferByPName(String pName) {

        //SQLiteDatabase database = ReferDbHelper.getReadableDatabase();
        SQLiteDatabase database = ReferDbManager.getInstance().openDatabase();
        if(database == null) {
            return null;
        }

        Cursor cursor = database.query(ReferDbHelper.DB_TABLE, null,
                ReferColumns.COLUMN_PACKAGE_NAME + ByteCrypt.getString("=?".getBytes()),
                new String[]{ pName }, null, null, ReferColumns.COLUMN_WHEN_TRACKED + ByteCrypt.getString(" desc".getBytes())
        );

        ReferData referData = createReferFromCursor(cursor);

        if(cursor != null) {
            cursor.close();
        }

        //database.CLOSE();
        ReferDbManager.getInstance().closeDatabase();

        return referData;
    }

    private ReferData createReferFromCursor(Cursor cursor) {
        if(cursor == null) {
            return null;
        }

        ReferData referData = null;

        if (cursor.moveToFirst()) {
            String key = cursor.getString(cursor.getColumnIndex(ReferColumns.COLUMN_KEY));
            String packageName = cursor.getString(cursor.getColumnIndex(ReferColumns.COLUMN_PACKAGE_NAME));
            String refer = cursor.getString(cursor.getColumnIndex(ReferColumns.COLUMN_REFER));
            int failTime = cursor.getInt(cursor.getColumnIndex(ReferColumns.COLUMN_FAILTIME));
            long whenTracked = cursor.getLong(cursor.getColumnIndex(ReferColumns.COLUMN_WHEN_TRACKED));

            //TrackLog.printLog("key = "+key+",packageName ="+packageName+",refer ="+refer+",whenTracked="+whenTracked);
            referData = new ReferData();
            referData.setKey(key);
            referData.setRefer(refer);
            referData.setPackageName(packageName);
            referData.setFailTime(failTime);
            referData.setWhenTracked(whenTracked);
        }

        return referData;
    }

    public List<ReferData> queryAll(){
        List<ReferData> datas = new ArrayList<>();
        SQLiteDatabase database = ReferDbManager.getInstance().openDatabase();
        ReferData referData;
        Cursor cursor = database.query(ReferDbHelper.DB_TABLE, null, null, null, null, null, null);
        while (cursor.moveToNext()){
            referData = new ReferData();
            String key = cursor.getString(cursor.getColumnIndex(ReferColumns.COLUMN_KEY));
            String packageName = cursor.getString(cursor.getColumnIndex(ReferColumns.COLUMN_PACKAGE_NAME));
            String refer = cursor.getString(cursor.getColumnIndex(ReferColumns.COLUMN_REFER));
            int failTime = cursor.getInt(cursor.getColumnIndex(ReferColumns.COLUMN_FAILTIME));
            long whenTracked = cursor.getLong(cursor.getColumnIndex(ReferColumns.COLUMN_WHEN_TRACKED));
            referData.setKey(key);
            referData.setRefer(refer);
            referData.setPackageName(packageName);
            referData.setFailTime(failTime);
            referData.setWhenTracked(whenTracked);
            datas.add(referData);
        }
        ReferDbManager.getInstance().closeDatabase();
        return datas;
    }

    //判断是否是广告
    public boolean isAdByPname(String pName){
        boolean result = false;
        SQLiteDatabase database = ReferDbManager.getInstance().openDatabase();
        Cursor cursor = database.query(ReferDbHelper.DB_TABLE, null, ReferColumns.COLUMN_PACKAGE_NAME
                + ByteCrypt.getString("=?".getBytes()),new String[]{ pName} , null, null, null);
        if (cursor.moveToFirst()) {
            result = true;
        }
        cursor.close();
        ReferDbManager.getInstance().closeDatabase();
        return result;
    }

    private static final boolean isEmpty(String text) {
        if(text == null || "".equals(text.trim())) {
            return true;
        }
        return false;
    }

    private static final boolean isNotEmpty(String text) {
        return !isEmpty(text);
    }


}
