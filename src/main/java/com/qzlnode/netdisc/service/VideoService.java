package com.qzlnode.netdisc.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.qzlnode.netdisc.pojo.Video;
import com.qzlnode.netdisc.pojo.VideoCover;
import org.springframework.web.multipart.MultipartFile;

import java.lang.reflect.InvocationTargetException;

/**
 * @author qzlzzz
 */
public interface VideoService extends IService<VideoCover> {

    /**
     *
     * @param multipartFile
     * @param videoId
     */
    void saveVideo(MultipartFile multipartFile, VideoCover videoCover);

    /**
     *
     * @param coverPath
     * @return
     */
    VideoCover saveVideoCover(String[] coverPath) throws InvocationTargetException, IllegalAccessException;
}
