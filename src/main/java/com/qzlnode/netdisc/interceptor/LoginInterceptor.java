package com.qzlnode.netdisc.interceptor;

import com.qzlnode.netdisc.redis.RedisService;
import com.qzlnode.netdisc.result.CodeMsg;
import com.qzlnode.netdisc.service.AsyncService;
import com.qzlnode.netdisc.util.MessageHolder;
import com.qzlnode.netdisc.util.Security;
import com.qzlnode.netdisc.SpringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;

@Component
public class LoginInterceptor implements HandlerInterceptor {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private AsyncService asyncService;


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if(asyncService == null) {
            this.asyncService = SpringUtil.getBean(AsyncService.class);
        }
        String token = request.getHeader("token");
        if(!Security.parseToken(token)){
            this.asyncService.recordIpAddress(request);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write(CodeMsg.MESSAGE_ERROR.toString());
            return false;
        }
        this.asyncService.recordUserAction(request, MessageHolder.getUserId());
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
    }
}
