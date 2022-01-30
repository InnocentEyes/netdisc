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


    public static AtomicInteger target = new AtomicInteger(0);

    public static Map<String, CopyOnWriteArraySet<String>> Cache = new ConcurrentHashMap<>();

    @Autowired
    private FastDFS dfs;

    @Autowired
    private VideoDao videoDao;

    @Autowired
    private DocumentDao documentDao;

    @Autowired
    private MusicDao musicDao;


    @Override
    public void uploadVideo(MultipartFile file,Integer fileId,Integer userId){
        if(file == null || fileId == null || fileId < 1 || userId < 1){
            return;
        }
        String key = VideoKey.video.getPrefix() + userId;
        String value = VideoKey.video.getPrefix() + fileId;
        Cache.computeIfAbsent(key,k -> new CopyOnWriteArraySet<>()).add(value);
        try{
            String[] uploadRes = dfs.upload(file.getBytes(),file.getOriginalFilename().split("\\.")[1]);
            videoDao.update(
                    null,
                    Wrappers.lambdaUpdate(Video.class)
                            .eq(Video::getVideoId,fileId)
                            .set(Video::getGroupName,uploadRes[0])
                            .set(Video::getVideoRemotePath,uploadRes[1])
            );
        } catch (MyException | IOException e) {
            logger.error("run the async method error.\n {}",e.getMessage());
        } catch (Exception e){
            logger.error("run the async method get a unexpected exception {} , the reason is {}",e,e.getMessage());
        }finally {
            Cache.get(key).remove(value);
        }

    }

    @Override
    public void uploadBatchVideo(MultipartFile[] files,Integer[] fileIds,Integer userId){
        if(files == null || fileIds == null){
            return;
        }
        if(fileIds.length == 0 || files.length != fileIds.length){
            return;
        }
        int index = 0;
        for (MultipartFile file : files) {
            uploadVideo(file,fileIds[index++],userId);
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
            documentDao.update(
                    null,
                    Wrappers.lambdaUpdate(Document.class)
                            .eq(Document::getUserId,userId)
                            .eq(Document::getFileId, fileId)
                            .set(Document::getGroupName, uploadRes[0])
                            .set(Document::getFileRemotePath, uploadRes[1])
            );
        } catch (MyException | IOException e) {
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
            musicDao.update(
                    null,
                    Wrappers.lambdaUpdate(Music.class)
                            .eq(Music::getUserId,userId)
                            .eq(Music::getMusicId,fileId)
                            .set(Music::getGroupName,uploadRes[0])
                            .set(Music::getMusicRemotePath,uploadRes[1])
            );
        } catch (MyException | IOException e) {
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
