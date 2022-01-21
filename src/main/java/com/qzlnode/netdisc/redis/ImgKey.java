package com.qzlnode.netdisc.redis;

/**
 * @author qzlzzz
 */
public class ImgKey extends BasePrefix{

    /**
     * 防止外部修改
     * @param expireSeconds
     * @param prefix
     */
    private ImgKey(int expireSeconds, String prefix) {
        super(expireSeconds, prefix);
    }

    public static ImgKey headerImg = new ImgKey(0,"headerImg");

    public static ImgKey img = new ImgKey(0,"img");

    public static ImgKey imgList = new ImgKey(0,"imgList");
}
