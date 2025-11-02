package com.hoppinzq.service.annotation.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

/**
 * @author:ZhangQi 防止内存泄漏
 **/
@Aspect
@Component
public class ServiceCleanupAspect {

    @Pointcut("@within(com.hoppinzq.service.annotation.ServiceRegister)")
    public void serviceClass() {
    }

    @Pointcut("@annotation(com.hoppinzq.service.annotation.ServiceRegisterMethod)")
    public void serviceMethod() {
    }

    @After("serviceClass() || serviceMethod()")
    public void afterServiceMethod(JoinPoint joinPoint) {
        //RPCContext.exit();
    }
}