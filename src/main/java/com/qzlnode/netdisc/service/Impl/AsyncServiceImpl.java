package com.qzlnode.netdisc.service.Impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.qzlnode.netdisc.dao.DocumentDao;
import com.qzlnode.netdisc.dao.MusicDao;
import com.qzlnode.netdisc.dao.VideoDao;
import com.qzlnode.netdisc.fastdfs.FastDFS;
import com.qzlnode.netdisc.pojo.Document;
import com.qzlnode.netdisc.pojo.Music;
import com.qzlnode.netdisc.pojo.Video;
import com.qzlnode.netdisc.redis.*;
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
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicInteger;

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

    private static final String DIFFERENCE = "List";

    public static AtomicInteger target = new AtomicInteger(0);

    public static Map<String, CopyOnWriteArraySet<String>> Cache = new ConcurrentHashMap<>();

    @Autowired
    private FastDFS dfs;

    @Autowired
    private FileInfoHandler fileInfoHandler;

    @Autowired
    private VideoDao videoDao;

    @Autowired
    private DocumentDao documentDao;

    @Autowired
    private RedisService redisService;

    @Autowired
    private MusicDao musicDao;


    @Override
    public <T> void saveVideo(String key, String userId, MultipartFile file, T value, KeyPrefix... keyPrefix){
        logger.info("异步任务已经启动");
        if(file == null || file.getOriginalFilename() == null || keyPrefix.length == 0) {
            return;
        }
        if(keyPrefix.length > 2){
            return;
        }
        try{
            target.incrementAndGet();
            String[] uploadRes = dfs.upload(file.getBytes(),file.getOriginalFilename().split("\\.")[1]);
            Video video = fileInfoHandler.fileInfoToBean(file, uploadRes, Video.class);
            Integer coverId = (Integer) value.getClass().getMethod("getVideoCoverId").invoke(value);
            video.setVideoCoverId(coverId);
            videoDao.insert(video);
            value.getClass().getMethod("setVideo").invoke(value,video);
            Arrays.stream(keyPrefix).forEach(element -> {
                if(element instanceof VideoKey){
                    redisService.set(element,key,video);
                }
                if(element instanceof VideoCoverKey && element.getPrefix().contains(DIFFERENCE)){
                    redisService.setList(element,userId,value);
                }
                if(element instanceof VideoCoverKey){
                    redisService.set(element,key,value);
                }
            });
            logger.info("异步任务已经结束");
        }catch (IOException | MyException | InvocationTargetException | IllegalAccessException e){
            logger.error("run the async method error.\n {}",e.getMessage());
        }catch (Exception e){
            logger.error("run the async method get a unexpected exception {} , the reason is {}",e,e.getMessage());
        }finally {
            target.decrementAndGet();
        }
    }

    @Override
    public <T> void saveBatchVideo(MultipartFile[] files, List<T> values, String userId, KeyPrefix... keyPrefixes){
        if(files == null || files.length == 0){
            return;
        }
        if(values == null || values.size() == 0){
            return;
        }
        if(files.length != values.size()){
            return;
        }
        Iterator<T> iterator = values.iterator();
        for (MultipartFile file : files) {
            T value = iterator.next();
            String key = null;
            try {
                key = (String) value.getClass().getMethod("getVideoCoverId").invoke(value);
            } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
                logger.error("In save video batch error. {}",e.getMessage());
                break;
            }
            saveVideo(key,userId,file,value,keyPrefixes);
        }
    }

    @Override
    public void saveDocument(MultipartFile file, Integer fileId,Integer userId) {
        if(file == null || fileId == null || fileId < 1 || userId < 1){
            return;
        }
        String key = DocumentKey.document.getPrefix() + userId;
        String value = DocumentKey.document.getPrefix() + fileId;
        Cache.computeIfAbsent(key,k -> new CopyOnWriteArraySet<>()).add(value);
        try {
            String[] uploadRes = dfs.upload(file.getBytes(),file.getOriginalFilename().split("\\.")[1]);
            Document document = fileInfoHandler.fileInfoToBean(file,uploadRes, Document.class);
            documentDao.update(
                    null,
                    Wrappers.lambdaUpdate(Document.class)
                            .eq(Document::getUserId,userId)
                            .eq(Document::getFileId, fileId)
                            .set(Document::getGroupName, document.getGroupName())
                            .set(Document::getFileRemotePath, document.getFileRemotePath()));
        } catch (MyException | IOException | InvocationTargetException | IllegalAccessException e) {
            logger.error("run the async method error.\n {}",e.getMessage());
        } catch (Exception e){
            logger.error("run the async method get a unexpected exception {} , the reason is {}",e,e.getMessage());
        }finally {
            Cache.get(key).remove(value);
        }

    }

    @Override
    public void saveBatchDocument(MultipartFile[] files,Integer[] fileIds,Integer userId){
        if(files == null || fileIds == null){
            return;
        }
        if(fileIds.length == 0 || files.length != fileIds.length){
            return;
        }
        int index = 0;
        for (MultipartFile file : files) {
            saveDocument(file,fileIds[index++],userId);
        }
    }

    @Override
    public void saveMusic(MultipartFile file, Integer fileId, Integer userId){
        if(file == null || fileId == null || fileId < 1 || userId == null || userId < 1){
            return;
        }
        String key = MusicKey.music.getPrefix() + userId;
        String value = MusicKey.music.getPrefix() + fileId;
        Cache.computeIfAbsent(key,k -> new CopyOnWriteArraySet<>()).add(value);
        try {
            String[] uploadRes = dfs.upload(file.getBytes(),file.getOriginalFilename().split("\\.")[1]);
            Music music = fileInfoHandler.fileInfoToBean(file,uploadRes, Music.class);
            musicDao.update(
                    null,
                    Wrappers.lambdaUpdate(Music.class)
                            .eq(Music::getUserId,userId)
                            .eq(Music::getMusicId,fileId)
                            .set(Music::getGroupName,music.getGroupName())
                            .set(Music::getMusicRemotePath,music.getMusicRemotePath())
            );
        } catch (MyException | IOException | InvocationTargetException | IllegalAccessException e) {
            logger.error("run the async method error.\n {}",e.getMessage());
        } catch (Exception e){
            logger.error("run the async method get a unexpected exception {} , the reason is {}",e,e.getMessage());
        } finally {
            Cache.get(key).remove(value);
        }
    }

    @Override
    public void saveBatchMusic(MultipartFile[] files, Integer[] fileIds, Integer userId) {
        if(files == null || fileIds == null){
            return;
        }
        if(fileIds.length == 0 || files.length != fileIds.length){
            return;
        }
        int index = 0;
        for (MultipartFile file : files) {
            saveMusic(file,fileIds[index++],userId);
        }
    }
}
