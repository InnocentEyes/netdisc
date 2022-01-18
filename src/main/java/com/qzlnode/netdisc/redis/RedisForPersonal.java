package com.qzlnode.netdisc.redis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author qzlzzz
 */
@Component
public class RedisForPersonal {

    /**
     * 日志
     */
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private static final String KEY_PREFIX = "updateCount";

    private static final String INIT_IMG_PREFIEX = "initImg";

    private static final long EFFECTIVE_TIME = 1000 * 60 * 60 * 24;

    private static final Integer MAX_UPDATE_COUNT = 3;

    @Resource(name = "redisTemplate")
    private RedisTemplate redis;

    /**
     *
     * @param userId
     * @return
     */
    public boolean checkUpdateCount(Integer userId){
        String key = KEY_PREFIX + userId;
        Integer updateCount = (Integer)redis.opsForValue().get(key);
        if(updateCount >= MAX_UPDATE_COUNT){
            return false;
        }
        return true;
    }

    /**
     *
     * @param userId
     * @return
     */
    public boolean updateCount(Integer userId){
        String key = KEY_PREFIX + userId;
        try {
            redis.opsForValue().increment(key);
            return true;
        }catch (Exception ex){
            logger.error("incr the updateCount error.\n" +
                    "{}",ex.getMessage());
            return false;
        }
    }

    /**
     *
     * @param userId
     * @return
     */
    public boolean initUpdateCount(Integer userId){
        String key = KEY_PREFIX + userId;
        try {
            redis.opsForValue().set(key,0,EFFECTIVE_TIME);
            return true;
        }catch (Exception ex){
            logger.error("init the updateCount error.\n" +
                    "{}",ex.getMessage());
            return false;
        }
    }

    /**
     *
     * @param userId
     * @return
     */
    public boolean hasUpdateCount(Integer userId){
        String key = KEY_PREFIX + userId;
        try {
            return redis.hasKey(key);
        }catch (Exception ex){
            logger.error("check the updateCount error.\n" +
                    "{}",ex.getMessage());
            return false;
        }
    }

    /**
     *
     * @param userId
     * @return
     */
    public boolean isInit(Integer userId){
        String key = INIT_IMG_PREFIEX + userId;
        try {
            return redis.hasKey(key);
        }catch (Exception ex){
            logger.error("check the init error.\n" +
                    "{}",ex.getMessage());
            return false;
        }
    }

    /**
     *
     * @param userId
     * @return
     */
    public boolean init(Integer userId){
        String key = INIT_IMG_PREFIEX + userId;
        try{
            redis.opsForValue().set(key,1);
            return true;
        }catch (Exception ex){
            logger.error(" init error.\n" +
                    "{}",ex.getMessage());
            return false;
        }
    }
}
