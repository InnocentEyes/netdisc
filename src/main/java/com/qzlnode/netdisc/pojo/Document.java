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
@NoArgsConstructor
@AllArgsConstructor
@TableName("file")
public class Document {

    @TableId(value = "file_id",type = IdType.AUTO)
    private Integer fileId;

    @TableId(value = "user_id",type = IdType.NONE)
    @JsonIgnore
    private Integer userId;

    @TableField(value = "file_size")
    private long fileSize;

    @TableField(value = "file_type")
    private String fileType;

    @TableField(value = "group_name")
    private String groupName;

    @TableField(value = "file_remote_path")
    private String fileRemotePath;
}
