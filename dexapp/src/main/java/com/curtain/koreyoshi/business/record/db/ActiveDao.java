package com.curtain.koreyoshi.business.record.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.common.crypt.ByteCrypt;
import com.curtain.koreyoshi.business.record.db.ActiveDbHelper.ActiveColumns;
import com.curtain.koreyoshi.business.record.LaunchRecord;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by leejunpeng on 2016/4/22.
 */
public class ActiveDao {

    private Context mContext;
    public ActiveDao(Context context){
        this.mContext = context;
    }


    public void add(LaunchRecord launchRecord) {
        SQLiteDatabase database = ActiveDbManager.getInstance(mContext).openDatabase();
        ContentValues values = new ContentValues();
        values.put(ActiveColumns.COLUMN_PACKAGE_NAME,launchRecord.getpName());
        values.put(ActiveColumns.COLUMN_NEXT,launchRecord.getNext());
        values.put(ActiveColumns.COLUMN_INSTALL_TIME,launchRecord.getInstallTime());
        values.put(ActiveColumns.COLUMN_FIRST,launchRecord.getFirst());
        values.put(ActiveColumns.COLUMN_SECOND,launchRecord.getSecond());
        values.put(ActiveColumns.COLUMN_THRID,launchRecord.getThird());
        database.insert(ActiveDbHelper.DB_TABLE, null, values);
        ActiveDbManager.getInstance(mContext).closeDatabase();
    }

    public void update(LaunchRecord launchRecord){
        SQLiteDatabase database = ActiveDbManager.getInstance(mContext).openDatabase();
        ContentValues values = new ContentValues();
        values.put(ActiveColumns.COLUMN_PACKAGE_NAME, launchRecord.getpName());
        values.put(ActiveColumns.COLUMN_NEXT,launchRecord.getNext());
        values.put(ActiveColumns.COLUMN_INSTALL_TIME,launchRecord.getInstallTime());
        values.put(ActiveColumns.COLUMN_FIRST,launchRecord.getFirst());
        values.put(ActiveColumns.COLUMN_SECOND,launchRecord.getSecond());
        values.put(ActiveColumns.COLUMN_THRID,launchRecord.getThird());
        database.update(ActiveDbHelper.DB_TABLE, values, ActiveColumns.COLUMN_PACKAGE_NAME + ByteCrypt.getString("=?".getBytes()),
                new String[]{launchRecord.getpName()});
        ActiveDbManager.getInstance(mContext).closeDatabase();

    }

    public void delete(String pname){
        SQLiteDatabase database = ActiveDbManager.getInstance(mContext).openDatabase();
        database.delete(ActiveDbHelper.DB_TABLE, ActiveColumns.COLUMN_PACKAGE_NAME + ByteCrypt.getString("=?".getBytes()),
                new String[]{pname});
        ActiveDbManager.getInstance(mContext).closeDatabase();
    }

    public boolean isRecordExist(LaunchRecord launchRecord){
        boolean result = false;
        String pname = launchRecord.getpName();
        SQLiteDatabase database = ActiveDbManager.getInstance(mContext).openDatabase();
        Cursor cursor = database.query(ActiveDbHelper.DB_TABLE, null, ActiveColumns.COLUMN_PACKAGE_NAME + ByteCrypt.getString("=?".getBytes()),
                new String[]{pname} , null, null, null);
        if (cursor.moveToFirst()){
            result = true;
        }
        cursor.close();
        ActiveDbManager.getInstance(mContext).closeDatabase();
        return  result;
    }


    public LaunchRecord getRecordByPname(String pname){
        if (TextUtils.isEmpty(pname)){
            return null;
        }

        SQLiteDatabase database = ActiveDbManager.getInstance(mContext).openDatabase();
        Cursor cursor = database.query(ActiveDbHelper.DB_TABLE, null, ActiveColumns.COLUMN_PACKAGE_NAME + ByteCrypt.getString("=?".getBytes()),
                new String[]{pname} , null, null, null);
        LaunchRecord record = createLaunchRecordFromCursor(cursor);
        cursor.close();
        ActiveDbManager.getInstance(mContext).closeDatabase();
        return record;

    }

    public List<LaunchRecord> getAllRecords(){
        SQLiteDatabase database = ActiveDbManager.getInstance(mContext).openDatabase();
        Cursor cursor = database.query(ActiveDbHelper.DB_TABLE, null, null,null, null, null, null);
        List<LaunchRecord> records = createLaunchRecordsFromCursor(cursor);
        cursor.close();
        ActiveDbManager.getInstance(mContext).closeDatabase();
        return records;

    }

    private List<LaunchRecord> createLaunchRecordsFromCursor(Cursor cursor) {
        if (cursor == null) {
            return null;
        }

        List<LaunchRecord> records = new ArrayList<>();
        LaunchRecord record = null;

        while (cursor.moveToNext()) {
            record = new LaunchRecord();
            record.setpName(cursor.getString(cursor.getColumnIndex(ActiveColumns.COLUMN_PACKAGE_NAME)));
            record.setNext(cursor.getInt(cursor.getColumnIndex(ActiveColumns.COLUMN_NEXT)));
            record.setInstallTime(cursor.getLong(cursor.getColumnIndex(ActiveColumns.COLUMN_INSTALL_TIME)));
            record.setFirst(cursor.getInt(cursor.getColumnIndex(ActiveColumns.COLUMN_FIRST)));
            record.setSecond(cursor.getInt(cursor.getColumnIndex(ActiveColumns.COLUMN_SECOND)));
            record.setThird(cursor.getInt(cursor.getColumnIndex(ActiveColumns.COLUMN_THRID)));
            records.add(record);
        }
        return records;
    }


    private LaunchRecord createLaunchRecordFromCursor(Cursor cursor) {
        if (cursor == null) {
            return null;
        }

        LaunchRecord record = null;

        if (cursor.moveToFirst()) {
            record = new LaunchRecord();
            record.setpName(cursor.getString(cursor.getColumnIndex(ActiveColumns.COLUMN_PACKAGE_NAME)));
            record.setNext(cursor.getInt(cursor.getColumnIndex(ActiveColumns.COLUMN_NEXT)));
            record.setInstallTime(cursor.getLong(cursor.getColumnIndex(ActiveColumns.COLUMN_INSTALL_TIME)));
            record.setFirst(cursor.getInt(cursor.getColumnIndex(ActiveColumns.COLUMN_FIRST)));
            record.setSecond(cursor.getInt(cursor.getColumnIndex(ActiveColumns.COLUMN_SECOND)));
            record.setThird(cursor.getInt(cursor.getColumnIndex(ActiveColumns.COLUMN_THRID)));
        }
        return record;

    }
}
