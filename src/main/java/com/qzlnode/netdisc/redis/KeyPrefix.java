package com.qzlnode.netdisc.redis;

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
