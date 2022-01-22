package com.qzlnode.netdisc.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.Version;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@TableName(value = "user_info",resultMap = "user",autoResultMap = true)
public class UserInfo {

    @JsonProperty("userid")
    @TableId(type = IdType.AUTO)
    private Integer id;

    private String avatarUrl;

    @JsonProperty("nickname")
    private String name;

    @JsonProperty("username")
    private String realName;

    private String account;

    @JsonIgnore
    private String password;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createAt;

    /**
     *  使用Mybatis-Plus提供的乐观锁
     */
    @Version
    private Integer Version;

}
