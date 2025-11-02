package com.hoppinzq.service.loadBalance.aop;

import com.hoppinzq.service.loadBalance.LoadBalanceConfig;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @author:ZhangQi 负载均衡开关注解
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(LoadBalanceConfig.class)
public @interface EnableHoppinBalance {
}
