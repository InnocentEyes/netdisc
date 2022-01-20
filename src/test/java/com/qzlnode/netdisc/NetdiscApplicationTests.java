package com.qzlnode.netdisc;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.qzlnode.netdisc.pojo.Img;
import com.qzlnode.netdisc.pojo.UserInfo;
import com.qzlnode.netdisc.result.CodeMsg;
import com.qzlnode.netdisc.result.Result;
import org.junit.jupiter.api.Test;

import java.util.Optional;

//@SpringBootTest
class NetdiscApplicationTests {

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

}
