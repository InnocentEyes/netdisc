package com.qzlnode.netdisc.service;

import com.qzlnode.netdisc.pojo.VideoCover;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @author qzlzzz
 */
public interface AsyncService {

    /**
     *
     * @param values
     * @param <T>
     */
    <T> void setDataToRedis(String userId,String key,T values);

    /**
     *
     * @param file
     */
    void saveVideo(MultipartFile file, VideoCover cover);

    /**
     *
     * @param files
     * @param covers
     */
    void saveBatchVideo(MultipartFile[] files, List<VideoCover> covers);

}
