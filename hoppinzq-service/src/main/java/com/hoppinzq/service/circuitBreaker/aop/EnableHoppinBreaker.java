package com.hoppinzq.service.circuitBreaker.aop;

import com.hoppinzq.service.circuitBreaker.config.BreakerAutoConfig;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 熔断器开关注解
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(BreakerAutoConfig.class)
public @interface EnableHoppinBreaker {
}