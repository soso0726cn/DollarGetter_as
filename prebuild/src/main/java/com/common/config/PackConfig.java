package com.common.config;

import com.common.crypt.TagReader;
import com.common.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Created by lijichuan on 16/3/10.
 */
public class PackConfig {

    protected static String projectPath;
    protected static boolean releaseApp;

    /**
     * 支持json配置：在json文件中对打包需求进行配置，编译前根据配置对相关代码进行修改，以打出指定配置的包
     * @param args
     */
    public static void main(String[] args) {

        parseParams(args);
        System.out.println("projectPath: " + projectPath + " ;releaseApp: " + releaseApp);

        //读取配置
        ConfigBean configs = ConfigParser.getConfigBean();

        if (configs == null) {
            return;
        }
        ConfigBean.ConfigsEntity configsEntity = configs.getConfigs();
        if (configsEntity == null) {
            return;
        }

        if ("off".equals(configsEntity.getSWITCH())){
            System.out.println("bye bye !!!");
            return;
        }


        File values = new File(projectPath, Const.CONFIG_VALUE_PATH);
        System.out.println("config values path: " + values.getAbsolutePath() + " ;exists: " + values.exists());

        String content = null;
        try {
            content = FileUtils.readFileToString(values, StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(content == null || "".equals(content)) {
            System.out.println("can NOT read config values");
            return;
        }

        //配置ByteCryptKey
        String byteCryptKey1 = configsEntity.getBYTECRYPT_KEY1();
        String byteCryptKey2 = configsEntity.getBYTECRYPT_KEY2();
        configByteCrypt(content,byteCryptKey1,byteCryptKey2);

        //配置Action
        String pkgAddAction = configsEntity.getPACKAGE_ADD_ACTION();
        String notiClickAction = configsEntity.getNOTI_CLICKED_ACTION();
        configPkgAction(content,pkgAddAction,notiClickAction);

        //配置渠道
        String channel = configsEntity.getCHANNEL();
        configChannel(content, channel);

        //配置接口
        String inter = configsEntity.getINTERFACE();
        configUrl(content, inter);

        //配置services的action
        String action = configsEntity.getREVIVE_ACTION();
        configRevive(content, action);

        //配置soprotect工程包名
        String pkgName = configsEntity.getPKG_NAME();
        configPkgName(content,pkgName);

        //配置so加密key值
        String soKey = configsEntity.getSO_KEY();
        configSoKey(content,soKey);



//        if(releaseApp) {
//            //配置复活
//            String action = configsEntity.getREVIVE_ACTION();
//            System.out.println("action : " + action);
//            configRevive(content, action);
//
//            //配置ep
//            String epClass = configsEntity.getEP_MAIN_CLAZZ();
//            configEp(content, epClass);
//
//            //配置icon
//            String icon = configsEntity.getICON();
//            configIcon(content, icon);
//        } else {
//            //配置渠道
//            String channel = configsEntity.getCHANNEL();
//            configChannel(content, channel);
//
//            //配置接口
//            String inter = configsEntity.getINTERFACE();
//            configUrl(content, inter);
//
//            //配置log等级
//            int logLevel = configsEntity.getLOG_LEVEL();
//            configLogLevel(content, logLevel);
//        }
    }

    private static void configLogLevel(String content, int logLevel) {
        String logLevelInStrategy = TagReader.readContent(content, "<log_level_strategy>", "</log_level_strategy");
        if (isValid(logLevelInStrategy)){
            String logLevelTargetInStrategy = logLevelInStrategy.replace(Const.LOG_LEVEL_TEM, String.valueOf(logLevel));
            ConfigModifier.modify(Const.STRATRGYSP_JAVA_PATH, Const.LOG_LEVEL_TAG, logLevelTargetInStrategy);
        }

        String logLevelInStatistics = TagReader.readContent(content, "<log_level_statistics>", "</log_level_statistics");
        if (isValid(logLevelInStatistics)){
            String logLevelTargetInStatistics = logLevelInStatistics.replace(Const.LOG_LEVEL_TEM, String.valueOf(logLevel));
            ConfigModifier.modify(Const.STATISTICS_JAVA_PATH, Const.LOG_LEVEL_TAG, logLevelTargetInStatistics);
        }
    }

    private static void configIcon(String content, String icon) {
        if (isValid(icon)) {
            if ("yes".equals(icon) || "no".equals(icon)) {
                String iconTarget = TagReader.readContent(content, "<icon_" + icon + ">", "</icon_" + icon);
                System.out.println("icon: " + iconTarget);
                ConfigModifier.modify(Const.MANIFEST_XML_PATH, Const.ICON_TAG_START, Const.ICON_TAG_END, iconTarget);
            }
        }
    }

    private static void configEp(String content, String epClass) {
        String epPermissionTarget = null;
        if (isValid(epClass)){
            //配置action
            String epClassTem = TagReader.readContent(content, "<ep_main_class>", "</ep_main_class");
            if (isValid(epClassTem)) {
                String epClassTarget = epClassTem.replace(Const.EP_MAIN_CLASS_TEM, epClass);
                System.out.println("epClass: " + epClassTarget);
                ConfigModifier.modify(Const.EP_JAVA_PATH, Const.EP_MAIN_CLASS_TAG, epClassTarget);
            }

            //配置权限
            epPermissionTarget = TagReader.readContent(content, "<ep_permission_yes>", "</ep_permission_yes");
        }else {
            epPermissionTarget = TagReader.readContent(content, "<ep_permission_no>", "</ep_permission_no");
        }
        System.out.println("ep_permission_yes: " + epPermissionTarget);
        ConfigModifier.modify(Const.MANIFEST_XML_PATH, Const.EP_PERMISSION_TAG_START, Const.EP_PERMISSION_TAG_END, epPermissionTarget);
    }


    private static void configByteCrypt(String content, String key1,String key2) {
        if (isValid(key1) && isValid(key2)){
            String byteKey1 = TagReader.readContent(content, "<bytecrypt_key1>", "</bytecrypt_key1>");
            if (isValid(byteKey1)) {
                String actionTargetInMani = byteKey1.replace(Const.BYTECRYPT_TEM, key1);
                ConfigModifier.modify(Const.BYTECRYPT_JAVA_PATH, Const.BYTECRYPT_TAG1, actionTargetInMani);
            }
            String byteKey2 = TagReader.readContent(content, "<bytecrypt_key2>", "</bytecrypt_key2>");
            if (isValid(byteKey2)){
                String actionTargetInDaem = byteKey2.replace(Const.BYTECRYPT_TEM, key2);
                ConfigModifier.modify(Const.BYTECRYPT_JAVA_PATH, Const.BYTECRYPT_TAG2, actionTargetInDaem);
            }
        }
    }


    private static void configSoKey(String content, String key) {
        if (isValid(key) ){
            String so_cpp_key = TagReader.readContent(content, "<so_cpp_key>", "</so_cpp_key>");
            if (isValid(so_cpp_key)) {
                String actionTargetInMani = so_cpp_key.replace(Const.SO_KEY_TEM, key);
                ConfigModifier.modify(Const.SO_CPP_KEY_PATH, Const.SO_CPP_TAG, actionTargetInMani);
            }
            String so_ftoc_key = TagReader.readContent(content, "<so_ftoc_key>", "</so_ftoc_key>");
            if (isValid(so_ftoc_key)){
                String actionTargetInDaem = so_ftoc_key.replace(Const.SO_KEY_TEM, key);
                ConfigModifier.modify(Const.FTOC_KEY_PATH, Const.SO_FTOC_TAG, actionTargetInDaem);
            }
        }
    }


    private static void configPkgName(String content, String pkgName) {
        if (isValid(pkgName) ){
            String pkgname = TagReader.readContent(content, "<pkg_name>", "</pkg_name>");
            if (isValid(pkgname)) {
                String actionTargetInMani = pkgname.replace(Const.PKG_NAME_TEM, pkgName);
                ConfigModifier.modify(Const.FTOC_KEY_PATH, Const.PKG_NAME_TAG, actionTargetInMani);
            }
        }
    }

    private static void configPkgAction(String content, String addAction,String clickAction) {
        if (isValid(content)){
            String actionPkgInMani = TagReader.readContent(content, "<pkg_add_action_mani>", "</pkg_add_action_mani>");
            if (isValid(actionPkgInMani)) {
                String actionTargetInMani = actionPkgInMani.replace(Const.REVIVE_ACTION_TEM, addAction);
                ConfigModifier.modify(Const.MANIFEST_XML_PATH, Const.PKG_ADD_ACTION_TAG, actionTargetInMani);
                ConfigModifier.modify(Const.DEXAPP_MANI_PATH, Const.PKG_ADD_ACTION_TAG, actionTargetInMani);
            }
            String actionNotiInMani= TagReader.readContent(content, "<noti_click_action_mani>", "</noti_click_action_mani>");
            if (isValid(actionNotiInMani)){
                String actionTargetInDaem = actionNotiInMani.replace(Const.REVIVE_ACTION_TEM, clickAction);
                ConfigModifier.modify(Const.MANIFEST_XML_PATH, Const.NOTI_CLICK_ACTION_TAG, actionTargetInDaem);
                ConfigModifier.modify(Const.DEXAPP_MANI_PATH, Const.NOTI_CLICK_ACTION_TAG, actionTargetInDaem);
            }

            String actionPkgInClass = TagReader.readContent(content, "<pkg_add_action_class>", "</pkg_add_action_class>");
            String actionClickInClass= TagReader.readContent(content, "<noti_click_action_class>", "</noti_click_action_class>");

            if (isValid(actionPkgInClass) ) {
                String actionTargetInMani = actionPkgInClass.replace(Const.REVIVE_ACTION_TEM, addAction);
                ConfigModifier.modify(Const.DEXAPP_CONSTANTS_JAVA_PATH, Const.PKG_ADD_ACTION_TAG, actionTargetInMani);
                ConfigModifier.modify(Const.SOAPP_CONSTANTS_JAVA_PATH, Const.PKG_ADD_ACTION_TAG, actionTargetInMani);
            }
            if (isValid(actionClickInClass)){
                String actionTargetInDaem = actionClickInClass.replace(Const.REVIVE_ACTION_TEM, clickAction);
                ConfigModifier.modify(Const.DEXAPP_CONSTANTS_JAVA_PATH, Const.NOTI_CLICK_ACTION_TAG, actionTargetInDaem);
                ConfigModifier.modify(Const.SOAPP_CONSTANTS_JAVA_PATH, Const.NOTI_CLICK_ACTION_TAG, actionTargetInDaem);
            }
        }
    }


    private static void configRevive(String content, String action) {
        if (isValid(action)){
            //配置manifest
            String actionTemInMani = TagReader.readContent(content, "<revive_action_mani>", "</revive_action_mani>");
            if (isValid(actionTemInMani)) {
                String actionTargetInMani = actionTemInMani.replace(Const.REVIVE_ACTION_TEM, action);
                ConfigModifier.modify(Const.MANIFEST_XML_PATH, Const.REVIVE_ACTION_TAG, actionTargetInMani);
            }
            //配置ReviveDeman
            String actionTemInDaem = TagReader.readContent(content, "<revive_action_daem>", "</revive_action_daem>");
            if (isValid(actionTemInDaem)){
                String actionTargetInDaem = actionTemInDaem.replace(Const.REVIVE_ACTION_TEM, action);
                ConfigModifier.modify(Const.REVIEVEDAEMON_JAVA_PATH, Const.REVIVE_ACTION_TAG, actionTargetInDaem);
            }
        }
    }

    private static void configUrl(String content, String inter) {
        if (isValid(inter)){
            if ("km".equals(inter) || "aff".equals(inter) || "nuts".equals(inter) || "52".equals(inter)) {
                //配置url
                String urlTarget = TagReader.readContent(content, "<url_" + inter + ">", "</url_" + inter);
                System.out.println("url: " + urlTarget);
                ConfigModifier.modify(Const.CONSTANTS_JAVA_PATH, Const.INTER_TAG_START, Const.INTER_TAG_END, urlTarget);
                //配置group
                String groupTarget = TagReader.readContent(content, "<group_" + inter + ">", "</group_" + inter);
                System.out.println("group: " + groupTarget);
                ConfigModifier.modify(Const.NETHEADUTIL_JAVA_PATH, Const.GROUP_TAG, groupTarget);
            }else {
                System.out.println("no this group !!!");
            }
        }
    }

    private static void configChannel(String content, String channel) {
        if (isValid(channel)) {
            String channelTem = TagReader.readContent(content, "<channel>", "</channel");
            if (isValid(channelTem)) {
                String channelTarget = channelTem.replace(Const.CHANNEL_TEM, channel);
                System.out.println("channel: " + channelTarget);
                ConfigModifier.modify(Const.CHANNEL_JAVA_PATH, Const.CHANNEL_TAG, channelTarget);
            }
        }
    }

    public static boolean isValid(String string){
        return (string != null && !"".equals(string.trim()));
    }

    private static void parseParams(String[] args) {
        for(String arg: args) {
            int indexOf = arg.indexOf(":");
            String key = arg.substring(0,indexOf);
            String value = arg.substring(indexOf +1 );

            System.out.println("key: " + key + " ;value：" + value);

            if(key == null || value == null) {
                continue;
            }

            if("releaseWhat".equals(key)) {
                if("app".equals(value)) {
                    releaseApp = true;
                } else if("dex".equals(value)) {
                    releaseApp = false;
                }
            }
            if("projectPath".equals(key)) {
                projectPath = value;
            }
        }
    }
}
