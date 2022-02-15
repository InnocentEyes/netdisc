package com.qzlnode.netdisc.netty;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.qzlnode.netdisc.util.Cache;
import com.qzlnode.netdisc.util.SpringUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketClientProtocolHandler;
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

    /**
     * 序列化
     */
    private final ObjectMapper mapper = new ObjectMapper();

    private final ChannelGroup group;

    public ChatHandler(ChannelGroup group){
        this.group = group;
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {


    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if(evt == WebSocketClientProtocolHandler.ClientHandshakeStateEvent.HANDSHAKE_COMPLETE){
            group.add(ctx.channel());
        }else {
            super.userEventTriggered(ctx,evt);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        group.remove(ctx.channel());
        logger.error("unExcept exception: ",cause);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
        Channel channel = ctx.channel();
        String text = msg.text();
        DataContent dataContent = mapper.readValue(text, DataContent.class);
        Integer actionId = dataContent.getActionId();
        if(Action.CONNECT.ordinal() == actionId){
            Cache.putChannel(dataContent.getChatMsg().getSenderId(),channel);
        }
        else if(Action.CHAT.ordinal() == actionId){

        }
    }

    enum Action{

        CONNECT,

        CHAT;

    }
}
