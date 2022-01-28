package com.qzlnode.netdisc.controller;

import com.qzlnode.netdisc.result.CodeMsg;
import com.qzlnode.netdisc.result.Result;
import com.qzlnode.netdisc.util.MessageHolder;
import com.qzlnode.netdisc.util.Security;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * @author qzlzzz
 */
@RestController
@RequestMapping("/send")
public class MessageSendController {

    /**
     * 日志
     */
    private final Logger logger = LoggerFactory.getLogger(getClass());


    @ExceptionHandler({
            IllegalAccessException.class
    })
    public Result handlerError(Exception exception, HttpServletRequest request){
        String ip = Security.getIPAddress(request);
        MessageHolder.clearData();
        logger.error("handler {} error. ip address is {}.\n" +
                "the reason is {}",request.getRequestURL(),ip,exception.getMessage());
        return Result.error(CodeMsg.ERROR.fillArgs(exception.getMessage()));
    }
}
