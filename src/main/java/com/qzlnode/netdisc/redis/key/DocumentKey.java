package com.qzlnode.netdisc.redis.key;

public class DocumentKey extends BasePrefix{

    public DocumentKey(long expireSeconds, String prefix) {
        super(expireSeconds, prefix);
    }

    public static DocumentKey document = new DocumentKey(0,"document");

    public static DocumentKey documentList = new DocumentKey(0,"documentList");
}
