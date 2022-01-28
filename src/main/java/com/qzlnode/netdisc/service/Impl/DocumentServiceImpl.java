package com.qzlnode.netdisc.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qzlnode.netdisc.dao.DocumentDao;
import com.qzlnode.netdisc.pojo.Document;
import com.qzlnode.netdisc.redis.DocumentKey;
import com.qzlnode.netdisc.redis.RedisService;
import com.qzlnode.netdisc.service.DocumentService;
import com.qzlnode.netdisc.util.MessageHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
public class DocumentServiceImpl extends ServiceImpl<DocumentDao, Document> implements DocumentService {

    /**
     * 日志
     */
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private DocumentDao documentDao;

    @Autowired
    private RedisService redisService;

    @Override
    public Document saveDocument(Document document) {
        if (document.getUserId() == null) {
            document.setUserId(MessageHolder.getUserId());
        }
        int result = documentDao.insert(document);
        if(result != 1){
            logger.error("insert to db error.");
            return null;
        }
        redisService.set(DocumentKey.document,String.valueOf(document.getFileId()),document);
        return document;
    }
}
