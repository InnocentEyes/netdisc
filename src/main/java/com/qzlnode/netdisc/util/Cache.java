package com.qzlnode.netdisc.util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author qzlzzz
 */
public class Cache {

    private static final Map<String,HashSet<String>> asyncCache = new HashMap<>();

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
        if(asyncCache.get(key) == null){
            return false;
        }
        return asyncCache.get(key).contains(value);
    }

    public static boolean hasTask(String key){
        if(asyncCache.get(key) == null){
            return false;
        }
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
            if(asyncCache.get(key) == null){
                return;
            }
            if (asyncCache.get(key).size() != 0){
                return;
            }
            asyncCache.remove(key);
        }finally {
            LOCK.unlock();
        }
    }

}
