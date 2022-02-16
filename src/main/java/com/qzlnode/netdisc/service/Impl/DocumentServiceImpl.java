package com.qzlnode.netdisc.service.Impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qzlnode.netdisc.dao.DocumentDao;
import com.qzlnode.netdisc.FastDFS;
import com.qzlnode.netdisc.pojo.Document;
import com.qzlnode.netdisc.redis.key.DocumentKey;
import com.qzlnode.netdisc.redis.RedisService;
import com.qzlnode.netdisc.service.DocumentService;
import com.qzlnode.netdisc.util.Cache;
import com.qzlnode.netdisc.util.FileInfoHandler;
import com.qzlnode.netdisc.util.MessageHolder;
import org.csource.common.MyException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.LockSupport;

/**
 * @author qzlzzz
 */
@Transactional(rollbackFor = {
        IllegalAccessException.class,
        MyException.class,
        IOException.class,
        InvocationTargetException.class
})
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

    @Autowired
    private FastDFS fastDFS;

    @Autowired
    private FileInfoHandler fileInfoHandler;

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
        return document;
    }

    @Override
    public Document getDocument(Integer fileId) {
        Document document = redisService.get(DocumentKey.document,String.valueOf(fileId),Document.class);
        if(document != null) {
            return document;
        }
        String key = DocumentKey.document.getPrefix() + MessageHolder.getUserId();
        String value = DocumentKey.document.getPrefix() + fileId;
        while (Cache.hasTask(key,value)){
            LockSupport.parkNanos(100);
        }
        Cache.removeAsyncKey(key);
        document = documentDao.selectOne(
                Wrappers.lambdaQuery(Document.class)
                        .eq(Document::getFileId,fileId)
                        .eq(Document::getUserId,MessageHolder.getUserId())
        );
        redisService.set(DocumentKey.document,String.valueOf(fileId),document);
        redisService.setSet(DocumentKey.documentList,String.valueOf(MessageHolder.getUserId()),document);
        return document;
    }

    @Override
    public byte[] download(String groupName, String fileRemotePath) throws MyException, IOException {
        if(groupName == null || fileRemotePath == null){
            return null;
        }
        return fastDFS.download(groupName,fileRemotePath);
    }

    @Override
    public List<Document> saveBatchDocument(MultipartFile[] files)
            throws InvocationTargetException, IllegalAccessException {
        List<Document> documents = new ArrayList<>();
        for (MultipartFile file : files) {
            Document document = fileInfoHandler.fileInfoToBean(file,null,Document.class);
            document.setUserId(MessageHolder.getUserId());
            documents.add(document);
        }
        boolean isSave = saveBatch(documents);
        if(!isSave){
            return null;
        }
        return documents;
    }

    @Override
    public List<Document> getBatchDocument() {
        Integer userId = MessageHolder.getUserId();
        String key = DocumentKey.document.getPrefix() + userId;
        if(!Cache.hasTask(key)){
            return redisService.getList(DocumentKey.documentList,String.valueOf(userId),Document.class);
        }
        while(Cache.hasTask(key)){
            LockSupport.parkNanos(100);
        }
        Cache.removeAsyncKey(key);
        List<Document> documentList = documentDao.selectList(
                Wrappers.lambdaQuery(Document.class)
                        .eq(Document::getUserId,userId)
        );
        redisService.setSet(DocumentKey.documentList,String.valueOf(userId),documentList);
        return documentList;
    }
}
