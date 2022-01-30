package com.qzlnode.netdisc.controller;

import com.qzlnode.netdisc.exception.NoSuchFileException;
import com.qzlnode.netdisc.exception.UploadFileToLargeException;
import com.qzlnode.netdisc.fastdfs.FastDFS;
import com.qzlnode.netdisc.pojo.Music;
import com.qzlnode.netdisc.redis.MusicKey;
import com.qzlnode.netdisc.result.CodeMsg;
import com.qzlnode.netdisc.result.Result;
import com.qzlnode.netdisc.service.AsyncService;
import com.qzlnode.netdisc.service.MusicService;
import com.qzlnode.netdisc.util.FileInfoHandler;
import com.qzlnode.netdisc.util.MessageHolder;
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
import java.util.Arrays;
import java.util.List;

/**
 * @author qzlzzz
 */
@RequestMapping("/music")
@RestController
public class MusicController {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private static final Integer MAX_MUSIC_UPLOAD = 3;

    @Autowired
    private FastDFS fastDFS;

    @Autowired
    private FileInfoHandler fileInfoHandler;

    @Autowired
    private MusicService musicService;

    @Autowired
    private AsyncService asyncService;

    @PostMapping("/single/upload")
    public Result<Music> singleUpload(@RequestParam(value = "music",required = false) MultipartFile file)
            throws InvocationTargetException, IllegalAccessException {
        if(file == null){
            logger.info("无文件接受至服务端");
            return Result.error(CodeMsg.FILE_CANNOT_ACCPET);
        }
        if(!fileInfoHandler.isSupport(file.getOriginalFilename(),Music.class)){
            return Result.error(CodeMsg.MUSIC_TYPE_ERROR);
        }
        Music music = musicService.saveMusic(file);
        asyncService.saveMusic(file,music.getMusicId(),music.getUserId());
        return Result.success(music,CodeMsg.SUCCESS);
    }

    @GetMapping("/download/{musicId}")
    public ResponseEntity<byte[]> download(@PathVariable(value = "musicId") Integer musicId)
            throws MyException, IOException {
        Music music = musicService.getMusic(musicId);
        if(music == null){
            throw new NoSuchFileException("没有此文件");
        }
        byte[] res = fastDFS.download(music.getGroupName(),music.getMusicRemotePath());
        HttpHeaders headers = new HttpHeaders();
        headers.setContentLength(music.getMusicSize());
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment",music.getMusicOriginName());
        return new ResponseEntity<>(res,headers, HttpStatus.OK);
    }

    @GetMapping("/get/{musicId}")
    public Result<Music> getMusic(@PathVariable(value = "musicId",required = false) Integer musicId){
        if(musicId == null){
            return Result.error(CodeMsg.BIND_ERROR);
        }
        if(musicId < 1){
            return Result.error(CodeMsg.SERVER_ERROR);
        }
        Music music = musicService.getMusic(musicId);
        return music == null ? Result.error(CodeMsg.FILE_NO_EXIST) : Result.success(music,CodeMsg.SUCCESS);
    }

    @RequestMapping("/user/get")
    public Result<List<Music>> getUserMusic(){
        List<Music> muscles = musicService.getBatchMusic();
        return muscles == null ? Result.error(CodeMsg.FILE_NO_EXIST) : Result.success(muscles,CodeMsg.SUCCESS);
    }

    @PostMapping("/multi/upload")
    public Result<List<Music>> multiUpload(@RequestParam(value = "musics",required = false) MultipartFile[] files)
            throws IOException, MyException, InvocationTargetException, IllegalAccessException {
        if(files == null){
            return Result.error(CodeMsg.FILE_CANNOT_ACCPET);
        }
        if(files.length > MAX_MUSIC_UPLOAD){
            throw new UploadFileToLargeException("上传文件超出限制");
        }
        files = Arrays.stream(files)
                .filter(file -> fileInfoHandler.isSupport(file.getOriginalFilename(),Music.class))
                .toArray(MultipartFile[]::new);
        List<Music> musicList = musicService.saveBatchMusic(files);
        if(musicList == null){
            return Result.error(CodeMsg.FILE_UPLOAD_ERROR);
        }
        asyncService.saveBatchMusic(
                files,
                musicList.stream().map(Music::getMusicId).toArray(Integer[]::new),
                MessageHolder.getUserId()
        );
        return Result.success(musicList,CodeMsg.SUCCESS);
    }

}
