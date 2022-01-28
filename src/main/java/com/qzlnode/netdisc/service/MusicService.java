package com.qzlnode.netdisc.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.qzlnode.netdisc.pojo.Music;

import java.util.List;

/**
 * @author qzlzzz
 */
public interface MusicService extends IService<Music> {

    /**
     *
     * @param music
     * @return
     */
    Music saveMusic(Music music);

    /**
     *
     * @return
     */
    Music getMusicByMusicId(Integer musicId);

    /**
     *
     * @return
     */
    List<Music> getUserMusic();
}
