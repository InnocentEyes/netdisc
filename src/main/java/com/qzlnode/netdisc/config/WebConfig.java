package com.qzlnode.netdisc.config;

import com.qzlnode.netdisc.interceptor.LoginInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    static final String[] OGIGINS = new String[]{"GET","POST","PUT","DELETE"};

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LoginInterceptor())
                .addPathPatterns("/**")
                .excludePathPatterns(Arrays.asList("/","/druid/**","/getCode","/favicon.ico","/index","/index.html","/error","/register", "/login", "/css/**", "/js/**", "/font/**", "/img/**"));
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**").allowedOriginPatterns("*").allowCredentials(true).allowedMethods(OGIGINS).allowedHeaders("*").maxAge(3600);
    }
}
