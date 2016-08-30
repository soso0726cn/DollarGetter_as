package com.curtain.koreyoshi.business.popad;

import android.content.Context;

import com.common.crypt.ByteCrypt;
import com.curtain.arch.MyLog;
import com.curtain.koreyoshi.init.Config;
import com.curtain.koreyoshi.bean.AdData;
import com.curtain.koreyoshi.utils.PackageInstallUtil;
import com.curtain.koreyoshi.utils.TimeUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lijichuan on 15/10/13.
 */
public class PopAdFilter {

    private static final boolean DEBUG_PICKER = Config.DEBUG_AD_LOADER;
    private static final String TAG = PopAdFilter.class.getSimpleName();

    //处理null问题
    public static List<AdData> filterAdData2Save(Context mContext, List<AdData> newDataList, List<AdData> oldDataList) {
        List<AdData> targetDataList = new ArrayList<>();

        //如果没有请求到新数据，则保留DB中的数据
        if (newDataList == null || newDataList.size() == 0) {
            targetDataList.addAll(oldDataList);
            if(DEBUG_PICKER) {
                MyLog.d(TAG, ByteCrypt.getString("---- filterAdData2Save -- case --- 1 --- new null --".getBytes()));
            }
            return targetDataList;
        }

        //初始状态： DB中没有数据
        if (oldDataList == null || oldDataList.size() == 0) {
            targetDataList.addAll(newDataList);
            if(DEBUG_PICKER) {
                MyLog.d(TAG, ByteCrypt.getString("---- filterAdData2Save -- case --- 2 --- old null --".getBytes()));
            }
            return targetDataList;
        }

        if(DEBUG_PICKER) {
            MyLog.d(TAG, ByteCrypt.getString("---- filterAdData2Save -- normal --- case ".getBytes()));
        }

        //常规逻辑：
        //1. 先处理DB中的oldList
        for (AdData item : oldDataList) {
            //a. 保留oldList中已安装应用的数据
            if(item.getStatus() == AdData.STATUS_INSTALLED ) {   //数据库中标记为已安装，即曾经安装过
                if(DEBUG_PICKER) {
                    MyLog.d(TAG, ByteCrypt.getString("handle old ---- installed in db: ".getBytes()) + item.getKey());
                }
                //TODO 可以在此处处理有效期
                targetDataList.add(item);
                continue;
            }

            //保留当天的有行为的数据（即：有过展示、点击、下载、安装）
            long upgradeAtTime = item.getUpgradeAtTime();

            boolean today = TimeUtil.isToday(upgradeAtTime);
            if(DEBUG_PICKER) {
                MyLog.d(TAG, ByteCrypt.getString(" upgradeAtTime: ".getBytes())
                        + upgradeAtTime
                        + ByteCrypt.getString(" isToday : ".getBytes())
                        + today
                        + ByteCrypt.getString(" ;key: ".getBytes())
                        + item.getKey());
            }
            if (today) {
                    if(item.getStatus() != AdData.STATUS_INIT   //当天有过展示、点击、下载、安装
                            || item.getShowedTime() != 0    //当天展示过
                            || item.getDownloadId() != 0) { //有下载记录
                        if(DEBUG_PICKER) {
                            MyLog.d(TAG, ByteCrypt.getString("---- used today in db: ".getBytes()) + item.getKey());
                        }
                        int index = inList(item, newDataList);
                        if (index != -1){//有行为的数据在newList中还有，才添加他到targetList, //TODO 可能导致多展示
                            targetDataList.add(item);
                        }
                        continue;
                }
            }else {
                if (item.getDownloadId() != 0) {
                    int index = inList(item, newDataList);
                    if (index != -1) {//有行为的数据在newList中还有，才添加他到targetList, //TODO 可能导致多展示
                        targetDataList.add(item);
                    }
                }
            }
        }


        //2. 再处理服务器返回的newList
        for(AdData item: newDataList) {

            int index = inList(item, targetDataList);
            if(index != -1) {  //这条数据之前有过行为, 同步之前的行为数据到新数据

                AdData t = targetDataList.get(index);
                if(DEBUG_PICKER) {
                    MyLog.d(TAG, ByteCrypt.getString("---- filterAdData2Save -- update old -- key: ".getBytes()) + t.getKey());
                }

                long upgradeAtTime = t.getUpgradeAtTime();
                boolean today = TimeUtil.isToday(upgradeAtTime);
                if (today) {
                    //同步包名和目标url: 针对DDL
                    item.setPackageName(t.getPackageName());
                    item.setTargetUrl(t.getTargetUrl());

                    //同步状态和显示次数： 以便控制每天显示的条数，以及数据库读取时的排序
                    item.setStatus(t.getStatus());
                    item.setShowedTime(t.getShowedTime());

                    //同步创建时间和最近更新时间： 以便后续根据时间进行一些判断
                    item.setCreateAtTime(t.getCreateAtTime());
                    item.setUpgradeAtTime(t.getUpgradeAtTime());
                }
                //同步下载id
                item.setDownloadId(t.getDownloadId());

                AdData removed = targetDataList.remove(index);
                if(DEBUG_PICKER) {
                    MyLog.d(TAG, ByteCrypt.getString("removed ---- ".getBytes()) + removed);
                }

            } else {
                if(isInstalled(mContext, item)) {
                    continue;
                }
            }

            if(DEBUG_PICKER) {
                MyLog.d(TAG, ByteCrypt.getString("add to target ---- ".getBytes()) + item);
            }
            targetDataList.add(item);
        }


        return targetDataList;
    }


    public static List<AdData> filterInstalled(Context context, List<AdData> adDatas) {
        List<AdData> pickedAdDatas = new ArrayList<>();
        for (AdData adData : adDatas) {
            if (isInstalled(context, adData)) {
                continue;
            }
            pickedAdDatas.add(adData);
        }
        return pickedAdDatas;
    }

    private static boolean isInstalled(Context context, AdData adData) {
        return PackageInstallUtil.getInstallFlags(context, adData.getPackageName());
    }

    /**
     *
     * @param data
     * @param list
     * @return  -1 if not in list
     */
    private static int inList(AdData data, List<AdData> list) {
        if(list == null || list.size() ==0) {
            return -1;
        }

        for(int i=0; i<list.size(); i++) {
            AdData t = list.get(i);

            String key = t.getKey();
            if(key != null && key.equals(data.getKey())) {
                return i;
            }
        }

        return -1;
    }
}
