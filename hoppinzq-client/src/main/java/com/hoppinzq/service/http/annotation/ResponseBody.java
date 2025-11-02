package com.hoppinzq.service.http.annotation;

import java.lang.annotation.*;

/**
 * @author:ZhangQi zq网关定制注解，格式化zq网关的响应值
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@ZQGatewayAnnotation
public @interface ResponseBody {
}
