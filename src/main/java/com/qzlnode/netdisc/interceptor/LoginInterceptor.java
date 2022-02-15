package com.qzlnode.netdisc.interceptor;

import com.qzlnode.netdisc.result.CodeMsg;
import com.qzlnode.netdisc.service.AsyncService;
import com.qzlnode.netdisc.util.MessageHolder;
import com.qzlnode.netdisc.util.Security;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class LoginInterceptor implements HandlerInterceptor {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private AsyncService asyncService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = request.getHeader("token");
        if(!Security.parseToken(token)){
            asyncService.recordIpAddress(request);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.getOutputStream().print(CodeMsg.MESSAGE_ERROR.toString());
            return false;
        }
        asyncService.recordUserAction(request, MessageHolder.getUserId());
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
