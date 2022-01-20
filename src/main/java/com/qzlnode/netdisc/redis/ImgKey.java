package com.qzlnode.netdisc.redis;

/**
 * @author qzlzzz
 */
public class ImgKey extends BasePrefix{

    public ImgKey(int expireSeconds, String prefix) {
        super(expireSeconds, prefix);
    }

    public static ImgKey headerImg = new ImgKey(0,"headerImg");

    public static ImgKey img = new ImgKey(0,"img");
}
