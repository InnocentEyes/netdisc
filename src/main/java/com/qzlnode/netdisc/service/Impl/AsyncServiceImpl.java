package com.qzlnode.netdisc.service.Impl;

import com.qzlnode.netdisc.dao.VideoDao;
import com.qzlnode.netdisc.fastdfs.FastDFS;
import com.qzlnode.netdisc.pojo.Img;
import com.qzlnode.netdisc.pojo.Video;
import com.qzlnode.netdisc.pojo.VideoCover;
import com.qzlnode.netdisc.redis.ImgKey;
import com.qzlnode.netdisc.redis.RedisService;
import com.qzlnode.netdisc.redis.VideoCoverKey;
import com.qzlnode.netdisc.redis.VideoKey;
import com.qzlnode.netdisc.service.AsyncService;
import com.qzlnode.netdisc.util.FileInfoHandler;
import org.csource.common.MyException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;
import java.util.List;

/**
 * @author qzlzzz
 */
@Async("asyncTaskExecutor")
@Service
public class AsyncServiceImpl implements AsyncService {

    /**
     * 日志
     */
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private FastDFS dfs;

    @Autowired
    private FileInfoHandler fileInfoHandler;

    @Autowired
    private VideoDao videoDao;

    @Autowired
    private RedisService redisService;


    @Override
    public <T> void setDataToRedis(String userId,String key,T value) {
        if(value == null){
            return;
        }
        if(value instanceof Video){
            redisService.set(VideoKey.video,key,value);
            return;
        }
        if(value instanceof VideoCover){
            redisService.set(VideoCoverKey.videoCover,key,value);
            redisService.setList(VideoCoverKey.videoCoverList,userId,value);
            return;
        }
        if(value instanceof Img){
            redisService.set(ImgKey.img,key,value);
            redisService.setList(ImgKey.imgList,userId,value);
        }

    }

    @Override
    public void saveVideo(MultipartFile file, VideoCover cover) {
        try {
            String[] uploadRes = dfs.upload(file.getBytes(), file.getOriginalFilename().split(".")[1]);
            Video video = fileInfoHandler.fileInfoToBean(file, uploadRes, Video.class);
            video.setVideoCoverId(cover.getVideoCoverId());
            videoDao.insert(video);
            cover.setVideo(video);
            String userId = String.valueOf(cover.getUserId());
            String key = String.valueOf(cover.getVideoCoverId());
            setDataToRedis(userId, key, cover);
            setDataToRedis(userId, key, video);
            logger.info("run the async method success.");
        }catch (IOException | MyException | InvocationTargetException | IllegalAccessException e){
            logger.error("run the async method error.\n {}",e.getMessage());
        }catch (Exception e){
            logger.error("run the async method get a unexpected exception {} , the reason is {}",e,e.getMessage());
        }
    }

    @Override
    public void saveBatchVideo(MultipartFile[] files, List<VideoCover> covers) {
        if(covers == null && covers.size() == 0){
            return;
        }
        if(files == null && files.length == 0){
            return;
        }
        if(files.length != covers.size()){
            return;
        }
        try{
            String userId = String.valueOf(covers.get(0).getUserId());
            String key = String.valueOf(covers.get(0).getVideoCoverId());
            Iterator<VideoCover> coverIterator = covers.iterator();
            for (MultipartFile file : files) {
                String[] uploadRes = dfs.upload(file.getBytes(),file.getOriginalFilename().split(".")[1]);
                Video video = fileInfoHandler.fileInfoToBean(file, uploadRes, Video.class);
                VideoCover cover = coverIterator.next();
                video.setVideoCoverId(cover.getVideoCoverId());
                cover.setVideo(video);
                videoDao.insert(video);
                setDataToRedis(userId,key,video);
                setDataToRedis(userId,key,cover);
            }
            logger.info("async method handler success.");
        }catch (IOException | MyException | InvocationTargetException | IllegalAccessException e){
            logger.error("run the async method error.\n {}",e.getMessage());
        }catch (Exception e){
            logger.error("run the async get a unexpected exception {} , the reason is {}",e,e.getMessage());
        }
    }
}
