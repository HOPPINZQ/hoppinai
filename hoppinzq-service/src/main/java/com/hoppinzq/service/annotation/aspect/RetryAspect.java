package com.hoppinzq.service.annotation.aspect;

import com.hoppinzq.service.annotation.Retry;
import com.hoppinzq.service.config.AbstractAbstractRetryRegisterService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * @author:ZhangQi 重试注解织入代码
 **/
@Aspect
@Component
public class RetryAspect {

    @Pointcut("@annotation(com.hoppinzq.service.annotation.Retry)")
    public void retryServiceRegister() {

    }

    @Around("retryServiceRegister()")
    public Object execute(ProceedingJoinPoint joinPoint) throws Exception {
        AbstractAbstractRetryRegisterService retryTemplate = new AbstractAbstractRetryRegisterService() {
            @Override
            protected Object toDo() throws Throwable {
                return joinPoint.proceed();
            }
        };
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        Retry retry = method.getAnnotation(Retry.class);
        retryTemplate.setRetryCount(retry.count()).setSleepTime(retry.sleep());
        return retryTemplate.execute();
    }

}

