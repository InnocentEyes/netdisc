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
@TableName("friend")
public class Friends {

    @TableId(value = "user_id",type = IdType.NONE)
    private Integer userId;

    @TableId(value = "friend_id")
    private Integer friendId;
}
