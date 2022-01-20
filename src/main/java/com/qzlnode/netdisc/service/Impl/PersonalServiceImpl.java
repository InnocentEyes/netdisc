package com.qzlnode.netdisc.service.Impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qzlnode.netdisc.dao.UserDao;
import com.qzlnode.netdisc.exception.UpdateCountException;
import com.qzlnode.netdisc.fastdfs.FastDFS;
import com.qzlnode.netdisc.pojo.UserInfo;
import com.qzlnode.netdisc.redis.CountKey;
import com.qzlnode.netdisc.redis.ImgKey;
import com.qzlnode.netdisc.redis.RedisService;
import com.qzlnode.netdisc.service.PersonalService;
import org.csource.common.MyException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.sql.SQLSyntaxErrorException;

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
    private RedisService redisService;

    /**
     *
     */
    @Autowired
    private UserDao userDao;

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
        String userId = String.valueOf(userInfo.getId());
        boolean hasUpdateCount = redisService.exists(CountKey.updateCount,userId);
        if(hasUpdateCount){
            int updateCount = redisService.get(CountKey.updateCount,userId,int.class);
            if(updateCount >= 3){
                throw new UpdateCountException("当日更新次数已达上限!");
            }
        }
        boolean isUpdate = userDao.update(userInfo,Wrappers.lambdaUpdate(UserInfo.class)
                .eq(UserInfo::getId,userInfo.getId())) == 1;
        if(!hasUpdateCount && isUpdate){
            return redisService.set(CountKey.updateCount,userId,0);
        }
        if(hasUpdateCount && isUpdate){
            redisService.incr(CountKey.updateCount,userId);
            return true;
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
        String id = String.valueOf(userId);
        if(redisService.exists(ImgKey.headerImg,id)){
            return false;
        }
        String avatarUrl = imgMsg[0] + "/" + imgMsg[1];
        boolean isInit = lambdaUpdate().eq(UserInfo::getId,userId)
                .set(UserInfo::getAvatarUrl,avatarUrl)
                .update();
        if(isInit){
            isInit = redisService.set(ImgKey.headerImg,id,avatarUrl);
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
        String avatarUrl = redisService.get(ImgKey.headerImg,String.valueOf(userId),String.class);
        String[] res = avatarUrl.trim().split("/", 2);
        fastDFS.delete(res[0],res[1]);
        return lambdaUpdate().eq(UserInfo::getId,userId)
                                        .set(UserInfo::getAvatarUrl,null)
                                        .update();
    }

    @Override
    public boolean saveHeader(String[] imgMsg,Integer userId) {
        String avatarUrl = imgMsg[0] + "/" + imgMsg[1];
        boolean isSave = userDao.update(null, Wrappers.lambdaUpdate(UserInfo.class)
                .eq(UserInfo::getId,userId)
                .set(UserInfo::getAvatarUrl,avatarUrl)) == 1;
        if(isSave){
            isSave = redisService.set(ImgKey.headerImg,String.valueOf(userId),avatarUrl);
        }
        return isSave;
    }


}
