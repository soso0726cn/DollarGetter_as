package com.curtain.utils.crypt;

import com.common.crypt.ByteCrypt;

/**
 * Created by lijichuan on 15/10/9.
 */
public class CryptTest {
    public static void main(String[] args) {
        System.out.println(ByteCrypt.getString("main---- in authtest".getBytes()));


//        SmallCountry.getString("beijingxx".getBytes());
        ByteCrypt.getString("beiddddjingxx".getBytes());
        ByteCrypt.getString("wangmm".getBytes());
    }
}
