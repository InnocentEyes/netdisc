package com.qzlnode.netdisc.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.qzlnode.netdisc.pojo.Img;
import org.csource.common.MyException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;

/**
 * @author qzlzzz
 */
public interface ImgService extends IService<Img> {

    /**
     *
     * @param file
     * @return
     */
    Img uploadImg(MultipartFile file) throws IOException, MyException, InvocationTargetException, IllegalAccessException;

    /**
     *
     * @param imgId
     * @return
     */
    Img getImg(Integer imgId);

    /**
     *
     * @return
     */
    List<Img> getAllImg();

    /**
     *
     * @return
     */
    List<Img> multiUpload(MultipartFile[] files) throws IOException, MyException, InvocationTargetException, IllegalAccessException;
}
