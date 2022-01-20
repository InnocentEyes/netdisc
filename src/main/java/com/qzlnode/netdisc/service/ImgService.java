package com.qzlnode.netdisc.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.qzlnode.netdisc.pojo.Img;

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
}
