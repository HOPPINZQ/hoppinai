package com.hoppinzq.service.http.annotation;

import java.lang.annotation.*;

/**
 * @author:ZhangQi 球球zq网关api的url的注解
 */
@Target({ElementType.METHOD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@ZQGatewayAnnotation
public @interface ZQGatewayMethod {
    String value() default "";
}
