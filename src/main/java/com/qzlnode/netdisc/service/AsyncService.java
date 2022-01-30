package com.qzlnode.netdisc.service;

import com.qzlnode.netdisc.redis.KeyPrefix;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
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
    void uploadDocument(MultipartFile file,Integer fileId,Integer userId);

    /**
     *
     * @param files
     * @param fileIds
     */
    void uploadBatchDocument(MultipartFile[] files,Integer[] fileIds,Integer userId);

    /**
     *
     * @param file
     * @param fileId
     * @param userId
     */
    void uploadMusic(MultipartFile file,Integer fileId,Integer userId);

    /**
     *
     * @param files
     * @param fileIds
     * @param userId
     */
    void uploadBatchMusic(MultipartFile[] files,Integer[] fileIds,Integer userId);

    /**
     *
     * @param request
     */
    void recordIpAddress(HttpServletRequest request);

}
