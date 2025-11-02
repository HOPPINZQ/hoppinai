package com.hoppinzq.service.proxy;

import com.hoppinzq.service.common.NoUser;
import com.hoppinzq.service.handler.MethodInvocationHandler;
import com.hoppinzq.service.manager.BeanManager;
import com.hoppinzq.service.manager.DefaultBeanManager;
import com.hoppinzq.service.utils.RpcTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

/**
 * @author:ZhangQi 为客户端创建服务代理的工厂，需提供要访问的接口类和公开服务地址并将结果强制转换到接口类
 * 有些服务需要提供凭证来进行身份验证和鉴权
 */
public class ServiceProxyFactory {
    //使用ConcurrentHashMap容器作为缓存容器
    private static final Map<String, Future<Object>> serviceCache = new ConcurrentHashMap();
    private static final Logger logger = LoggerFactory.getLogger(ServiceProxyFactory.class);
    public static int streamBufferSize = 16384;
    private static final BeanManager beanManager = new DefaultBeanManager();

    /**
     * 不需要验证调用服务
     *
     * @param serviceInterface 要实现的服务接口
     * @param serviceURI       远程服务的URI
     * @return 给定服务接口的服务代理
     */
    public static <T> T createProxy(Class<? extends T> serviceInterface, String serviceURI) {
        return createProxy(serviceInterface, serviceURI, new NoUser(), null, null);
    }

    public static <T> T createProxy(Class<? extends T> serviceInterface, String serviceURI, Map headers) {
        for (Object key : headers.keySet()) {
            RpcTemplate.setRequestHeader(key.toString(), String.valueOf(headers.get(key.toString())));
        }
        return createProxy(serviceInterface, serviceURI, new NoUser(), headers, null);
    }

    /**
     * 不加缓存的创建代理对象，在极少数情况下，某服务的url或者用户鉴权可能会改变，由于缓存是根据服务接口名去缓存的
     * 因而不会因为传入的url或者用户信息的不同而更新缓存内容
     *
     * @param serviceInterface
     * @param serviceURI
     * @param credentials
     * @param <T>
     * @return
     */
    public static <T> T createProxyWithoutCache(Class<? extends T> serviceInterface, String serviceURI, Serializable credentials) {
        return (T) Proxy.newProxyInstance(serviceInterface.getClassLoader(), new Class[]{serviceInterface}, new MethodInvocationHandler(serviceInterface, serviceURI, credentials));
    }

    public static <T> T createProxy(Class<? extends T> serviceInterface, Serializable credentials, String serviceName) {
        return createProxy(serviceInterface, null, credentials, null, serviceName);
    }

    public static <T> T createProxy(Class<? extends T> serviceInterface, Serializable credentials) {
        return createProxy(serviceInterface, null, credentials, null);
    }

    public static <T> T createProxy(Class<? extends T> serviceInterface, String serviceURI, Serializable credentials) {
        return createProxy(serviceInterface, serviceURI, credentials, null, null);
    }

    public static <T> T createProxy(Class<? extends T> serviceInterface, String serviceURI, Serializable credentials, String serviceName) {
        return createProxy(serviceInterface, serviceURI, credentials, null, serviceName);
    }

    /**
     * 缓存代理服务接口
     *
     * @param serviceInterface 要实现的服务接口
     * @param serviceURI       远程服务的URI
     * @param credentials      用于身份验证的凭据
     * @return
     * @headers headers 自定义请求头
     */
    public static <T> T createProxy(Class<? extends T> serviceInterface, String serviceURI, Serializable credentials, Map headers, String serviceName) {
        Future<Object> f = serviceCache.get(serviceInterface.getName());
        if (f == null) {
            logger.debug("创建" + serviceInterface.getName() + "服务的代理对象，服务地址是:" + serviceURI);
            Callable<Object> eval = new Callable<Object>() {
                @Override
                public Object call() {
                    return Proxy.newProxyInstance(serviceInterface.getClassLoader(), new Class[]{serviceInterface}, new MethodInvocationHandler(serviceInterface, serviceURI, credentials, serviceName));
                }
            };
            FutureTask<Object> ft = new FutureTask(eval);
            f = ft;
            serviceCache.putIfAbsent(serviceInterface.getName(), ft);
            ft.run();
        } else {
            logger.debug("命中" + serviceInterface.getName() + "服务代理对象的缓存！，服务地址是:" + serviceURI);
        }
        try {
            return (T) f.get();
        } catch (Exception ex) {
            serviceCache.remove(serviceInterface.getName());
            ex.printStackTrace();
        }
        return null;
    }

    /**
     * @param serviceInterface        要实现的服务接口
     * @param methodInvocationHandler 已实例化的MethodInvocationHandler
     * @return
     */
    public static <T> T createProxy(Class<? extends T> serviceInterface, MethodInvocationHandler methodInvocationHandler) {
        return (T) Proxy.newProxyInstance(serviceInterface.getClassLoader(), new Class[]{serviceInterface}, methodInvocationHandler);
    }

    /**
     * 为服务代理设置凭证
     *
     * @param proxy       使用createProxy()创建的代理
     * @param credentials 可序列化凭据对象
     */
    public static void setCredentials(Object proxy, Serializable credentials) {
        MethodInvocationHandler methodInvocationHandler = (MethodInvocationHandler) Proxy.getInvocationHandler(proxy);
        methodInvocationHandler.setCredentials(credentials);
    }

    /**
     * 为服务代理设置远程服务的URI
     *
     * @param proxy      使用createProxy()创建的代理
     * @param serviceURI 远程服务的URI
     */
    public static void setServiceURI(Object proxy, String serviceURI) {
        MethodInvocationHandler methodInvocationHandler = (MethodInvocationHandler) Proxy.getInvocationHandler(proxy);
        methodInvocationHandler.setServiceURI(serviceURI);
    }
}