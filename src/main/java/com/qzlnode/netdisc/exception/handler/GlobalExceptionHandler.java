package com.qzlnode.netdisc.exception.handler;

import com.qzlnode.netdisc.exception.*;
import com.qzlnode.netdisc.result.CodeMsg;
import com.qzlnode.netdisc.result.Result;
import com.qzlnode.netdisc.util.MessageHolder;
import com.qzlnode.netdisc.util.Security;
import org.csource.common.MyException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

/**
 * @author qzlzzz
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * 服务端异常
     * @param exception
     * @param request
     * @return
     */
    @ExceptionHandler({
            IOException.class,
            MyException.class,
            InvocationTargetException.class,
            IllegalAccessException.class,
            NoSuchMethodException.class,
            InconsistentException.class,
            UploadFileToLargeException.class,
            NullPointerException.class,
            HasPhoneException.class,
            UpdateCountException.class,
            RegisterErrorException.class
    })
    public Result handlerError(Exception exception, HttpServletRequest request){
        String realIp = Security.getIPAddress(request);
        MessageHolder.clearData();
        logger.error("handler {} error. user ip address is {}.\n" +
                "the reason is {}",request.getRequestURL(),realIp,exception.getCause().getMessage());
        return Result.error(CodeMsg.ERROR.fillArgs(exception.getCause().getMessage()));
    }
}
