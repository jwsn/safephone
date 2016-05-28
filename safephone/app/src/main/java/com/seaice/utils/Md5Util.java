package com.seaice.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Md5加密工具类
 * Created by seaice on 2016/3/4.
 */
public class Md5Util {

    /**
     * 对输入的字符串进行MD5加密处理
     *
     * @param val
     * @return 返回加密后的md5字符串
     * @throws NoSuchAlgorithmException
     */
    public static String getMd5(String val) throws NoSuchAlgorithmException {
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        md5.update(val.getBytes());
        byte[] m = md5.digest();
        return getMd5String(m);
    }

    public static String getMd5String(byte[] b) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < b.length; i++) {
            sb.append(b[i]);
        }
        return sb.toString();
    }
}
