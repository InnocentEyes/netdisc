package com.qzlnode.netdisc;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.qzlnode.netdisc.pojo.Img;
import com.qzlnode.netdisc.pojo.UserInfo;
import com.qzlnode.netdisc.pojo.VideoCover;
import com.qzlnode.netdisc.redis.VideoCoverKey;
import com.qzlnode.netdisc.result.CodeMsg;
import com.qzlnode.netdisc.result.Result;
import com.qzlnode.netdisc.util.FileInfoHandler;
import org.junit.jupiter.api.Test;
import org.springframework.cglib.core.ReflectUtils;
import org.springframework.util.ConcurrentReferenceHashMap;
import org.springframework.web.multipart.MultipartFile;

import java.beans.PropertyDescriptor;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

//@SpringBootTest
class NetdiscApplicationTests {

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

    @Test
    void contextLoads() {
        UserInfo userInfo = null;
        userInfo = Optional.ofNullable(userInfo)
                .filter(element -> element.getAccount() != null)
                .orElse(null);
        System.out.println(userInfo);
    }

    @Test
    void testSplit() throws JsonProcessingException {
        Img img = new Img();
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        System.out.println(mapper.writeValueAsString(img));
    }


    @Test
    void testJson() throws JsonProcessingException {
        Img img = new Img();
        img.setGroupName("asd");
        img.setImgRemotePath("....");
        img.setImgId(1);
        Result<Img> result = new Result<>();
        result.setData(img);
        result.setCode(CodeMsg.SUCCESS.getCode());
        result.setMsg(CodeMsg.UPDATE_ERROR.getMsg());
        ObjectMapper mapper = new ObjectMapper();
        System.out.println(mapper.writeValueAsString(result));
    }

    @Test
    void test01() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        System.out.println(mapper.writeValueAsString(Result.error(CodeMsg.ERROR.fillArgs("exception"))));
    }

    @Test
    void test02() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        VideoCover cover = new VideoCover();
        cover.setVideoCoverId(1);
        Integer coverId = (Integer)cover.getClass().getMethod("getVideoCoverId").invoke(cover);
        System.out.println(coverId);
    }

    @Test
    void test03(){
        String originName = "dsajdawd.pls";
        String[] split = originName.split("\\.", 2);
        for (String s : split) {
            System.out.println(s);
        }
    }

    @Test
    void test04(){
        System.out.println(VideoCoverKey.videoCoverList.getPrefix());
    }

    private boolean isMusic(String originName){
        for (String support : SUPPORT_MUSIC) {
            if(originName.endsWith(support)){
                return true;
            }
        }
        return false;
    }

}
