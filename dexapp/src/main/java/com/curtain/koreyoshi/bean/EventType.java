package com.curtain.koreyoshi.bean;

import com.common.crypt.ByteCrypt;

/**
 * Created by leejunpeng on 2015/10/28.
 */
public class EventType {
    /**广告显示次数**/
    public static final String EVENT_AD_SHOW                        =  ByteCrypt.getString("2z5pwo".getBytes());
    /**广告展现点击安装次数，包括误点（即点了关闭按钮仍然安装的）**/
    public static final String EVENT_AD_SHOW_INSTALL_CLICK          =  ByteCrypt.getString("q93h64".getBytes());
    /**点击关闭按钮次数，不包括误点**/
    public static final String EVENT_AD_SHOW_CANCEL_CLICK           =  ByteCrypt.getString("nsp3hq".getBytes());

    /**广告安装次数，即广告弹出后点击安装按钮
     * 1.待下载完后进行安装
     * 2.已经下载过了，直接安装**/
    public static final String EVENT_AD_INSTALL                     =  ByteCrypt.getString("zavj8p".getBytes());
    /**广告安装成功次数：即收到广告对应的安装包的包安装广播后上传**/
    public static final String EVENT_AD_INSTALL_SUCCEED             =  ByteCrypt.getString("ewlk8h".getBytes());

// -------- 下载相关事件  start----------
    /**广告下载次数： 只包含新建下载任务次数，不含恢复下载次数**/
    public static final String EVENT_AD_DOWNLOAD                    =  ByteCrypt.getString("q0i8u1".getBytes());

    /**广告预下载次数：不包含恢复下载次数**/
    public static final String EVENT_AD_PRE_DOWNLOAD                =  ByteCrypt.getString("eyn916".getBytes());

    /**广告恢复下载次数：网络变化广播等带来的恢复下载(包含预下载的恢复和用户点击广告正常下载的恢复)**/
    public static final String EVENT_AD_RESUME_DOWNLOAD                =  ByteCrypt.getString("eaallrd".getBytes());

    /**广告预下载失败次数：包含恢复下载后下载失败的次数**/
    public static final String EVENT_AD_PRE_DOWNLOAD_FAILED                =  ByteCrypt.getString("eapdf".getBytes());

    /**广告正常下载失败次数：包含恢复下载后下载失败的次数(包含服务器没有对应apk跳转GP)**/
    public static final String EVENT_AD_NORMAL_DOWNLOAD_FAILED                =  ByteCrypt.getString("eandf".getBytes());

    /**广告预下载成功: 预下载成功并且下载的文件是合法的apk。(包含恢复下载后下载成功)**/
    public static final String EVENT_AD_PRE_DOWNLOAD_SUCCEED        =  ByteCrypt.getString("20mqd8".getBytes());

    /**广告预下载成功: 预下载成功但下载的文件不是合法的apk。(包含恢复下载后下载成功)**/
    public static final String EVENT_AD_PRE_DOWNLOAD_SUCCEED_BUT_FILE_ERROR        =  ByteCrypt.getString("eapdsfe".getBytes());



    /**广告正常下载次数： 用户点击带来的下载次数，不包含恢复下载次数**/
    public static final String EVENT_AD_NORMAL_DOWNLOAD             =  ByteCrypt.getString("wwbkux".getBytes());

    /**广告正常下载成功：用户点击的广告，下载成功且下载的文件正确。 (包含恢复下载后下载成功)**/
    public static final String EVENT_AD_NORMAL_DOWNLOAD_SUCCEED     =  ByteCrypt.getString("r5ua96".getBytes());

    /**广告正常下载成功：用户点击的广告，下载成功但下载的文件不是合法的apk。(包含恢复下载后下载成功)**/
    public static final String EVENT_AD_NORMAL_DOWNLOAD_SUCCEED_BUT_FILE_ERROR     =  ByteCrypt.getString("eadsfe".getBytes());
// -------- 下载相关事件  end----------

    //----------------------------------------------------------------------------------------------

    /**dex更新成功次数**/
    public static final String EVENT_DEX_UPDATE_SUCCEED             =  ByteCrypt.getString("dexusu".getBytes());

    /**满足展示条件的次数，即各种展示需要的条件都满足了，广告也有了，就要弹出广告了**/
    public static final String EVENT_AD_READY_TO_SHOW               =  ByteCrypt.getString("earts".getBytes());

    /**广告load成功总次数: 即可以请求到广告，且refer已经获取到，或者是DDL广告**/
    public static final String EVENT_AD_LOADED_SUCCEED              =  ByteCrypt.getString("ealu".getBytes());
    /**广告load失败总次数：请求不到广告，或者GP广告获取不到refer (含load失败后重试次数)**/
    public static final String EVENT_AD_LOADED_FAILED               =  ByteCrypt.getString("ealf".getBytes());
    /**广告load总次数：
     * load条件：1. 亮屏解锁状态；2. 网络连接状态；3. 时间时隔满足；4.展示总次数不够服务器设置的总次数
     * 备注： 包含load失败后的重试次数，load失败后会重试一次，如果重试后仍没有广告，需要等到下个时间间隔才能load
     **/
    public static final String EVENT_AD_LOADED                      =  ByteCrypt.getString("eal".getBytes());

    /**load失败原因1： 请求到的广告都track失败**/
    public static final String EVENT_AD_ALL_TRACK_FAILED            =  ByteCrypt.getString("eaatf".getBytes());
    /**load时数据库中没有数据，即需要去请求服务器获取广告**/
    public static final String EVENT_NOT_AD_IN_LOAD                 =  ByteCrypt.getString("enail".getBytes());
    /**load时数据库中没有可用数据，即数据库中虽然有广告但是
     * 1.已经都安装过需要去请求服务器
     * 2.有未安装的但没refer需要去请求refer
     **/
    public static final String EVENT_NOT_USED_AD_IN_LOAD            =  ByteCrypt.getString("enuail".getBytes());


// -------- 服务器请求相关事件 ----------
    /**从服务器请求广告次数**/
    public static final String EVENT_AD_REQUEST_SERVER              =  ByteCrypt.getString("ears".getBytes());
    /**从服务器请求广告失败次数**/
    public static final String EVENT_AD_REQUEST_SERVER_FAILED       =  ByteCrypt.getString("ealuf".getBytes());
    /**从服务器请求广告成功次数**/
    public static final String EVENT_AD_REQUEST_SERVER_SUCCEES      =  ByteCrypt.getString("ealus".getBytes());
    /**从服务器没有请求到广告数据次数**/
    public static final String EVENT_AD_SIZE_IS_ZERO                =  ByteCrypt.getString("easiz".getBytes());

//----------2015年12月15日新增字段---------
    /**load时候，获取到新的refer**/
    public static final String EVENT_GET_NEW_REFER_IN_LOAD          =  ByteCrypt.getString("ct_egnril".getBytes());
	
    /**监听到用户从gp下载**/
    public static final String EVENT_MONITOR_DOWNLOAD_IN_GP          =  ByteCrypt.getString("ct_emdig".getBytes());
    /**监听到用户从gp下载，从我们服务器能取到offer**/
    public static final String EVENT_MONITOR_GP_REQUEST_SUCCEED      =  ByteCrypt.getString("ct_emgrs".getBytes());

    /**广告预下载调用次数**/
    public static final String EVENT_AD_PRE_DOWNLOAD_CALLED          =  ByteCrypt.getString("ct_eapdc".getBytes());
    /**广告预下载调用次数**/


    /**请求静默接口成功，但没有数据**/
    public static final String EVENT_SILENT_AD_SIZE_IS_ZERO      =  ByteCrypt.getString("ct_esasiz".getBytes());

    /**从服务器请求静默gp广告次数**/
    public static final String EVENT_AD_REQUEST_SERVER_FOR_SILENT              =  ByteCrypt.getString("ct_earsfs".getBytes());
    /**从服务器请求静默gp广告失败次数**/
    public static final String EVENT_AD_REQUEST_SERVER_FAILED_FOR_SILENT       =  ByteCrypt.getString("ct_ealuffs".getBytes());
    /**从服务器请求静默gp广告成功次数**/
    public static final String EVENT_AD_REQUEST_SERVER_SUCCEES_FOR_SILENT      =  ByteCrypt.getString("ct_ealusfs".getBytes());

    /**预下载失败重试cdn次数**/
    public static final String EVENT_PRE_DOWNLOAD_FAILED_AND_RETRY_CDN      =  ByteCrypt.getString("ct_epdfarc".getBytes());

    /**正常下载失败重试cdn次数**/
    public static final String EVENT_NORMAL_DOWNLOAD_FAILED_AND_RETRY_CDN      =  ByteCrypt.getString("ct_endfarc".getBytes());

    /**预载失败重试cdn成功次数**/
    public static final String EVENT_PRE_DOWNLOAD_FAILED_AND_RETRY_CDN_SUCCEED      =  ByteCrypt.getString("ct_epdfarcs".getBytes());

    /**正常载失败重试cdn成功次数**/
    public static final String EVENT_NORMAL_DOWNLOAD_FAILED_AND_RETRY_CDN_SUCCEED      =  ByteCrypt.getString("ct_endfarcs".getBytes());
}
