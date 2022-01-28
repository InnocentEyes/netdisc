package com.qzlnode.netdisc.redis;

public class DocumentKey extends BasePrefix{

    public DocumentKey(int expireSeconds, String prefix) {
        super(expireSeconds, prefix);
    }

    public static DocumentKey document = new DocumentKey(0,"document");

    public static DocumentKey documentList = new DocumentKey(0,"documentList");
}
