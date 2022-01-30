package com.qzlnode.netdisc.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.qzlnode.netdisc.pojo.Music;
import org.springframework.web.multipart.MultipartFile;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

/**
 * @author qzlzzz
 */
public interface MusicService extends IService<Music> {

    /**
     *
     * @param file
     * @return
     */
    Music saveMusic(MultipartFile file) throws InvocationTargetException, IllegalAccessException;

    /**
     *
     * @return
     */
    Music getMusic(Integer musicId);

    /**
     *
     * @return
     */
    List<Music> getBatchMusic();

    /**
     *
     * @param files
     * @return
     */
    List<Music> saveBatchMusic(MultipartFile[] files) throws InvocationTargetException, IllegalAccessException;
}
