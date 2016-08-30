package com.curtain.koreyoshi.business.gptracker.db;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.common.crypt.ByteCrypt;
import com.curtain.arch.MyLog;

/**
 * Created by lijichuan on 15/10/15.
 */
public class ReferDbHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = ByteCrypt.getString("gref.db".getBytes());
    public static final String DB_TABLE = ByteCrypt.getString("gref".getBytes());


    public static final class ReferColumns {
        public static final String COLUMN_ID = ByteCrypt.getString("_id".getBytes());
        public static final String COLUMN_KEY = ByteCrypt.getString("key".getBytes());
        public static final String COLUMN_PACKAGE_NAME = ByteCrypt.getString("packageName".getBytes());
        public static final String COLUMN_REFER = ByteCrypt.getString("refer".getBytes());
        public static final String COLUMN_FAILTIME = ByteCrypt.getString("failtime".getBytes());
        public static final String COLUMN_WHEN_TRACKED = ByteCrypt.getString("whenTracked".getBytes());
    }

    public ReferDbHelper(Context context) {
        super(context, DB_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try{
            db.execSQL(ByteCrypt.getString("DROP TABLE IF EXISTS ".getBytes()) + DB_TABLE);
            db.execSQL(ByteCrypt.getString("CREATE TABLE ".getBytes()) + DB_TABLE +
                    ByteCrypt.getString("(".getBytes()) +
                    ReferColumns.COLUMN_ID + ByteCrypt.getString(" INTEGER PRIMARY KEY AUTOINCREMENT,".getBytes()) +
                    ReferColumns.COLUMN_KEY + ByteCrypt.getString(" TEXT, ".getBytes()) +
                    ReferColumns.COLUMN_PACKAGE_NAME + ByteCrypt.getString(" TEXT, ".getBytes()) +
                    ReferColumns.COLUMN_REFER + ByteCrypt.getString(" TEXT, ".getBytes()) +
                    ReferColumns.COLUMN_FAILTIME + ByteCrypt.getString(" INTEGER, ".getBytes()) +
                    ReferColumns.COLUMN_WHEN_TRACKED + ByteCrypt.getString(" TEXT);".getBytes()));
        } catch (SQLException ex) {
            MyLog.e(ReferDbHelper.class.getSimpleName(), ByteCrypt.getString("couldn't create table in gref database".getBytes()));
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
