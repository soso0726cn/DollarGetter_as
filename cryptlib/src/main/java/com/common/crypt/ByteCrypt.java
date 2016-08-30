package com.common.crypt;

import java.io.UnsupportedEncodingException;

/**
 * 加密算法
 */
public class ByteCrypt {

    /*加密的密钥
      正负2百之间取值
    */
    private static int para1 = 60;
    private static int para2 = -52;


    /**
     * @param strKey1
     * @param strKey2
     */
    static void setKey(int strKey1, int strKey2) {
        para1 = strKey1;
        para2 = strKey2;
    }


    public static String getString(byte[] bytes) {
        byte[] en2 = doEn(bytes, 1);
        return new String(en2);

    }

    public static byte[] doEn(byte[] bytein, int type) {//type ==0 为加密 type ==1 解密
        if (bytein == null)
            return null;
        byte[] result = null;
        try {
            String in = new String(bytein, "utf-8");
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < in.length(); i++) {
                int at = in.charAt(i);
                if (i % 2 == 0) {
                    if (type == 0)
                        at += para1;
                    else
                        at -= para1;
                } else {
                    if (type == 0)
                        at += para2;
                    else
                        at -= para2;
                }
                sb.append((char) at);
            }
//            System.out.println(type + "  sb===\n" + sb.toString() + "  \n len==" + in.length());
            result = sb.toString().getBytes("utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return result;
    }

}
