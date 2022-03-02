package com.qzlnode.netdisc.util;


import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.Md5Crypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

/**
 * @author qzlzzz
 * 加密工具类
 */
public class Md5 {

    private static final Logger logger = LoggerFactory.getLogger(Md5.class);

    /**
     * 给字符串加密
     * @param password
     * @return
     */
    public static String encode(String password) {
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            String encodePassword = Base64.encodeBase64String(md5.digest(password.getBytes(StandardCharsets.UTF_8)));
            return encodePassword;
        }catch (Exception e){
            logger.error("encode password error");
            return Md5Crypt.md5Crypt(password.getBytes(StandardCharsets.UTF_8));
        }
    }

}