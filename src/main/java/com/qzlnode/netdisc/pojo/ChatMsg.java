package com.qzlnode.netdisc.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "chat_msg")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ChatMsg{

    @TableField(value = "sender_id")
    //发送者Id
    private Integer senderId;

    @TableField(value = "receive_id")
    //接收者Id
    private Integer receiveId;

    //发送的信息
    private String message;

    @TableId(value = "message_id",type = IdType.AUTO)
    private Integer messageId;

    //是否阅读
    private String signed;
}
