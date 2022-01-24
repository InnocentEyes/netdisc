package com.qzlnode.netdisc.service;

import com.qzlnode.netdisc.redis.KeyPrefix;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @author qzlzzz
 */
public interface AsyncService {

    /**
     *
     * @param key
     * @param userId
     * @param value
     * @param keyPrefixes
     * @param <T>
     */
    <T> void setDataToRedis(String key,String userId,T value,KeyPrefix... keyPrefixes);

    /**
     *
     * @param key
     * @param userId
     * @param file
     * @param value
     * @param keyPrefix
     * @param <T>
     */
    <T> void saveVideo(String key, String userId, MultipartFile file, T value, KeyPrefix... keyPrefix);

    /**
     *
     * @param files
     * @param values
     * @param userId
     * @param keyPrefixes
     * @param <T>
     */
    <T> void saveBatchVideo(MultipartFile[] files, List<T> values, String userId, KeyPrefix... keyPrefixes);

}
