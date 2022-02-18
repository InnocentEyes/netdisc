package com.qzlnode.netdisc;

import com.qzlnode.netdisc.pojo.VideoCover;
import com.qzlnode.netdisc.util.JsonUtil;
import com.qzlnode.netdisc.util.VideoUtil;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.*;
import java.util.List;

public class VideoUtilTest {


    @Test
    void testVideo() throws IOException {
        String path = "C:\\Users\\邱泽林\\Desktop\\gcw\\第一条视频.mp4";
        File file = new File(path);
        FileInputStream fileInputStream = new FileInputStream(file);
        BufferedInputStream buffer = new BufferedInputStream(fileInputStream);
        VideoUtil videoUtil = new VideoUtil();
        byte[] bytes = videoUtil.fetchFrame(buffer);
        System.out.println(bytes);
    }

    @Test
    void testJsonUtil(){
        String json = "[{\"videoId\":2,\"video\":{\"videoSize\":1267244,\"videoType\":\"video/mp4\",\"groupName\":\"group1\",\"videoRemotePath\":\"M00/00/00/wKh3gmIOOleAVKjAABNWLGsKkAk973.mp4\"},\"videoOriginName\":\"第一条视频.mp4\",\"videoCoverType\":\"png\",\"videoCoverRemotePath\":\"M00/00/00/wKh3gmIOOleAeXQeAAp8-YxhLXI625.png\"}]";
        List<VideoCover> covers = JsonUtil.jsonToList(json, VideoCover.class);
        System.out.println(covers);
    }

    @Test
    void testSubString(){
        String res = "头像.png";
        String substring = res.substring(res.lastIndexOf(".") + 1);
        System.out.println(substring);
    }

}
