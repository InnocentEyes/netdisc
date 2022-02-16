package com.qzlnode.netdisc.netty;

import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Session {

    private static final Map<Integer, Channel> usernameChannelMap = new ConcurrentHashMap<>();
    private final Map<Channel, Integer> channelUsernameMap = new ConcurrentHashMap<>();
    private final ChannelGroup group;

    public Session(ChannelGroup group) {
        this.group = group;
    }


    public void bind(Channel channel, Integer userId) {
        usernameChannelMap.put(userId, channel);
        channelUsernameMap.put(channel, userId);
        group.add(channel);
    }

    public void unbind(Channel channel) {
        Integer userId = channelUsernameMap.remove(channel);
        usernameChannelMap.remove(userId);
        group.remove(channel);
    }

    public static Channel channelGet(Integer userId){
        return usernameChannelMap.get(userId);
    }

    public Channel getChannel(Integer userId) {
        return usernameChannelMap.get(userId);
    }


    @Override
    public String toString() {
        return usernameChannelMap.toString();
    }
}
