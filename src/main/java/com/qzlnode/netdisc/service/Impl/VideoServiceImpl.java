package com.qzlnode.netdisc.service.Impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qzlnode.netdisc.dao.VideoCoverDao;
import com.qzlnode.netdisc.dao.VideoDao;
import com.qzlnode.netdisc.fastdfs.FastDFS;
import com.qzlnode.netdisc.pojo.Video;
import com.qzlnode.netdisc.pojo.VideoCover;
import com.qzlnode.netdisc.redis.RedisService;
import com.qzlnode.netdisc.redis.VideoCoverKey;
import com.qzlnode.netdisc.redis.VideoKey;
import com.qzlnode.netdisc.service.VideoService;
import com.qzlnode.netdisc.util.FileInfoHandler;
import com.qzlnode.netdisc.util.MessageHolder;
import com.qzlnode.netdisc.util.VideoUtil;
import org.csource.common.MyException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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

    @Autowired
    private VideoDao videoDao;

    private static final String DEFAULT_COVER_TYPE = "png";

    @Autowired
    private VideoCoverDao videoCoverDao;

    @Autowired
    private FastDFS fastDFS;

    @Autowired
    private VideoUtil videoUtil;

    @Autowired
    private FileInfoHandler fileInfoHandler;

    @Autowired
    private RedisService redisService;


    @Override
    public VideoCover uploadVideoCover(MultipartFile file)
            throws IOException, MyException, InvocationTargetException, IllegalAccessException {
        byte[] cover = videoUtil.fetchFrame(file.getInputStream());
        String[] uploadRes = fastDFS.upload(cover,DEFAULT_COVER_TYPE);
        VideoCover videoCover = fileInfoHandler.pathToBean(uploadRes,VideoCover.class);
        videoCover.setUserId(MessageHolder.getUserId());
        videoCover.setVideoCoverType(DEFAULT_COVER_TYPE);
        videoCover.setVideoOriginName(file.getOriginalFilename());
        int result = videoCoverDao.insert(videoCover);
        if(result != 1){
            logger.info("insert video cover to db error");
            return null;
        }
        return videoCover;
    }

    @Override
    public Video handlerVideo(MultipartFile file,Integer videoCoverId) throws InvocationTargetException, IllegalAccessException {
        Video video = fileInfoHandler.fileInfoToBean(file,null,Video.class);
        video.setVideoCoverId(videoCoverId);
        int result = videoDao.insert(video);
        if(result != 1){
            logger.info("insert video to db error");
            return null;
        }
        return video;
    }

    /**
     *
     * @param coverId
     * @return
     */
    @Override
    public Video getVideo(Integer coverId) {
        Video video = redisService.get(VideoKey.video,String.valueOf(coverId),Video.class);
        if(video != null){
            return video;
        }
        String key = VideoKey.video.getPrefix() + MessageHolder.getUserId();
        String value = VideoKey.video.getPrefix() + coverId;
        while (AsyncServiceImpl.Cache.get(key).contains(value)){
            LockSupport.parkNanos(100);
        }
        if(AsyncServiceImpl.Cache.get(key).size() == 0){
            AsyncServiceImpl.Cache.remove(key);
        }
        video = videoDao.selectOne(
                Wrappers.lambdaQuery(Video.class)
                        .eq(Video::getVideoCoverId,coverId)
        );
        if(video == null){
            return null;
        }
        redisService.set(VideoKey.video,String.valueOf(coverId),video);
        return video;
    }

    @Override
    public VideoCover getVideoDetail(Integer coverId) {
        VideoCover cover = redisService.get(VideoCoverKey.videoCover,String.valueOf(coverId),VideoCover.class);
        if(cover != null && cover.getVideo() != null){
            return cover;
        }
        String key = VideoKey.video.getPrefix() + MessageHolder.getUserId();
        String value = VideoKey.video.getPrefix() + coverId;
        while (AsyncServiceImpl.Cache.get(key).contains(value)){
            LockSupport.parkNanos(100);
        }
        if(AsyncServiceImpl.Cache.get(key).size() == 0){
            AsyncServiceImpl.Cache.remove(key);
        }
        if(cover == null){
            cover = videoDao.queryCoverAndVideo(coverId, MessageHolder.getUserId());
        }
        if(cover.getVideo() == null) {
            cover.setVideo(videoDao.selectOne(
                    Wrappers.lambdaQuery(Video.class)
                            .eq(Video::getVideoCoverId, coverId)
            ));
        }
        redisService.set(VideoCoverKey.videoCover,String.valueOf(coverId),cover);
        redisService.setSet(VideoCoverKey.videoCoverList,String.valueOf(MessageHolder.getUserId()),cover);
        return cover;
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
        while (AsyncServiceImpl.target.get() != 0){
            LockSupport.parkNanos(100);
        }
        return redisService.get(VideoCoverKey.videoCover,key,VideoCover.class);
    }

}
