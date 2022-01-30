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
     * @param file
     * @param fileId
     * @param userId
     */
    void uploadVideo(MultipartFile file,Integer fileId,Integer userId);

    /**
     *
     * @param files
     * @param fileIds
     * @param userId
     */
    void uploadBatchVideo(MultipartFile[] files,Integer[] fileIds,Integer userId);

    /**
     *
     * @param file
     * @param fileId
     */
    void saveDocument(MultipartFile file,Integer fileId,Integer userId);

    /**
     *
     * @param files
     * @param fileIds
     */
    void saveBatchDocument(MultipartFile[] files,Integer[] fileIds,Integer userId);

    /**
     *
     * @param file
     * @param fileId
     * @param userId
     */
    void saveMusic(MultipartFile file,Integer fileId,Integer userId);

    /**
     *
     * @param files
     * @param fileIds
     * @param userId
     */
    void saveBatchMusic(MultipartFile[] files,Integer[] fileIds,Integer userId);

}
