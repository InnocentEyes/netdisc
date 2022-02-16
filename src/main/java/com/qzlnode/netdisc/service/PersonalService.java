package com.qzlnode.netdisc.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.qzlnode.netdisc.pojo.ChatMsg;
import com.qzlnode.netdisc.pojo.FriendRequest;
import com.qzlnode.netdisc.pojo.Friends;
import com.qzlnode.netdisc.pojo.UserInfo;
import com.qzlnode.netdisc.result.CodeMsg;
import org.csource.common.MyException;

import java.io.IOException;
import java.util.List;

/**
 * @author qzlzzz
 */
public interface PersonalService extends IService<UserInfo> {

    /**
     * @param userInfo
     * @return
     */
    boolean updateUserMsg(UserInfo userInfo);

    /**
     * @param img
     * @return
     */
    boolean initHeader(String[] img, Integer userId);

    /**
     * @param userId
     * @return
     */
    boolean deleteHeader(Integer userId) throws MyException, IOException;

    /**
     * @param userId
     * @return
     */
    boolean saveHeader(String[] imgMsg, Integer userId);

    /**
     * @param chatMsg
     * @return
     */
    Integer saveChatMsg(ChatMsg chatMsg);

    /**
     * @return
     */
    List<ChatMsg> getUnsignedMsg();


    /**
     *
     * @return
     */
    CodeMsg sendFriendRequest(String account);

    /**
     *
     * @return
     */
    List<FriendRequest> findFriendRequest();

    /**
     *
     * @param userId
     * @param friendId
     */
    void saveFriend(Integer userId,Integer friendId);

    /**
     *
     * @return
     */
    List<UserInfo> findFriends();

    /**
     *
     * @param senderId
     * @return
     */
    void passFriendRequest(Integer senderId);

    /**
     *
     * @param requestId
     * @return
     */
    CodeMsg deleteFriendRequest(Integer requestId);
}
