package com.qzlnode.netdisc.redis;

/**
 * @author qzlzzz
 */
public class BasePrefix implements KeyPrefix{

    private int expireSeconds;

    private String prefix;

    public BasePrefix(String prefix){
        //表示永不过期
        this(0,prefix);
    }

    public BasePrefix(int expireSeconds, String prefix) {
        this.expireSeconds = expireSeconds;
        this.prefix = prefix;
    }

    @Override
    public int getExpireSeconds() {
        return expireSeconds;
    }

    @Override
    public String getPrefix() {
        //拿到参数类类名
        String className = getClass().getSimpleName();
        return className + ":" + prefix;
    }
}
