package com.qzlnode.netdisc.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.qzlnode.netdisc.pojo.Video;
import com.qzlnode.netdisc.pojo.VideoCover;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

/**
 * @author qzlzzz
 */
public interface VideoService extends IService<VideoCover> {

    /**
     *
     * @param coverPath
     * @return
     */
    VideoCover saveVideoCover(String[] coverPath,String fileOriginName)
            throws InvocationTargetException, IllegalAccessException;

    /**
     *
     * @param coverId
     * @return
     */
    Video getVideoByCoverId(Integer coverId);

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
