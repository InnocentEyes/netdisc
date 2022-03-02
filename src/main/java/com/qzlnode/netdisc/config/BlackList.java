package com.qzlnode.netdisc.config;

import com.qzlnode.netdisc.interceptor.BlackListFilter;
import com.qzlnode.netdisc.redis.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BlackList {

    @Autowired
    private RedisService redisService;

    @Bean
    @ConditionalOnMissingBean(FilterRegistrationBean.class)
    public FilterRegistrationBean<BlackListFilter> blackListFilter(){
        FilterRegistrationBean<BlackListFilter> blackListFilter = new FilterRegistrationBean<>(new BlackListFilter(redisService));
        blackListFilter.addUrlPatterns("/*");
        return blackListFilter;
    }
}
