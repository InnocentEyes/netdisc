package com.qzlnode.netdisc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@EnableAspectJAutoProxy(exposeProxy = true)
@SpringBootApplication
public class NetdiscApplication {

    public static void main(String[] args) {
        SpringApplication.run(NetdiscApplication.class, args);
    }

    @Bean
    public SpringUtil getSpringUtil(){
        return new SpringUtil();
    }
}
