package com.qzlnode.netdisc.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qzlnode.netdisc.dao.UserDao;
import com.qzlnode.netdisc.exception.RegisterErrorException;
import com.qzlnode.netdisc.pojo.UserInfo;
import com.qzlnode.netdisc.redis.RedisForIndex;
import com.qzlnode.netdisc.service.IndexService;
import com.qzlnode.netdisc.util.BASE64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLSyntaxErrorException;
import java.util.List;

@Transactional(rollbackFor = {
        RuntimeException.class,
        NullPointerException.class,
        SQLSyntaxErrorException.class
})
@Service
public class IndexServiceImpl extends ServiceImpl<UserDao, UserInfo> implements IndexService {

    /**
     * 日志
     */
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private RedisForIndex redis;

    @Override
    public boolean registerService(UserInfo userInfo) {
        if(redis.hasUser(userInfo.getAccount())){
            return false;
        }
        int res = lambdaQuery()
                .eq(UserInfo::getAccount,userInfo.getAccount())
                .count();
        if(res != 0){
            redis.set(userInfo.getAccount(),Integer.MIN_VALUE);
            return false;
        }
        userInfo.setPassword(BASE64.encode(userInfo.getPassword()));
        boolean target = save(userInfo);
        if(!target){
            throw new RegisterErrorException("注册失败");
        }
        redis.set(userInfo.getAccount(),Integer.MAX_VALUE);
        return true;
    }

    @Override
    public UserInfo loginService(UserInfo userInfo) {
        Integer userId = redis.get(userInfo.getAccount());
        if(userId == -1){
            return null;
        }
        if(userId > 0){
            userInfo.setId(userId);
            return userInfo;
        }
        List<UserInfo> list = lambdaQuery()
                .eq(UserInfo::getAccount,userInfo.getAccount())
                .eq(element ->{
                    return BASE64.decode(element.getPassword());
                },userInfo.getPassword()).list();
        if(list == null || list.size() == 0) {
            redis.set(userInfo.getAccount(),-1);
            return null;
        }
        UserInfo res = list.stream()
                .filter(element -> element.getId() != null)
                .map(element -> {
                    userInfo.setName(element.getName());
                    userInfo.setId(element.getId());
                    return userInfo;
                }).findFirst().get();
        redis.set(res.getAccount(),res.getId());
        return res;
    }
}
