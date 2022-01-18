package com.qzlnode.netdisc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@EnableAspectJAutoProxy
@SpringBootApplication
public class NetdiscApplication {

    public static void main(String[] args) {
        SpringApplication.run(NetdiscApplication.class, args);
    }

}
