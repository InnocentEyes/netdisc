package com.qzlnode.netdisc.controller;

import com.qzlnode.netdisc.pojo.UserInfo;
import com.qzlnode.netdisc.redis.RedisService;
import com.qzlnode.netdisc.redis.key.UserKey;
import com.qzlnode.netdisc.result.CodeMsg;
import com.qzlnode.netdisc.result.Result;
import com.qzlnode.netdisc.service.AsyncService;
import com.qzlnode.netdisc.service.IndexService;
import com.qzlnode.netdisc.util.Security;
import com.qzlnode.netdisc.util.VerifyCodeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;

/**
 * @author qzlzzz
 */
@RestController
public class IndexController {
    /**
     * 日志
     */
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private IndexService indexService;

    @Autowired
    private AsyncService asyncService;

    @Autowired
    private RedisService redisService;


    @GetMapping(value = {"/","/index"})
    public ModelAndView index(){
        ModelAndView mv = new ModelAndView();
        mv.setViewName("redirect:index.html");
        return mv;
    }


    @PostMapping(value = "/login",produces = MediaType.APPLICATION_JSON_VALUE)
    public Result<UserInfo> login(@RequestBody UserInfo userInfo, HttpServletResponse response){
        if(userInfo.getAccount() == null || userInfo.getAccount().equals("")){
            return Result.error(CodeMsg.MOBILE_EMPTY);
        }
        if(userInfo.getPassword() == null || userInfo.getPassword().equals("")){
            return Result.error(CodeMsg.PASSWORD_EMPTY);
        }
        UserInfo userMessage = indexService.loginService(userInfo);
        if (userMessage == null){
            return Result.error(CodeMsg.LOGIN_ERROR);
        }
        if(userMessage.getId() == null){
            SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            logger.info("user login error at {}",ft.format(new Date()));
            return Result.error(CodeMsg.PASSWORD_ERROR);
        }
        response.setHeader("Access-Control-Expose-Headers","token");
        response.setHeader("token", Security.getToken(userMessage));
        logger.info("user {} named {} login success.",userMessage.getId(),userMessage.getName());
        userMessage.setPassword(null);
        return Result.success(userMessage,CodeMsg.SUCCESS);
    }

    @PostMapping(value = "/register",produces = MediaType.APPLICATION_JSON_VALUE)
    public Result register(@RequestBody UserInfo userInfo, @RequestParam("code") String verifyCode){
        if(!redisService.exists(UserKey.verifyCode,verifyCode)){
            return Result.error(CodeMsg.VERIFY_CODE_NOT_EXIST);
        }
        redisService.delete(UserKey.verifyCode,verifyCode);
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


    @GetMapping(value = "/getCode",produces = MediaType.APPLICATION_JSON_VALUE)
    public Result sendVerifyCode(HttpSession session,@RequestParam("account") String phone){
        Integer verifyCode = (Integer) session.getAttribute("verifyCode");
        if(verifyCode == null){
            session.setAttribute("verifyCode",1);
        }else {
            if (++verifyCode > 5) {
                return Result.error(CodeMsg.GET_VERIFY_CODE_TO_MANY);
            }
            session.setAttribute("verifyCode",verifyCode);
        }
        String code = VerifyCodeUtil.generateVerifyCode(4);
        SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if(redisService.set(UserKey.verifyCode,code,ft.format(new Date()))){
            asyncService.sendVerifyCode(code,phone);
        }
        return Result.success(CodeMsg.SUCCESS);
    }

}
