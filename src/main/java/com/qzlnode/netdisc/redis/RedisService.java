package com.qzlnode.netdisc.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.qzlnode.netdisc.redis.key.KeyPrefix;
import com.qzlnode.netdisc.util.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * 自己封装的对redis缓存库的操作,能运行就行。
 *
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
     *
     * @param keyPrefix key的前缀
     * @param key       key
     * @param value     value
     * @param <T>       值的类型
     * @return {@code true} or {@code false}
     */
    public <T> boolean set(KeyPrefix keyPrefix, String key, T value) {
        String realKey = keyPrefix.getPrefix() + key;
        String str = JsonUtil.objectToJson(value);
        if (str == null || str.length() < 1) {
            return false;
        }
        try {
            redis.opsForValue().set(realKey, str);
            if (keyPrefix.getExpireSeconds() > 0) {
                redis.expire(realKey, keyPrefix.getExpireSeconds(), TimeUnit.MILLISECONDS);
            }
            return true;
        } catch (Exception ex) {
            logger.error("set key to redis error.\n" +
                    "the reason is {}", ex.getCause().getMessage());
            return false;
        }
    }

    /**
     * 将值设置到redis的Set类型中去
     *
     * @param keyPrefix key的前缀
     * @param key       key
     * @param <T>       values value
     * @param <T>       值的类型
     * @return {@code true} or {@code false}
     */
    public <T> boolean setSet(KeyPrefix keyPrefix, String key, T value){
        String realKey = keyPrefix.getPrefix() + key;
        try {
            String json = JsonUtil.objectToJson(value);
            redis.opsForSet().add(realKey,json);
            if (keyPrefix.getExpireSeconds() > 0) {
                redis.expire(realKey, keyPrefix.getExpireSeconds(), TimeUnit.MILLISECONDS);
            }
            return true;
        } catch (Exception ex) {
            logger.error("set key to redis error.\n" +
                    "the reason is {}", ex.getCause().getMessage());
            return false;
        }
    }

    public <T> boolean setSet(KeyPrefix keyPrefix,String key,List<T> values){
        String realKey = keyPrefix.getPrefix() + key;
        try {
            String[] jsons = values.stream().map(JsonUtil::objectToJson).toArray(String[]::new);
            redis.opsForSet().add(realKey,jsons);
            return true;
        }catch (Exception ex) {
            logger.error("set key to redis error.\n" +
                    "the reason is {}", ex.getCause().getMessage());
            return false;
        }
    }

    /**
     * 根据key将值从redis中取出，并处理成需要的类型
     *
     * @param keyPrefix key的前缀
     * @param key       key
     * @param claszz    需要取出的类型
     * @param <T>       传入需要取出的值的类型
     * @return 返回想要的类型的值
     */
    public <T> T get(KeyPrefix keyPrefix, String key, Class<T> claszz) {
        String realKey = keyPrefix.getPrefix() + key;
        try {
            String json = redis.opsForValue().get(realKey);
            return JsonUtil.jsonToObject(json, claszz);
        } catch (Exception ex) {
            logger.error("get key value from redis error.\n" +
                    "the reason is {}", ex.getCause().getMessage());
            return null;
        }
    }

    public <T> List<T> getList(KeyPrefix keyPrefix, String key, Class<T> memberClazz) {
        String realKey = keyPrefix.getPrefix() + key;
        try {
            StringBuilder builder = new StringBuilder();
            builder.append("[");
            redis.opsForSet()
                    .members(realKey)
                    .stream()
                    .filter(Objects::nonNull)
                    .forEach(element -> {
                        builder.append(element);
                        builder.append(",");
                    });
            if(builder.length() == 0){
                return null;
            }
            builder.delete(builder.length() - 1, builder.length()).append("]");
            return JsonUtil.jsonToList(builder.toString(), memberClazz);
        } catch (Exception ex) {
            logger.error("get key value from redis error.\n" +
                    "the reason is {}", ex.getCause().getMessage());
            return null;
        }
    }

    /**
     * 值加1 这里list类型不能用
     *
     * @param keyPrefix key的前缀
     * @param key       key
     * @return 返回long值
     */
    public long incr(KeyPrefix keyPrefix, String key) {
        try {
            return redis.opsForValue().increment(keyPrefix.getPrefix() + key);
        } catch (Exception ex) {
            logger.error("incr error.\n" +
                    "the reason is {}", ex.getCause().getMessage());
            return -1;
        }
    }

    /**
     * 值减一,这里list类型的键值对不能用
     *
     * @param keyPrefix key的前缀
     * @param key       key
     * @return 返回long类型的值
     */
    public long decr(KeyPrefix keyPrefix, String key) {
        try {
            return redis.opsForValue().decrement(keyPrefix.getPrefix() + key);
        } catch (Exception ex) {
            logger.error("decr error.\n" +
                    "the reason is {}", ex.getCause().getMessage());
            return -1;
        }
    }

    /**
     * 将键值对从redis中删除
     *
     * @param keyPrefix key的前缀
     * @param key       key
     * @return {@code true} or {@code false}
     */
    public boolean delete(KeyPrefix keyPrefix, String key) {
        try {
            return redis.delete(keyPrefix.getPrefix() + key);
        } catch (Exception ex) {
            logger.error("delete the key error.\n" +
                    "the reason is {}", ex.getCause().getMessage());
            return false;
        }
    }


    /**
     * 判断redis是否有该key
     *
     * @param keyPrefix key前缀
     * @param key
     * @return {@code true} or {@code false}
     */
    public boolean exists(KeyPrefix keyPrefix, String key) {
        try {
            return redis.hasKey(keyPrefix.getPrefix() + key);
        } catch (Exception ex) {
            logger.error("check the key in redis error.\n" +
                    "the reason is {}", ex.getCause().getMessage());
            return false;
        }
    }

}
