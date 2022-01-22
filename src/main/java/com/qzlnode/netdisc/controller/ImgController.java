package com.qzlnode.netdisc.controller;

import com.qzlnode.netdisc.exception.InconsistentException;
import com.qzlnode.netdisc.exception.UploadFileToLargeException;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * @author qzlzzz
 */
@RequestMapping("/img")
@RestController
public class ImgController {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private static final String IMG_PNG = "png";

    private static final String IMG_JPG = "jpg";

    private static final String IMG_JPEG = "jpeg";

    private static final int MAX_FILE_UPLOAD_COUNT = 5;

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
    public Result<Img> imgUpload(@RequestParam("img")MultipartFile img) throws IOException, MyException,
            InvocationTargetException, IllegalAccessException, NoSuchMethodException {
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

    /**
     *
     * @param imgId
     * @return
     * @throws MyException
     * @throws IOException
     */
    @RequestMapping("/single/download/{imgId}")
    public ResponseEntity<byte[]> singleImgload(@PathVariable("imgId") Integer imgId) throws MyException, IOException {
        Img img = service.imgDownload(imgId);
        String imgType = img.getImgType();
        HttpHeaders headers = new HttpHeaders();
        if(imgType.contains(IMG_PNG)){
            headers.setContentType(MediaType.IMAGE_PNG);
        }else if(imgType.contains(IMG_JPG) || imgType.contains(IMG_JPEG)){
            headers.setContentType(MediaType.IMAGE_JPEG);
        }
        headers.setContentLength(img.getImgSize());
        byte[] downloadRes = fastDFS.download(img.getGroupName(),img.getImgRemotePath());
        if(downloadRes == null || downloadRes.length == 0){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(downloadRes, headers,HttpStatus.OK);
    }

    /**
     *
     * @return
     */
    @GetMapping("/user")
    public Result<List<Img>> getUserAllImg(){
        List<Img> imgs = service.getUserImg();
        return imgs == null ?
                Result.error(CodeMsg.GET_IMG_ERROR) :
                Result.success(imgs,CodeMsg.SUCCESS);
    }

    /**
     *
     * @param files
     * @return
     */
    @PostMapping("/mult/upload")
    public Result<List<Img>> mulletUpload(@RequestParam("imgs") MultipartFile[] files) throws IOException, MyException,
            InvocationTargetException, IllegalAccessException{
        if(files.length > MAX_FILE_UPLOAD_COUNT) {
            throw new UploadFileToLargeException("文件上传数量超过限制。");
        }
        String suffix = "png|jpe?g|bmp";
        List<Img> imgs = new ArrayList<>();
        for (MultipartFile file : files) {
            if(!Pattern.compile(suffix).matcher(file.getContentType()).find()){
                return Result.error(CodeMsg.IMG_TYPE_ERROR);
            }
            String fileExtName = file.getOriginalFilename().split(".")[1];
            String[] filePath = fastDFS.upload(file.getBytes(), fileExtName);
            imgs.add(fileHandler.fileInfoToBean(file,filePath,Img.class));
        }
        imgs = service.saveMultImg(imgs);
        return imgs == null ?
                Result.error(CodeMsg.FILE_UPLOAD_ERROR) :
                Result.success(imgs,CodeMsg.SUCCESS);
    }

    @ExceptionHandler({
            IOException.class,
            MyException.class,
            InvocationTargetException.class,
            IllegalArgumentException.class,
            NoSuchMethodException.class,
            InconsistentException.class,
            UploadFileToLargeException.class
    })
    public Result handlerError(Exception exception, HttpServletRequest request){
        logger.error("handler {} error. \n" +
                "the reason is {}",request.getRequestURL(),exception.getMessage());
        return Result.error(CodeMsg.ERROR.fillArgs(exception.getMessage()));
    }

}
