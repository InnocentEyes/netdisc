package com.qzlnode.netdisc.util;


import java.io.UnsupportedEncodingException;
import java.util.Base64;

/**
 * @author qzlzzz
 * 加密工具类
 */
public class BASE64 {

    private final static Base64.Encoder ENCODER = Base64.getEncoder();
    private final static Base64.Decoder DECODER = Base64.getDecoder();

    /**
     * 给字符串加密
     * @param password
     * @return
     */
    public static String encode(String password) {
        byte[] textByte = null;
        try {
            textByte = password.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String encodedPassword = null;
        if (textByte != null) {
            encodedPassword =ENCODER.encodeToString(textByte);
        }
        return encodedPassword;
    }

    /**
     * 将加密后的字符串进行解密
     * @param encodedPassword
     * @return
     */
    public static String decode(String encodedPassword) {
        String password = null;
        try {
            password = new String(DECODER.decode(encodedPassword), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return password;
    }
}