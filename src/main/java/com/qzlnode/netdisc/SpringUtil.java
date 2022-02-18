package com.qzlnode.netdisc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

/**
 * @author qzlzzz
 */
@Order(Ordered.HIGHEST_PRECEDENCE)
public class SpringUtil implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    private static final Logger logger = LoggerFactory.getLogger(SpringUtil.class);


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        if(SpringUtil.applicationContext == null){
            SpringUtil.applicationContext = applicationContext;
        }
    }

    public static  ApplicationContext getApplicationContext(){
        if (applicationContext == null){
            logger.info("applicationContext is null");
        }
        return applicationContext;
    }

    //通过name获取Bean
    public static Object getBean(String name){
        return getApplicationContext().getBean(name);
    }

    //通过class获取Bean
    public static <T> T getBean(Class<T> claszz){
        return getApplicationContext().getBean(claszz);
    }

    //通过name,以及Clazz返回指定的Bean
    public static <T> T getBean(String name,Class<T> claszz){
        return getApplicationContext().getBean(name,claszz);
    }
}
