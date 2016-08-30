package com.sylar.signvalues;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;

import java.security.MessageDigest;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.e("xxx","signvalues : " + getSignMd5key(this,this.getPackageName()));
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
        return "error";
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
            MessageDigest localMessageDigest = MessageDigest.getInstance("MD5");
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
