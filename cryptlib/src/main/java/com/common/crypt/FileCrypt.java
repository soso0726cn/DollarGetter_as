package com.common.crypt;

import java.io.File;
import java.io.InputStream;

/**
 * Created by lijichuan on 16/1/27.
 */
public class FileCrypt {

    public static final class Config {
        static final boolean DO_FILE_ENCRYPT = true;
        static final String DEFAULT_KEY = "helloworld";
    }

    /**
     * 在gradle中调用， 用以对指定路径的文件进行文件加密，加密完成后替换原文件
     * @param args  文件绝对路径
     */
    public static void main(String[] args) {

        //如果不需要加密，则直接返回
        if(!Config.DO_FILE_ENCRYPT) {
            System.out.printf("NOT need to do file encrypt");
            return;
        }

        if(args.length != 1) {
            System.err.println("No File to encrypt！ need file path as param in args[0]");
        }
        String filePath = args[0];

        if(!new File(filePath).exists()) {
            System.err.println("File to encrypt not exists! ");
        }


        String targetPath = filePath + ".bak";

        XEncryptUtil.encrypt(filePath, targetPath);

        File srcFile = new File(filePath);
        boolean delete = srcFile.delete();

        if(delete) {
            boolean renameTo = new File(targetPath).renameTo(srcFile);
            if(!renameTo) {
                System.err.println("Failed to create encrypt file");
            } else {
                System.out.printf("file encrypt successfully: " + filePath);
            }
        }
    }

    public static InputStream decrypt(InputStream is){
        if(Config.DO_FILE_ENCRYPT) {
            return XEncryptUtil.decrypt(is);
        } else {
            return is;
        }
    }
}
