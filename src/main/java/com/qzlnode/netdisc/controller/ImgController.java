package com.qzlnode.netdisc.controller;

import com.qzlnode.netdisc.fastdfs.FastDFS;
import com.qzlnode.netdisc.pojo.Img;
import com.qzlnode.netdisc.result.CodeMsg;
import com.qzlnode.netdisc.result.Result;
import com.qzlnode.netdisc.service.ImgService;
import com.qzlnode.netdisc.util.FileInfoHandler;
import org.csource.common.MyException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.regex.Pattern;

/**
 * @author qzlzzz
 */
@RequestMapping("/img")
@RestController
public class ImgController {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private FastDFS fastDFS;

    @Autowired
    private ImgService service;

    @Autowired
    private FileInfoHandler fileHandler;

    /**
     *
     * @return
     */
    @PostMapping("/single/upload")
    public Result<Img> imgUpload(@RequestParam("img")MultipartFile img) throws IOException, MyException, InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        String suffix = "png|jpe?g|bmp";
        if(!Pattern.compile(suffix).matcher(img.getContentType()).find()){
            return Result.error(CodeMsg.IMG_TYPE_ERROR);
        }
        String[] uploadRes = fastDFS.upload(img.getBytes(), img.getOriginalFilename().split(".")[1]);
        Img res = fileHandler.fileInfoToBean(img, uploadRes, Img.class);
        return service.imgUpload(res) == null ?
                Result.error(CodeMsg.FILE_UPLOAD_ERROR) :
                Result.success(res,CodeMsg.SUCCESS);
    }

    @RequestMapping("/single/download/{imgId}")
    public ResponseEntity<byte[]> singleImgload(@PathVariable("imgId") Integer imgId){
        Img img = service.imgDownload(imgId);
        HttpHeaders headers = new HttpHeaders();
        return null;
    }

    @ExceptionHandler({
            IOException.class,
            MyException.class,
            InvocationTargetException.class,
            IllegalArgumentException.class,
            NoSuchMethodException.class
    })
    public Result handlerError(Exception exception, HttpServletRequest request){
        logger.error("handler {} error. \n" +
                "the reason is {}",request.getRequestURL(),exception.getMessage());
        return Result.error(CodeMsg.ERROR.fillArgs(exception.getMessage()));
    }

}
