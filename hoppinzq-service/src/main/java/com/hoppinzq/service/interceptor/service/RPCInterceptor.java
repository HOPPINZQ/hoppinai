package com.hoppinzq.service.interceptor.service;


import com.hoppinzq.service.bean.OrderServiceClazz;
import com.hoppinzq.service.bean.RPCBean;
import com.hoppinzq.service.bean.ServiceWrapper;
import com.hoppinzq.service.cache.ServiceStore;
import com.hoppinzq.service.common.HoppinInvocationRequest;
import com.hoppinzq.service.common.HoppinInvocationResponse;
import com.hoppinzq.service.log.bean.ServiceLogBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author:ZhangQi 拦截器处理类
 */
public class RPCInterceptor {
    private static Logger logger = LoggerFactory.getLogger(RPCInterceptor.class);

    public static void beforeAuthentication(HoppinInvocationRequest request, ServiceWrapper serviceWrapper) {
        List<OrderServiceClazz> orderServiceClazzes = ServiceStore.orderServiceClazzes.stream()
                .filter((item -> {
                    return item.getType() == OrderServiceClazz.OrderServiceTypeEnum.INTERCEPTOR.getState();
                }))
                .sorted(Comparator.comparing(OrderServiceClazz::getOrder))
                .collect(Collectors.toList());
        for (OrderServiceClazz orderServiceClazz : orderServiceClazzes) {
            try {
                RPCInterceptorInterface rpcInterceptorInterface = ServiceStore.applicationContext
                        .getBean(orderServiceClazz.getClassName(), RPCInterceptorInterface.class);
                rpcInterceptorInterface.beforeAuthentication(request, serviceWrapper);
            } catch (Exception ex) {
                logger.error("拦截器：" + orderServiceClazz.getClazz().getSimpleName() + "，执行失败");
                throw ex;
            }
        }
    }

    public static void beforeMethod(HoppinInvocationRequest request, ServiceWrapper serviceWrapper, RPCBean rpcBean) {
        List<OrderServiceClazz> orderServiceClazzes = ServiceStore.orderServiceClazzes.stream()
                .filter((item -> {
                    return item.getType() == 1;
                }))
                .sorted(Comparator.comparing(OrderServiceClazz::getOrder))
                .collect(Collectors.toList());
        for (OrderServiceClazz orderServiceClazz : orderServiceClazzes) {
            try {
                RPCInterceptorInterface rpcInterceptorInterface = ServiceStore.applicationContext
                        .getBean(orderServiceClazz.getClassName(), RPCInterceptorInterface.class);
                rpcInterceptorInterface.beforeMethod(request, serviceWrapper, rpcBean);
            } catch (Exception ex) {
                logger.error("拦截器：" + orderServiceClazz.getClazz().getSimpleName() + "，执行失败");
                throw ex;
            }
        }
    }

    public static void after(HoppinInvocationResponse response, ServiceWrapper serviceWrapper, RPCBean rpcBean) {
        List<OrderServiceClazz> orderServiceClazzes = ServiceStore.orderServiceClazzes.stream()
                .filter((item -> {
                    return item.getType() == 1;
                }))
                .sorted(Comparator.comparing(OrderServiceClazz::getOrder))
                .collect(Collectors.toList());
        for (OrderServiceClazz orderServiceClazz : orderServiceClazzes) {
            try {
                RPCInterceptorInterface rpcInterceptorInterface = ServiceStore.applicationContext
                        .getBean(orderServiceClazz.getClassName(), RPCInterceptorInterface.class);
                rpcInterceptorInterface.after(response, serviceWrapper, rpcBean);
            } catch (Exception ex) {
                logger.error("拦截器：" + orderServiceClazz.getClazz().getSimpleName() + "，执行失败");
                throw ex;
            }
        }
    }

    public static void exception(HoppinInvocationResponse response, ServiceWrapper serviceWrapper, RPCBean rpcBean, Exception exception) {
        List<OrderServiceClazz> orderServiceClazzes = ServiceStore.orderServiceClazzes.stream()
                .filter((item -> {
                    return item.getType() == 1;
                }))
                .sorted(Comparator.comparing(OrderServiceClazz::getOrder))
                .collect(Collectors.toList());
        for (OrderServiceClazz orderServiceClazz : orderServiceClazzes) {
            try {
                RPCInterceptorInterface rpcInterceptorInterface = ServiceStore.applicationContext
                        .getBean(orderServiceClazz.getClassName(), RPCInterceptorInterface.class);
                rpcInterceptorInterface.exception(response, serviceWrapper, rpcBean, exception);
            } catch (Exception ex) {
                logger.error("拦截器：" + orderServiceClazz.getClazz().getSimpleName() + "，执行失败");
                throw ex;
            }
        }
    }

    public static void end(HoppinInvocationRequest request, HoppinInvocationResponse response,
                           ServiceWrapper serviceWrapper, RPCBean rpcBean, ServiceLogBean logBean) {
        List<OrderServiceClazz> orderServiceClazzes = ServiceStore.orderServiceClazzes.stream()
                .filter((item -> {
                    return item.getType() == 1;
                }))
                .sorted(Comparator.comparing(OrderServiceClazz::getOrder))
                .collect(Collectors.toList());
        for (OrderServiceClazz orderServiceClazz : orderServiceClazzes) {
            try {
                RPCInterceptorInterface rpcInterceptorInterface = ServiceStore.applicationContext
                        .getBean(orderServiceClazz.getClassName(), RPCInterceptorInterface.class);
                rpcInterceptorInterface.end(request, response, serviceWrapper, rpcBean, logBean);
            } catch (Exception ex) {
                logger.error("拦截器：" + orderServiceClazz.getClazz().getSimpleName() + "，执行失败");
                throw ex;
            }
        }
    }
}

