package com.hoppinzq.service.annotation;

import com.hoppinzq.service.config.RegisterCoreConfig;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 注册中心开关注解
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(RegisterCoreConfig.class)
public @interface EnableHoppinCore {
}
