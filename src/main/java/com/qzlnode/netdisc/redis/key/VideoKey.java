package com.qzlnode.netdisc.redis.key;

/**
 * @author qzlzzz
 */
public class VideoKey extends BasePrefix{

    private VideoKey(long expireSeconds, String prefix) {
        super(expireSeconds, prefix);
    }

    /**
     * 视频的key前缀
     */
    public static VideoKey video = new VideoKey(0,"video");
}
