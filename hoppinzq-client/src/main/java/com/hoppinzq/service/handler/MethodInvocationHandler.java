package com.hoppinzq.service.handler;

import com.hoppinzq.service.bean.RPCBean;
import com.hoppinzq.service.common.HoppinInputStreamArgument;
import com.hoppinzq.service.common.HoppinInvocationRequest;
import com.hoppinzq.service.common.HoppinInvocationResponse;
import com.hoppinzq.service.common.ModificationList;
import com.hoppinzq.service.exception.RemotingException;
import com.hoppinzq.service.exception.aop.ExceptionCatch;
import com.hoppinzq.service.exception.aop.ExceptionCatchRemote;
import com.hoppinzq.service.exception.aop.ExceptionContainer;
import com.hoppinzq.service.http.annotation.HttpHeader;
import com.hoppinzq.service.http.annotation.HttpHeaders;
import com.hoppinzq.service.serializer.HessionSerializer;
import com.hoppinzq.service.transport.HoppinRPCProvider;
import com.hoppinzq.service.transport.HoppinRPCSession;
import com.hoppinzq.service.url.HttpURLConnectionHoppinRPCProvider;
import com.hoppinzq.service.utils.RpcTemplate;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * @author:ZhangQi 该方法实现提供的服务的接口，获取并发起方法调用，并返回调用结果
 */
public class MethodInvocationHandler implements InvocationHandler, Serializable {
    private static final String REGEXP_PROPERTY_DELIMITER = "\\.";
    private static final HoppinRPCProvider defaultHoppinRPCProvider = new HttpURLConnectionHoppinRPCProvider();
    private Class<?> interfaceService;
    private String interfaceServiceName;
    private String serviceURI;
    //服务模块名称，用于负载均衡
    private String serviceName;
    private Serializable credentials;
    private HoppinRPCProvider hoppinRPCProvider;

    public MethodInvocationHandler() {
    }

    /**
     * @param serviceURI        服务URI
     * @param credentials       凭证，用于用户校验和鉴权
     * @param hoppinRPCProvider 服务提供者
     */
    public MethodInvocationHandler(Class<?> interfaceService, String serviceURI, Serializable credentials, String serviceName, HoppinRPCProvider hoppinRPCProvider) {
        this.interfaceService = interfaceService;
        this.interfaceServiceName = interfaceService.getSimpleName();
        this.serviceURI = serviceURI;
        this.credentials = credentials;
        this.serviceName = serviceName;
        this.hoppinRPCProvider = hoppinRPCProvider;
    }

    public MethodInvocationHandler(Class<?> serviceInterface, String serviceURI, Serializable credentials, String serviceName) {
        this(serviceInterface, serviceURI, credentials, serviceName, null);
    }

    /**
     * @param serviceURI
     * @param credentials
     */
    public MethodInvocationHandler(Class<?> serviceInterface, String serviceURI, Serializable credentials) {
        this(serviceInterface, serviceURI, credentials, null, null);
    }

    /**
     * 在给定URI上创建服务代理
     *
     * @param serviceURI
     */
    public MethodInvocationHandler(Class<?> serviceInterface, String serviceURI) {
        this(serviceInterface, serviceURI, null);
    }

    /**
     * 开启负载均衡
     *
     * @param serviceName
     */
    public void openLoadBalance(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getServiceName() {
        return serviceName;
    }

    public String getInterfaceServiceName() {
        return interfaceServiceName;
    }

    public void setInterfaceServiceName(String interfaceServiceName) {
        this.interfaceServiceName = interfaceServiceName;
    }

    /**
     * 拦截对代理的方法调用，并通过HTTP发送调用
     * 如果在服务器端抛出异常，它将被重新抛出给调用方。
     * 返回方法调用的返回值。
     *
     * @param obj
     * @param method
     * @param args
     * @return
     * @throws Throwable
     */
    @Override
    public Object invoke(Object obj, Method method, Object[] args) throws Throwable {
        RPCBean rpcBean = RpcTemplate.getRpcBean();
        rpcBean.setProxy(obj);
        rpcBean.setMethod(method.getName());
        rpcBean.setArgs(args);
        Map requestHeader = RpcTemplate.getRequestRequest();
        if (method.isAnnotationPresent(HttpHeaders.class) || method.isAnnotationPresent(HttpHeader.class)) {
            HttpHeader[] httpHeaders = method.getAnnotationsByType(HttpHeader.class);
            for (HttpHeader httpHeader : httpHeaders) {
                requestHeader.put(httpHeader.key(), httpHeader.value());
            }
        }
        HoppinInvocationRequest request = null;
        HoppinInvocationResponse response = null;
        HoppinRPCProvider currentProvider = hoppinRPCProvider != null ? hoppinRPCProvider : defaultHoppinRPCProvider;
        HoppinRPCSession session = currentProvider.createSession(this);
        //负载均衡模块，需要动态改变serviceURI
        currentProvider.loadBalance(this);
        rpcBean.setSession(session);
        rpcBean.setProvider(currentProvider);
        try {
            request = new HoppinInvocationRequest(method, args, getCredentials());
            rpcBean.setHoppinInvocationRequest(request);
            request.setHeaders(requestHeader);
            // 查找作为输入流的第一个参数，从参数数组中删除参数数据
            // 并准备在序列化调用请求后通过ConnectionOutputStream传输数据。
            InputStream streamArgument = null;
            if (args != null) {
                for (int i = 0; i < args.length; i++) {
                    if (args[i] != null && InputStream.class.isAssignableFrom(args[i].getClass())) {
                        streamArgument = (InputStream) args[i];
                        args[i] = new HoppinInputStreamArgument();
                        break;
                    }
                }
            }

            InputStream inputStream = session.sendInvocationRequest(method, request, streamArgument);
            if (!method.getReturnType().equals(Object.class) && method.getReturnType().isAssignableFrom(InputStream.class)) {
                return inputStream;
            }
            //  反序列化开始  todo 先强制使用Hessian反序列化,因此注解配置的序列化方式将不生效
            HessionSerializer hessionSerializer = new HessionSerializer();
            response = hessionSerializer.deserialize(inputStream, HoppinInvocationResponse.class);
            //  反序列化结束
            response.setHeaders(rpcBean.getHeaders());
            rpcBean.setHoppinInvocationResponse(response);
            exceptionHandler(method, response);
            RpcTemplate.setResponse(response);
            applyModifications(args, response.getModifications());
        } catch (IOException e) {
            rpcBean.setException(e.getMessage());
            throw new RemotingException(e);
        } finally {
            currentProvider.endSession(session, this);
            RpcTemplate.remove();
        }
        if (response.getException() != null) {
            throw response.getException();
        }
        return response.getResult();
    }

    /**
     * 异常处理
     *
     * @param method
     * @param response
     */
    private void exceptionHandler(Method method, HoppinInvocationResponse response) throws InstantiationException, IllegalAccessException {
        if (method.isAnnotationPresent(ExceptionCatch.class) || method.isAnnotationPresent(ExceptionContainer.class)
                || method.isAnnotationPresent(ExceptionCatchRemote.class)) {
            ExceptionCatchRemote exceptionCatchRemote = method.getAnnotation(ExceptionCatchRemote.class);
            ExceptionCatch[] exceptionCatches = {};
            if (exceptionCatchRemote != null) {
                exceptionCatches = exceptionCatchRemote.annotationType().getAnnotationsByType(ExceptionCatch.class);
            } else {
                exceptionCatches = method.getAnnotationsByType(ExceptionCatch.class);
            }
            for (ExceptionCatch exceptionCatch : exceptionCatches) {
                if (response.getStatus() == exceptionCatch.code() &&
                        exceptionCatch.value().getSimpleName().equals(response.getException().getClass().getSimpleName())) {
                    if (response.getException() instanceof Exception) {
                        exceptionCatch.handler().newInstance().handleException((Exception) response.getException());
                    }

                }
            }
        }
    }

    private void applyModifications(Object[] args, ModificationList[] modifications) {
        if (modifications != null) {
            for (int i = 0; i < modifications.length; i++) {
                ModificationList mods = modifications[i];
                if (mods != null) {
                    for (Map.Entry<String, Object> entry : mods.getModifiedProperties().entrySet()) {
                        try {
                            setModifiedValue(entry.getKey(), entry.getValue(), args[i]);
                        } catch (Exception ignored) {
                        }
                    }
                }
            }
        }
    }

    private void setModifiedValue(String key, Object value, Object object) throws NoSuchFieldException, IllegalAccessException {
        String[] propertyGraph = key.split(REGEXP_PROPERTY_DELIMITER);
        int i = 0;
        for (; i < propertyGraph.length - 1; i++) {
            object = getValue(object, object.getClass().getDeclaredField(propertyGraph[i]));
        }
        setValue(object, object.getClass().getDeclaredField(propertyGraph[i]), value);
    }

    /**
     * 访问修饰符一定要还原
     *
     * @param object
     * @param field
     * @param value
     * @throws IllegalAccessException
     */
    private void setValue(Object object, Field field, Object value) throws IllegalAccessException {
        boolean accessible = field.isAccessible();
        if (!accessible) {
            field.setAccessible(true);
        }
        field.set(object, value);
        if (!accessible) {
            field.setAccessible(false);
        }
    }

    /**
     * 访问修饰符一定要还原
     *
     * @param object
     * @param field
     * @throws IllegalAccessException
     */
    private Object getValue(Object object, Field field) throws IllegalAccessException {
        boolean accessible = field.isAccessible();
        if (!accessible) {
            field.setAccessible(true);
        }
        Object value = field.get(object);
        if (!accessible) {
            field.setAccessible(false);
        }
        return value;
    }

    public String getServiceURI() {
        return serviceURI;
    }

    public void setServiceURI(String serviceURI) {
        this.serviceURI = serviceURI;
    }

    public Serializable getCredentials() {
        return credentials;
    }

    public void setCredentials(Serializable credentials) {
        this.credentials = credentials;
    }

}
