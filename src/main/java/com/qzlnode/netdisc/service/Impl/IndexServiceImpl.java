package com.qzlnode.netdisc.service.Impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qzlnode.netdisc.dao.UserDao;
import com.qzlnode.netdisc.exception.HasPhoneException;
import com.qzlnode.netdisc.exception.RegisterErrorException;
import com.qzlnode.netdisc.pojo.UserInfo;
import com.qzlnode.netdisc.redis.RedisService;
import com.qzlnode.netdisc.redis.key.UserKey;
import com.qzlnode.netdisc.service.IndexService;
import com.qzlnode.netdisc.util.Md5;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLSyntaxErrorException;
import java.util.Objects;

/**
 * @author qzlzzz
 */
@Transactional(rollbackFor = {
        RuntimeException.class,
        NullPointerException.class,
        SQLSyntaxErrorException.class,
        RegisterErrorException.class
})
@Service
public class IndexServiceImpl extends ServiceImpl<UserDao, UserInfo> implements IndexService {

    /**
     * 日志
     */
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private RedisService redisService;

    @Autowired
    private UserDao userDao;

    @Override
    public boolean registerService(UserInfo userInfo) {
        if (redisService.exists(UserKey.phone, userInfo.getAccount())) {
            throw new HasPhoneException("电话号码已存在");
        }
        userInfo.setPassword(Md5.encode(userInfo.getPassword()));
        int target = userDao.insert(userInfo);
        if (target != 1) {
            throw new RegisterErrorException("注册失败");
        }
        if (redisService.set(UserKey.phone, userInfo.getAccount(), userInfo)) {
            return true;
        }
        return false;
    }

    @Override
    public UserInfo loginService(UserInfo userInfo) {
        UserInfo res = redisService.get(UserKey.phone, userInfo.getAccount(), UserInfo.class);
        if (res != null) {
            if (Objects.equals(res.getPassword(), Md5.encode(userInfo.getPassword()))) {
                return res;
            }
            return null;
        }
        String password = Md5.encode(userInfo.getPassword());
        res = userDao.selectOne(
                Wrappers.lambdaQuery(UserInfo.class)
                        .eq(UserInfo::getAccount, userInfo.getAccount())
                        .eq(UserInfo::getPassword, password)
        );
        if (res != null) {
            res.setPassword(password);
            redisService.set(UserKey.phone, userInfo.getAccount(), res);
            return res;
        }
        return null;
    }
}
