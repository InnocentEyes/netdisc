package com.qzlnode.netdisc.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.qzlnode.netdisc.pojo.Document;
import org.csource.common.MyException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

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

    /**
     *
     * @param fileId
     * @return
     */
    Document getDocument(Integer fileId);

    /**
     *
     * @param groupName
     * @param fileRemotePath
     * @return
     */
    byte[] download(String groupName,String fileRemotePath) throws MyException, IOException;

    /**
     *
     * @param files
     * @return
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    List<Document> saveBatchDocument(MultipartFile[] files) throws InvocationTargetException, IllegalAccessException;

    /**
     *
     * @return
     */
    List<Document> getBatchDocument();
}
