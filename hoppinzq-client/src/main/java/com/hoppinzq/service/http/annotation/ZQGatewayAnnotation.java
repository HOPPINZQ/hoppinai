package com.hoppinzq.service.http.annotation;

import java.lang.annotation.*;

/**
 * @author:ZhangQi zq网关元注解，声明注解是zq网关定制注解
 */
@Target({ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ZQGatewayAnnotation {
}
