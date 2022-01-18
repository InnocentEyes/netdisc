package com.qzlnode.netdisc.util;

import com.qzlnode.netdisc.pojo.UserInfo;
import org.springframework.util.Assert;

/**
 * @author qzlzzz
 */
public class MessageHolder {

    /**
     * sessionId,线程本地变量
     */
    private static final ThreadLocal<String> SID_THREAD_LOCAL= new ThreadLocal<>();
    /**
     *   用户信息,线程本地变量
     */
    private static final ThreadLocal<UserInfo> USER_THREAD_LOCAL = new ThreadLocal<>();



    /**
     * 取得 user
     * @return
     */
    public static UserInfo getUser(){
        UserInfo user = USER_THREAD_LOCAL.get();
        Assert.notNull(user,"user未设置");
        return  user;
    }

    /**
     *
     * @param user
     */
    public static void setUser(UserInfo user){
        USER_THREAD_LOCAL.set(user);
    }


    /**
     * 清楚线程局部变量,防止内存泄漏
     */
    public static void clearData(){
        USER_THREAD_LOCAL.remove();
        SID_THREAD_LOCAL.remove();
    }

    /**
     * 获取session中的 userId
     * @return
     */
    public static Integer getUserId(){
        UserInfo user = getUser();
        if(user.getId() == 0){
            throw new IllegalArgumentException("user id is null");
        }
        return user.getId();
    }

    /**
     *
     * @return
     */
    public static String getSidId(){
        return SID_THREAD_LOCAL.get();
    }

    /**
     *
     * @param sidId
     */
    public static void setSidId(String sidId){
        SID_THREAD_LOCAL.set(sidId);
    }

    public static boolean hasUser(){
        return !((USER_THREAD_LOCAL.get() == null) ? Boolean.TRUE : Boolean.FALSE);
    }

}
