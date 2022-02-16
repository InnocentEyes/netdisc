package com.qzlnode.netdisc.redis.key;

/**
 * @author qzlzzz
 */
public interface KeyPrefix {

    /**
     *
     * @return
     */
    long getExpireSeconds();

    /**
     *
     * @return
     */
    String getPrefix();
}
