package com.curtain.koreyoshi.utils;

import android.text.TextUtils;

import com.common.crypt.ByteCrypt;
import com.curtain.arch.MyLog;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by leejunpeng on 2015/11/11.
 */
public class EPUtil {
    private static final String TAG = EPUtil.class.getSimpleName();

    private int mode;

    private static EPUtil mEPUtil = new EPUtil();
    private EPUtil(){
        init();
    }

    public static EPUtil getInstance(){
        return mEPUtil;
    }

    public void init(){
        File file1 = new File(ByteCrypt.getString("/system/bin/conbb".getBytes()));
        File file2 = new File(ByteCrypt.getString("/system/xbin/conbb".getBytes()));

        if (file1.exists() || file2.exists()) {
            if(hasPP()){
                mode=1;
            }
        }

        if(mode==0){
            File file3 = new File(ByteCrypt.getString("/system/bin/bybox".getBytes()));
            File file4 = new File(ByteCrypt.getString("/system/xbin/bybox".getBytes()));
            if (file3.exists() || file4.exists()) {
                mode=2;
                if(hasPP()){
                    mode=2;
                }else{
                    mode=0;
                }
            }

        }
    }

    public  boolean hasPP(){
        String result = makeCmd("id");
        if(result.contains(new StringBuffer("ro").append("ot").toString())){
            return true;
        }
        return false;
    }

    public boolean hasRoot() {
        init();
        return mode>0;
    }

    public String makeCmd(String cmd)
    {
        String result = "";
        DataOutputStream dos = null;
        BufferedReader dis = null;
        BufferedReader errorResult = null;
        try
        {
            MyLog.d(TAG, "makeCmd ---- cmd= " + cmd);
            StringBuffer stringBuffer = new StringBuffer();
            if (mode < 2){

                stringBuffer.append("con").append("bb ").append("od2gf").append("04pd9");
            }else{
                stringBuffer.append("byb").append("ox ").append("0xA").append("1B1");
            }

            Process p = Runtime.getRuntime().exec(stringBuffer.toString());
            dos = new DataOutputStream(p.getOutputStream());
            dis = new BufferedReader(new InputStreamReader(p.getInputStream()));
            dos.writeBytes(cmd + "\n");
            dos.writeBytes("exit\n");
            dos.flush();
            String line = null;
            while ((line = dis.readLine()) != null)
            {
                result += line + "\n";
            }

            StringBuilder  errorMsg = new StringBuilder();
            errorResult = new BufferedReader(new InputStreamReader(p.getErrorStream()));
            String s;
            while ((s = errorResult.readLine()) != null) {
                errorMsg.append(s);
            }

            if(TextUtils.isEmpty(result)){
                result = errorMsg.toString();
            }

            p.waitFor();

        }
        catch (Exception e)
        {
            MyLog.d(TAG, "makeCmd ---- exception: " + e.getMessage());
            e.printStackTrace();
        }
        finally
        {
            MyLog.d(TAG,"makeCmd ---- finally: ");
            if (dos != null)
            {
                try
                {
                    dos.close();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
            if (dis != null)
            {
                try
                {
                    dis.close();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }

            if (errorResult != null) {
                try {
                    errorResult.close();
                }catch (IOException e){
                    e.printStackTrace();
                }
            }
        }
        MyLog.d(TAG, "makeCmd ----result: " + result);
        return result;
    }
}
