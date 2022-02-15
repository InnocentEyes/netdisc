package com.qzlnode.netdisc.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.qzlnode.netdisc.pojo.ChatMsg;
import com.qzlnode.netdisc.pojo.UserInfo;
import org.csource.common.MyException;

import java.io.IOException;

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

    /**
     *
     * @param img
     * @return
     */
    boolean initHeader(String[] img,Integer userId);

    /**
     *
     * @param userId
     * @return
     */
    boolean deleteHeader(Integer userId) throws MyException, IOException;

    /**
     *
     * @param userId
     * @return
     */
    boolean saveHeader(String[] imgMsg,Integer userId);


    void saveChatMsg(ChatMsg chatMsg);
}
