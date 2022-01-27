package com.qzlnode.netdisc.redis;

/**
 * @author qzlzzz
 */
public class VideoCoverKey extends BasePrefix{

    /**
     * 防止外部实例化
     * @param expireSeconds
     * @param prefix
     */
    private VideoCoverKey(int expireSeconds, String prefix) {
        super(expireSeconds, prefix);
    }


    public static VideoCoverKey videoCoverList = new VideoCoverKey(0,"videoCoverList");

    public static VideoCoverKey videoCover = new VideoCoverKey(0,"videoCover");

}
