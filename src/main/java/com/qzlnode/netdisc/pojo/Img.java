package com.qzlnode.netdisc.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
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
@TableName("img")
public class Img{

    public interface ImgView{}

    @JsonView(ImgView.class)
    @TableId(type = IdType.AUTO)
    private Integer imgId;

    private Integer userId;

    @TableField("img_origin_name")
    private String imgOriginName;

    private long imgSize;

    private String imgType;

    @JsonView(ImgView.class)
    private String groupName;

    @JsonView(ImgView.class)
    @TableField("img_remote_path")
    private String imgRemotePath;

    @TableField(value = "create_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;


}
