package com.fh.shop.api.config;

import com.fh.shop.api.interceptor.LoginInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


//等同于xml中的beans根标签
@Configuration
public class WebConfig implements WebMvcConfigurer {

    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginInterceptor()).addPathPatterns("/api/**");
    }

    //<bean id="loginInterceptor" class="com.fh.shop.api.interceptor.LoginInterceptor"></bean>
    @Bean
    public LoginInterceptor loginInterceptor() {
        return new LoginInterceptor();
    }

}
