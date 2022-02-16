package com.qzlnode.netdisc.service.Impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qzlnode.netdisc.FastDFS;
import com.qzlnode.netdisc.dao.ChatMsgDao;
import com.qzlnode.netdisc.dao.FriendDao;
import com.qzlnode.netdisc.dao.FriendRequestDao;
import com.qzlnode.netdisc.dao.UserDao;
import com.qzlnode.netdisc.exception.UpdateCountException;
import com.qzlnode.netdisc.netty.DataContent;
import com.qzlnode.netdisc.netty.Session;
import com.qzlnode.netdisc.pojo.ChatMsg;
import com.qzlnode.netdisc.pojo.FriendRequest;
import com.qzlnode.netdisc.pojo.Friends;
import com.qzlnode.netdisc.pojo.UserInfo;
import com.qzlnode.netdisc.redis.RedisService;
import com.qzlnode.netdisc.redis.key.CountKey;
import com.qzlnode.netdisc.redis.key.ImgKey;
import com.qzlnode.netdisc.redis.key.UserKey;
import com.qzlnode.netdisc.result.CodeMsg;
import com.qzlnode.netdisc.service.PersonalService;
import com.qzlnode.netdisc.util.JsonUtil;
import com.qzlnode.netdisc.util.MessageHolder;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.csource.common.MyException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.sql.SQLSyntaxErrorException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author qzlzzz
 */
@Transactional(rollbackFor = {
        RuntimeException.class,
        NullPointerException.class,
        SQLSyntaxErrorException.class
})
@Service
public class PersonalServiceImpl extends ServiceImpl<UserDao, UserInfo> implements PersonalService {

    /**
     * 日志
     */
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private static final int MAX_UPDATE_COUNT = 3;

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
     */
    @Autowired
    private ChatMsgDao chatMsgDao;

    @Autowired
    private FriendRequestDao friendRequestDao;

    @Autowired
    private FriendDao friendDao;

    /**
     *
     */
    enum MessageSign {

        SIGNED("1", "形容发送消息已被查阅"),

        UNSIGNED("0", "形容发送消息未被查阅");

        private String value;

        private String description;

        private MessageSign(String value, String description) {
            this.value = value;
            this.description = description;
        }

        public String getValue() {
            return this.value;
        }
    }


    /**
     * @param userInfo
     * @return
     */
    @Override
    public boolean updateUserMsg(UserInfo userInfo) {
        String userId = String.valueOf(userInfo.getId());
        boolean hasUpdateCount = redisService.exists(CountKey.updateCount, userId);
        if (hasUpdateCount) {
            int updateCount = redisService.get(CountKey.updateCount, userId, int.class);
            if (updateCount >= MAX_UPDATE_COUNT) {
                throw new UpdateCountException("当日更新次数已达上限!");
            }
        }
        boolean isUpdate = userDao.update(userInfo, Wrappers.lambdaUpdate(UserInfo.class)
                .eq(UserInfo::getId, userInfo.getId())) == 1;
        if (!hasUpdateCount && isUpdate) {
            return redisService.set(CountKey.updateCount, userId, 0);
        }
        if (hasUpdateCount && isUpdate) {
            redisService.incr(CountKey.updateCount, userId);
            return true;
        }
        return false;
    }

    /**
     * @param imgMsg
     * @return
     */
    @Override
    public boolean initHeader(String[] imgMsg, Integer userId) {
        String id = String.valueOf(userId);
        if (redisService.exists(ImgKey.headerImg, id)) {
            return false;
        }
        String avatarUrl = imgMsg[0] + "/" + imgMsg[1];
        boolean isInit = lambdaUpdate().eq(UserInfo::getId, userId)
                .set(UserInfo::getAvatarUrl, avatarUrl)
                .update();
        if (isInit) {
            isInit = redisService.set(ImgKey.headerImg, id, avatarUrl);
        }
        return isInit;
    }

    /**
     * @param userId
     * @return
     */
    @Override
    public boolean deleteHeader(Integer userId) throws MyException, IOException {
        String avatarUrl = redisService.get(ImgKey.headerImg, String.valueOf(userId), String.class);
        String[] res = avatarUrl.trim().split("/", 2);
        fastDFS.delete(res[0], res[1]);
        return lambdaUpdate().eq(UserInfo::getId, userId)
                .set(UserInfo::getAvatarUrl, null)
                .update();
    }

    @Override
    public boolean saveHeader(String[] imgMsg, Integer userId) {
        String avatarUrl = imgMsg[0] + "/" + imgMsg[1];
        boolean isSave = userDao.update(null, Wrappers.lambdaUpdate(UserInfo.class)
                .eq(UserInfo::getId, userId)
                .set(UserInfo::getAvatarUrl, avatarUrl)) == 1;
        if (isSave) {
            isSave = redisService.set(ImgKey.headerImg, String.valueOf(userId), avatarUrl);
        }
        return isSave;
    }


    @Override
    public Integer saveChatMsg(ChatMsg chatMsg) {
        if (chatMsg.getSenderId() == null || chatMsg.getReceiveId() == null) {
            logger.error("senderId and receivedId must exist.");
            return -1;
        }
        if (chatMsg.getMessage() == null) {
            logger.error("send message must no be null.");
            return -1;
        }
        return chatMsgDao.insert(chatMsg) == 1 ? chatMsg.getMessageId() : -1;
    }

    @Override
    public List<ChatMsg> getUnsignedMsg() {
        Integer receiveId = MessageHolder.getUserId();
        List<ChatMsg> chatMsgs = chatMsgDao.selectList(
                Wrappers.lambdaQuery(ChatMsg.class)
                        .select(ChatMsg::getMessageId)
                        .select(ChatMsg::getSenderId)
                        .select(ChatMsg::getMessage)
                        .eq(ChatMsg::getReceiveId, receiveId)
                        .eq(ChatMsg::getSigned, MessageSign.UNSIGNED.getValue())
        );
        return chatMsgs;
    }

    @Override
    public CodeMsg sendFriendRequest(String account) {
        UserInfo receiveUser = redisService.get(UserKey.phone, account, UserInfo.class);
        if (receiveUser == null) {
            return CodeMsg.USER_NO_EXIST;
        }
        Friends friends = friendDao.selectOne(
                Wrappers.lambdaQuery(Friends.class)
                        .eq(Friends::getUserId, MessageHolder.getUserId())
                        .eq(Friends::getFriendId, receiveUser.getId())
        );
        if (friends != null) {
            return CodeMsg.ALREADY_EXISTS;
        }
        FriendRequest friendRequest = new FriendRequest();
        friendRequest.setSenderId(MessageHolder.getUserId());
        friendRequest.setReceiveId(receiveUser.getId());
        friendRequestDao.insert(friendRequest);
        return CodeMsg.SUCCESS;
    }

    @Override
    public List<FriendRequest> findFriendRequest() {
        return friendRequestDao.selectList(
                Wrappers.lambdaQuery(FriendRequest.class)
                        .eq(FriendRequest::getReceiveId, MessageHolder.getUserId())
        );
    }

    @Override
    public void saveFriend(Integer userId, Integer friendId) {
        Friends friend = new Friends(userId, friendId);
        friendDao.insert(friend);
    }

    @Override
    public List<UserInfo> findFriends() {
        List<Integer> friendIds = friendDao.selectList(
                Wrappers
                        .lambdaQuery(Friends.class)
                        .eq(Friends::getUserId, MessageHolder.getUserId()))
                .stream()
                .map(Friends::getFriendId)
                .collect(Collectors.toList());
        return userDao.selectBatchIds(friendIds);
    }

    @Override
    public void passFriendRequest(Integer senderId) {
        Integer userId = MessageHolder.getUserId();
        saveFriend(userId,senderId);
        saveFriend(senderId,userId);
        Channel toChannel = Session.channelGet(senderId);
        if(toChannel != null){
            DataContent dataContent = new DataContent();
            dataContent.setActionId(CodeMsg.ACCEPT.getCode());
            toChannel.writeAndFlush(new TextWebSocketFrame(
                    JsonUtil.objectToJson(dataContent)
            ));
        }
    }

    @Override
    public CodeMsg deleteFriendRequest(Integer requestId){
        int result = friendRequestDao.delete(
                Wrappers.lambdaQuery(FriendRequest.class)
                        .eq(FriendRequest::getRequestId, requestId)
        );
        return result == 1 ? CodeMsg.SUCCESS : CodeMsg.ERROR.fillArgs("删除失败，再试试.");
    }


}
