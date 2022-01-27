package com.qzlnode.netdisc.service;

import com.qzlnode.netdisc.pojo.Music;

import java.util.List;

/**
 * @author qzlzzz
 */
public interface MusicService {

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
