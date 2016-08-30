package com.curtain.koreyoshi.business.gptracker.trackping;

import android.content.Context;
import android.text.TextUtils;

import com.curtain.koreyoshi.business.gptracker.ReferData;
import com.curtain.koreyoshi.business.gptracker.db.ReferDao;

import java.util.Set;

/**
 * Created by leejunpeng on 2015/12/11.
 */
public class TrackDataManager {
    private static final String TAG = TrackDataManager.class.getSimpleName();

    private Context context;

    private static TrackDataManager instance;

    public static TrackDataManager getInstance(Context context) {
        if (instance == null){
            instance = new TrackDataManager(context);
        }
        return instance;
    }

    private TrackDataManager(Context context) {
        this.context = context;
    }


    /**
     * 判断refer表中是否包含这个包名的refer
     * @param key 包名
     * @return 如果有refer返回true
     *         否则返回false
     */
    public boolean contains(String key) {
        ReferDao referDao = new ReferDao(context);
        Set<String> monitorKeys = referDao.getAllPnameOfReffer();
        return monitorKeys.contains(key);
    }

    /**
     * 根据包名查询refer表中的对应refer
     * @param key 报名
     * @return 返回包名的对应refer
     *         返回null表明没有refer或refer为空
     */
    public String getValueForKey(String key) {
        ReferDao referDao = new ReferDao(context);
        ReferData referData = referDao.queryReferByPName(key);
        if (referData != null){
            String referrer = referData.getRefer();
            if (!TextUtils.isEmpty(referrer)) {
                return referrer;
            }
        }
        return null;
    }


    /**
     * 根据报名删除对应refer
     * @param key
     */
    public void removeKey(String key) {
        ReferDao referDao = new ReferDao(context);
        referDao.deleteReferByPname(key);
    }

    public Set<String> getAllKeys() {
        ReferDao referDao = new ReferDao(context);
        return referDao.getAllPnameOfReffer();
    }

}
