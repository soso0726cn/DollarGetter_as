package com.slk.daemon.nativ;

import android.content.Context;

import com.slk.daemon.NativeDaemonBase;

public class NativeDaemonAPI extends NativeDaemonBase {

    public NativeDaemonAPI(Context context) {
        super(context);
    }

//    static{
//        try {
//            System.loadLibrary("daemon_api");
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    public native void doDaemon20(String pkgName, String svcName, String daemonPath);

    public native void doDaemon21(String indicatorSelfPath, String indicatorDaemonPath, String observerSelfPath, String observerDaemonPath);
}
