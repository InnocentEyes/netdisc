package com.qzlnode.netdisc.controller;

import com.qzlnode.netdisc.exception.NoSuchFileException;
import com.qzlnode.netdisc.fastdfs.FastDFS;
import com.qzlnode.netdisc.pojo.Music;
import com.qzlnode.netdisc.redis.MusicKey;
import com.qzlnode.netdisc.result.CodeMsg;
import com.qzlnode.netdisc.result.Result;
import com.qzlnode.netdisc.service.AsyncService;
import com.qzlnode.netdisc.service.MusicService;
import com.qzlnode.netdisc.util.FileInfoHandler;
import com.qzlnode.netdisc.util.MessageHolder;
import com.qzlnode.netdisc.util.Security;
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
import java.util.List;

/**
 * @author qzlzzz
 */
@RequestMapping("/music")
@RestController
public class MusicController {

    private static final String MUSIC_LOGO = "-";

    private static final String UNNAMED_SINGER = "未知歌手";

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private static final String[] SUPPORT_MUSIC = {
            "mp3",
            "m4a",
            "wav",
            "amr",
            "awb",
            "aac",
            "flac",
            "mid",
            "midi",
            "xmf",
            "rtx",
            "ota",
            "wma",
            "ra",
            "mka",
            "m3u",
            "pls"
    };

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
            throws IOException, MyException, InvocationTargetException, IllegalAccessException {
        if(file == null){
            logger.info("无文件接受至服务端");
            return Result.error(CodeMsg.FILE_CANNOT_ACCPET);
        }
        if(!isMusic(file.getOriginalFilename())){
            return Result.error(CodeMsg.MUSIC_TYPE_ERROR);
        }
        String[] fileNameInfo = file.getOriginalFilename().split("\\.",2);
        String[] uploadRes = fastDFS.upload(file.getBytes(),fileNameInfo[1]);
        Music music = fileInfoHandler.fileInfoToBean(file,uploadRes, Music.class);
        handlerNameInfo(fileNameInfo[0], music);
        music.setUserId(MessageHolder.getUserId());
        music = musicService.saveMusic(music);
        String userId = String.valueOf(MessageHolder.getUserId());
        String key = String.valueOf(music.getMusicId());
        asyncService.setDataToRedis(key,userId,music, MusicKey.music,MusicKey.musicList);
        return Result.success(music,CodeMsg.SUCCESS);
    }

    @GetMapping("/download/{musicId}")
    public ResponseEntity<byte[]> download(@PathVariable(value = "musicId") Integer musicId)
            throws MyException, IOException {
        Music music = musicService.getMusicByMusicId(musicId);
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
        Music music = musicService.getMusicByMusicId(musicId);
        return music == null ? Result.error(CodeMsg.FILE_NO_EXIST) : Result.success(music,CodeMsg.SUCCESS);
    }

    @RequestMapping("/user/get")
    public Result<List<Music>> getUserMusic(){
        List<Music> muscles = musicService.getUserMusic();
        return muscles == null ? Result.error(CodeMsg.FILE_NO_EXIST) : Result.success(muscles,CodeMsg.SUCCESS);
    }


    private boolean isMusic(String originName){
        if(originName == null){
            return false;
        }
        for (String support : SUPPORT_MUSIC) {
            if(originName.endsWith(support)){
                return true;
            }
        }
        return false;
    }

    private Music handlerNameInfo(String fileName,Music music){
        if(!fileName.contains(MUSIC_LOGO)){
            music.setSinger(UNNAMED_SINGER);
            music.setSongName(fileName);
            return music;
        }
        String[] detail = fileName.split("-", 2);
        music.setSinger(detail[0]);
        music.setSongName(detail[1]);
        return music;
    }

    @ExceptionHandler({
            NoSuchFileException.class
    })
    public Result handlerError(Exception exception, HttpServletRequest request){
        String ip = Security.getIPAddress(request);
        MessageHolder.clearData();
        logger.error("handler {} error. ip address is {}.\n" +
                "the reason is {}",request.getRequestURL(),ip,exception.getMessage());
        return Result.error(CodeMsg.ERROR.fillArgs(exception.getMessage()));
    }
}
