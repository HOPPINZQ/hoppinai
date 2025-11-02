package com.hoppinzq.service.annotation;

import com.hoppinzq.service.config.SpringHoppinzqCoreConfig;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * zqRPC开关注解
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(SpringHoppinzqCoreConfig.class)
public @interface EnableHoppin {
}
