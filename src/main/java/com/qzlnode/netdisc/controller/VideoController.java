package com.qzlnode.netdisc.controller;

import com.qzlnode.netdisc.fastdfs.FastDFS;
import com.qzlnode.netdisc.pojo.Video;
import com.qzlnode.netdisc.pojo.VideoCover;
import com.qzlnode.netdisc.result.CodeMsg;
import com.qzlnode.netdisc.result.Result;
import com.qzlnode.netdisc.service.VideoService;
import com.qzlnode.netdisc.util.VideoUtil;
import org.csource.common.MyException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

/**
 * @author qzlzzz
 */
@RequestMapping("/video")
@RestController
public class VideoController {

    /**
     * 日志
     */
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private static final String DEFAULT_IMG_TYPE = "png";

    @Autowired
    private VideoUtil videoUtil;

    @Autowired
    private VideoService videoService;

    @Autowired
    private FastDFS fastDFS;


    /**
     *
     * @param file
     * @return
     * @throws IOException
     */
    @PostMapping("/single/upload")
    public Result<VideoCover> singleUpload(@RequestParam(value = "video",required = false) MultipartFile file)
            throws IOException, MyException, InvocationTargetException, IllegalAccessException {
        if(file == null){
            return Result.error(CodeMsg.FILE_CANNOT_ACCPET);
        }
        byte[] coverBytes = videoUtil.fetchFrame(file.getInputStream());
        String[] uploadRes = fastDFS.upload(coverBytes,DEFAULT_IMG_TYPE);
        if(uploadRes == null){
            return Result.error(CodeMsg.FILE_UPLOAD_ERROR);
        }
        VideoCover cover = videoService.saveVideoCover(uploadRes);
        /**
         * 这里调用异步任务,由于是异步任务所以无需在控制器方法内先处理文件，
         * 直接将文件传入异步方法中处理。
         */
        videoService.saveVideo(file,cover);
        return Result.success(cover,CodeMsg.SUCCESS);
    }

    @GetMapping("/get/{videoId}")
    public Result<Video> getVideo(@PathVariable("videoId") Integer videoId){
        return null;
    }

    /**
     * 服务端异常
     * @param exception
     * @param request
     * @return
     */
    @ExceptionHandler({
            IOException.class,
            MyException.class,
            InvocationTargetException.class,
            IllegalAccessException.class
    })
    public Result handlerError(Exception exception, HttpServletRequest request){
        logger.error("handler {} error.\n" +
                "the reason is {}",request.getRequestURL(),exception.getMessage());
        return Result.error(CodeMsg.ERROR.fillArgs(exception.getMessage()));
    }
}
