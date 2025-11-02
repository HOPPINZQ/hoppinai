package com.hoppinzq.service.cache;

import com.hoppinzq.service.bean.OrderServiceClazz;
import com.hoppinzq.service.bean.ServiceWrapper;
import com.hoppinzq.service.filter.RPCFilterChain;
import org.springframework.context.ApplicationContext;

import java.util.*;

/**
 * @author:ZhangQi 注册中心服务存放
 **/
public class ServiceStore {
    //服务缓存
    public static List<ServiceWrapper> serviceWrapperList = Collections.synchronizedList(new ArrayList<ServiceWrapper>());
    //心跳服务缓存，仅注册中心调用
    public static List<ServiceWrapper> heartbeatService = new ArrayList<>();
    public static Map heartbeatHeader = new HashMap(1);

    //服务ID
    public static String serviceId;
    //解析的springIOC容器中的类
    public static List<Class<?>> allSpringBeans = new ArrayList<>();

    public static ApplicationContext applicationContext;
    //
    public static List<OrderServiceClazz> orderServiceClazzes = new ArrayList<>();

    public static RPCFilterChain chain = new RPCFilterChain();

}
