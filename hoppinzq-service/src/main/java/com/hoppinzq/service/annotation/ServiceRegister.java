package com.hoppinzq.service.annotation;


import com.hoppinzq.service.auth.AuthenticationNotCheckAuthorizer;
import com.hoppinzq.service.auth.AuthenticationProvider;
import com.hoppinzq.service.auth.AuthorizationProvider;
import com.hoppinzq.service.auth.SimpleUserCheckAuthenticator;
import com.hoppinzq.service.modification.ModificationManager;
import com.hoppinzq.service.modification.NotModificationManager;
import com.hoppinzq.service.serializer.CustomSerializer;
import com.hoppinzq.service.serializer.HessionSerializer;
import org.springframework.core.annotation.AliasFor;
import org.springframework.stereotype.Service;

import java.lang.annotation.*;

/**
 * 自定义服务注册注解，需要在要注册的服务实现类加上该注解，那么服务就会自动注册到注册中心
 *
 * @author:ZhangQi
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Service
public @interface ServiceRegister {
    @AliasFor(
            annotation = Service.class
    )
    String value() default "";

    String title() default "";

    String description() default "";

    int timeout() default 5000;

    //全局身份验证方式
    Class<? extends AuthenticationProvider> authentication() default SimpleUserCheckAuthenticator.class;

    //全局权限验证方式
    Class<? extends AuthorizationProvider> authorization() default AuthenticationNotCheckAuthorizer.class;

    //全局服务跟踪参数方式
    Class<? extends ModificationManager> modification() default NotModificationManager.class;

    //服务调用所使用的序列化方式
    Class<? extends CustomSerializer> serializer() default HessionSerializer.class;

    RegisterType registerType() default RegisterType.AUTO;

    enum RegisterType {
        /**
         * 项目启动自动注册
         */
        AUTO,
        /**
         * 不会随着项目启动注册，声明服务需要手动注册。
         * 服务仍然存储到一个集合内，但是没有注册，你可以在合适的时机将其手动注册（通过注册中心的RegisterServer内方法）
         */
        NOT_AUTO
    }

}

