package com.qzlnode.netdisc.config;

import com.qzlnode.netdisc.util.ThreadUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ThreadPoolExecutor;

@Configuration
public class ThreadTaskPoolConfig {

    @Bean("asyncTaskExecutor")
    public ThreadPoolExecutor asyncTaskExecutor(){
        return ThreadUtil.getIOTargetThreadPool();
    }

    @Bean("loggerTaskExecutor")
    public ThreadPoolExecutor loggerTaskExecutor(){
        return ThreadUtil.getCPUTargetThreadPool();
    }
}