package com.hoppinzq.service.loadBalance;

import com.hoppinzq.service.bean.ServiceWrapperRPC;
import com.hoppinzq.service.bean.ZqServerConfig;
import com.hoppinzq.service.cache.OuterRPCServiceStore;
import com.hoppinzq.service.common.UserPrincipal;
import com.hoppinzq.service.interfaceService.RegisterService;
import com.hoppinzq.service.proxy.ServiceProxyFactory;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @author:ZhangQi 负载均衡缓存
 * 主要缓存注册中心所有可用的服务到本地内存，在内存里通过负载均衡算法获取可用负载均衡的服务
 **/
public class LoadBalanceCache {
    private final static String HTTP = "http://";
    public static Map<String, List<ServiceWrapperRPC>> loadBalanceMap = null;
    public static Map<String, Integer> indexMap = new ConcurrentHashMap<>();
    private final ZqServerConfig zqServerConfig;

    public LoadBalanceCache(ZqServerConfig zqServerConfig) {
        this.zqServerConfig = zqServerConfig;
        loadService();
    }

    public synchronized static void setService() {
        loadBalanceMap = new ConcurrentHashMap<>();
        for (ServiceWrapperRPC serviceWrapperRPC : OuterRPCServiceStore.coreServiceOuterServiceList) {
            String name = serviceWrapperRPC.getName();
            if (name != null) {
                if (loadBalanceMap.containsKey(name)) {
                    loadBalanceMap.get(name).add(serviceWrapperRPC);
                } else {
                    List<ServiceWrapperRPC> list = new ArrayList<>();
                    list.add(serviceWrapperRPC);
                    loadBalanceMap.put(name, list);
                }
            }
        }
    }

    public synchronized static String getNextService(String serviceName, String interfaceServiceName) {
        List<ServiceWrapperRPC> servers = loadBalanceMap.get(serviceName);
        if (servers == null || servers.size() == 0) {
            throw new RuntimeException("找不到可用的服务");
        }
        List<ServiceWrapperRPC> tempService = new ArrayList<>();
        for (ServiceWrapperRPC swr : servers) {
            if (swr.getAvailable()) {
                Map serviceRegisterBean = swr.getServiceRegisterBean();
                ArrayList<String> interfaceRegisterService = (ArrayList<String>) serviceRegisterBean.get("serviceInterfaceName");
                for (int i = 0; i < interfaceRegisterService.size(); i++) {
                    if (interfaceRegisterService.get(i).equals(interfaceServiceName)) {
                        tempService.add(swr);
                    }
                }
            }
        }
//        servers=servers.stream()
//                .filter(serviceWrapperRPC -> serviceWrapperRPC.getAvailable())
//                .filter(serviceWrapperRPC -> serviceName.equals(serviceWrapperRPC.getName()))
//                .collect(Collectors.toList());
        if (tempService.size() == 0) {
            throw new RuntimeException("找不到可用的服务");
        }
        int currentIndex = indexMap.getOrDefault(serviceName + ":" + interfaceServiceName, 0);
        int nextIndex = (currentIndex + 1) % tempService.size();
        indexMap.put(serviceName + ":" + interfaceServiceName, nextIndex);
        return HTTP + tempService.get(currentIndex).getServiceMessage().get("serviceIP")
                + ":" + tempService.get(currentIndex).getServiceMessage().get("servicePort")
                + tempService.get(currentIndex).getServiceMessage().get("servicePrefix");
    }

    public void loadService() {
        UserPrincipal upp = new UserPrincipal(zqServerConfig.getUserName(), zqServerConfig.getPassword());
        RegisterService registerService = ServiceProxyFactory.createProxy(RegisterService.class, zqServerConfig.getServerCenter(), upp);
        List<ServiceWrapperRPC> serviceWrapperRPCS = registerService.queryAllOuterServices();
        OuterRPCServiceStore.coreServiceOuterServiceList = serviceWrapperRPCS.stream()
                .sorted(Comparator.comparing(ServiceWrapperRPC::getName))
                .collect(Collectors.toList());
        setService();
    }
}
