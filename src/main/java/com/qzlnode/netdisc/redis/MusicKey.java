package com.qzlnode.netdisc.redis;

public class MusicKey extends BasePrefix{

    /**
     * 防止外部实例化
     * @param expireSeconds
     * @param prefix
     */
    private MusicKey(int expireSeconds, String prefix) {
        super(expireSeconds, prefix);
    }

    public static MusicKey music = new MusicKey(0,"music");

    public static MusicKey musicList = new MusicKey(0,"musicList");
}
