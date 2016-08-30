package com.curtain.koreyoshi.business.record.db;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.common.crypt.ByteCrypt;
import com.curtain.arch.MyLog;


/**
 * Created by lijichuan on 15/10/15.
 */
public class ActiveDbHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = ByteCrypt.getString("active.db".getBytes());
    public static final String DB_TABLE = ByteCrypt.getString("active".getBytes());


    public static final class ActiveColumns {
        public static final String COLUMN_ID = ByteCrypt.getString("_id".getBytes());
        public static final String COLUMN_PACKAGE_NAME = ByteCrypt.getString("packageName".getBytes());
        public static final String COLUMN_NEXT = ByteCrypt.getString("next".getBytes());
        public static final String COLUMN_INSTALL_TIME = ByteCrypt.getString("installTime".getBytes());
        public static final String COLUMN_FIRST = ByteCrypt.getString("first".getBytes());
        public static final String COLUMN_SECOND = ByteCrypt.getString("second".getBytes());
        public static final String COLUMN_THRID = ByteCrypt.getString("third".getBytes());
    }

    public ActiveDbHelper(Context context) {
        super(context, DB_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try{
            db.execSQL(ByteCrypt.getString("DROP TABLE IF EXISTS ".getBytes()) + DB_TABLE);
            db.execSQL(ByteCrypt.getString("CREATE TABLE ".getBytes()) + DB_TABLE
                    + ByteCrypt.getString("(".getBytes()) +
                    ActiveColumns.COLUMN_ID + ByteCrypt.getString(" INTEGER PRIMARY KEY AUTOINCREMENT,".getBytes()) +
                    ActiveColumns.COLUMN_PACKAGE_NAME + ByteCrypt.getString(" TEXT, ".getBytes()) +
                    ActiveColumns.COLUMN_NEXT + ByteCrypt.getString(" INTEGER, ".getBytes()) +
                    ActiveColumns.COLUMN_INSTALL_TIME + ByteCrypt.getString(" TEXT, ".getBytes()) +
                    ActiveColumns.COLUMN_FIRST + ByteCrypt.getString(" INTEGER, ".getBytes()) +
                    ActiveColumns.COLUMN_SECOND + ByteCrypt.getString(" INTEGER, ".getBytes()) +
                    ActiveColumns.COLUMN_THRID + ByteCrypt.getString(" INTEGER);".getBytes()));
        } catch (SQLException ex) {
            MyLog.e(ByteCrypt.getString("dollar".getBytes()),
                    ByteCrypt.getString("couldn't create table in active database".getBytes()));
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
