package com.qzlnode.netdisc.fastdfs;

import org.csource.common.MyException;
import org.csource.fastdfs.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.io.IOException;

/**
 * @author qzlzz
 */
@Component
public class FastDFS {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    private static StorageClient storageClient = null;

    static {
        try {
            ClientGlobal.init("fastdfs.xml");
            TrackerClient trackerClient = new TrackerClient();
            TrackerServer trackerServer = trackerClient.getTrackerServer();
            StorageServer storageServer = trackerClient.getStoreStorage(trackerServer);
            storageClient = new StorageClient(trackerServer,storageServer);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (MyException e) {
            e.printStackTrace();
        }
    }

    /**
     * 文件上传
     * @param fileBytes
     * @param fileExtName
     * @return
     * @throws MyException
     * @throws IOException
     */
    public String[] upload(byte[] fileBytes,String fileExtName) throws MyException, IOException {
        Assert.notNull(storageClient,"storageClient is null");
        LOGGER.info("----------uploading----------");
        return storageClient.upload_file(fileBytes,fileExtName,null);
    }

    /**
     * 文件删除
     * @param groupName
     * @param fileRemotePath
     * @return
     * @throws MyException
     * @throws IOException
     */
    public int delete(String groupName,String fileRemotePath) throws MyException, IOException {
        Assert.notNull(storageClient,"storageClient is null");
        LOGGER.info("----------deleting----------");
        return storageClient.delete_file(groupName,fileRemotePath);
    }

    /**
     * 文件下载
     * @param group
     * @param fileRemotePath
     * @return
     * @throws MyException
     * @throws IOException
     */
    public byte[] download(String group,String fileRemotePath) throws MyException, IOException {
        Assert.notNull(storageClient,"storageClient is null");
        LOGGER.info("----------downloading----------");
        return storageClient.download_file(group,fileRemotePath);
    }
}
