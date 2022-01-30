package com.qzlnode.netdisc.controller;

import com.qzlnode.netdisc.exception.UploadFileToLargeException;
import com.qzlnode.netdisc.fastdfs.FastDFS;
import com.qzlnode.netdisc.pojo.Video;
import com.qzlnode.netdisc.pojo.VideoCover;
import com.qzlnode.netdisc.result.CodeMsg;
import com.qzlnode.netdisc.result.Result;
import com.qzlnode.netdisc.service.AsyncService;
import com.qzlnode.netdisc.service.VideoService;
import com.qzlnode.netdisc.util.FileInfoHandler;
import com.qzlnode.netdisc.util.MessageHolder;
import com.qzlnode.netdisc.util.VideoUtil;
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
import java.util.ArrayList;
import java.util.List;

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

    private static final int MAX_UPLOAD_VIDEO = 3;

    @Autowired
    private VideoUtil videoUtil;

    @Autowired
    private VideoService videoService;

    @Autowired
    private FastDFS fastDFS;

    @Autowired
    private AsyncService asyncService;

    @Autowired
    private FileInfoHandler fileInfoHandler;


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
            logger.info("无文件接受至服务端");
            return Result.error(CodeMsg.FILE_CANNOT_ACCPET);
        }
        if(!fileInfoHandler.isSupport(file.getOriginalFilename(),Video.class)){
            return Result.error(CodeMsg.VIDEO_TYPE_ERROR);
        }
        VideoCover videoCover = videoService.uploadVideoCover(file);
        if(videoCover == null){
            return Result.error(CodeMsg.FILE_UPLOAD_ERROR);
        }
        Video video = videoService.handlerVideo(file,videoCover.getVideoCoverId());
        if(video == null){
            return Result.error(CodeMsg.FILE_UPLOAD_ERROR);
        }
        asyncService.uploadVideo(file,video.getVideoCoverId(),MessageHolder.getUserId());
        return Result.success(videoCover,CodeMsg.SUCCESS);
    }

    /**
     *
     * @param coverId
     * @return
     */
    @GetMapping("/getVideo/{coverId}")
    public Result<Video> getVideo(@PathVariable(value = "coverId",required = false) Integer coverId){
        if(coverId == null){
            logger.info("无参数接受至服务端");
            return Result.error(CodeMsg.BIND_ERROR);
        }
        Video video = videoService.getVideo(coverId);
        return video == null ? Result.error(CodeMsg.UNWOUND_VIDEO) : Result.success(video,CodeMsg.SUCCESS);
    }

    /**
     *
     * @return
     */
    @RequestMapping("/user/get")
    public Result<List<VideoCover>> getUserVideo(){
        List<VideoCover> videoCoverList = videoService.getUserVideoList();
        return videoCoverList == null ?
                Result.error(CodeMsg.UNWOUND_VIDEO) :
                Result.success(videoCoverList,CodeMsg.SUCCESS);
    }

    /**
     *
     * @param coverId
     * @return
     */
    @GetMapping("/getDetail/{coverId}")
    public Result<VideoCover> getCoverWithVideo(@PathVariable(value = "coverId",required = false) Integer coverId){
        if(coverId == null){
            return Result.error(CodeMsg.BIND_ERROR);
        }
        VideoCover cover = videoService.getVideoDetail(coverId);
        return cover == null ? Result.error(CodeMsg.FILE_NO_EXIST) : Result.success(cover,CodeMsg.SUCCESS);
    }

    /**
     *
     * @param videoId
     * @return
     * @throws MyException
     * @throws IOException
     */
    @GetMapping("/download/{videoId}")
    public ResponseEntity<byte[]> downloadVideo(@PathVariable("videoId") Integer videoId)
            throws MyException, IOException {
        Video video = videoService.getVideo(videoId);
        byte[] downloadRes = fastDFS.download(video.getGroupName(), video.getVideoRemotePath());
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentLength(video.getVideoSize());
        return new ResponseEntity<>(downloadRes,headers, HttpStatus.OK);
    }

    @PostMapping("/multi/upload")
    public Result<List<VideoCover>> multiUpload(@RequestParam(value = "videos",required = false) MultipartFile[] files)
            throws IOException, MyException, InvocationTargetException, IllegalAccessException {
        if(files == null || files.length == 0){
            logger.info("无参数接受至服务端");
            return Result.error(CodeMsg.FILE_CANNOT_ACCPET);
        }
        if(files.length > MAX_UPLOAD_VIDEO){
            logger.info("上传文件过多");
            throw new UploadFileToLargeException("上传文件超出限制");
        }
        List<VideoCover> covers = new ArrayList<>();
        for (MultipartFile file : files) {
            byte[] imgBytes = videoUtil.fetchFrame(file.getInputStream());
            String[] uploadRes = fastDFS.upload(imgBytes, DEFAULT_IMG_TYPE);
            VideoCover cover = fileInfoHandler.pathToBean(uploadRes,VideoCover.class);
            cover.setVideoOriginName(file.getOriginalFilename());
            cover.setUserId(MessageHolder.getUserId());
            covers.add(cover);
        }
        covers = videoService.saveVideoCoverList(covers);
        /**
         * 启动异步
         */
        String userId = String.valueOf(MessageHolder.getUserId());
        return covers == null ? Result.error(CodeMsg.FILE_UPLOAD_ERROR) : Result.success(covers,CodeMsg.SUCCESS);
    }

    /**
     * 推荐使用此接口
     * @return
     */
    @PostMapping("/upload")
    public Result<VideoCover> uploadWithCover(@RequestParam(value = "cover",required = false) MultipartFile cover,
                                              @RequestParam(value = "video",required = false) MultipartFile video)
            throws IOException, MyException, InvocationTargetException, IllegalAccessException {
        if(cover == null || video == null){
            return Result.error(CodeMsg.FILE_CANNOT_ACCPET);
        }
        return null;
    }
}
