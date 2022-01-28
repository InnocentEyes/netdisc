package com.qzlnode.netdisc.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.qzlnode.netdisc.pojo.Document;

/**
 * @author qzlzzz
 */
public interface DocumentService extends IService<Document> {

    /**
     *
     * @param document
     * @return
     */
    Document saveDocument(Document document);
}
