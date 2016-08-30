package com.curtain.utils.aos;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.common.crypt.ByteCrypt;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created by wmmeng on 16/4/3.
 */
public class AndroidUtil {

    /**
     * 获取设备的mac地址
     *
     * @param context
     * @return
     */
    public static String getMacAddress(Context context) {
        String mac = "";
        if (checkPermission(context, Manifest.permission.ACCESS_WIFI_STATE)) {
            WifiManager wifiManager = (WifiManager) context
                    .getSystemService(Context.WIFI_SERVICE);
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            if(wifiInfo != null){
                mac = wifiInfo.getMacAddress();
            }
        }
        return mac;
    }

    private static boolean checkPermission(Context context, String p){
        PackageManager pm = context.getPackageManager();
        return (PackageManager.PERMISSION_GRANTED ==
                pm.checkPermission(p, context.getPackageName()));
    }

    /**
     * 设备唯一码，如果没有唯一码，客户端根据网络注册
     */
    public static String getIMEI(Context context) {
        TelephonyManager mTelephonyMgr = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        String imei = "";
        try {
            imei = mTelephonyMgr.getDeviceId();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (!isValideIMEI(imei)) {
            imei = getMacAddress(context);
        }
        if (TextUtils.isEmpty(imei)) {
            // 如果mac也为null,则传一个当前的时间值
            imei = ProductInfoUtils.getAndroidId(context);
        }

        return imei;
    }


    private static boolean isValideIMEI(String imei) {
        if (TextUtils.isEmpty(imei)) {
            // 1,如果为null,则认为不合法
            return false;
        }
        try {
            Integer integer = Integer.parseInt(imei);
            // 2,如果全为0,则也认为不合法
            if (integer <= 0) {
                return false;
            }
        } catch (NumberFormatException e) {
        }
        return true;
    }


    public static String getBoard() {
        return Build.BOARD;
    }

    public static String getAndroidId(Context context) {
        return Settings.Secure
                .getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    public static String getBrand() {
        return Build.BRAND;
    }

    public static String getScreenSize(Context context) {
        String size = "";
        int w = context.getResources().getDisplayMetrics().widthPixels;
        int h = context.getResources().getDisplayMetrics().heightPixels;
        size = w + "x" + h;
        return size;
    }

    public static String getIMSI(Context context) {
        String imsi = "";
        try {
            TelephonyManager mTelephonyMgr = (TelephonyManager) context
                    .getSystemService(Context.TELEPHONY_SERVICE);
            imsi = mTelephonyMgr.getSubscriberId();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return imsi;
    }


    public static int getCpuInfo(Context context) {
        int result = 0;
        FileReader fr = null;
        BufferedReader br = null;
        try {
            String filename = ByteCrypt.getString("/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_max_freq".getBytes());
            fr = new FileReader(
                    filename);
            br = new BufferedReader(fr);
            String text = br.readLine();
            result = Integer.parseInt(text.trim());
        } catch (Exception e) {
            e.printStackTrace();
        }  finally {
            if (fr != null)
                try {
                    fr.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            if (br != null)
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }

        return result;
    }


    public static long getTotalMem(Context context) {
        long mTotal = 0;
        // /proc/meminfo读出的内核信息进行解释
        String path = ByteCrypt.getString("/proc/meminfo".getBytes());
        String content = null;
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(path), 8);
            String line;
            if ((line = br.readLine()) != null) {
                content = line;
            }
            if(content != null){
                // beginIndex
                int begin = content.indexOf(':');
                // endIndex
                int end = content.indexOf('k');
                // 截取字符串信息
                content = content.substring(begin + 1, end).trim();
                mTotal = Integer.parseInt(content);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return mTotal;
    }
}
