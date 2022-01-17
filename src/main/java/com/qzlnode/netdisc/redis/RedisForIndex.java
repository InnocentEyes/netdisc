package com.qzlnode.netdisc.redis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;

/**
 * @author qzlzzz
 */
@Component
public class RedisForIndex {

    @Resource(name = "redisTemplate")
    private RedisTemplate redis;

    private static final long EFFECTIVE_TIME = 1000 * 60 * 30;

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private static final String KEY_PREFIX = "user";

    public boolean set(String account,Integer id){
        String key = KEY_PREFIX + account;
        try {
            redis.opsForValue().set(key,id);
            redis.expireAt(key,new Date(System.currentTimeMillis() + EFFECTIVE_TIME));
            logger.info("send" + account + " to redis success");
            return true;
        }catch (Exception exception){
            logger.error("send" + account + " to redis error.\n" +
                    "the reason is {}",exception.getMessage());
            return false;
        }
    }

    /**
     *
     * @param account
     * @return
     */
    public Integer get(String account){
        String key = KEY_PREFIX + account;
        return  (Integer)redis.opsForValue().get(key);
    }

    /**
     *
     * @param account
     * @return
     */
    public boolean hasUser(String account){
        String key = KEY_PREFIX + account;
        return redis.hasKey(key);
    }
}
