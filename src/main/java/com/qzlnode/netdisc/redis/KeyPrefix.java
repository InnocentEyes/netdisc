package com.qzlnode.netdisc.redis;

/**
 * @author qzlzzz
 */
public interface KeyPrefix {

    /**
     *
     * @return
     */
    int getExpireSeconds();

    /**
     *
     * @return
     */
    String getPrefix();
}
