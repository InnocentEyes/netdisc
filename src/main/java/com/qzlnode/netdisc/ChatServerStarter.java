package com.qzlnode.netdisc;

import com.qzlnode.netdisc.netty.ChatServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;

@Component
public class ChatServerStarter implements ApplicationListener<ContextRefreshedEvent> {

    @Autowired
    private ChatServer chatServer;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if (event.getApplicationContext().getParent() == null){
            InetSocketAddress address = new InetSocketAddress(chatServer.getServerPort());
            try {
                chatServer.start(address);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
