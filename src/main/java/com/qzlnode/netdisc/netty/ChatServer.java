package com.qzlnode.netdisc.netty;

import com.qzlnode.netdisc.util.Cache;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.ImmediateEventExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;

/**
 * 引导服务器
 * @author qzlzzz
 */
@Component
public class ChatServer {

    @Value("${netty.server.port}")
    private Integer serverPort;

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final ChannelGroup channelGroup = new DefaultChannelGroup(ImmediateEventExecutor.INSTANCE);

    private final EventLoopGroup group = new NioEventLoopGroup();

    private Channel channel;

    protected ChannelInitializer<Channel> createInitializer(ChannelGroup group){
        return new ChatServerInitializer(group);
    }

    public void destroy(){
        logger.info("chat server closing ...");
        if(channel != null){
            channel.close();
        }
        channelGroup.close();
        group.shutdownGracefully();
        logger.info("chat server closed");
    }

    public void start(InetSocketAddress address) throws InterruptedException {
        Channel channel = new ServerBootstrap()
                .group(group)
                .channel(NioServerSocketChannel.class)
                .childHandler(createInitializer(channelGroup))
                .bind(address)
                .syncUninterruptibly()
                .channel();
        this.channel = channel;
        Runtime.getRuntime().addShutdownHook(new Thread(this::destroy));
    }

    public Integer getServerPort(){
        return this.serverPort;
    }
}
