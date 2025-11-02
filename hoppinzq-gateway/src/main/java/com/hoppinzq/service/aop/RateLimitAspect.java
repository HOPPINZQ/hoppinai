package com.hoppinzq.service.aop;

import cn.hutool.crypto.digest.MD5;
import com.alibaba.fastjson.JSON;
import com.hoppinzq.service.annotation.RateLimit;
import com.hoppinzq.service.bean.ApiResponse;
import com.hoppinzq.service.util.ServletUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static com.hoppinzq.service.bean.ErrorEnum.REQUEST_LIMIT;


@Aspect
@Component
public class RateLimitAspect {

    private static final Lock lock = new ReentrantLock(true);
    private final Map<String, Map<String, Long>> limitMap = new ConcurrentHashMap<>();

    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private HttpServletResponse response;

    private static String obtainMethodArgs(ProceedingJoinPoint joinPoint) {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        String[] argNames = methodSignature.getParameterNames();
        Object[] argValues = joinPoint.getArgs();
        Map<String, Object> args = new HashMap<>(argValues.length);
        for (int i = 0; i < argNames.length; i++) {
            String argName = argNames[i];
            Object argValue = argValues[i];
            args.put(argName, argValue);
        }
        return JSON.toJSONString(args);
    }

    @Around("@annotation(rateLimit)")
    public Object limit(ProceedingJoinPoint point, RateLimit rateLimit) throws Throwable {
        lock.lock();
        try {
            long timeout = rateLimit.timeout();
            String mark;
            if (rateLimit.limitType() == RateLimit.LimitType.LOGIN_USER) {
                mark = "未知用户";
//                if(getLoginUserId()!=null){
//                    mark = String.valueOf(getLoginUserId());
//                }else{
//                    mark = "未知用户";
//                }
            } else if (rateLimit.limitType() == RateLimit.LimitType.IP) {
                ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
                HttpServletRequest request = attributes.getRequest();
                mark = request.getRemoteAddr();
            } else if (rateLimit.limitType() == RateLimit.LimitType.CUSTOMER) {
                mark = rateLimit.custom_mark();
            } else {
                mark = "all";
            }

            MethodSignature signature = (MethodSignature) point.getSignature();
            String methodName = signature.getMethod().getName();
            String methodParam = obtainMethodArgs(point);
            if (timeout <= 0) {
                return point.proceed();
            }
            String key = MD5.create().digestHex(mark + methodName + methodParam);
            String s = stringRedisTemplate.opsForValue().get(key);
            if (s != null) {
                ServletUtils.writeJSON(response, ApiResponse.error(REQUEST_LIMIT));
            } else {
                stringRedisTemplate.opsForValue().set(key, "ok", timeout, TimeUnit.SECONDS);
            }
            return point.proceed();
        } catch (RedisConnectionFailureException redisConnectionFailureException) {
            return point.proceed();
        } catch (Throwable e) {
            ServletUtils.writeJSON(response, ApiResponse.error(REQUEST_LIMIT));
        } finally {
            lock.unlock();
        }
        return point.proceed();
    }

}
