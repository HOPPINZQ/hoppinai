package com.hoppinzq.service.annotation;

import java.lang.annotation.*;

/**
 * 自定义注解,注解加上该注解，即可在网关注册方法的时候获取自定义参数，以在网关处理类中获取一些值
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CustomMapping {

}
