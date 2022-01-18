package com.qzlnode.netdisc.service.Impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qzlnode.netdisc.dao.UserDao;
import com.qzlnode.netdisc.exception.UpdateCountException;
import com.qzlnode.netdisc.fastdfs.FastDFS;
import com.qzlnode.netdisc.pojo.UserInfo;
import com.qzlnode.netdisc.redis.RedisForPersonal;
import com.qzlnode.netdisc.service.PersonalService;
import com.qzlnode.netdisc.util.BASE64;
import com.qzlnode.netdisc.util.MessageHolder;
import org.csource.common.MyException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.sql.SQLSyntaxErrorException;
import java.util.Arrays;
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

    /**
     *
     */
    @Autowired
    private RedisForPersonal redis;

    /**
     *
     */
    @Autowired
    private FastDFS fastDFS;

    /**
     *
     * @param userInfo
     * @return
     */
    @Override
    public boolean updateUserMsg(UserInfo userInfo) {
        boolean hasUser = redis.hasUpdateCount(userInfo.getId());
        if(hasUser) {
            if (!redis.checkUpdateCount(userInfo.getId())) {
                throw new UpdateCountException("更新次数已达上限");
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

    /**
     *
     * @param imgMsg
     * @return
     */
    @Override
    public boolean initHeader(String[] imgMsg,Integer userId) {
        if(!redis.isInit(userId)){
            return false;
        }
        String avatarUrl = imgMsg[0] + "/" + imgMsg[1];
        boolean isInit = lambdaUpdate().eq(UserInfo::getId,userId)
                .set(UserInfo::getAvatarUrl,avatarUrl)
                .update();
        if(isInit){
            isInit = redis.init(userId);
        }
        return isInit;
    }

    /**
     *
     * @param userId
     * @return
     */
    @Override
    public boolean deleteHeader(Integer userId) throws MyException, IOException {
        String avatarUrl = lambdaQuery().select(UserInfo::getAvatarUrl)
                                        .eq(UserInfo::getId,userId)
                                        .getEntity()
                                        .getAvatarUrl();
        String[] res = avatarUrl.trim().split("/", 2);
        fastDFS.delete(res[0],res[1]);
        return lambdaUpdate().eq(UserInfo::getId,userId)
                                        .set(UserInfo::getAvatarUrl,null)
                                        .update();
    }

    @Override
    public boolean saveHeader(String[] imgMsg,Integer userId) {
        String avatarUrl = imgMsg[0] + "/" + imgMsg[1];
        return lambdaUpdate().eq(UserInfo::getId,userId)
                .set(UserInfo::getAvatarUrl,avatarUrl)
                .update();
    }


}
