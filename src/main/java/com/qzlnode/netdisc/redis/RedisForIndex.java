package com.qzlnode.netdisc.redis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

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
        try {
            redis.opsForValue().set(KEY_PREFIX + account,id);
            redis.expireAt(account,new Date(System.currentTimeMillis() + EFFECTIVE_TIME));
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
     * @param key
     * @return
     */
    public boolean get(Integer key){
        return (boolean)redis.opsForValue().get(KEY_PREFIX + key);
    }

    /**
     *
     * @param key
     * @return
     */
    public boolean hasUser(Integer key){
        return redis.hasKey(key);
    }
}
