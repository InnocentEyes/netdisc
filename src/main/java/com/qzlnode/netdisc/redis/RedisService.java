package com.qzlnode.netdisc.redis;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author qzlzzz
 */
@Component
public class RedisService {

    /**
     * 日志
     */
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Resource(name = "redisTemplate")
    private RedisTemplate redis;

    private final ObjectMapper mapper = new ObjectMapper();

    /**
     * 将键值对存入到redis中
     * @param keyPrefix
     * @param key
     * @param value
     * @param <T>
     * @return
     */
    public <T> boolean set(KeyPrefix keyPrefix,String key,T value){
        String realKey = keyPrefix.getPrefix() + key;
        String str = beanToJson(value);
        if(str == null || str.length() < 1){
            return false;
        }
        try{
            redis.opsForValue().set(realKey,str);
            return true;
        }catch (Exception ex){
            logger.error("set key to redis error.\n" +
                    "the reason is {}",ex.getMessage());
            return false;
        }
    }

    /**
     * 根据key将值从redis中取出，并处理成需要的类型
     * @param keyPrefix
     * @param key
     * @param claszz
     * @param <T>
     * @return
     */
    public <T> T get(KeyPrefix keyPrefix,String key,Class<T> claszz){
        String realKey = keyPrefix.getPrefix() + key;
        try{
            String str = (String) redis.opsForValue().get(realKey);
            return jsonToBean(str,claszz);
        }catch (Exception ex){
            logger.error("get key value from redis error.\n" +
                    "the reason is {}",ex.getMessage());
            return null;
        }

    }

    /**
     *
     * @param keyPrefix
     * @param key
     * @return
     */
    public long incr(KeyPrefix keyPrefix,String key){
        try {
            return redis.opsForValue().increment(keyPrefix.getPrefix() + key);
        }catch (Exception ex){
            logger.error("incr error.\n" +
                    "the reason is {}",ex.getMessage());
            return -1;
        }
    }

    /**
     *
     * @param keyPrefix
     * @param key
     * @return
     */
    public long decr(KeyPrefix keyPrefix,String key){
        try {
            return redis.opsForValue().decrement(keyPrefix.getPrefix() + key);
        }catch (Exception ex){
            logger.error("decr error.\n" +
                    "the reason is {}",ex.getMessage());
            return -1;
        }
    }

    /**
     * 将键值对从redis中删除
     * @param keyPrefix
     * @param key
     * @return
     */
    public boolean delete(KeyPrefix keyPrefix,String key){
        try {
            return redis.delete(keyPrefix.getPrefix() + key);
        }catch (Exception ex){
            logger.error("delete the key error.\n" +
                    "the reason is {}",ex.getMessage());
            return false;
        }
    }


    /**
     * 判断redis是否有该key
     * @param keyPrefix
     * @param key
     * @return
     */
    public boolean exists(KeyPrefix keyPrefix,String key){
        try{
            return redis.hasKey(keyPrefix.getPrefix() + key);
        }catch (Exception ex){
            logger.error("check the key in redis error.\n" +
                    "the reason is {}",ex.getMessage());
            return false;
        }
    }

    /**
     *
     * @param value
     * @param <T>
     * @return
     */
    private  <T> String beanToJson(T value){
        if(value == null){
            return null;
        }
        Class<?> claszz = value.getClass();
        if(claszz == int.class || claszz == Integer.class){
            return String.valueOf(value);
        }
        if(claszz == long.class || claszz == Long.class){
            return String.valueOf(value);
        }
        try {
            return mapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            logger.error("handler bean to json error.\n" +
                    "the reason is {}",e.getMessage());
            return null;
        }
    }

    /**
     *
     * @param str
     * @param claszz
     * @param <T>
     * @return
     */
    private  <T> T jsonToBean(String str,Class<T> claszz){
        if(str == null || str.length() < 1 || claszz == null){
            return null;
        }
        if(claszz == int.class || claszz == Integer.class){
            return (T) Integer.valueOf(str);
        }
        if(claszz == long.class || claszz == Long.class){
            return (T) Long.valueOf(str);
        }
        if(claszz == String.class){
            return (T) str;
        }
        try {
            return mapper.readValue(str,claszz);
        } catch (JsonProcessingException e) {
            logger.error("handler json to bean error.\n" +
                    "the reason is {}",e.getMessage());
            return null;
        }
    }
}
