package com.qzlnode.netdisc.controller;

import com.qzlnode.netdisc.exception.UpdateCountException;
import com.qzlnode.netdisc.fastdfs.FastDFS;
import com.qzlnode.netdisc.pojo.UserInfo;
import com.qzlnode.netdisc.result.CodeMsg;
import com.qzlnode.netdisc.result.Result;
import com.qzlnode.netdisc.service.PersonalService;
import com.qzlnode.netdisc.util.MessageHolder;
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

    /**
     *
     * @param userInfo
     * @return
     * @throws IOException
     * @throws MyException
     */
    @PostMapping(value = "/user/update",produces = MediaType.APPLICATION_JSON_VALUE)
    public Result update(@RequestBody(required = false) UserInfo userInfo) throws IOException, MyException {
        userInfo = Optional.of(userInfo)
                .filter(element -> element.getAccount() == null)
                .filter(element -> element.getRealName() == null)
                .filter(element -> element.getId() != null)
                .filter(element -> {
                    return element.getName() != null && element.getName().length() < 10;
                })
                .orElse(null);
        if(userInfo == null){
            return Result.error(CodeMsg.UPDATE_ERROR);
        }
        return service.updateUserMsg(userInfo) ?
                Result.success(CodeMsg.SUCCESS) :
                Result.error(CodeMsg.UPDATE_ERROR);
    }

    /**
     *
     * @param img
     * @return
     */
    @PostMapping(value = "/header/change",produces = MediaType.APPLICATION_JSON_VALUE)
    public Result<String> changeHeader(@RequestParam(value = "header") MultipartFile img) throws MyException, IOException {
        boolean target;
        target = service.deleteHeader(MessageHolder.getUserId());
        if(!target){
            return Result.error(CodeMsg.FILE_DELETE_ERROR);
        }
        String fileExtName = img.getOriginalFilename().split(".")[1];
        String[] res = fastDFS.upload(img.getBytes(),fileExtName);
        target = service.saveHeader(res,MessageHolder.getUserId());
        if(!target){
            return Result.error(CodeMsg.FILE_UPLOAD_ERROR);
        }
        return Result.success(CodeMsg.SUCCESS);
    }

    /**
     *
     * @param img
     * @return
     * @throws IOException
     * @throws MyException
     */
    @PostMapping(value = "/header/init",produces = MediaType.APPLICATION_JSON_VALUE)
    public Result<String> initHeader(@RequestParam(value = "header") MultipartFile img) throws IOException, MyException {
        String fileExtName = img.getOriginalFilename().split(".")[1];
        String[] res = fastDFS.upload(img.getBytes(), fileExtName);
        if(!service.initHeader(res,MessageHolder.getUserId())){
            logger.error("file upload error.");
            fastDFS.delete(res[0],res[1]);
            return Result.error(CodeMsg.FILE_UPLOAD_ERROR);
        }
        return Result.success(res[0] + "/" + res[1],CodeMsg.SUCCESS);
    }

    @ExceptionHandler({
            IOException.class,
            MyException.class,
            UpdateCountException.class,
            NullPointerException.class
    })
    public Result handlerError(Exception ex, HttpServletRequest request){
        MessageHolder.clearData();
        logger.error("handler {} error.\n" +
                "the reason is {}",request.getRequestURL(),ex.getMessage());
        return Result.error(CodeMsg.ERROR.fillArgs(ex.getMessage()));
    }
}
