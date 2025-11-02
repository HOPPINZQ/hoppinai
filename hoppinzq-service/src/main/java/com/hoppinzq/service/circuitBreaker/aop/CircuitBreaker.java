package com.hoppinzq.service.circuitBreaker.aop;

import org.springframework.core.annotation.Order;

import java.lang.annotation.*;

/**
 * 熔断器注解
 */
@Order(2)
@Target(ElementType.METHOD)
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface CircuitBreaker {
    int threshold() default 3;

    int aliveTime() default 5000;

    int timeout() default 5000;

    String fallback() default "";
}