package com.curtain.koreyoshi.bean;

import com.common.crypt.ByteCrypt;

/**
 * Created by liumin on 2016/5/6.
 */
public class AdField {

    public static final String ID = ByteCrypt.getString("id".getBytes());
    public static final String OFFER_ID = ByteCrypt.getString("offer_id".getBytes());
    public static final String TYPE = ByteCrypt.getString("type".getBytes());
    public static final String BEHAVE = ByteCrypt.getString("behave".getBytes());
    public static final String DATA = ByteCrypt.getString("data".getBytes());
    public static final String IMG_URL = ByteCrypt.getString("img_url".getBytes());
    public static final String TITLE = ByteCrypt.getString("title".getBytes());
    public static final String CONTENT = ByteCrypt.getString("content".getBytes());
    public static final String PNAME = ByteCrypt.getString("pname".getBytes());
    public static final String ICON_URL = ByteCrypt.getString("icon_url".getBytes());
    public static final String SIZE = ByteCrypt.getString("size".getBytes());
    public static final String CLICK_TRACK_URL = ByteCrypt.getString("click_track_url".getBytes());
    public static final String SHOW_URL = ByteCrypt.getString("show_url".getBytes());
    public static final String DOWNLOAD_URL = ByteCrypt.getString("download_url".getBytes());
    public static final String FROM = ByteCrypt.getString("from".getBytes());
    public static final String INSTALL_PATH = ByteCrypt.getString("install_path".getBytes());
}
