package com.qzlnode.netdisc.redis;

/**
 * @author qzlzzz
 */
public class UserKey extends BasePrefix{

    //两天时间
    private static final int TOKEN_EXPIRE = 3600 * 24 * 2;

    private UserKey(int expireSeconds, String prefix) {
        super(expireSeconds, prefix);
    }

    /**
     * 用户key的前缀
     */
    public static UserKey token = new UserKey(TOKEN_EXPIRE,"token");

    public static UserKey phone = new UserKey(0,"userPhone");
}
