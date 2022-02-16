package com.qzlnode.netdisc;

import com.qzlnode.netdisc.netty.ChatServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;

@Component
public class ChatServerStarter implements ApplicationListener<ContextRefreshedEvent> {

    @Autowired
    private ChatServer chatServer;

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if (event.getApplicationContext().getParent() == null){
            InetSocketAddress address = new InetSocketAddress(chatServer.getServerPort());
            try {
                logger.info("netty starting....");
                chatServer.start(address);
            } catch (InterruptedException e) {
                chatServer.destroy();
                logger.error("netty start error",e);
            }
        }
    }
}
