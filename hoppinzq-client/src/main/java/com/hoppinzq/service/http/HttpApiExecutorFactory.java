package com.hoppinzq.service.http;

import com.hoppinzq.service.http.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Proxy;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

/**
 * @author:ZhangQi httpapi代理工厂
 */
public class HttpApiExecutorFactory {
    private static final Logger logger = LoggerFactory.getLogger(HttpApiExecutorFactory.class);
    private static final Map<String, Future<Object>> serviceCache = new ConcurrentHashMap();

    public static <T> T create(Class<T> apiInterface) {
        return create(apiInterface, new HoppinHttpClient());
    }

    public static <T> T create(Class<T> apiInterface, HoppinHttpClient hoppinHttpClient) {
        String clazzSign = apiInterface.getName();

        Future<Object> f = serviceCache.get(clazzSign);
        if (f == null) {
            logger.debug("创建" + apiInterface.getName() + "服务的代理对象。");
            Callable<Object> eval = new Callable<Object>() {
                @Override
                public Object call() {
                    return Proxy.newProxyInstance(
                            apiInterface.getClassLoader(),
                            new Class[]{apiInterface},
                            (Object obj, Method method, Object[] args) -> {
                                if (method.isAnnotationPresent(HttpApi.class)) {
                                    HttpApi httpHeadersAnnotation = method.getAnnotation(HttpApi.class);
                                    String httpMethod = httpHeadersAnnotation.method();
                                    Map<String, String> headers = new HashMap<>();
                                    if (method.isAnnotationPresent(UsingRequest.class)) {
                                        if (RequestContextHolder.getRequestAttributes() != null) {
                                            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
                                            Enumeration<String> headerNames = request.getHeaderNames();
                                            while (headerNames.hasMoreElements()) {
                                                String key = headerNames.nextElement();
                                                headers.put(key, request.getHeader(key));
                                            }
                                        }
                                    }
                                    if (method.isAnnotationPresent(HttpHeaders.class) || method.isAnnotationPresent(HttpHeader.class)) {
                                        HttpHeader[] httpHeaders = method.getAnnotationsByType(HttpHeader.class);
                                        for (HttpHeader httpHeader : httpHeaders) {
                                            headers.put(httpHeader.key(), httpHeader.value());
                                        }
                                    }
                                    if (method.isAnnotationPresent(HttpCookie.class)) {
                                        HttpCookie httpCookie = method.getAnnotation(HttpCookie.class);
                                        headers.put("Cookie", httpCookie.value());
                                    }
                                    String url = httpHeadersAnnotation.url();
                                    String params = null;
                                    Parameter[] parameters = method.getParameters();
                                    for (int i = 0; i < parameters.length; i++) {
                                        Parameter parameter = parameters[i];
                                        HttpHeader httpHeader = parameter.getDeclaredAnnotation(HttpHeader.class);
                                        if (httpHeader != null) {
                                            if (args[i] instanceof String) {
                                                headers.put(httpHeader.key(), (String) args[i]);
                                            } else {
                                                logger.error("请求头: " + httpHeader.key() + " 设置失败，原因是请求头得设置到String类型的参数上，设置错误的参数名：" + parameter.getName());
                                            }
                                        }
                                        HttpCookie httpCookie = parameter.getDeclaredAnnotation(HttpCookie.class);
                                        if (httpCookie != null) {
                                            if (args[i] instanceof String) {
                                                headers.put("Cookie", (String) args[i]);
                                            } else {
                                                logger.error("cookie: " + httpHeader.key() + " 设置失败，原因是请求头得设置到String类型的参数上，设置错误的参数名：" + parameter.getName());
                                            }
                                        }
                                        RequestBody requestBody = parameter.getDeclaredAnnotation(RequestBody.class);
                                        if (!"GET".equalsIgnoreCase(httpMethod) && requestBody != null) {
                                            if (args[i] == null) {
                                                params = null;
                                            } else {
                                                params = (String) args[i];
                                            }
                                        }
                                    }
                                    ZQGatewayMethod zqGatewayMethod = method.getAnnotation(ZQGatewayMethod.class);
                                    if (zqGatewayMethod != null) {
                                        if (url.lastIndexOf("?") != -1) {
                                            url += "?method=" + zqGatewayMethod.value();
                                        }
                                    }
                                    url = ExpressionParser.replaceExpression(url, parameters, args);
                                    for (int i = 0; i < parameters.length; i++) {
                                        Parameter parameter = parameters[i];
                                        if (parameter.isAnnotationPresent(GetParam.class)) {
                                            url += (url.lastIndexOf("?") == -1 ? "?" : "&") + parameter.getName() + "=" + args[i];
                                        }
                                    }
                                    ResponseBody responseBody = method.getAnnotation(ResponseBody.class);
                                    String response = null;
                                    if (responseBody != null) {
                                        response = hoppinHttpClient.sendRequest(url, httpMethod, headers, params, true);
                                    } else {
                                        response = hoppinHttpClient.sendRequest(url, httpMethod, headers, params);
                                    }
                                    if (method.isAnnotationPresent(UsingResponse.class)) {
                                        if (RequestContextHolder.getRequestAttributes() != null) {
                                            HttpServletResponse response1 = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
                                            Map<String, List<String>> responseHeader = hoppinHttpClient.getResponseHeader();
                                            for (String key : responseHeader.keySet()) {
                                                response1.setHeader(key, responseHeader.get(key).toString());
                                            }
                                        }
                                    }
                                    return response;
                                }
                                return null;
                            }
                    );
                }
            };
            FutureTask<Object> ft = new FutureTask(eval);
            f = ft;
            serviceCache.putIfAbsent(clazzSign, ft);
            ft.run();
        } else {
            logger.debug("命中" + apiInterface.getName() + "服务代理对象的缓存！");
        }
        try {
            return (T) f.get();
        } catch (Exception ex) {
            serviceCache.remove(clazzSign);
            ex.printStackTrace();
        }
        return null;
    }
}