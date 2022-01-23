package com.qzlnode.netdisc.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qzlnode.netdisc.pojo.Video;
import com.qzlnode.netdisc.pojo.VideoCover;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @author qzlzzz
 */
@Mapper
public interface VideoDao extends BaseMapper<Video> {

    /**
     *
     * @param coverId
     * @return
     */
    VideoCover queryCoverAndVideo(@Param("coverId") Integer coverId,@Param("userId") Integer userId);
}
