package com.hoppinzq.service.exception.aop;

import com.hoppinzq.service.exception.interceptor.DefaultExceptionHandler;
import com.hoppinzq.service.exception.interceptor.ExceptionHandler;

import java.lang.annotation.*;

/**
 * @author:ZhangQi 服务调用方异常捕获
 **/
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Repeatable(ExceptionContainer.class)
public @interface ExceptionCatch {
    int code() default 500;

    Class<? extends Exception> value();

    Class<? extends ExceptionHandler> handler() default DefaultExceptionHandler.class;
}
