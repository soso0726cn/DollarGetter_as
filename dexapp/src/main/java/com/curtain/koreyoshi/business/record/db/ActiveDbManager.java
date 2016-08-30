package com.curtain.koreyoshi.business.record.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by yangzheng on 15/10/29.
 */
public class ActiveDbManager {
    private AtomicInteger mOpenCounter = new AtomicInteger();

    private static ActiveDbManager instance;
    private static ActiveDbHelper mDatabaseHelper;
    private SQLiteDatabase mDatabase;

    public static synchronized void initializeInstance(ActiveDbHelper helper)
    {
        if(instance == null)
        {
            instance = new ActiveDbManager();
            mDatabaseHelper = (ActiveDbHelper)helper;
        }
    }

    public static synchronized ActiveDbManager getInstance(Context context)
    {
        if (instance == null) {
            initializeInstance(new ActiveDbHelper(context));
        }

        return instance;
    }

    public synchronized SQLiteDatabase openDatabase() {
        if(mOpenCounter.incrementAndGet() == 1) {
            // Opening new database
            mDatabase = mDatabaseHelper.getReadableDatabase();
        }
        return mDatabase;
    }

    public synchronized void closeDatabase() {
        if (mOpenCounter.decrementAndGet() == 0) {
            // Closing database
            mDatabase.close();

        }
    }
}
