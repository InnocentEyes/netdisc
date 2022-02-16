package com.qzlnode.netdisc.redis.key;

/**
 * @author qzlzzz
 */
public class VideoCoverKey extends BasePrefix{

    /**
     * 防止外部实例化
     * @param expireSeconds
     * @param prefix
     */
    private VideoCoverKey(long expireSeconds, String prefix) {
        super(expireSeconds, prefix);
    }


    public static VideoCoverKey videoCoverList = new VideoCoverKey(0,"videoCoverList");

    public static VideoCoverKey videoCover = new VideoCoverKey(0,"videoCover");

}
