package com.ecarezone.android.doctor.utils;

import com.ecarezone.android.doctor.config.Constants;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;

/**
 * Created by jifeng.zhang on 14/06/15.
 */
public class PasswordUtil {

    private static boolean isTemporaryFix = false;

    public static String getHashedPassword(String password) {

        if(isTemporaryFix){
            return password;
        }
        else {
//            String temp = new String(Hex.encodeHex(DigestUtils.md5(password + Constants.salt)));
//            return new String(Hex.encodeHex(DigestUtils.md5(temp)));
            return AESUtil.encrypt(password);
        }
    }
}