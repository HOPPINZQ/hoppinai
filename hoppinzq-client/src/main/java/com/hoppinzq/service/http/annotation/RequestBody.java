package com.hoppinzq.service.http.annotation;

import java.lang.annotation.*;

/**
 * @author:ZhangQi zq网关定制注解，将参数声明为zq网关风格的传参
 */
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@ZQGatewayAnnotation
public @interface RequestBody {
}
