package com.curtain.koreyoshi.business.gptracker;

import android.content.Context;

import com.curtain.koreyoshi.bean.AdData;
import com.curtain.koreyoshi.utils.PackageInstallUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yangzheng on 15/11/17.
 */
public class TrackPicker {
    public static List<AdData> getPrepareAdData(Context context,List<AdData> adDatas)
    {
        List<AdData> pickedAdDatas = new ArrayList<>();
        for (AdData adData : adDatas){
            //过滤掉DDL的广告
            if (isDDLAdData(context,adData)){
                continue;
            }

            //过滤掉已安装的
            if(isInstalled(context, adData)) {
                continue;
            }

            pickedAdDatas.add(adData);
        }
        return pickedAdDatas;
    }

    private static boolean isInstalled(Context context, AdData adData) {
        return PackageInstallUtil.getInstallFlags(context, adData.getPackageName());
    }


    private static boolean isDDLAdData(Context context,AdData adData) {
        if(adData.getBehave() == 1){
            return true;
        }
        return false;
    }

}
