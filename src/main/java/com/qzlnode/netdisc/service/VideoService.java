package com.qzlnode.netdisc.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.qzlnode.netdisc.pojo.Video;
import com.qzlnode.netdisc.pojo.VideoCover;
import org.csource.common.MyException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

/**
 * @author qzlzzz
 */
public interface VideoService extends IService<VideoCover> {


    /**
     *
     * @param file
     * @return
     */
    VideoCover uploadVideoCover(MultipartFile file) throws IOException, MyException, InvocationTargetException, IllegalAccessException;

    /**
     *
     * @param file
     * @return
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    Video handlerVideo(MultipartFile file,Integer videoCoverId)
            throws InvocationTargetException, IllegalAccessException;

    /**
     *
     * @param coverId
     * @return
     */
    Video getVideo(Integer coverId);

    /**
     *
     * @param coverId
     * @return
     */
    VideoCover getVideoDetail(Integer coverId);

    /**
     *
     * @return
     */
    List<VideoCover> getUserVideoList();

    /**
     *
     * @return
     */
    List<VideoCover> saveVideoCoverList(List<VideoCover> covers);

    /**
     *
     * @param videoCover
     * @return
     */
    VideoCover saveVideoCover(VideoCover videoCover);

    /**
     *
     * @param coverId
     * @return
     */
    VideoCover getCoverWithVideo(Integer coverId);
}
