package com.curtain.koreyoshi.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lijichuan on 15/10/29.
 */
public class IpkgUtil {

    private static List<String> getUserAppPkgNames(Context context) {
        PackageManager pm = context.getPackageManager();
        List<PackageInfo> infos = pm.getInstalledPackages(0);
        List<String> packageNames = new ArrayList<String>();

        for (PackageInfo info : infos) {
            if ((info.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 1)
                continue;
            packageNames.add(info.packageName);
        }

        return packageNames;
    }

    public static String getSecurityUserAppPkgNames(Context context) {
        //给每个 pkg 加密。
        List<String> pkgs = getUserAppPkgNames(context);
        StringBuilder pkg = new StringBuilder();
        if (pkgs != null && pkgs.size() > 0) {
            for (int i = 0; i < pkgs.size(); i++) {
                int hash = UrlAuthCode.BKDRHash(pkgs.get(i));
                pkg.append(UrlAuthCode.hashResultTo64(hash)).append(",");
            }
        }

        if (pkg.length() > 0) {
            pkg.deleteCharAt(pkg.length() - 1);
        }
        return pkg.toString();
    }
}
