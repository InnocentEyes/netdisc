package com.qzlnode.netdisc.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
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
@TableName("music")
public class Music {

    @TableId(value = "music_id",type = IdType.AUTO)
    private Integer musicId;

    @TableField(value = "user_id")
    @JsonIgnore
    private Integer userId;

    @TableField(value = "singer")
    private String singer;

    @TableField(value = "song_name")
    private String songName;

    @TableField(value = "music_origin_name")
    private String musicOriginName;

    @TableField(value = "music_type")
    private String musicType;

    @TableField(value = "music_size")
    private long musicSize;

    @TableField(value = "group_name")
    private String groupName;

    @TableField(value = "music_remote_path")
    private String musicRemotePath;

    @TableField("create_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;
}
