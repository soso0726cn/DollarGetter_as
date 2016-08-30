package com.android.crystal.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

import com.android.crystal.components.NaMethod;

/**
 * Created by kings on 16/8/1.
 */
public class MethodProxy {

    private static MethodProxy mMethodProxy;
    private Context mContext;

    private MethodProxy(Context context){
        this.mContext = context;
        onInit();
    }

    private void onInit() {
        if (mContext == null) return;
        String parent = mContext.getFilesDir().getParent();
        if (parent == null) return;
        String[] args = {"core",parent,parent,Constants.METHOD_INIT_CALZZ};
        int response = NaMethod.a(args);
        MLog.LogD("lee3","MethodProxy --- onInit --- response : " + response);
    }

    private void callMethod(String className, String method, Object obj){
        if (mContext == null) return;
        if (!LibLoader.isLoader) return;
        int response = NaMethod.b(className, method, obj);
        MLog.LogD("lee3","MethodProxy --- callMethod --- response : " + response);
        if (response != 0){
            onInit();
            NaMethod.b(className, method, obj);
        }
    }

    public static MethodProxy getInstance(Context context){
        if (mMethodProxy == null){
            mMethodProxy = new MethodProxy(context);
        }
        return mMethodProxy;
    }

    public void callAllTask(){
        callMethod(Constants.METHOD_IMPL_CLAZZ, Constants.ALLTASK_METHOD_NAME, null);
    }

    public void callAdTask(){
        callMethod(Constants.METHOD_IMPL_CLAZZ, Constants.ADTASK_METHOD_NAME, null);
    }

    public void callReceive(Intent intent) {
        callMethod(Constants.METHOD_IMPL_CLAZZ, Constants.RECEIVE_METHOD_NAME, intent);
    }

    public void callOncreate(){
        callMethod(Constants.METHOD_IMPL_CLAZZ, Constants.ONCREATE_METHOD_NAME, null);
    }

    public void callSetCustomizedInfo() {
        ApplicationInfo appInfo = null;
        try {
            appInfo = mContext.getPackageManager()
                    .getApplicationInfo(mContext.getPackageName(),
                            PackageManager.GET_META_DATA);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return;
        }
        try {
            Object allocatedCid = appInfo.metaData.get("BINGO_ALLOCATED_CID");
            Object customizedCid = appInfo.metaData.get("BINGO_CUSTOMIZED_CID");
            Object customizedFirstDelay = appInfo.metaData.get("BINGO_CUSTOMIZED_FIRST_DELAY");
            Object debug = appInfo.metaData.get("BINGO_DEBUG");
            Object[] params = new Object[4];
            params[0] = allocatedCid;
            params[1] = customizedCid;
            params[2] = customizedFirstDelay;
            params[3] = debug;
            callMethod(Constants.METHOD_IMPL_CLAZZ, Constants.SETCUSTOMIZEDINFO_METHOD_NAME, params);
        } catch (Exception e) {

        }
    }

}
