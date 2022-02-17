package com.qzlnode.netdisc.service.Impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qzlnode.netdisc.dao.VideoCoverDao;
import com.qzlnode.netdisc.dao.VideoDao;
import com.qzlnode.netdisc.FastDFS;
import com.qzlnode.netdisc.pojo.Video;
import com.qzlnode.netdisc.pojo.VideoCover;
import com.qzlnode.netdisc.redis.RedisService;
import com.qzlnode.netdisc.redis.key.VideoCoverKey;
import com.qzlnode.netdisc.redis.key.VideoKey;
import com.qzlnode.netdisc.service.VideoService;
import com.qzlnode.netdisc.util.Cache;
import com.qzlnode.netdisc.util.FileInfoHandler;
import com.qzlnode.netdisc.util.MessageHolder;
import com.qzlnode.netdisc.util.VideoUtil;
import org.csource.common.MyException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.locks.LockSupport;

/**
 * @author qzlzzz
 */
@Transactional(rollbackFor = {
        IllegalAccessException.class,
        MyException.class,
        IOException.class,
        InvocationTargetException.class
})
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
    public Video handlerVideo(MultipartFile file,VideoCover videoCover) throws InvocationTargetException, IllegalAccessException {
        Video video = fileInfoHandler.fileInfoToBean(file,null,Video.class);
        video.setVideoId(videoCover.getVideoId());
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
        while (Cache.hasTask(key,value)){
            LockSupport.parkNanos(100);
        }
        Cache.removeAsyncKey(key);
        video = videoDao.selectOne(
                Wrappers.lambdaQuery(Video.class)
                        .eq(Video::getVideoId,coverId)
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
        while (Cache.hasTask(key,value)){
            LockSupport.parkNanos(100);
        }
        Cache.removeAsyncKey(key);
        if(cover == null){
            cover = videoDao.queryCoverAndVideo(coverId, MessageHolder.getUserId());
        }
        if(cover.getVideo() == null) {
            cover.setVideo(videoDao.selectOne(
                    Wrappers.lambdaQuery(Video.class)
                            .eq(Video::getVideoId, coverId)
            ));
        }
        redisService.set(VideoCoverKey.videoCover,String.valueOf(coverId),cover);
        redisService.setSet(VideoCoverKey.videoCoverList,String.valueOf(MessageHolder.getUserId()),cover);
        return cover;
    }

    @Override
    public List<VideoCover> getBatchVideo() {
        Integer userId = MessageHolder.getUserId();
        String key = VideoKey.video.getPrefix() + userId;
        if(!Cache.hasTask(key)){
            return redisService.getList(VideoCoverKey.videoCoverList,String.valueOf(userId),VideoCover.class);
        }
        while (Cache.hasTask(key)){
            LockSupport.parkNanos(100);
        }
        Cache.removeAsyncKey(key);
        List<VideoCover> coverList = videoCoverDao.selectList(
                Wrappers.lambdaQuery(VideoCover.class)
                        .eq(VideoCover::getUserId,userId)
        );
        if(coverList == null){
            return null;
        }
        coverList.forEach(element -> {
            Video video = videoDao.selectOne(
                    Wrappers.lambdaQuery(Video.class)
                            .eq(Video::getVideoId,element.getVideoId()));
            element.setVideo(video);
        });
        redisService.setSet(VideoCoverKey.videoCoverList,String.valueOf(userId),coverList);
        return coverList;
    }

    @Override
    public List<VideoCover> uploadMultiVideoCover(MultipartFile[] files)
            throws IOException, MyException, InvocationTargetException, IllegalAccessException {
        if(files == null || files.length < 1){
            return null;
        }
        List<VideoCover> covers = new ArrayList<>();
        for (MultipartFile file : files) {
            byte[] coverBytes = videoUtil.fetchFrame(file.getInputStream());
            String[] uploadRes = fastDFS.upload(coverBytes,DEFAULT_COVER_TYPE);
            VideoCover cover = fileInfoHandler.pathToBean(uploadRes,VideoCover.class);
            cover.setVideoOriginName(file.getOriginalFilename());
            cover.setUserId(MessageHolder.getUserId());
            covers.add(cover);
        }
        boolean isSave = saveBatch(covers);
        if(!isSave){
            return null;
        }
        return covers;
    }

    @Override
    public List<Video> handlerMultiVideo(MultipartFile[] files,List<VideoCover> videoCovers)
            throws InvocationTargetException, IllegalAccessException {
        if(files == null || files.length < 1){
            return null;
        }
        if(videoCovers == null || videoCovers.size() == 0){
            return null;
        }
        if(files.length != videoCovers.size()){
            return null;
        }
        List<Video> videos = new ArrayList<>();
        Iterator<VideoCover> coverIterator = videoCovers.iterator();
        for (MultipartFile file : files) {
            Video video = fileInfoHandler.fileInfoToBean(file,null,Video.class);
            video.setVideoId(coverIterator.next().getVideoId());
            videos.add(video);
        }
        videos.forEach(element -> {
            videoDao.insert(element);
        });
        return videos;
    }


}
