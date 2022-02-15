package com.qzlnode.netdisc.netty;


import com.qzlnode.netdisc.pojo.ChatMsg;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DataContent {

    //请求的动作
    private Integer actionId;

    //信息主体
    private ChatMsg chatMsg;

}
