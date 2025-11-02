package com.hoppinzq.service.http.annotation;

import com.hoppinzq.service.http.handler.HttpApiDefaultHandler;

import java.lang.annotation.*;

/**
 * @author:ZhangQi http声明式注解，用来声明一个http请求，需要借助其他注解使用
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface HttpApi {
    String url();

    String method() default "GET";//请求类型

    Class<? extends HttpApiDefaultHandler> handler() default HttpApiDefaultHandler.class;
}

