package com.qzlnode.netdisc.netty;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.group.ChannelGroup;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.timeout.IdleStateHandler;

/**
 * @author qzlzzz
 */
public class ChatServerInitializer extends ChannelInitializer<Channel> {

    private final ChatHandler chatHandler;

    public ChatServerInitializer(ChannelGroup group){
        this.chatHandler = new ChatHandler(group);
    }


    @Override
    protected void initChannel(Channel channel) throws Exception {
        ChannelPipeline pipeline = channel.pipeline();
        pipeline.addLast(new HttpServerCodec());
        pipeline.addLast(new ChunkedWriteHandler());
        pipeline.addLast(new HttpObjectAggregator(6 * 1024));
        pipeline.addLast(new IdleStateHandler(8,10,12));
        pipeline.addLast(new WebSocketServerProtocolHandler("/ws"));
        pipeline.addLast(chatHandler);
    }
}
