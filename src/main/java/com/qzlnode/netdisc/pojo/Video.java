package com.qzlnode.netdisc.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author qzlzzz
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Video {

    @TableId(value = "video_id",type = IdType.AUTO)
    private Integer videoId;

    @TableField(value = "video_cover_id")
    private Integer videoCoverId;

    @TableField("video_size")
    private long videoSize;

    @TableField("video_type")
    private String videoType;

    @TableField("group_name")
    private String groupName;

    @TableField("video_remote_path")
    private String videoRemotePath;
}
