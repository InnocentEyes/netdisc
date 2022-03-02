package com.qzlnode.netdisc.interceptor;

import com.qzlnode.netdisc.redis.RedisService;
import com.qzlnode.netdisc.redis.key.UserKey;
import com.qzlnode.netdisc.result.CodeMsg;
import com.qzlnode.netdisc.util.Security;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public class BlackListFilter implements Filter {

    private final RedisService redisService;

    private final Logger logger = LoggerFactory.getLogger(getClass());

    public BlackListFilter(RedisService redisService) {
        this.redisService = redisService;
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        String requestURI = request.getRequestURI();
        if (requestURI.equals("/favicon.ico")
                || requestURI.equals("/")
                || requestURI.equals("/error")
                || requestURI.contains("/index")
                || requestURI.contains("/druid/")
                || requestURI.contains("/img")) {
            filterChain.doFilter(request, servletResponse);
            return;
        }
        String realIp = Security.getIPAddress(request);
        if(realIp == null){
            return;
        }
        Integer doError = redisService.get(UserKey.blackUser, realIp, Integer.class);
        if (doError != null && doError >= 3) {
            servletResponse.setContentType("application/json;charset=UTF-8");
            servletResponse.getWriter().write(CodeMsg.UNENABLED.toString());
            return;
        }
        filterChain.doFilter(request, servletResponse);
    }
}
