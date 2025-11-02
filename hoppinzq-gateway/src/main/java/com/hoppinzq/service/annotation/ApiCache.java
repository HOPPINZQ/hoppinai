package com.hoppinzq.service.annotation;

import java.lang.annotation.*;

/**
 * 自定义注解：缓存注解
 * 只会在被ApiServiceMapping环绕的类中和被ApiMapping环绕的方法上生效
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@CustomMapping
public @interface ApiCache {
    int time() default 60 * 5;//默认5分
}
