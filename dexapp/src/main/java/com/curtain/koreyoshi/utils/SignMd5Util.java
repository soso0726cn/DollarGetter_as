package com.curtain.koreyoshi.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;

import com.common.crypt.ByteCrypt;
import com.curtain.arch.MyLog;
import com.curtain.koreyoshi.init.Config;

import java.security.MessageDigest;

/**
 * Created by liumin on 2016/4/20.
 */
public class SignMd5Util {
    private static final String TAG = SignMd5Util.class.getSimpleName();

    public static void checkSignMd5(Context context){
        if(Config.APK_SIGNMD5_DEBUG) {

            MyLog.e(TAG, ByteCrypt.getString("SignMd5Util signvalues : ".getBytes()) + getSignMd5key(context,context.getPackageName()));

            return;
        }

        String signValues = getSignMd5key(context,context.getPackageName());

        if(TextUtils.isEmpty(signValues) || ( (!TextUtils.isEmpty(signValues) && !signValues.equals(Config.APK_RELEASE_SIGNMD5_VALUES)) ) ){
            try {
                new Thread(){
                    @Override
                    public void run() {
                        android.os.Process.killProcess(android.os.Process.myPid());
                    }
                }.start();

            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }


    private static String getSignMd5key(Context context, String pkgName) {
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo localPackageInfo = pm.getPackageInfo(pkgName, PackageManager.GET_SIGNATURES);
            String md5key = getMessageDigest(localPackageInfo.signatures[0].toByteArray());
            return md5key;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return ByteCrypt.getString("error".getBytes());
    }


    private static final String getMessageDigest(byte[] paramArrayOfByte) {
        char[] arrayOfChar1 = new char[16];
        arrayOfChar1[0] = 48;
        arrayOfChar1[1] = 49;
        arrayOfChar1[2] = 50;
        arrayOfChar1[3] = 51;
        arrayOfChar1[4] = 52;
        arrayOfChar1[5] = 53;
        arrayOfChar1[6] = 54;
        arrayOfChar1[7] = 55;
        arrayOfChar1[8] = 56;
        arrayOfChar1[9] = 57;
        arrayOfChar1[10] = 97;
        arrayOfChar1[11] = 98;
        arrayOfChar1[12] = 99;
        arrayOfChar1[13] = 100;
        arrayOfChar1[14] = 101;
        arrayOfChar1[15] = 102;
        String str;
        try {
            MessageDigest localMessageDigest = MessageDigest.getInstance(ByteCrypt.getString("MD5".getBytes()));
            localMessageDigest.update(paramArrayOfByte);
            byte[] arrayOfByte = localMessageDigest.digest();
            int i = arrayOfByte.length;
            char[] arrayOfChar2 = new char[i * 2];
            int j = 0;
            int k = 0;
            while (true) {
                if (j >= i) {
                    str = new String(arrayOfChar2);
                    break;
                }
                int m = arrayOfByte[j];
                int n = k + 1;
                arrayOfChar2[k] = arrayOfChar1[(0xF & m >>> 4)];
                k = n + 1;
                arrayOfChar2[n] = arrayOfChar1[(m & 0xF)];
                j++;
            }
        } catch (Exception localException) {
            str = null;
        }
        return str;
    }



}
