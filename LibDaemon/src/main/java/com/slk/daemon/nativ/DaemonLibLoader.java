package com.slk.daemon.nativ;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by liumin on 2016/5/19.
 */
public class DaemonLibLoader {

    private String pkgName;
    private String APP_DATA = "/data/data/";

    private static DaemonLibLoader mLibLoader;
    private boolean isLoader = false;

    public static DaemonLibLoader getLoader(Context mContext){
        if(mLibLoader == null) mLibLoader = new DaemonLibLoader(mContext);
        return mLibLoader;
    }

    public boolean isLoader(){
        return isLoader;
    }


    private File getLibFile(String pkgName) {
        File folder = new File(APP_DATA + pkgName + "/app_s_tmp_down/");
        folder.mkdirs();
        File srcFile = new File(folder, "dae_s_t.tmp");
        return srcFile;
    }

    private DaemonLibLoader(Context mContext) {

        if (mContext == null) return;

        String pkgName = mContext.getPackageName();
        this.pkgName = pkgName;

        File mFile = getLibFile(pkgName);

        //TODO
        if (!mFile.exists()) {
//            loadLibFromAssets(mContext,mFile);
//            loadLibFromJar(mFile);
//            loadLibFromJarAccordCpu(mFile);
            loadLibFromAssetsAccordCpu(mContext, mFile);
        }

        loadLibToSys(mFile);

    }

    private void loadLibFromJarAccordCpu(File mFile) {
        String cpuABI = getCpuABI();
        Log.d("lee", "cpuABI:" + cpuABI);
        String resourceDirName = null;
        if (cpuABI.startsWith(CUP_ABI_ARM64_V8A)) {
            resourceDirName = "arm64-v8a";
        }else if(cpuABI.startsWith(CUP_ABI_X86)) {
            resourceDirName = "x86";
        }else {
            resourceDirName = "armeabi";
        }
        String resourceFileName = (TextUtils.isEmpty(resourceDirName) ? "" : (resourceDirName + File.separator)) + SO_FILE_NAME;


        try {
            InputStream in = DaemonLibLoader.class.getResourceAsStream("/resource/"+resourceFileName);
            FileOutputStream out = new FileOutputStream(mFile);
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0)
                out.write(buf, 0, len);
            in.close();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static final String CUP_ABI_ARM64_V8A = "arm64-v8a";
    public static final String CUP_ABI_ARMEABI = "armeabi";
    public static final String CUP_ABI_ARMEABI_V7 = "armeabi-v7a";
    public static final String CUP_ABI_X86 = "x86";
    public static final String SO_FILE_NAME = "google+";

    /**
     * 按照cpu架构从assets下加载so
     * @param mContext
     * @param mFile
     */
    private void loadLibFromAssetsAccordCpu(Context mContext, File mFile) {
        Log.d("lee","Copy Daemon Lib From Assets----");
        String cpuABI = getCpuABI();
        Log.d("lee","cpuABI:"  + cpuABI);
        String assetsDirName = null;
        if (cpuABI.startsWith(CUP_ABI_ARM64_V8A)) {
            assetsDirName = "arm64-v8a";
        }else if(cpuABI.startsWith(CUP_ABI_X86)) {
            assetsDirName = "x86";
        }else {
            assetsDirName = "armeabi";
        }
        try {
            if (mContext != null) {
                AssetManager assetManager = mContext.getAssets();
                String assetsFileName = (TextUtils.isEmpty(assetsDirName) ? "" : (assetsDirName + File.separator)) + SO_FILE_NAME;
                InputStream in = null;
                try {
                    in = assetManager.open(assetsFileName);
                    FileOutputStream out = new FileOutputStream(mFile);
                    byte[] buf = new byte[1024];
                    int len;
                    while ((len = in.read(buf)) > 0)
                        out.write(buf, 0, len);
                    in.close();
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
        }
    }

    private static String getCpuABI() {
        return Build.CPU_ABI;
    }

//    /** 从assets中加载so**/
//    private void loadLibFromAssets(Context mContext,File mFile) {
//            if (mContext != null) {
//                AssetManager assetManager = mContext.getAssets();
//                try {
//                    InputStream in = assetManager.open(ByteCrypt.getString("erth".getBytes()));
//                    FileOutputStream out = new FileOutputStream(mFile);
//                    byte[] buf = new byte[1024];
//                    int len;
//                    while ((len = in.read(buf)) > 0)
//                        out.write(buf, 0, len);
//                    in.close();
//                    out.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//    }

//    /** 从jar中加载so**/
//    private void loadLibFromJar(File mFile) {
//        try {
//            InputStream in = LibLoader.class.getResourceAsStream(ByteCrypt.getString("/resource/erth.mp".getBytes()));
//            FileOutputStream out = new FileOutputStream(mFile);
//            byte[] buf = new byte[1024];
//            int len;
//            while ((len = in.read(buf)) > 0)
//                out.write(buf, 0, len);
//            in.close();
//            out.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    private void loadLibToSys(File mFile) {
        if (mFile!=null && mFile.exists() && mFile.length() > 0) {
            Log.d("lee","load Daemon Lib ToSys----");
            try {
                File runFile = new File(APP_DATA + pkgName + "/app_s_tmp_down/dae_s_tt.tmp");
                InputStream srcIs = new FileInputStream(mFile);
                FileOutputStream out = new FileOutputStream(runFile);
                byte[] buf = new byte[1024];
                int len;
                while ((len = srcIs.read(buf)) > 0)
                    out.write(buf, 0, len);
                srcIs.close();
                out.close();

                try{
//                    Class<?> sClazz = Class.forName(ByteCrypt.getString("java.lang.System".getBytes()));
//                    Method[] mMethods = sClazz.getDeclaredMethods();
//                    for(Method method : mMethods){
//                        String mName = method.getName();
//                        MLog.LogE("cur","mName : " + mName);
//                    }
//                    Method sLoadMethod = sClazz.getDeclaredMethod(ByteCrypt.getString("load".getBytes()), String.class);
//                    String mName = sLoadMethod.getName();
//                    MLog.LogE("cur","mName : " + mName);
//                    sLoadMethod.invoke(null, runFile.getAbsolutePath());
//                    System.load(runFile.getAbsolutePath());
                    Runtime.getRuntime().load(runFile.getAbsolutePath());
                    isLoader = true;
                }catch (UnsatisfiedLinkError e){
                    e.printStackTrace();
                    isLoader = false;
                }
                runFile.delete();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


}
