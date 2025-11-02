package com.hoppinzq.service.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RateLimit {
    /**
     * 时间间隔，该时间内不允许重复请求
     * 单位秒
     *
     * @return
     */
    long timeout() default 5;

    /**
     * 类型
     */
    LimitType limitType() default LimitType.CUSTOMER;

    String custom_mark() default "mark";

    enum LimitType {
        /**
         * 自定义key
         */
        CUSTOMER,
        /**
         * 根据请求者IP限制
         */
        IP,
        /**
         * 根据当前登录人现在
         */
        LOGIN_USER
    }
}