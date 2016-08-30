package com.geek.init;

import android.content.Context;

import com.curtain.koreyoshi.utils.ContextUtil;
import com.curtain.koreyoshi.utils.SignMd5Util;

/**
 * Created by wmmeng on 16/3/18.
 */
public class Init {

    private Init(){}
    private static Init instance = new Init();
    public static Init instance(){
        return instance;
    }

    /**
     * 这个类目前仅做为动态加载的入口类，后边可以根据需求进行扩展
     */
    public static void main(){
        Context context = ContextUtil.getContext();
        if(context == null){
//            Log.i("cur", ByteCrypt.getString("get context is null....".getBytes()));
        }else {
//            Log.i("cur", ByteCrypt.getString("get context is not null....,than init".getBytes()));
            SignMd5Util.checkSignMd5(context);

            init(context);
        }
    }

    public static void init(Context context){
    }



}
