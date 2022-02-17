package com.qzlnode.netdisc.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author qzlzzz
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("video_cover")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class VideoCover {

    @TableId(value = "video_id",type = IdType.AUTO)
    private Integer videoId;

    @TableField(exist = false)
    private Video video;

    @JsonIgnore
    @TableField(value = "user_id")
    private Integer userId;

    @TableField("video_name")
    private String videoOriginName;


    @TableField("video_cover_type")
    private String videoCoverType;

    @TableField("group_name")
    private String groupName;

    @TableField("video_cover_remote_path")
    private String videoCoverRemotePath;

    @TableField(value = "create_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;
}
