package com.qzlnode.netdisc.redis;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * 自己封装的对redis缓存库的操作,能运行就行。
 * @author qzlzzz
 */
@Component
public class RedisService {

    /**
     * 日志
     */
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private StringRedisTemplate redis;

    private final ObjectMapper mapper = new ObjectMapper();

    /**
     * 将键值对存入到redis中
     * @param keyPrefix key的前缀
     * @param key key
     * @param value value
     * @param <T> 值的类型
     * @return {@code true} or {@code false}
     */
    public <T> boolean set(KeyPrefix keyPrefix,String key,T value){
        String realKey = keyPrefix.getPrefix() + key;
        String str = beanToJson(value);
        if(str == null || str.length() < 1){
            return false;
        }
        try{
            redis.opsForValue().set(realKey,str);
            if(keyPrefix.getExpireSeconds() > 0){
                redis.expire(realKey,keyPrefix.getExpireSeconds(),TimeUnit.MILLISECONDS);
            }
            return true;
        }catch (Exception ex){
            logger.error("set key to redis error.\n" +
                    "the reason is {}",ex.getMessage());
            return false;
        }
    }

    /**
     * 将值设置到redis的list类型中去
     * @param keyPrefix key的前缀
     * @param key key
     * @param value value
     * @param <T> 值的类型
     * @return {@code true} or {@code false}
     */
    public  <T> boolean setList(KeyPrefix keyPrefix,String key,T value){
        String realKey = keyPrefix.getPrefix() + key;
        String str = beanToJson(value);
        if(str == null || str.length() < 1){
            return false;
        }
        try {
            redis.opsForList().leftPush(realKey,str);
            return true;
        }catch (Exception ex){
            logger.error("set key to redis error.\n" +
                    "the reason is {}",ex.getMessage());
            return false;
        }
    }

    public <T> boolean setSet(KeyPrefix keyPrefix, String key, T... values){
        String realKey = keyPrefix.getPrefix() + key;
        try {
            if(values.length == 1){
                String str = beanToJson(values[0]);
                redis.opsForSet().add(realKey,str);
            }
            if(values.length > 1) {
                redis.opsForSet().add(realKey, Arrays.stream(values).map(this::beanToJson).toArray(String[]::new));
            }
            if (keyPrefix.getExpireSeconds() > 0) {
                redis.expire(realKey, keyPrefix.getExpireSeconds(), TimeUnit.MILLISECONDS);
            }
            return true;
        }catch (Exception ex){
            logger.error("set key to redis error.\n" +
                    "the reason is {}",ex.getMessage());
            return false;
        }
    }

    /**
     * 根据key将值从redis中取出，并处理成需要的类型
     * @param keyPrefix key的前缀
     * @param key key
     * @param claszz 需要取出的类型
     * @param <T> 传入需要取出的值的类型
     * @return 返回想要的类型的值
     */
    public <T> T get(KeyPrefix keyPrefix,String key,Class<T> claszz){
        String realKey = keyPrefix.getPrefix() + key;
        try{
            if(!claszz.isArray()){
                String str = redis.opsForValue().get(realKey);
                return jsonToBean(str,claszz);
            }
            StringBuilder builder = new StringBuilder();
            Set<String> members = redis.opsForSet().members(realKey);
            if(members == null){
                return null;
            }
            members.stream()
                    .filter(String::isEmpty)
                    .forEach(element -> {
                        builder.append(element);
                        builder.append(",");
                    });
            builder.delete(builder.length() - 1,builder.length());
            return jsonToBean(builder.toString(),claszz);
        }catch (Exception ex){
            logger.error("get key value from redis error.\n" +
                    "the reason is {}",ex.getMessage());
            return null;
        }

    }

    /**
     * 值加1 这里list类型不能用
     * @param keyPrefix key的前缀
     * @param key key
     * @return 返回long值
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
     * 值减一,这里list类型的键值对不能用
     * @param keyPrefix key的前缀
     * @param key key
     * @return 返回long类型的值
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
     * @param keyPrefix key的前缀
     * @param key key
     * @return {@code true} or {@code false}
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
     * @param keyPrefix key前缀
     * @param key
     * @return {@code true} or {@code false}
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
        if(!claszz.isArray() && claszz == String.class){
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
