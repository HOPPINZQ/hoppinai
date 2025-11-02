package com.hoppinzq.service.annotation;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * 自定义Servlet注解，自己定义的Servlet处理类必须加这个注解，
 * 因为是动态注册Servlet处理类，一定要确保注册前被Spring容器扫描到
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface GatewayServlet {
    String prefix() default "/service/hoppinzq";

    String description() default "";
}
