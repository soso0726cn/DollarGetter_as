package com.curtain.koreyoshi.business.gptracker.db;

import android.database.sqlite.SQLiteDatabase;

import com.common.crypt.ByteCrypt;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by yangzheng on 15/10/29.
 */
public class ReferDbManager {
    private AtomicInteger mOpenCounter = new AtomicInteger();

    private static ReferDbManager instance;
    private static ReferDbHelper mDatabaseHelper;
    private SQLiteDatabase mDatabase;

    public static synchronized void initializeInstance(ReferDbHelper helper)
    {
        if(instance == null)
        {
            instance = new ReferDbManager();
            mDatabaseHelper = (ReferDbHelper)helper;
        }
    }

    public static synchronized ReferDbManager getInstance()
    {
        if (instance == null) {
            throw new IllegalStateException(ReferDbManager.class.getSimpleName() +
                    ByteCrypt.getString(" is not initialized, call initializeInstance(..) method first.".getBytes()));
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
