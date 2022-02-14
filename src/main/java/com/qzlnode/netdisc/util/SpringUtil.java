package com.qzlnode.netdisc.util;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * @author qzlzzz
 */
public class SpringUtil implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        if(SpringUtil.applicationContext == null){
            SpringUtil.applicationContext = applicationContext;
        }
    }

    public static  ApplicationContext getApplicationContext(){
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
