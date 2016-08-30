package com.curtain.arch.errorlog;

import android.content.Context;
import android.os.Environment;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.common.crypt.ByteCrypt;
import com.curtain.utils.io.FileUtils;
import com.curtain.utils.io.IOUtils;
import com.curtain.arch.MyLog;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.Arrays;
import java.util.TreeSet;


/**
 * UncaughtException处理类,当程序发生Uncaught异常的时候,由该类来接管程序,并记录、发送错误报告.. 註冊方式
 * CrashHandler crashHandler = CrashHandler.getInstance();
 * crashHandler.init(getApplicationContext()); //注册crashHandler
 * crashHandler.sendPreviousReportsToServer(); //发送以前没发送的报告(可选)
 *
 *
 * 原理说明：线程级的异常是不能try...catch的，
 * 	1. 如果想要处理线程级的异常，可以为线程设置UncaughtExceptionHandler
 * 		new Thread().setUncaughtExceptionHandler();
 * 	2. 每个线程都有一个自己所属的线程组ThreadGroup, 如果创建线程时没有手动设定，则默认与其父线程属于同一个线程组。
 *	   可以调用Thread的静态方法Thread.setDefaultUncaughtExceptionHandler,设置线程组级别的异常处理
 *
 */
public class CrashHandler implements UncaughtExceptionHandler {

    public static final String TAG = ByteCrypt.getString("CrashHandler".getBytes());
    //是否开启日志输出,在Debug状态下开启, 在Release状态下关闭以提高程序性能
    public static final boolean DEBUG = false;		//TODO 不同的应用可能会发生变化


    //错误报告文件的扩展名
    private static final String CRASH_REPORT_EXTENSION = ByteCrypt.getString(".cr".getBytes());
    //错误日志在SD卡中保存的路径
    private static String CRASH_REPORT_DIR;

    /**
     * @param dir  错误日志保存的路径
     * @return
     */
    public static boolean setCrashReportDir(String dir) {
        if(dir == null) {
            Log.e(TAG, ByteCrypt.getString("setCrashReportDir failed: dir can NOT be null".getBytes()));
            return false;
        }

        try {
            FileUtils.forceMkdir(new File(dir));
        } catch (IOException e) {
            Log.e(TAG, ByteCrypt.getString("setCrashReportDir failed: can NOT create directory ".getBytes()) + dir);
            e.printStackTrace();
            return false;
        }
        if(DEBUG) {
            Log.d(TAG,ByteCrypt.getString("crashDir: ".getBytes()) + dir);
        }
        CRASH_REPORT_DIR = dir;
        return true;
    }

    //初始化错误日志保存目录
    private static void initCrashReportDir(Context context) {
        Context ctx = context;
        if(CRASH_REPORT_DIR == null) {	//用户没有设置
            String crashDir = null;
            File externalCacheDir = ctx.getExternalCacheDir();
            if(externalCacheDir != null) {
                crashDir = externalCacheDir.getAbsolutePath();
            } else {
                crashDir = Environment.getExternalStorageDirectory() +File.separator + ByteCrypt.getString("Juice".getBytes())
                        + File.separator + ByteCrypt.getString("error".getBytes());
            }
            setCrashReportDir(crashDir);
        }
        ctx = null;
    }
    // 系统默认的UncaughtException处理类
    private Thread.UncaughtExceptionHandler mDefaultHandler;
    // 程序的Context对象
    private Context mContext;

    // CrashHandler实例
    private static CrashHandler INSTANCE;
    // 保证只有一个CrashHandler实例
    private CrashHandler() {
    }

    /** 获取CrashHandler实例 ,单例模式 */
    public static CrashHandler getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new CrashHandler();
        }
        return INSTANCE;
    }

    /**
     * 初始化,注册Context对象, 获取系统默认的UncaughtException处理器, 设置该CrashHandler为程序的默认处理器
     *
     * @param ctx
     */
    public void init(Context ctx) {
        mContext = ctx;
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
        initCrashReportDir(ctx);
    }

    /**
     * 当UncaughtException发生时会转入该函数来处理
     */
    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        if (DEBUG) {
            Log.e(TAG,ByteCrypt.getString("uncaughtException: ".getBytes()) + ex.getMessage());
        }
        if (!handleException(ex) && mDefaultHandler != null) {
            // 如果用户没有处理则让系统默认的异常处理器来处理
            mDefaultHandler.uncaughtException(thread, ex);
        } else {
            // Sleep一会后结束程序
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                Log.e(TAG, ByteCrypt.getString("Error : ".getBytes()), e);
            }
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(10);
        }
    }

    /**
     * 自定义错误处理,收集错误信息 发送错误报告等操作均在此完成. 开发者可以根据自己的情况来自定义异常处理逻辑
     *
     * @param ex
     * @return true:如果处理了该异常信息;否则返回false
     */
    private boolean handleException(Throwable ex) {
        if (ex == null) {
            return true;
        }

        String msg = ex.getLocalizedMessage();
        if (DEBUG) {
            Log.e(TAG,ByteCrypt.getString("handleException: ".getBytes()) + msg);
            MyLog.printException(ex);
            ex.printStackTrace();
            // 使用Toast来显示异常信息
            //showToast(msg);
        }

        // 保存错误报告文件
        saveCrashInfoToFile(ex);

        // 发送错误报告到服务器
        sendCrashReportsToServer();
        return true;
    }

    private void showToast(final String msg) {
        new Thread() {
            @Override
            public void run() {
                Looper.prepare();
                Toast.makeText(mContext, ByteCrypt.getString("程序出错啦:".getBytes()) + msg, Toast.LENGTH_LONG).show();
                Looper.loop();
            }
        }.start();
    }

    /**
     * 在程序启动时候, 可以调用该函数来发送以前没有发送的报告
     */
    public void sendPreviousReportsToServer() {
        sendCrashReportsToServer();
    }

    /**
     * 把错误报告发送给服务器,包含新产生的和以前没发送的.
     */
    private void sendCrashReportsToServer() {
        String[] crFiles = getCrashReportFiles();
        if (crFiles != null && crFiles.length > 0) {
            TreeSet<String> sortedFiles = new TreeSet<String>();
            sortedFiles.addAll(Arrays.asList(crFiles));

            for (String fileName : sortedFiles) {
                File cr = new File(CRASH_REPORT_DIR, fileName);
                if (DEBUG) {
                    Log.e(TAG,ByteCrypt.getString("sendCrashReportsToServer --- fileName: ".getBytes()) + fileName);
                }
                postReport(cr);
//                cr.delete();// 删除已发送的报告
            }
        }
    }

    private void postReport(File file) {
        // TODO 使用HTTP Post 发送错误报告到服务器
        if(DEBUG) Log.d(TAG, ByteCrypt.getString("postReport --- file: ".getBytes()) +file);
        CrashReporter.reportCrash(file, mContext);
    }

    /**
     * 获取错误报告文件名
     * @return
     */
    private String[] getCrashReportFiles() {
        File crashDir = FileUtils.getFile(CRASH_REPORT_DIR);
        FilenameFilter filter = new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.endsWith(CRASH_REPORT_EXTENSION);
            }
        };
        return crashDir.list(filter);
    }

    /**
     * 保存错误信息到文件中
     *
     * @param ex
     * @return
     */
    private String saveCrashInfoToFile(Throwable ex) {
        Writer info = new StringWriter();
        PrintWriter printWriter = new PrintWriter(info);
        printWriter.write(ex.toString());
        ex.printStackTrace(printWriter);

        Throwable cause = ex.getCause();
        while (cause != null) {
            printWriter.write(ByteCrypt.getString("\n        --------------------------------        \n".getBytes()));
            cause.printStackTrace(printWriter);
            cause = cause.getCause();
        }

        String result = info.toString();
        printWriter.close();
        String fileName = "";
        try {
            long timestamp = System.currentTimeMillis();
            fileName = ByteCrypt.getString("crash-".getBytes()) + timestamp + CRASH_REPORT_EXTENSION;

            File crashFile = FileUtils.getFile(CRASH_REPORT_DIR, fileName);
            if (DEBUG) {
                Log.e(TAG, ByteCrypt.getString("saveCrashInfoToFile --- crashFile: ".getBytes()) + crashFile);
            }
            FileOutputStream trace = FileUtils.openOutputStream(crashFile);
            IOUtils.write(result, trace);
            IOUtils.closeQuietly(trace);

            return fileName;
        } catch (Exception e) {
            if (DEBUG) {
                Log.e(TAG, ByteCrypt.getString("an error occured while writing report file: ".getBytes()) + fileName, e);
            }
        }
        return null;
    }
}