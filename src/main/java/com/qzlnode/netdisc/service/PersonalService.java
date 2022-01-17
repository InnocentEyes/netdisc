package com.qzlnode.netdisc.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.qzlnode.netdisc.pojo.UserInfo;

/**
 * @author qzlzzz
 */
public interface PersonalService extends IService<UserInfo> {

    /**
     *
     * @param userInfo
     * @return
     */
    boolean updateUserMsg(UserInfo userInfo);


}
