package com.qzlnode.netdisc.service.Impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qzlnode.netdisc.dao.ImgDao;
import com.qzlnode.netdisc.pojo.Img;
import com.qzlnode.netdisc.redis.ImgKey;
import com.qzlnode.netdisc.redis.RedisService;
import com.qzlnode.netdisc.service.AsyncService;
import com.qzlnode.netdisc.service.ImgService;
import com.qzlnode.netdisc.util.MessageHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
        if(result == 1){
            return img;
        }
        return null;
    }

    /**
     *
     * @param imgId
     * @return
     */
    @Override
    public Img imgDownload(Integer imgId) {
        return redisService.get(ImgKey.img,String.valueOf(imgId),Img.class);
    }

    /**
     *
     * @return
     */
    @Override
    public List<Img> getUserImg(){
       return Arrays.stream(redisService.get(ImgKey.imgList, String.valueOf(MessageHolder.getUserId()), Img[].class))
                    .collect(Collectors.toList());
    }

    /**
     *
     * @param imgs
     * @return
     */
    @Override
    public List<Img> saveMultImg(List<Img> imgs) {
        String userId = String.valueOf(MessageHolder.getUserId());
        if(!saveBatch(imgs)){
            return null;
        }
        imgs.stream().forEach(element ->{
            redisService.set(ImgKey.img,String.valueOf(element.getImgId()),element);
            redisService.setList(ImgKey.imgList,userId,element);
        });
        return imgs;
    }
}
