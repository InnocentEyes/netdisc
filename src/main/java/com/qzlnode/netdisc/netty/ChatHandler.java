package com.qzlnode.netdisc.netty;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.qzlnode.netdisc.dao.ChatMsgDao;
import com.qzlnode.netdisc.pojo.ChatMsg;
import com.qzlnode.netdisc.service.PersonalService;
import com.qzlnode.netdisc.util.JsonUtil;
import com.qzlnode.netdisc.SpringUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 文本数据帧处理器
 * @author qzlzzz
 */
@ChannelHandler.Sharable
public class ChatHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    /**
     * 日志
     */
    private final Logger logger = LoggerFactory.getLogger(ChatHandler.class);


    private final Session session;

    public ChatHandler(ChannelGroup group){
        this.session = new Session(group);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        session.unbind(ctx.channel());
        logger.info("channel 移除: {}",ctx.channel().id());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        session.unbind(ctx.channel());
        logger.info("channel移除: {}",ctx.channel().id());
        logger.error("unExcept exception: ",cause);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
        Channel channel = ctx.channel();
        String text = msg.text();
        DataContent dataContent = JsonUtil.jsonToObject(text, DataContent.class);
        Integer actionId = dataContent.getActionId();
        if(Action.CONNECT.ordinal() == actionId){
            session.bind(channel,dataContent.getChatMsg().getSenderId());
        }
        else if(Action.CHAT.ordinal() == actionId){
            PersonalService service = SpringUtil.getBean(PersonalService.class);
            Integer messageId = service.saveChatMsg(dataContent.getChatMsg());
            if(messageId == -1){
                logger.error("record message error");
                return;
            }
            dataContent.getChatMsg().setMessageId(messageId);
            Integer receiveId = dataContent.getChatMsg().getReceiveId();
            Channel toChannel = session.getChannel(receiveId);
            if(toChannel == null){

            }else{
                toChannel.writeAndFlush(new TextWebSocketFrame(JsonUtil.objectToJson(dataContent)));
            }
        }else if(Action.CLEAR.ordinal() == actionId){
            ChatMsgDao service = SpringUtil.getBean(ChatMsgDao.class);
            service.update(
                    null,
                    Wrappers.lambdaUpdate(ChatMsg.class)
                    .set(ChatMsg::getSigned,"1")
                    .eq(ChatMsg::getSenderId,dataContent.getClearMsg().getCurrentPageUserId())
                    .eq(ChatMsg::getReceiveId,dataContent.getClearMsg().getCurrentUserId())
            );
        }
    }

    enum Action{

        CONNECT,

        CHAT,

        CLEAR

    }
}
