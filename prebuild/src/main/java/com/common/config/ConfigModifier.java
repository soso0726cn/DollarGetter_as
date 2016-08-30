package com.common.config;

import com.common.crypt.StringReplacer;
import com.common.io.IOUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

/**
 * Created by lijichuan on 16/3/9.
 */
public class ConfigModifier {

    public static void modify(String path, String tag, String target){
        File modify = findFile(path);
        String origin = findOneLine(modify,tag);
        if (isValid(origin) && isValid(target)){
            replaceOneLine(modify,origin,target);
        }
    }

    public static void modify(String path, String start, String end, String target){
        File modify = findFile(path);
        if (isValid(target)){
            replaceStartToEnd(modify,start,end,target);
        }
    }

    private static void replaceOneLine(File modify, String original, String target) {
        System.out.println("file : " + modify.getAbsolutePath());
        System.out.println("origin : " + original);
        System.out.println("target : " + target);
        System.out.println("--------------------------------------------------------------------------");
        StringReplacer.replaceCotent(modify, original, target);
    }

    private static void replaceStartToEnd(File modify, String start,String end, String target) {
        System.out.println("file : " + modify.getAbsolutePath());
        System.out.println("start : " + start + "; end : " + end);
        System.out.println("target : " + target);
        System.out.println("--------------------------------------------------------------------------");
        StringReplacer.replaceContent(modify, start, end, target);
    }

    private static String findOneLine(File modify, String tag) {
        BufferedReader bufferedReader = null;
        try {
            bufferedReader = new BufferedReader(
                    new InputStreamReader(
                            new FileInputStream(modify)));
            String line = null;
            while( (line = bufferedReader.readLine()) != null) {
                //读包含该标签的那一行
                if (line.contains(Const.PATTERN) && line.contains(tag)){
                    return line;
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(bufferedReader);
        }
        return null;
    }

    private static File findFile(String configPath) {
        return new File(PackConfig.projectPath, configPath);
    }

    private static boolean isValid(String string) {
        return (string != null && !"".equals(string.trim()));
    }
}
