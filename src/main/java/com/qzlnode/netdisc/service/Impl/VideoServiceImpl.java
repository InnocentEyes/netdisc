package com.qzlnode.netdisc.service.Impl;

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
import org.csource.common.MyException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

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

    @Autowired
    private FastDFS fastDFS;

    /**
     * 异步任务
     * @param file
     * @return
     */
    @Async("asyncTaskExecutor")
    @Override
    public void saveVideo(MultipartFile file,VideoCover videoCover) {
        try {
            String[] uploadRes = fastDFS.upload(file.getBytes(),file.getOriginalFilename().split(".")[1]);
            Video video = fileInfoHandler.fileInfoToBean(file,uploadRes,Video.class);
            video.setVideoId(videoCover.getVideoCoverId());
            videoDao.insert(video);
            redisService.set(VideoKey.video,String.valueOf(videoCover.getVideoCoverId()),video);
            redisService.setList(VideoKey.videoList,String.valueOf(videoCover.getUserId()),video);
        }catch (IOException | MyException | InvocationTargetException | IllegalAccessException e){
            logger.error("run the async method error.\n {}",e.getMessage());
        }catch (Exception e){
            logger.error("get the unexpected exception {} , the reason is {}",e,e.getMessage());
        }
    }

    @Override
    public VideoCover saveVideoCover(String[] coverPath)
            throws InvocationTargetException, IllegalAccessException {
        VideoCover cover = fileInfoHandler.pathToBean(coverPath, VideoCover.class);
        cover.setUserId(MessageHolder.getUserId());
        cover.setVideoCoverType(DEFAULT_IMG_TYPE);
        int res = videoCoverDao.insert(cover);
        if(res != 1){
            return null;
        }
        redisService.set(
                VideoCoverKey.videoCover,
                String.valueOf(cover.getVideoCoverId()),
                cover);
        redisService.setList(
                VideoCoverKey.videoCoverList,
                String.valueOf(MessageHolder.getUserId()),
                cover
        );
        return cover;
    }
}
