package com.qzlnode.netdisc.redis.key;

/**
 * @author qzlzzz
 */
public class UserKey extends BasePrefix{

    //两天时间
    private static final long BLACK_EXPIRE = 3600 * 24 * 2;

    //两分钟
    private static final long VERIFY_EXPIRE = 1000 * 60 * 60 * 2;

    private UserKey(long expireSeconds, String prefix) {
        super(expireSeconds, prefix);
    }

    /**
     * 用户key的前缀
     */
    public static UserKey blackUser = new UserKey(BLACK_EXPIRE,"black");

    public static UserKey phone = new UserKey(0,"userPhone");

    public static UserKey verifyCode = new UserKey(VERIFY_EXPIRE,"verifyCode");
}
