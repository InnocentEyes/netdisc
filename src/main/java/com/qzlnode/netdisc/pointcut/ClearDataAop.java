package com.qzlnode.netdisc.pointcut;


import com.qzlnode.netdisc.util.MessageHolder;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * @author qzlzzz
 */
@Component
@Aspect
public class ClearDataAop {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @After(value = "within(com.qzlnode.netdisc.controller..*) && !this(com.qzlnode.netdisc.controller.IndexController)")
    public void clearData(){
        MessageHolder.clearData();
        logger.info("clear threadLocal data success.");
    }
}
