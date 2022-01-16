package com.qzlnode.netdisc.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qzlnode.netdisc.pojo.UserInfo;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserDao extends BaseMapper<UserInfo> {
}
