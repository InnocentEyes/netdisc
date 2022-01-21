package com.qzlnode.netdisc.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.qzlnode.netdisc.pojo.Img;

import java.util.List;
import java.util.Map;

/**
 * @author qzlzzz
 */
public interface ImgService extends IService<Img> {

    /**
     *
     * @param img
     * @return
     */
    Img imgUpload(Img img);

    /**
     *
     * @param imgId
     * @return
     */
    Img imgDownload(Integer imgId);

    /**
     *
     * @return
     */
    List<Img> getUserImg();

    /**
     *
     * @return
     */
    List<Img> saveMultImg(List<Img> imgs);
}
