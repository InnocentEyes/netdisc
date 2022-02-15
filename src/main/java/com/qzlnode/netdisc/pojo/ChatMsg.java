package com.qzlnode.netdisc.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "chat_msg")
public class ChatMsg{

    @TableId(value = "sender_id",type = IdType.NONE)
    //发送者Id
    private Integer senderId;

    @TableId(value = "receive_id",type = IdType.NONE)
    //接收者Id
    private Integer receiveId;

    //发送的信息
    private String message;

    @TableId(value = "message_id",type = IdType.AUTO)
    private Integer messageId;

    //是否阅读
    private String signed;
}
