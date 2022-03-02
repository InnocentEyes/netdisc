package com.qzlnode.netdisc;

import com.qzlnode.netdisc.pojo.VideoCover;
import com.qzlnode.netdisc.util.JsonUtil;
import com.qzlnode.netdisc.util.Md5;
import com.qzlnode.netdisc.util.VideoUtil;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.List;

public class VideoUtilTest {


    @Test
    void testVideo() throws IOException {
//        String path = "xxxxxxx";
//        File file = new File(path);
//        FileInputStream fileInputStream = new FileInputStream(file);
//        BufferedInputStream buffer = new BufferedInputStream(fileInputStream);
//        VideoUtil videoUtil = new VideoUtil();
//        byte[] bytes = videoUtil.fetchFrame(buffer);
//        System.out.println(bytes);
    }

    @Test
    void testJsonUtil(){
//        String json = "[{xxxxxxx}]";
//        List<VideoCover> covers = JsonUtil.jsonToList(json, VideoCover.class);
//        System.out.println(covers);
    }

    @Test
    void testSubString(){
//        String res = "头像.png";
//        String substring = res.substring(res.lastIndexOf(".") + 1);
//        System.out.println(substring);
    }

    @Test
    void testEncode(){
        System.out.println(Md5.encode("qzl200919yya"));
    }

}
