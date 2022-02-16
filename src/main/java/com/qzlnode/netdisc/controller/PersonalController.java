package com.qzlnode.netdisc.controller;

import com.qzlnode.netdisc.FastDFS;
import com.qzlnode.netdisc.pojo.ChatMsg;
import com.qzlnode.netdisc.pojo.FriendRequest;
import com.qzlnode.netdisc.pojo.UserInfo;
import com.qzlnode.netdisc.result.CodeMsg;
import com.qzlnode.netdisc.result.Result;
import com.qzlnode.netdisc.service.IndexService;
import com.qzlnode.netdisc.service.PersonalService;
import com.qzlnode.netdisc.util.MessageHolder;
import org.csource.common.MyException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

/**
 * @author qzlzzz
 */
@RequestMapping(value = "/user")
@RestController
public class PersonalController {

    /**
     * 日志
     */
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private PersonalService service;

    @Autowired
    private IndexService indexService;

    @Autowired
    private FastDFS fastDFS;

    /**
     * @param userInfo
     * @return
     * @throws IOException
     * @throws MyException
     */
    @PostMapping(value = "/update", produces = MediaType.APPLICATION_JSON_VALUE)
    public Result update(@RequestBody(required = false) UserInfo userInfo) {
        userInfo = Optional.of(userInfo)
                .filter(element -> element.getAccount() == null)
                .filter(element -> element.getRealName() == null)
                .filter(element -> element.getId() != null)
                .filter(element -> {
                    return element.getName() != null && element.getName().length() < 10;
                })
                .orElse(null);
        if (userInfo == null) {
            return Result.error(CodeMsg.UPDATE_ERROR);
        }
        return service.updateUserMsg(userInfo) ?
                Result.success(CodeMsg.SUCCESS) :
                Result.error(CodeMsg.UPDATE_ERROR);
    }

    /**
     * @param img
     * @return
     */
    @PostMapping(value = "/header/change", produces = MediaType.APPLICATION_JSON_VALUE)
    public Result<String> changeHeader(@RequestParam(value = "header") MultipartFile img)
            throws MyException, IOException {
        boolean target;
        target = service.deleteHeader(MessageHolder.getUserId());
        if (!target) {
            return Result.error(CodeMsg.FILE_DELETE_ERROR);
        }
        String fileExtName = img.getOriginalFilename().split(".")[1];
        String[] res = fastDFS.upload(img.getBytes(), fileExtName);
        target = service.saveHeader(res, MessageHolder.getUserId());
        if (!target) {
            return Result.error(CodeMsg.FILE_UPLOAD_ERROR);
        }
        return Result.success(CodeMsg.SUCCESS);
    }

    /**
     * @param img
     * @return
     * @throws IOException
     * @throws MyException
     */
    @PostMapping(value = "/header/init", produces = MediaType.APPLICATION_JSON_VALUE)
    public Result<String> initHeader(@RequestParam(value = "header") MultipartFile img) throws IOException, MyException {
        String fileExtName = img.getOriginalFilename().split(".")[1];
        String[] res = fastDFS.upload(img.getBytes(), fileExtName);
        if (!service.initHeader(res, MessageHolder.getUserId())) {
            logger.error("file upload error.");
            fastDFS.delete(res[0], res[1]);
            return Result.error(CodeMsg.FILE_UPLOAD_ERROR);
        }
        return Result.success(res[0] + "/" + res[1], CodeMsg.SUCCESS);
    }

    /**
     * @return
     */
    @GetMapping(value = "/unsigned")
    public Result<List<ChatMsg>> getUnSignedMsg() {
        return Result.success(service.getUnsignedMsg(), CodeMsg.SUCCESS);
    }

    /**
     * @param account
     * @return
     */
    @GetMapping(value = "/make")
    public Result sendFriendRequest(@RequestParam(value = "phone", required = false) String account) {
        if (account == null) {
            return Result.error(CodeMsg.BIND_ERROR);
        }
        return Result.success(service.sendFriendRequest(account));
    }

    /**
     * @return
     */
    @GetMapping(value = "/friendRequest")
    public Result<List<FriendRequest>> queryFriendRequest() {
        return Result.success(service.findFriendRequest(), CodeMsg.SUCCESS);
    }

    @GetMapping("/friends")
    public Result<List<UserInfo>> queryFriends() {
        return Result.success(service.findFriends(), CodeMsg.SUCCESS);
    }

    @GetMapping("/handler")
    public Result<List<UserInfo>> handlerFriendRequest(
            @RequestParam(value = "requestId",required = false) Integer requestId,
            @RequestParam(value = "userId", required = false) Integer senderId,
            @RequestParam(value = "operation", required = false) Integer operation
    ) {
        if (senderId == null || operation == null || requestId == null) {
            return Result.error(CodeMsg.BIND_ERROR);
        }
        if (CodeMsg.IGNORE.getCode() == operation) {
            return Result.error(service.deleteFriendRequest(requestId));
        }

        if(CodeMsg.ACCPET.getCode() != operation){
            return Result.error(CodeMsg.ERROR.fillArgs("operation 参数错误.."));
        }
        service.passFriendRequest(senderId);
        return Result.success(service.findFriends(),CodeMsg.SUCCESS);
    }


}
