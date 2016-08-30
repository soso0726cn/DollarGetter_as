package com.curtain.koreyoshi.listener;

/**
 * Created by lijichuan on 15/10/28.
 */

import com.curtain.koreyoshi.bean.AdData;

import java.util.List;

/**
 * 请求广告列表时的回调
 */
public interface AdRequestListener {
    /**
     * list长度为0，表示请求连接正常，但是没有请求到数据
     * @param adDatas
     */
    void onRequestSuccess(List<AdData> adDatas);


    /**
     * 请求失败，与服务器交互失败
     * @param failCode
     */
    void onRequestFailed(int failCode);

}