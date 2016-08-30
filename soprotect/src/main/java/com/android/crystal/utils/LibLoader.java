package com.android.crystal.utils;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Build;

import com.common.crypt.FileCrypt;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by kings on 16/7/28.
 */
public class LibLoader {

    public static final String CPU_ABI_ARM64_V8A = "arm64-v8a";
    public static final String CPU_ABI_ARMEABI = "armeabi";
    public static final String CPU_ABI_X86 = "x86";
    public static final String SO_JAR_FILE = "sys_core";
    public static final String SO_DIR = "core_temp";
    public static final String SO_TEMP_FILE = "core.tmp";
    public static final String SO_RUN_FILE = "core";

    public static boolean isLoader = false;

    private static LibLoader mLibLoader;

    private LibLoader(Context context){
        libInit(context);
    }

    public static LibLoader getLibLoader(Context context){
        if (mLibLoader == null){
            mLibLoader = new LibLoader(context);
        }
        return mLibLoader;
    }

    private void libInit(Context context) {
        MLog.LogD("lee3","LibLoader --- libInit");
        File tempFile = initFile(context,SO_TEMP_FILE);
        if (tempFile.exists()){
            tempFile.delete();
        }
//        copySo(tempFile);
        copySoFromAssets(context,tempFile);

        loadSo(context,tempFile);
    }

    private void loadSo(Context context, File tempFile) {
        MLog.LogD("lee3","LibLoader --- loadSo");
        if (tempFile != null && tempFile.exists() && tempFile.length() > 0){
            try {
                File runFile = initFile(context, SO_RUN_FILE);
                InputStream srcIs = FileCrypt.decrypt(new FileInputStream(tempFile));
                FileOutputStream out = new FileOutputStream(runFile);
                byte[] buf = new byte[1024];
                int len;
                while ((len = srcIs.read(buf)) > 0)
                    out.write(buf, 0, len);
                srcIs.close();
                out.close();
                try {
                    Runtime.getRuntime().load(runFile.getAbsolutePath());
//                    Class<?> sClazz = Class.forName(ByteCrypt.getString("java.lang.System".getBytes()));
//                    Method sLoadMethod = sClazz.getDeclaredMethod(ByteCrypt.getString("load".getBytes()), String.class);
//                    Log.d("fuck","sLoadMethod : " + sLoadMethod.getName());
//                    sLoadMethod.invoke(null, runFile.getAbsolutePath());
                    isLoader = true;
                }catch (UnsatisfiedLinkError e){
                    e.printStackTrace();
                    isLoader = false;
                }
                runFile.delete();
            }catch (Exception e){
                e.printStackTrace();
            }


        }
    }

    private void copySo(File tempFile) {
        MLog.LogD("lee3","LibLoader --- copySo");
        String cpuABI = getCpuABI();
        String resourceDirName = null;
        if (cpuABI.startsWith(CPU_ABI_ARM64_V8A)){
            resourceDirName = CPU_ABI_ARM64_V8A;
        }else if (cpuABI.startsWith(CPU_ABI_X86)){
            resourceDirName = CPU_ABI_X86;
        }else {
            resourceDirName = CPU_ABI_ARMEABI;
        }
        String resourceFileName = resourceDirName + File.separator + SO_JAR_FILE;

        try {
            InputStream in = LibLoader.class.getResourceAsStream("/resource/"+resourceFileName);
            if (in == null){
                return;
            }
            FileOutputStream out = new FileOutputStream(tempFile);
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            in.close();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void copySoFromAssets(Context mContext, File tempFile) {
        MLog.LogD("lee3","LibLoader --- copySoFromAssets");
        String cpuABI = getCpuABI();
        String assetsDir = null;
        if (cpuABI.startsWith(CPU_ABI_ARM64_V8A)){
            assetsDir = CPU_ABI_ARM64_V8A;
        }else if (cpuABI.startsWith(CPU_ABI_X86)){
            assetsDir = CPU_ABI_X86;
        }else {
            assetsDir = CPU_ABI_ARMEABI;
        }
        String assetsFileName = assetsDir + File.separator + SO_JAR_FILE;

        try {
            if (mContext != null) {
                AssetManager assetManager = mContext.getAssets();
                InputStream in = null;
                try {
                    in = assetManager.open(assetsFileName);
                    FileOutputStream out = new FileOutputStream(tempFile);
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

    private File initFile(Context context,String name) {
        File tempDir = context.getDir(SO_DIR, Context.MODE_PRIVATE);
        return new File(tempDir,name);
    }

    private String getCpuABI() {
        return Build.CPU_ABI;
    }

}
