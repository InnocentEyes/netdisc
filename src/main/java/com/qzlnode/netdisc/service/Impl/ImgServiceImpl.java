package com.qzlnode.netdisc.service.Impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qzlnode.netdisc.dao.ImgDao;
import com.qzlnode.netdisc.pojo.Img;
import com.qzlnode.netdisc.redis.ImgKey;
import com.qzlnode.netdisc.redis.RedisService;
import com.qzlnode.netdisc.service.ImgService;
import com.qzlnode.netdisc.util.MessageHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @author qzlzzz
 */
@Service
public class ImgServiceImpl extends ServiceImpl<ImgDao, Img> implements ImgService {

    /**
     * 日志
     */
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private ImgDao imgDao;

    @Autowired
    private RedisService redisService;

    /**
     *
     * @param img
     * @return
     */
    @Override
    public Img imgUpload(Img img) {
        int result = imgDao.insert(img);
        if(result == 1 && redisService.set(ImgKey.img,String.valueOf(MessageHolder.getUserId()),img)) {
            return img;
        }
        return null;
    }

    @Override
    public Img imgDownload(Integer imgId) {
        return null;
    }
}
