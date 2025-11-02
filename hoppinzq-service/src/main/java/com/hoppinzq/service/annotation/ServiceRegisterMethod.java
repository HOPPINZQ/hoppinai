package com.hoppinzq.service.annotation;

import java.lang.annotation.*;

/**
 * 自定义注解：rpc服务方法参数
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ServiceRegisterMethod {
    String title() default "";

    String description() default "";

    // 服务方法是否暴露
    boolean isShow() default true;

}
