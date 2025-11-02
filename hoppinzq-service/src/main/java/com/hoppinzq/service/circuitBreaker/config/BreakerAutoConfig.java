package com.hoppinzq.service.circuitBreaker.config;

import com.hoppinzq.service.annotation.ServiceRegister;
import com.hoppinzq.service.cache.ServiceStore;
import com.hoppinzq.service.circuitBreaker.aop.CircuitBreaker;
import com.hoppinzq.service.circuitBreaker.aop.CircuitBreakerAspect;
import com.hoppinzq.service.circuitBreaker.bean.CircuitBreakerSetting;
import com.hoppinzq.service.circuitBreaker.cache.CircuitBreakerStore;
import com.hoppinzq.service.util.AopTargetUtil;
import com.hoppinzq.service.util.UUIDUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.Advised;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

import java.lang.reflect.Method;

@ConditionalOnWebApplication
public class BreakerAutoConfig {
    private static final String PARAMS_ASPECT = "circuitBreakerAspect";
    public static boolean isBreaker = false;
    private static Logger logger = LoggerFactory.getLogger(BreakerAutoConfig.class);
    @Autowired
    private ApplicationContext applicationContext;

    @Bean
    public CircuitBreakerAspect circuitBreakerAspect() {
        logger.debug("熔断器开启成功！");
        isBreaker = true;
        if (ServiceStore.allSpringBeans.size() == 0) {
            String[] classNames = applicationContext.getBeanDefinitionNames();
            Class<?> type;
            for (String className : classNames) {
                if ("circuitBreakerAspect".equals(className)) {
                    continue;
                }
                Object bean = applicationContext.getBean(className);
                if (bean == this) {
                    continue;
                }
                if (bean instanceof Advised) {
                    try {
                        bean = AopTargetUtil.getTarget(bean);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    type = bean.getClass();
                } else {
                    type = applicationContext.getType(className);
                }
                ServiceStore.allSpringBeans.add(type);
                //servlet处理类和映射解析
                ServiceRegister serviceRegister = type.getAnnotation(ServiceRegister.class);
                if (serviceRegister != null) {
                    for (Method m : type.getDeclaredMethods()) {
                        CircuitBreakerSetting setting = new CircuitBreakerSetting();
                        CircuitBreaker circuitBreaker = m.getAnnotation(CircuitBreaker.class);
                        if (circuitBreaker != null) {
                            setting.setUuid(UUIDUtil.getUUID());
                            setting.setThreshold(circuitBreaker.threshold());
                            setting.setFallback(circuitBreaker.fallback());
                            setting.setAliveTime(circuitBreaker.aliveTime());
                            setting.setTimeout(circuitBreaker.timeout());
                            //setting.setClazz(type);
                            setting.setMethod(m);
                            setting.setClassName(type.getName());
                            setting.setMethodName(m.getName());
                            CircuitBreakerStore.circuitBreakerSettings.add(setting);
                        }
                    }
                }
            }
        } else {
            for (Class<?> type : ServiceStore.allSpringBeans) {
                ServiceRegister serviceRegister = type.getAnnotation(ServiceRegister.class);
                if (serviceRegister != null) {
                    for (Method m : type.getDeclaredMethods()) {
                        CircuitBreakerSetting setting = new CircuitBreakerSetting();
                        CircuitBreaker circuitBreaker = m.getAnnotation(CircuitBreaker.class);
                        if (circuitBreaker != null) {
                            setting.setUuid(UUIDUtil.getUUID());
                            setting.setThreshold(circuitBreaker.threshold());
                            setting.setFallback(circuitBreaker.fallback());
                            setting.setAliveTime(circuitBreaker.aliveTime());
                            setting.setTimeout(circuitBreaker.timeout());
                            //setting.setClazz(type);
                            setting.setMethod(m);
                            setting.setClassName(type.getName());
                            setting.setMethodName(m.getName());
                            CircuitBreakerStore.circuitBreakerSettings.add(setting);
                        }
                    }
                }
            }
        }
        return new CircuitBreakerAspect();
    }
}