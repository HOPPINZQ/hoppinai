package com.hoppinzq.service.interceptor.aop;

import org.springframework.stereotype.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author:ZhangQi 声明（配置）一个类为RPC的拦截器
 **/
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Component
public @interface RPCInterceptor {
    int value() default 10;
}
