package com.qzlnode.netdisc.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qzlnode.netdisc.dao.VideoCoverDao;
import com.qzlnode.netdisc.dao.VideoDao;
import com.qzlnode.netdisc.exception.GetTimeOutException;
import com.qzlnode.netdisc.pojo.Video;
import com.qzlnode.netdisc.pojo.VideoCover;
import com.qzlnode.netdisc.redis.RedisService;
import com.qzlnode.netdisc.redis.VideoCoverKey;
import com.qzlnode.netdisc.redis.VideoKey;
import com.qzlnode.netdisc.service.VideoService;
import com.qzlnode.netdisc.util.FileInfoHandler;
import com.qzlnode.netdisc.util.MessageHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.locks.LockSupport;
import java.util.stream.Collectors;

/**
 * @author qzlzzz
 */
@Service
public class VideoServiceImpl extends ServiceImpl<VideoCoverDao, VideoCover> implements VideoService {

    /**
     * 日志
     */
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private static final String DEFAULT_IMG_TYPE = "png";

    @Autowired
    private VideoDao videoDao;

    @Autowired
    private VideoCoverDao videoCoverDao;

    @Autowired
    private FileInfoHandler fileInfoHandler;

    @Autowired
    private RedisService redisService;



    /**
     * 可扩展
     * @param coverPath
     * @return
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    @Override
    public VideoCover saveVideoCover(String[] coverPath,String fileOriginName)
            throws InvocationTargetException, IllegalAccessException {
        VideoCover cover = fileInfoHandler.pathToBean(coverPath, VideoCover.class);
        cover.setUserId(MessageHolder.getUserId());
        cover.setVideoOriginName(fileOriginName);
        cover.setVideoCoverType(DEFAULT_IMG_TYPE);
        int res = videoCoverDao.insert(cover);
        if(res != 1){
            return null;
        }
        return cover;
    }

    /**
     *
     * @param coverId
     * @return
     */
    @Override
    public Video getVideoByCoverId(Integer coverId) {
        String key = String.valueOf(coverId);
        while (AsyncServiceImpl.target.get() != 0){
            LockSupport.parkNanos(100);
        }
        return redisService.get(VideoKey.video,key,Video.class);
    }

    @Override
    public List<VideoCover> getUserVideoList() {
        String userId = String.valueOf(MessageHolder.getUserId());
        while (AsyncServiceImpl.target.get() != 0){
            LockSupport.parkNanos(100);
        }
        VideoCover[] videoCovers = redisService.get(VideoCoverKey.videoCoverList,userId,VideoCover[].class);
        return Arrays.stream(videoCovers)
                .collect(Collectors.toList());
    }

    @Override
    public List<VideoCover> saveVideoCoverList(List<VideoCover> covers) {
        if(saveBatch(covers)){
            return covers;
        }
        return null;
    }

    @Override
    public VideoCover saveVideoCover(VideoCover videoCover) {
        int result = videoCoverDao.insert(videoCover);
        if(result != 1){
            return null;
        }
        return videoCover;
    }

    @Override
    public VideoCover getCoverWithVideo(Integer coverId) {
        String key = String.valueOf(coverId);
        return redisService.get(VideoCoverKey.videoCover,key,VideoCover.class);
    }

}
