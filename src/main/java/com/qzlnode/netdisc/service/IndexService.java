package com.qzlnode.netdisc.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.qzlnode.netdisc.pojo.UserInfo;

public interface IndexService extends IService<UserInfo> {

    /**
     *
     * @param userInfo
     * @return
     */
    boolean registerService(UserInfo userInfo);

    /**
     *
     * @param userInfo
     * @return
     */
    UserInfo loginService(UserInfo userInfo);
}
