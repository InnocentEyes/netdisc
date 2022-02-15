package com.qzlnode.netdisc.service.Impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qzlnode.netdisc.dao.ImgDao;
import com.qzlnode.netdisc.fastdfs.FastDFS;
import com.qzlnode.netdisc.pojo.Img;
import com.qzlnode.netdisc.redis.ImgKey;
import com.qzlnode.netdisc.redis.RedisService;
import com.qzlnode.netdisc.service.ImgService;
import com.qzlnode.netdisc.util.FileInfoHandler;
import com.qzlnode.netdisc.util.MessageHolder;
import org.csource.common.MyException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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

    @Autowired
    private FileInfoHandler fileInfoHandler;

    @Autowired
    private FastDFS fastDFS;


    /**
     *
     * @param file
     * @return
     */
    @Override
    public Img uploadImg(MultipartFile file)
            throws IOException, MyException, InvocationTargetException, IllegalAccessException {
        if(file == null){
            return null;
        }
        String[] uploadRes = fastDFS.upload(file.getBytes(),file.getOriginalFilename().split("\\.")[1]);
        Img img = fileInfoHandler.fileInfoToBean(file,uploadRes,Img.class);
        img.setUserId(MessageHolder.getUserId());
        int result = imgDao.insert(img);
        if(result != 1){
            return null;
        }
        return img;
    }

    /**
     *
     * @param imgId
     * @return
     */
    @Override
    public Img getImg(Integer imgId) {
        Img img = redisService.get(ImgKey.img,String.valueOf(imgId),Img.class);
        if(img != null){
            return img;
        }
        img = imgDao.selectOne(
                Wrappers.lambdaQuery(Img.class)
                        .eq(Img::getImgId,imgId)
                        .eq(Img::getUserId,MessageHolder.getUserId())
        );
        redisService.set(ImgKey.img,String.valueOf(imgId),img);
        redisService.setSet(ImgKey.imgList,String.valueOf(MessageHolder.getUserId()),img);
        return img;
    }

    /**
     *
     * @return
     */
    @Override
    public List<Img> getAllImg(){
        List<Img> images = redisService.getList(ImgKey.imgList,String.valueOf(MessageHolder.getUserId()),Img.class);
        if(images != null || images.size() != 0){
            return images;
        }
        List<Img> imgList = imgDao.selectList(
                Wrappers.lambdaQuery(Img.class)
                        .eq(Img::getUserId,MessageHolder.getUserId())
        );
        if(imgList == null || imgList.size() == 0){
            return null;
        }
        redisService.setSet(ImgKey.imgList,String.valueOf(MessageHolder.getUserId()),imgList);
        return imgList;
    }

    /**
     *
     * @param files
     * @return
     */
    @Override
    public List<Img> multiUpload(MultipartFile[] files)
            throws IOException, MyException, InvocationTargetException, IllegalAccessException {
        if(files == null || files.length == 0){
            return null;
        }
        List<Img> images = new ArrayList<>();
        for (MultipartFile file : files) {
            String[] uploadRes = fastDFS.upload(file.getBytes(),file.getOriginalFilename().split("\\.")[1]);
            Img image = fileInfoHandler.fileInfoToBean(file,uploadRes,Img.class);
            image.setUserId(MessageHolder.getUserId());
            images.add(image);
        }
        return saveBatch(images) ? images : null;
    }
}
