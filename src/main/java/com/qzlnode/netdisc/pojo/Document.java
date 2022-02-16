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
@NoArgsConstructor
@AllArgsConstructor
@TableName("document")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Document {

    @TableId(value = "document_id",type = IdType.AUTO)
    private Integer fileId;

    @TableField(value = "user_id")
    @JsonIgnore
    private Integer userId;

    @TableField("document_origin_name")
    private String fileOriginName;

    @TableField(value = "document_size")
    private long fileSize;

    @TableField(value = "document_type")
    private String fileType;

    @TableField(value = "group_name")
    private String groupName;

    @TableField(value = "document_remote_path")
    private String fileRemotePath;
}
