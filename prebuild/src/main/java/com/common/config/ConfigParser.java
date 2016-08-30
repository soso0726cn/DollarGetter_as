package com.common.config;

import com.common.io.FileUtils;
import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;

/**
 * Created by lijichuan on 16/1/21.
 */
public class ConfigParser {

    public static ConfigBean getConfigBean() {
        File configFile = new File(PackConfig.projectPath, Const.CONFIG_SUB_PATH);

        System.out.println("configFile Path: " + configFile.getAbsolutePath() + " ;exists: " + configFile.exists());

        if(!configFile.exists()) {
            System.out.println("do NOT have config file");
            return null;
        }

        //读取配置文件
        ConfigBean bean = readConfig(configFile);
        System.out.println("configs: " + bean.getConfigs());

        return bean;
    }

    /**
     * 把config.json配置文件解析后读到Map
     * @param configFile
     * @return
     */
    private static ConfigBean readConfig(File configFile) {
        String content = null;
        try {
            content = FileUtils.readFileToString(configFile);
            System.out.println(content);

            Gson gson = new Gson();
            return gson.fromJson(content, ConfigBean.class);


        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
