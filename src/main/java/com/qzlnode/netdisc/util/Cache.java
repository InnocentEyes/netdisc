package com.qzlnode.netdisc.util;

import java.nio.channels.Channel;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author qzlzzz
 */
public class Cache {

    private static final Map<String,HashSet<String>> asyncCache = new HashMap<>();

    private static final Map<Integer, Channel> channelCache = new ConcurrentHashMap<>();

    private static final ReentrantLock LOCK = new ReentrantLock();

    /**
     *
     * @param key
     * @param value
     */
    public static void putAsync(String key,String value){
        LOCK.lock();
        try {
            asyncCache.computeIfAbsent(key, k -> new HashSet<>()).add(value);
        }finally {
            LOCK.unlock();
        }
    }

    /**
     *
     * @param key
     * @param value
     * @return
     */
    public static boolean hasTask(String key,String value){
        return asyncCache.get(key).contains(value);
    }

    public static boolean hasTask(String key){
        return asyncCache.get(key).size() != 0;
    }

    /**
     *
     * @param key
     * @param value
     */
    public static void removeAsync(String key,String value){
        asyncCache.get(key).remove(value);
    }

    /**
     *
     * @param key
     */
    public static void removeAsyncKey(String key){
        LOCK.lock();
        try {
            if (asyncCache.get(key).size() != 0){
                return;
            }
            asyncCache.remove(key);
        }finally {
            LOCK.lock();
        }
    }


    /**
     *
     * @param userId
     * @param channel
     */
    public static void putChannel(Integer userId,Channel channel){
        channelCache.put(userId,channel);
    }

    /**
     *
     * @param userId
     */
    public static void removeChannel(Integer userId){
        channelCache.remove(userId);
    }
}
