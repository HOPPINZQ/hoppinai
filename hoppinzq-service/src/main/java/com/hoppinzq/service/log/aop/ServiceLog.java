package com.hoppinzq.service.log.aop;

import com.alibaba.fastjson.JSON;
import com.hoppinzq.service.constant.Constant;
import com.hoppinzq.service.handler.FileLogHandler;
import com.hoppinzq.service.handler.MessageEnum;
import com.hoppinzq.service.log.bean.LogContext;
import com.hoppinzq.service.log.bean.ServiceLogBean;
import com.hoppinzq.service.log.cache.LogCache;
import com.hoppinzq.service.message.MessageBean;
import com.hoppinzq.service.message.MessageBuffer;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.UndeclaredThrowableException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author:ZhangQi 日志aop
 **/
@Component
@Aspect
public class ServiceLog implements Ordered {
    private final static Logger LOGGER = LoggerFactory.getLogger(ServiceLog.class);

    @Override
    public int getOrder() {
        return 20;
    }

    @Pointcut("@annotation(com.hoppinzq.service.annotation.ServiceRegisterMethod)")
    public void requestServer() {
    }

    @Around("requestServer()")
    public Object doAround(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        ServiceLogBean log = (ServiceLogBean) LogContext.getPrincipal();
        long start = System.currentTimeMillis();
        log.setMethodStartTime(start);
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        log.setIp(request.getRemoteAddr());
        log.setMethod(String.format("%s.%s", proceedingJoinPoint.getSignature().getDeclaringTypeName(),
                proceedingJoinPoint.getSignature().getName()));
        log.setRequestParams(getRequestParamsByProceedingJoinPoint(proceedingJoinPoint).toString());
        Object result = proceedingJoinPoint.proceed();
        long endTime = System.currentTimeMillis();
        log.setEndTime(endTime);
        log.setTime(endTime - start);

        System.out.println("----------------日志开始-----------------------");
        if (log.isError()) {
            LOGGER.error("错误的请求:\n {}", log.toString());
        } else {
            LOGGER.info("请求信息:\n {}", log.toString());
        }
        MessageBuffer.put(new MessageBean("记录日志", log, MessageEnum.LOG, new FileLogHandler(Constant.LOG_PATH + log.getName() + ".txt")));
        System.out.println("----------------日志结束-----------------------");
        LogCache.logList.add(JSON.toJSONString(log));
        return result;
    }


    @AfterThrowing(pointcut = "requestServer()", throwing = "e")
    public void doAfterThrow(JoinPoint joinPoint, Exception e) throws InterruptedException {
        ServiceLogBean log = (ServiceLogBean) LogContext.getPrincipal();
        if (e instanceof InvocationTargetException) {
            InvocationTargetException ite = (InvocationTargetException) e;
            log.setException(ite.getTargetException().getMessage());
        } else if (e instanceof UndeclaredThrowableException) {
            UndeclaredThrowableException ute = (UndeclaredThrowableException) e;
            //ute.getCause()
            log.setException(ute.getUndeclaredThrowable().getMessage());
        } else {
            log.setException(e.getMessage());
        }
        log.setError(true);
        long endTime = System.currentTimeMillis();
        log.setEndTime(endTime);
        log.setTime(endTime - log.getMethodStartTime());
        System.out.println("----------------报错了，日志开始-----------------------");
        LOGGER.error("错误的请求:\n {}", log.toString());
        MessageBuffer.put(new MessageBean("记录日志", log, MessageEnum.LOG, new FileLogHandler(Constant.LOG_PATH + log.getName() + ".txt")));
        System.out.println("----------------报错了，日志结束-----------------------");
        LogCache.logList.add(JSON.toJSONString(log));
    }

    /**
     * 获取入参
     *
     * @param proceedingJoinPoint
     * @return
     */
    private Map<String, Object> getRequestParamsByProceedingJoinPoint(ProceedingJoinPoint proceedingJoinPoint) {
        //参数名
        String[] paramNames = ((MethodSignature) proceedingJoinPoint.getSignature()).getParameterNames();
        //参数值
        Object[] paramValues = proceedingJoinPoint.getArgs();

        return buildRequestParam(paramNames, paramValues);
    }

    private Map<String, Object> getRequestParamsByJoinPoint(JoinPoint joinPoint) {
        //参数名
        String[] paramNames = ((MethodSignature) joinPoint.getSignature()).getParameterNames();
        //参数值
        Object[] paramValues = joinPoint.getArgs();

        return buildRequestParam(paramNames, paramValues);
    }

    private Map<String, Object> buildRequestParam(String[] paramNames, Object[] paramValues) {
        Map<String, Object> requestParams = new HashMap<>();
        for (int i = 0; i < paramNames.length; i++) {
            Object value = paramValues[i];

            //如果是文件对象
            if (value instanceof MultipartFile) {
                MultipartFile file = (MultipartFile) value;
                value = file.getOriginalFilename();  //获取文件名
            }

            requestParams.put(paramNames[i], value);
        }

        return requestParams;
    }

}
