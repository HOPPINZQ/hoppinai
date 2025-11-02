package com.hoppinzq.service.exception.aop;

import java.lang.annotation.*;

/**
 * @author:ZhangQi 服务调用方异常捕获
 **/
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ExceptionContainer {
    ExceptionCatch[] value();
}
