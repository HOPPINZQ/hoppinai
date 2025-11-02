package com.hoppinzq.service.exception.aop;

import com.hoppinzq.service.exception.AuthenticationFailedException;
import com.hoppinzq.service.exception.AuthorizationFailedException;
import com.hoppinzq.service.exception.RemotingException;
import com.hoppinzq.service.exception.interceptor.DefaultExceptionHandler;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author:ZhangQi 服务调用方只对rpc抛出的异常进行默认处理
 **/
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@ExceptionContainer({
        @ExceptionCatch(value = RemotingException.class, handler = DefaultExceptionHandler.class),
        @ExceptionCatch(value = AuthenticationFailedException.class, handler = DefaultExceptionHandler.class),
        @ExceptionCatch(value = AuthorizationFailedException.class, handler = DefaultExceptionHandler.class)
})
public @interface ExceptionCatchRemote {

}
