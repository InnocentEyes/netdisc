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



}
