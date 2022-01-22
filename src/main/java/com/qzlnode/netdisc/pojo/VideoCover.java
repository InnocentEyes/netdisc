package com.qzlnode.netdisc.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
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
public class VideoCover {

    @TableId(value = "video_cover_id",type = IdType.AUTO)
    private Integer videoCoverId;

    @JsonIgnore
    @TableField("userId")
    private Integer userId;

    @TableField("video_cover_size")
    private long videoCoverSize;

    @TableField("video_cover_type")
    private String videoCoverType;

    @TableField("group_name")
    private String groupName;

    @TableField("video_cover_remote_path")
    private String videoCoverRemotePath;
}
