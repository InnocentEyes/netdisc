package com.qzlnode.netdisc.service.Impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qzlnode.netdisc.dao.UserDao;
import com.qzlnode.netdisc.fastdfs.FastDFS;
import com.qzlnode.netdisc.pojo.UserInfo;
import com.qzlnode.netdisc.redis.RedisForPersonal;
import com.qzlnode.netdisc.service.PersonalService;
import com.qzlnode.netdisc.util.BASE64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLSyntaxErrorException;
import java.util.Optional;

/**
 * @author qzlzzz
 */
@Transactional(rollbackFor = {
        RuntimeException.class,
        NullPointerException.class,
        SQLSyntaxErrorException.class
})
@Service
public class PersonalServiceImpl extends ServiceImpl<UserDao,UserInfo> implements PersonalService {

    /**
     * 日志
     */
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private RedisForPersonal redis;


    @Override
    public boolean updateUserMsg(UserInfo userInfo) {
        boolean hasUser = redis.hasUpdateCount(userInfo.getId());
        if(hasUser) {
            if (!redis.checkUpdateCount(userInfo.getId())) {
                return false;
            }
        }
        boolean isUpdate;
        if(userInfo.getPassword() == null) {
            isUpdate = lambdaUpdate().eq(UserInfo::getId, userInfo.getId())
                    .set(UserInfo::getName, userInfo.getName())
                    .update();
        }else {
            isUpdate = lambdaUpdate().eq(UserInfo::getId, userInfo.getId())
                    .set(UserInfo::getName, userInfo.getName())
                    .set(UserInfo::getPassword, BASE64.encode(userInfo.getPassword()))
                    .update();
        }
        if(!hasUser){
            hasUser = redis.initUpdateCount(userInfo.getId());
        }
        if(isUpdate && hasUser){
            return redis.updateCount(userInfo.getId());
        }
        return false;
    }


}
