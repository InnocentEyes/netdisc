package com.qzlnode.netdisc.redis;

/**
 * @author qzlzzz
 */
public class CountKey extends BasePrefix{

    //一天时间
    private static final long COUNT_EXPIRE = 3600 * 60 * 1;

    /**
     * 防止外部实例化修改
     * @param expireSeconds
     * @param prefix
     */
    private CountKey(long expireSeconds, String prefix) {
        super(expireSeconds, prefix);
    }

    /**
     * count key
     */
    public static CountKey updateCount = new CountKey(COUNT_EXPIRE,"update");



}
