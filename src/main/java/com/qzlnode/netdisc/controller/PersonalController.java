package com.qzlnode.netdisc.controller;

import com.qzlnode.netdisc.fastdfs.FastDFS;
import com.qzlnode.netdisc.pojo.UserInfo;
import com.qzlnode.netdisc.result.CodeMsg;
import com.qzlnode.netdisc.result.Result;
import com.qzlnode.netdisc.service.PersonalService;
import com.sun.tools.javac.jvm.Code;
import org.csource.common.MyException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Optional;

/**
 * @author qzlzzz
 */
@RestController
public class PersonalController {

    /**
     * 日志
     */
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private PersonalService service;

    @Autowired
    private FastDFS fastDFS;

    @PostMapping(value = "/update",produces = MediaType.APPLICATION_JSON_VALUE)
    public Result<String> update(@RequestBody(required = false) UserInfo userInfo) throws IOException, MyException {
        userInfo = Optional.of(userInfo)
                .filter(element -> element.getId() != null)
                .filter(element -> element.getName() == null)
                .orElse(null);
        if(userInfo == null){
            return Result.error(CodeMsg.UPDATE_ERROR);
        }
        return service.updateUserMsg(userInfo) ?
                Result.success(CodeMsg.SUCCESS) :
                Result.error(CodeMsg.UPDATE_ERROR);
    }

    @PostMapping(value = "/change/{id}",produces = MediaType.APPLICATION_JSON_VALUE)
    public Result<String> changeHeader(@RequestParam(value = "header") MultipartFile img,
                                       @PathVariable("id") Integer id){
        return Result.success(CodeMsg.SUCCESS);
    }

    @PostMapping(value = "/init",produces = MediaType.APPLICATION_JSON_VALUE)
    public Result<String> initHeader(@RequestParam(value = "header") MultipartFile img){
        return Result.success(CodeMsg.SUCCESS);
    }

    @ExceptionHandler({
            IOException.class,
            MyException.class,
            NullPointerException.class
    })
    public Result<String> handlerError(Exception ex, HttpServletRequest request){
        logger.error("handler {} error.\n" +
                "the reason is {}",request.getRequestURL(),ex.getMessage());
        return Result.error(CodeMsg.SERVER_ERROR.fillArgs(ex.getMessage()));
    }
}
