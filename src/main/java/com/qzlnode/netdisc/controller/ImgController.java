package com.qzlnode.netdisc.controller;

import com.qzlnode.netdisc.exception.UploadFileToLargeException;
import com.qzlnode.netdisc.FastDFS;
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

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;

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
    private FileInfoHandler fileInfoHandler;

    /**
     *
     * @return
     */
    @PostMapping("/single/upload")
    public Result<Img> singleUpload(@RequestParam(value = "img",required = false)MultipartFile img) throws IOException, MyException,
            InvocationTargetException, IllegalAccessException{
        if (img == null){
            return Result.error(CodeMsg.FILE_CANNOT_ACCPET);
        }
        if(!fileInfoHandler.isSupport(img.getOriginalFilename(),Img.class)){
            return Result.error(CodeMsg.IMG_TYPE_ERROR);
        }
        Img uploadRes = service.uploadImg(img);
        if(uploadRes == null){
            return Result.error(CodeMsg.FILE_UPLOAD_ERROR);
        }
        return Result.success(uploadRes,CodeMsg.SUCCESS);
    }

    /**
     *
     * @param imgId
     * @return
     * @throws MyException
     * @throws IOException
     */
    @RequestMapping("/download/{imgId}")
    public ResponseEntity<byte[]> download(@PathVariable("imgId") Integer imgId) throws MyException, IOException {
        Img img = service.getImg(imgId);
        if(img == null){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        String imgType = img.getImgType();
        HttpHeaders headers = new HttpHeaders();
        if(imgType.contains(IMG_PNG)){
            headers.setContentType(MediaType.IMAGE_PNG);
        }else if(imgType.contains(IMG_JPG) || imgType.contains(IMG_JPEG)){
            headers.setContentType(MediaType.IMAGE_JPEG);
        }else {
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        }
        headers.setContentLength(img.getImgSize());
        headers.setContentDispositionFormData("attachment",img.getImgOriginName());
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
        List<Img> imgs = service.getAllImg();
        return imgs == null ?
                Result.error(CodeMsg.GET_IMG_ERROR) :
                Result.success(imgs,CodeMsg.SUCCESS);
    }

    /**
     *
     * @param files
     * @return
     */
    @PostMapping("/multi/upload")
    public Result<List<Img>> mulletUpload(@RequestParam("imgs") MultipartFile[] files) throws IOException, MyException,
            InvocationTargetException, IllegalAccessException{
        if(files.length > MAX_FILE_UPLOAD_COUNT) {
            throw new UploadFileToLargeException("文件上传数量超过限制。");
        }
        files = Arrays.stream(files)
                .filter(file -> fileInfoHandler.isSupport(file.getOriginalFilename(),Img.class))
                .toArray(MultipartFile[]::new);
        List<Img> images = service.multiUpload(files);
        return images == null ?
                Result.error(CodeMsg.FILE_UPLOAD_ERROR) :
                Result.success(images,CodeMsg.SUCCESS);
    }

}
