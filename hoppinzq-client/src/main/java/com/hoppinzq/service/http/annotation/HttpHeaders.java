package com.hoppinzq.service.http.annotation;

import java.lang.annotation.*;

/**
 * @author:ZhangQi 用来设置一批@HttpHeader注解
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface HttpHeaders {
    HttpHeader[] value();
}