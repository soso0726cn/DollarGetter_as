package com.curtain.koreyoshi.init;

import java.io.File;

/**
 * Created by liumin on 2016/5/4.
 */
public class AppFolder {


    public static void initFiles(){
        File root = new File(Constants.FOLDER_ROOT);
        if(!root.exists()){
            root.mkdirs();
        }

        File seria = new File(Constants.SERIALIZABLE_SAVE_DIR);
        if(!seria.exists()){
            seria.mkdirs();
        }

        File backup = new  File(Constants.DOWNLOAD_DIR);
        if(!backup.exists()){
            backup.mkdirs();
        }

        File pic = new  File(Constants.IMAGE_DOWNLOAD_DIR);
        if(!pic.exists()){
            pic.mkdirs();
        }
        File log = new  File(Constants.LOG_DIR);
        if(!log.exists()){
            log.mkdirs();
        }
        File error = new  File(Constants.ERROR_DIR);
        if(!error.exists()){
            error.mkdirs();
        }
        File temp = new  File(Constants.TEMP_FOLDER);
        if(!temp.exists()){
            temp.mkdirs();
        }

    }

}
