package com.qzlnode.netdisc.controller;

import com.qzlnode.netdisc.exception.RegisterErrorException;
import com.qzlnode.netdisc.pojo.UserInfo;
import com.qzlnode.netdisc.result.CodeMsg;
import com.qzlnode.netdisc.result.Result;
import com.qzlnode.netdisc.service.IndexService;
import com.qzlnode.netdisc.util.MessageHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;

@RestController
public class IndexController {
    /**
     * 日志
     */
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private IndexService indexService;

    @PostMapping(value = "/login",produces = MediaType.APPLICATION_JSON_VALUE)
    public Result<UserInfo> login(@RequestBody UserInfo userInfo){
        if(userInfo.getAccount() == null){
            return Result.error(CodeMsg.MOBILE_EMPTY);
        }
        if(userInfo.getPassword() == null){
            return Result.error(CodeMsg.PASSWORD_EMPTY);
        }
        UserInfo userMessage = indexService.loginService(userInfo);
        if(userMessage.getId() == null){
            SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            logger.info("user login error at {}",ft.format(new Date()));
            return Result.error(CodeMsg.PASSWORD_ERROR);
        }
        logger.info("user {} named {} login success.",userMessage.getId(),userMessage.getName());
        return Result.success(userMessage,CodeMsg.SUCCESS);
    }

    @PostMapping(value = "/register",produces = MediaType.APPLICATION_JSON_VALUE)
    public Result register(@RequestBody UserInfo userInfo){
        userInfo = Optional.of(userInfo)
                .filter(element -> element.getAccount() != null)
                .filter(element -> element.getPassword() != null)
                .filter(element -> element.getRealName() != null)
                .orElse(null);
        if(userInfo == null){
            return Result.error(CodeMsg.USER_MESSAGE_EMPTY);
        }
        boolean target = indexService.registerService(userInfo);
        if(!target){
            return Result.error(CodeMsg.REGISTRATION_FAILED);
        }
        return Result.success(CodeMsg.SUCCESS);
    }

    @ExceptionHandler({
            RegisterErrorException.class,
            NullPointerException.class
    })
    public Result handlerError(Exception ex, HttpServletRequest request){
        MessageHolder.clearData();
        logger.error("handler {} error.\n" +
                "the reason is {}",request.getRequestURL(),ex.getMessage());
        return Result.error(CodeMsg.SERVER_ERROR.fillArgs(ex.getMessage()));
    }
}
