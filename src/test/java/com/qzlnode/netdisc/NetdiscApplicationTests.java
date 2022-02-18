package com.qzlnode.netdisc;

import com.qzlnode.netdisc.dao.VideoDao;
import com.qzlnode.netdisc.pojo.UserInfo;
import com.qzlnode.netdisc.pojo.VideoCover;
import com.qzlnode.netdisc.service.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class NetdiscApplicationTests {

    @Autowired
    private IndexService indexService;

    @Autowired
    private VideoDao videoDao;

//    @Autowired
//    private ImgService imgService;
//
//    @Autowired
//    private PersonalService personalService;
//
//    @Autowired
//    private MusicService musicService;
//
//    @Autowired
//    private VideoService videoService;
//
//    @Autowired
//    private AsyncService asyncService;

    @DisplayName("测试Index接口的服务")
    @Test
    void testIndex(){
        UserInfo userInfo = new UserInfo();
        userInfo.setAccount("15322255046");
        userInfo.setPassword("qzl200919yya");
        userInfo.setName("qzlzzz");
        userInfo.setRealName("邱泽林");
        Assertions.assertTrue(indexService.registerService(userInfo));
    }

    @DisplayName("测试登录业务接口")
    @Test
    void testLogin(){
        long startTime = System.currentTimeMillis();
        UserInfo userInfo = new UserInfo();
        userInfo.setAccount("15322255046");
        userInfo.setPassword("qzl200919yya");
        Assertions.assertTrue(indexService.loginService(userInfo) != null);
        System.out.println((System.currentTimeMillis() - startTime) + "ms");
    }

    @Test
    void testVideoDao(){
        VideoCover videoCover = videoDao.queryCoverAndVideo(2, 1);
        System.out.println(videoCover);
    }


}
