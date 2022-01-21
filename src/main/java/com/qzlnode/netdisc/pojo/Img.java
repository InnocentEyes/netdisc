package com.qzlnode.netdisc.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;

/**
 * @author qzlzzz
 */
@TableName("img")
public class Img{

    public interface ImgView{}

    @JsonView(ImgView.class)
    @TableId(type = IdType.AUTO)
    private Integer imgId;

    @JsonIgnore
    private Integer userId;

    private long imgSize;

    private String imgType;

    @JsonView(ImgView.class)
    private String groupName;

    @JsonView(ImgView.class)
    @TableField("img_remote_path")
    private String imgRemotePath;

    public Integer getImgId() {
        return imgId;
    }

    public void setImgId(Integer imgId) {
        this.imgId = imgId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public long getImgSize() {
        return imgSize;
    }

    public void setImgSize(long imgSize) {
        this.imgSize = imgSize;
    }

    public String getImgType() {
        return imgType;
    }

    public void setImgType(String imgType) {
        this.imgType = imgType;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getImgRemotePath() {
        return imgRemotePath;
    }

    public void setImgRemotePath(String imgRemotePath) {
        this.imgRemotePath = imgRemotePath;
    }

}
