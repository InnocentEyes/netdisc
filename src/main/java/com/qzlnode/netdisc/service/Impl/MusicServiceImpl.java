package com.qzlnode.netdisc.service.Impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qzlnode.netdisc.dao.MusicDao;
import com.qzlnode.netdisc.pojo.Music;
import com.qzlnode.netdisc.redis.DocumentKey;
import com.qzlnode.netdisc.redis.MusicKey;
import com.qzlnode.netdisc.redis.RedisService;
import com.qzlnode.netdisc.service.MusicService;
import com.qzlnode.netdisc.util.Cache;
import com.qzlnode.netdisc.util.FileInfoHandler;
import com.qzlnode.netdisc.util.MessageHolder;
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
import java.util.Arrays;
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
public class MusicServiceImpl extends ServiceImpl<MusicDao,Music> implements MusicService {

    /**
     * 日志
     */
    private final Logger logger = LoggerFactory.getLogger(getClass());


    @Autowired
    private MusicDao musicDao;

    @Autowired
    private RedisService redisService;

    @Autowired
    private FileInfoHandler fileInfoHandler;


    @Override
    public Music saveMusic(MultipartFile file)
            throws InvocationTargetException, IllegalAccessException {
        if(file == null || file.getOriginalFilename() == null){
            return null;
        }
        String fileNameInfo = file.getOriginalFilename().substring(0,file.getOriginalFilename().indexOf("."));
        Music music = fileInfoHandler.fileInfoToBean(file,null,Music.class);
        music = fileInfoHandler.handlerNameInfo(fileNameInfo,music);
        music.setUserId(MessageHolder.getUserId());
        int result = musicDao.insert(music);
        if(result != 1){
            return null;
        }
        return music;
    }

    @Override
    public Music getMusic(Integer musicId) {
        Music music = redisService.get(MusicKey.music,String.valueOf(musicId),Music.class);
        if(music != null){
            return music;
        }
        String key = MusicKey.music.getPrefix() + MessageHolder.getUserId();
        String value = MusicKey.music.getPrefix() + musicId;
        while (Cache.hasTask(key,value)){
            LockSupport.parkNanos(100);
        }
        Cache.removeAsyncKey(key);
        music = musicDao.selectOne(
                Wrappers.lambdaQuery(Music.class)
                        .eq(Music::getUserId,MessageHolder.getUserId())
                        .eq(Music::getMusicId,musicId)
        );
        if(music == null){
            return null;
        }
        redisService.set(MusicKey.music,String.valueOf(musicId),music);
        redisService.setSet(MusicKey.musicList,String.valueOf(MessageHolder.getUserId()),music);
        return music;
    }

    @Override
    public List<Music> getBatchMusic() {
        Integer userId = MessageHolder.getUserId();
        String key = DocumentKey.document.getPrefix() + userId;
        if(!Cache.hasTask(key)){
            return redisService.getList(DocumentKey.documentList,String.valueOf(userId),Music.class);
        }
        while(Cache.hasTask(key)){
            LockSupport.parkNanos(100);
        }
        Cache.removeAsyncKey(key);
        List<Music> musicList = musicDao.selectList(
                Wrappers.lambdaQuery(Music.class)
                        .eq(Music::getUserId,userId)
        );
        if(musicList == null || musicList.size() == 0){
            return null;
        }
        redisService.setSet(DocumentKey.documentList,String.valueOf(userId),musicList);
        return musicList;
    }

    @Override
    public List<Music> saveBatchMusic(MultipartFile[] files)
            throws InvocationTargetException, IllegalAccessException {
        List<Music> musicList = new ArrayList<>();
        for (MultipartFile file : files) {
            Music music = fileInfoHandler.fileInfoToBean(file,null,Music.class);
            music.setUserId(MessageHolder.getUserId());
            musicList.add(music);
        }
        boolean isSave = saveBatch(musicList);
        if(!isSave){
            return null;
        }
        return musicList;
    }
}
