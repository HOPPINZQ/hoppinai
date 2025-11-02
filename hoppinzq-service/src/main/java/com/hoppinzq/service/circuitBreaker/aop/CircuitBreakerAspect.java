package com.hoppinzq.service.circuitBreaker.aop;

import com.hoppinzq.service.circuitBreaker.bean.CircuitBreakerSetting;
import com.hoppinzq.service.circuitBreaker.cache.CircuitBreakerCache;
import com.hoppinzq.service.circuitBreaker.cache.CircuitBreakerStore;
import com.hoppinzq.service.log.bean.LogContext;
import com.hoppinzq.service.log.bean.ServiceLogBean;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeoutException;

import static com.hoppinzq.service.circuitBreaker.cache.CircuitBreakerCache.getState;

/**
 * 熔断器注解处理
 */
@Aspect
public class CircuitBreakerAspect {

    @Around("@annotation(com.hoppinzq.service.circuitBreaker.aop.CircuitBreaker)")
    public Object circuitBreaker(ProceedingJoinPoint joinPoint) throws Throwable {
        ServiceLogBean log = (ServiceLogBean) LogContext.getPrincipal();
        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        List<CircuitBreakerSetting> circuitBreakerSettings = CircuitBreakerStore.circuitBreakerSettings;
        CircuitBreaker breaker = method.getAnnotation(CircuitBreaker.class);
        int timeout = breaker.timeout();
        int aliveTime = breaker.aliveTime();
        int threshold = breaker.threshold();
        String fallback = breaker.fallback();
        String methodName = method.getName();
        CircuitBreakerCache.CircuitBreakerState state = getState(methodName);
        //配置重写注解配置项
        for (CircuitBreakerSetting setting : circuitBreakerSettings) {
            if (method.equals(setting.getMethod())) {
                timeout = setting.getTimeout();
                aliveTime = setting.getAliveTime();
                fallback = setting.getFallback();
                threshold = setting.getThreshold();
                state.setSetting(setting);
                break;
            }
        }

        if (state.isCircuitOpen()) {
            long currentTime = System.currentTimeMillis();
            long duringTime = currentTime - state.getLastInvocationTime();
            if (duringTime > state.getMaxOpenTime()) {
                state.setMaxOpenTime(duringTime);
            }
            if (duringTime > aliveTime) {
                // 超时后重置服务状态，这个超时不是服务调用超时，而是将熔断器状态改为半开,
                // 此时会允许一部分请求通过。已测试实际服务是否恢复正常的一段时间
                state.setCircuitOpen(false);
                state.setLastCloseTime(currentTime);
                state.setCircuitHalfOpen(true);
                state.setHalfOpenCount(state.getHalfOpenCount() + 1);
            } else {
                log.setLogType(1);
                log.setError(true);
                state.setCircuitHalfOpen(false);
                state.setFailureFuncCount(state.getFailureFuncCount() + 1);
                if (!fallback.isEmpty()) {
                    log.setException("服务出现异常，触发熔断");
                    return invokeFallbackMethod(joinPoint.getTarget(), fallback);
                } else {
                    log.setException("服务出现异常，已经熔断但么有定义自定义方法，请联系服务提供者");
                    throw new RuntimeException("服务出现异常，已经熔断但么有定义自定义方法，请联系服务提供者");
                }
            }
        }
        //熔断器没有开启，则正常走服务
        try {
            //todo 线程池用这个吗？
            ExecutorService executor = Executors.newCachedThreadPool();
            Future future = executor.submit(() -> {
                try {
                    Object result = joinPoint.proceed();
                    state.setSuccessCount(state.getSuccessCount() + 1);
                    return result;
                } catch (Throwable throwable) {
                    if (state.getSuccessCount() > 0) {
                        state.setSuccessCount(state.getSuccessCount() - 1);
                    }
                    throw new RuntimeException(throwable);//todo
                }
            });
            long t = System.currentTimeMillis();
            while (true) {
                Thread.sleep(100);
                if (System.currentTimeMillis() - t >= timeout) {
                    if (state.getSuccessCount() > 0) {
                        state.setSuccessCount(state.getSuccessCount() - 1);
                    }
                    throw new TimeoutException("调用超时,响应时间超过调用该服务配置的超时时长:" + timeout + "ms,将服务降级熔断。");
                }
                if (future.isDone()) {
                    return future.get();
                }
            }
        } catch (Exception ex) {
            log.setLogType(1);
            log.setError(true);
            log.setException(ex.getMessage());
            //若服务抛出异常，先计算失败次数，若失败次数超出阈值，开启熔断器
            //若服务超时，直接开启熔断器java-1.8.0-openjdk*
            state.setFailureCount(state.getFailureCount() + 1);
            if (state.getFailureCount() >= threshold || ex instanceof TimeoutException) {
                state.setCircuitOpen(true);
                state.setOpenCount(state.getOpenCount() + 1);
                state.setCircuitHalfOpen(false);
                state.setLastInvocationTime(System.currentTimeMillis());
                if (!fallback.isEmpty()) {
                    return invokeFallbackMethod(joinPoint.getTarget(), fallback);
                } else {
                    throw new RuntimeException("服务出现异常，已经熔断但没有定义自定义方法，请联系服务提供者");
                }
            }
            throw ex;
        }
    }

    private Object invokeFallbackMethod(Object target, String methodName) throws Exception {
        Method method = target.getClass().getMethod(methodName);
        return method.invoke(target);
    }

}