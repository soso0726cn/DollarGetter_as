package com.curtain.arch;

import android.os.Environment;
import android.util.Log;

import com.common.crypt.ByteCrypt;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;


public class MyLog {

    protected static boolean myDebug = false;
    protected static boolean writeLog = false;


    public static String LOGTAG = "Default";

    private static final SimpleDateFormat DATA_FORMAT = new SimpleDateFormat(
            ByteCrypt.getString("yyyy-MM-dd HH:mm:ss.SSS".getBytes()));

    private static final SimpleDateFormat DATA_FORMAT_FILE_NAME = new SimpleDateFormat(
            ByteCrypt.getString("yyyy-MM-dd".getBytes()));

    private static final String sep = " ";

    public static void setDebug(boolean isDebug) {
        myDebug = isDebug;
    }

    public static boolean isDebug() {
        return myDebug;
    }

    public static void i(String msg) {
        if (myDebug) {
            Log.i(LOGTAG, msg);
        }
        if (writeLog)
            printIntoFile(msg);

    }

    public static void d(String tag, String msg) {
        if (myDebug)
            Log.d(tag, msg);
        if (writeLog)
            printIntoFile(msg);
    }

    public static void e(String tag, String msg) {
        if (myDebug) {
            Log.e(tag, msg);
        }
        if (writeLog)
            printIntoFile(msg);
    }

    public static void i(String tag, String msg) {
        if (myDebug) {
            Log.i(tag, msg);
        }
        if (writeLog)
            printIntoFile(msg);
    }

    public static void v(String tag, String msg) {
        if (myDebug)
            Log.v(tag, msg);
        if (writeLog)
            printIntoFile(msg);
    }

    public static void w(String tag, String msg) {
        if (myDebug)
            Log.w(tag, msg);
        if (writeLog)
            printIntoFile(msg);
    }
    public static void w(String msg) {
        if (myDebug)
            Log.w(LOGTAG, msg);
        if (writeLog)
            printIntoFile(msg);
    }

    public static void w(String tag, String msg, Throwable tr) {
        if (myDebug)
            Log.w(tag, msg, tr);
        if (writeLog)
            printIntoFile(msg);
    }

    public static void e(String tag, String msg, Throwable tr) {
        if (myDebug)
            Log.e(tag, msg, tr);
        if (writeLog)
            printIntoFile(msg);
    }

    public static void printException(Throwable e)
    {
        try {
            String info = "";
            StringWriter errors = new StringWriter();
            e.printStackTrace(new PrintWriter(errors));

            info = errors.toString();
            w(info);
        }catch (Exception ee)
        {
            ee.toString();
        }
    }

    public static final String FOLDER_ROOT = Environment.getExternalStorageDirectory().getAbsolutePath()
            +ByteCrypt.getString("/Juice".getBytes());
    public static final String LOG_DIR = FOLDER_ROOT +ByteCrypt.getString("/log/".getBytes());

    //add by Wmmeng 20150106
    public static void printIntoFile(String str) {
        try {
            File path = null;
            path = new File(LOG_DIR);
            if (!path.exists())
                path.mkdirs();

			File logFile = new File(path,DATA_FORMAT_FILE_NAME.format(new Date(System.currentTimeMillis()))+".txt");
			FileOutputStream logStream = new FileOutputStream(logFile, true);
			logStream.write(DATA_FORMAT.format(new Date(System.currentTimeMillis())).getBytes());
			logStream.write(sep.getBytes());
			logStream.write(str.getBytes());
			logStream.write("\r\n".getBytes());
			logStream.flush();
			logStream.close();
        } catch (Exception e) {
            Log.e("Log","--- write log to file exception: " + e.getMessage());
        }
    }
}
