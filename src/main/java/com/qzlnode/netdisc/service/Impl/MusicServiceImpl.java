package com.qzlnode.netdisc.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qzlnode.netdisc.dao.MusicDao;
import com.qzlnode.netdisc.pojo.Music;
import com.qzlnode.netdisc.redis.MusicKey;
import com.qzlnode.netdisc.redis.RedisService;
import com.qzlnode.netdisc.service.MusicService;
import com.qzlnode.netdisc.util.MessageHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author qzlzzz
 */
@Service
public class MusicServiceImpl extends ServiceImpl<MusicDao,Music> implements MusicService {

    @Autowired
    private MusicDao musicDao;

    @Autowired
    private RedisService redisService;

    @Override
    public Music saveMusic(Music music) {
        if(music.getUserId() == null || music.getMusicId() == 0){
            music.setUserId(MessageHolder.getUserId());
        }
        musicDao.insert(music);
        return music;
    }

    @Override
    public Music getMusicByMusicId(Integer musicId) {
        String key = String.valueOf(musicId);
        return redisService.get(MusicKey.music,key,Music.class);
    }

    @Override
    public List<Music> getUserMusic() {
        String userId = String.valueOf(MessageHolder.getUserId());
        Music[] musics = redisService.get(MusicKey.musicList,userId,Music[].class);
        if(musics == null){
            return null;
        }
        return Arrays.stream(musics).collect(Collectors.toList());
    }
}
