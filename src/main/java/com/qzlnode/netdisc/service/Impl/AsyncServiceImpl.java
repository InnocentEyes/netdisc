package com.qzlnode.netdisc.service.Impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.qzlnode.netdisc.FastDFS;
import com.qzlnode.netdisc.SpringUtil;
import com.qzlnode.netdisc.dao.DocumentDao;
import com.qzlnode.netdisc.dao.MusicDao;
import com.qzlnode.netdisc.dao.UserDao;
import com.qzlnode.netdisc.dao.VideoDao;
import com.qzlnode.netdisc.pojo.Document;
import com.qzlnode.netdisc.pojo.Music;
import com.qzlnode.netdisc.pojo.UserInfo;
import com.qzlnode.netdisc.pojo.Video;
import com.qzlnode.netdisc.pojo.tencent.TenCentShortMessage;
import com.qzlnode.netdisc.pojo.tencent.TencentUser;
import com.qzlnode.netdisc.redis.RedisService;
import com.qzlnode.netdisc.redis.key.DocumentKey;
import com.qzlnode.netdisc.redis.key.MusicKey;
import com.qzlnode.netdisc.redis.key.UserKey;
import com.qzlnode.netdisc.redis.key.VideoKey;
import com.qzlnode.netdisc.service.AsyncService;
import com.qzlnode.netdisc.util.Cache;
import com.qzlnode.netdisc.util.Security;
import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.common.exception.TencentCloudSDKException;
import com.tencentcloudapi.sms.v20210111.SmsClient;
import com.tencentcloudapi.sms.v20210111.models.SendSmsRequest;
import com.tencentcloudapi.sms.v20210111.models.SendSmsResponse;
import org.csource.common.MyException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author qzlzzz
 */
@Transactional(rollbackFor = {
        RuntimeException.class,
        MyException.class,
        IOException.class
})
@Service
public class AsyncServiceImpl implements AsyncService {
    /**
     * 日志
     */
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final SmsClient smsClient;

    @Autowired
    private TenCentShortMessage shortMessage;

    @Autowired
    private FastDFS dfs;

    @Autowired
    private VideoDao videoDao;

    @Autowired
    private DocumentDao documentDao;

    @Autowired
    private MusicDao musicDao;

    @Autowired
    private UserDao userDao;

    @Autowired
    private RedisService redisService;


    @Autowired
    public AsyncServiceImpl(TencentUser user) {
        Credential cred = new Credential(user.getSecretId(), user.getSecretKey());
        this.smsClient = new SmsClient(cred, user.getRegion());
    }

    @Async("asyncTaskExecutor")
    @Override
    public void uploadVideo(MultipartFile file, Integer fileId, Integer userId) {
        if (file == null || fileId == null || fileId < 1 || userId < 1) {
            return;
        }
        String key = VideoKey.video.getPrefix() + userId;
        String value = VideoKey.video.getPrefix() + fileId;
        Cache.putAsync(key, value);
        try {
            String[] uploadRes = dfs.upload(file.getBytes(), file.getOriginalFilename().split("\\.")[1]);
            videoDao.update(
                    null,
                    Wrappers.lambdaUpdate(Video.class)
                            .eq(Video::getVideoId, fileId)
                            .set(Video::getGroupName, uploadRes[0])
                            .set(Video::getVideoRemotePath, uploadRes[1])
            );
        } catch (MyException | IOException e) {
            logger.error("run the async method error.\n {}", e.getMessage());
        } catch (Exception e) {
            logger.error("run the async method get a unexpected exception {} , the reason is {}", e, e.getMessage());
        } finally {
            Cache.removeAsync(key, value);
        }

    }

    @Override
    public void uploadBatchVideo(MultipartFile[] files, Integer[] fileIds, Integer userId) {
        if (files == null || fileIds == null) {
            return;
        }
        if (fileIds.length == 0 || files.length != fileIds.length) {
            return;
        }
        int index = 0;
        AsyncService service = SpringUtil.getBean(AsyncService.class);
        for (MultipartFile file : files) {
            service.uploadVideo(file, fileIds[index++], userId);
        }
    }

    @Async("asyncTaskExecutor")
    @Override
    public void uploadDocument(MultipartFile file, Integer fileId, Integer userId) {
        if (file == null || fileId == null || fileId < 1 || userId < 1) {
            return;
        }
        String key = DocumentKey.document.getPrefix() + userId;
        String value = DocumentKey.document.getPrefix() + fileId;
        Cache.putAsync(key, value);
        try {
            String[] uploadRes = dfs.upload(file.getBytes(), file.getOriginalFilename().split("\\.")[1]);
            documentDao.update(
                    null,
                    Wrappers.lambdaUpdate(Document.class)
                            .eq(Document::getUserId, userId)
                            .eq(Document::getFileId, fileId)
                            .set(Document::getGroupName, uploadRes[0])
                            .set(Document::getFileRemotePath, uploadRes[1])
            );
        } catch (MyException | IOException e) {
            logger.error("run the async method error.\n {}", e.getMessage());
        } catch (Exception e) {
            logger.error("run the async method get a unexpected exception {} , the reason is {}", e, e.getMessage());
        } finally {
            Cache.removeAsync(key, value);
        }

    }

    @Override
    public void uploadBatchDocument(MultipartFile[] files, Integer[] fileIds, Integer userId) {
        if (files == null || fileIds == null) {
            return;
        }
        if (fileIds.length == 0 || files.length != fileIds.length) {
            return;
        }
        int index = 0;
        AsyncService service = SpringUtil.getBean(AsyncService.class);
        for (MultipartFile file : files) {
            service.uploadDocument(file, fileIds[index++], userId);
        }
    }

    @Async("asyncTaskExecutor")
    @Override
    public void uploadMusic(MultipartFile file, Integer fileId, Integer userId) {
        if (file == null || fileId == null || fileId < 1 || userId == null || userId < 1) {
            return;
        }
        String key = MusicKey.music.getPrefix() + userId;
        String value = MusicKey.music.getPrefix() + fileId;
        Cache.putAsync(key, value);
        try {
            String[] uploadRes = dfs.upload(file.getBytes(), file.getOriginalFilename().split("\\.")[1]);
            musicDao.update(
                    null,
                    Wrappers.lambdaUpdate(Music.class)
                            .eq(Music::getUserId, userId)
                            .eq(Music::getMusicId, fileId)
                            .set(Music::getGroupName, uploadRes[0])
                            .set(Music::getMusicRemotePath, uploadRes[1])
            );
        } catch (MyException | IOException e) {
            logger.error("run the async method error.\n {}", e.getMessage());
        } catch (Exception e) {
            logger.error("run the async method get a unexpected exception {} , the reason is {}", e, e.getMessage());
        } finally {
            Cache.removeAsync(key, value);
        }
    }

    @Override
    public void uploadBatchMusic(MultipartFile[] files, Integer[] fileIds, Integer userId) {
        if (files == null || fileIds == null) {
            return;
        }
        if (fileIds.length == 0 || files.length != fileIds.length) {
            return;
        }
        int index = 0;
        AsyncService service = SpringUtil.getBean(AsyncService.class);
        for (MultipartFile file : files) {
            service.uploadMusic(file, fileIds[index++], userId);
        }
    }

    @Async("loggerTaskExecutor")
    @Override
    public void recordIpAddress(HttpServletRequest request) {
        String realIp = Security.getIPAddress(request);
        SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            if (realIp != null) {
                logger.info("log info: {}", realIp);
                redisService.incr(UserKey.blackUser, realIp);
            }
            if (request.getRequestURI() == null) {
                return;
            }
            logger.info("{} get the {} server at {}",
                    realIp == null ? (request.getRemoteAddr() == null ? "null" : request.getRemoteAddr()) : realIp,
                    request.getRequestURI(),
                    ft.format(new Date()));
        } catch (Exception e) {
            logger.info("logger record error.", e);
        }
    }

    @Async("loggerTaskExecutor")
    @Override
    public void recordUserAction(HttpServletRequest request, Integer userId) {
        UserInfo userInfo = userDao.selectById(userId);
        String realIp = Security.getIPAddress(request);
        SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            if (request.getRequestURI() == null) {
                logger.info("error happen at {}", ft.format(new Date()));
                return;
            }
            logger.info("user id {} trueNamed {} phoned {} get the {} server in {} at {}",
                    userInfo.getId(),
                    userInfo.getRealName(),
                    userInfo.getAccount(),
                    request.getRequestURI(),
                    ft.format(new Date()),
                    realIp == null ? (request.getRemoteAddr() == null ? "null" : request.getRemoteAddr()) : realIp);
        } catch (Exception e) {
            logger.info("logger record error.", e);
        }
    }

    /**
     * 发送验证码
     * @param code
     * @param phone
     */
    @Async("asyncTaskExecutor")
    @Override
    public void sendVerifyCode(String code, String phone) {
        SendSmsRequest sendSmsRequest = new SendSmsRequest();
        //为请求设置SdkId 也即是短信应用的Id
        sendSmsRequest.setSmsSdkAppId(shortMessage.getSdkAppId());
        //设置发送短信的电话号码 +86xxxxxx
        String[] phones = {shortMessage.getPhonePerfix() + phone};
        sendSmsRequest.setPhoneNumberSet(phones);
        //设置发送短信的模板
        sendSmsRequest.setTemplateId(shortMessage.getTemplateId());
        //为模板设置参数
        String[] templateParam = {code, TenCentShortMessage.getExipreTime()};
        sendSmsRequest.setTemplateParamSet(templateParam);
        //为短信添加签名内容
        sendSmsRequest.setSignName(TenCentShortMessage.getSign());
        try {
            //发送请求
            SendSmsResponse response = smsClient.SendSms(sendSmsRequest);
            logger.info("send message success, {}",response);
        } catch (TencentCloudSDKException e) {
            redisService.delete(UserKey.verifyCode,code);
            logger.error("send verify message error. the reason is {}",e.getCause().getMessage());
        }
    }
}
