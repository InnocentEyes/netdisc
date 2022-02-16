package com.qzlnode.netdisc.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author qzlzzz
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("video_cover")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class VideoCover {

    @TableId(value = "video_cover_id",type = IdType.AUTO)
    private Integer videoCoverId;

    @TableField(exist = false)
    private Video video;

    @JsonIgnore
    @TableField(value = "user_id")
    private Integer userId;

    @TableField("video_origin_name")
    private String videoOriginName;

    @TableField("video_cover_size")
    private long videoCoverSize;

    @TableField("video_cover_type")
    private String videoCoverType;

    @TableField("group_name")
    private String groupName;

    @TableField("video_cover_remote_path")
    private String videoCoverRemotePath;
}
