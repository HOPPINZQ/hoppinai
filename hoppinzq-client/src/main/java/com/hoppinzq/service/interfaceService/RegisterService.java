package com.hoppinzq.service.interfaceService;

import com.hoppinzq.service.bean.ServiceWrapperRPC;

import java.util.List;
import java.util.Map;

public interface RegisterService {
    String sayHello(String name);

    void insertService(ServiceWrapperRPC serviceWrapperRPC);

    void insertServices(List<ServiceWrapperRPC> serviceWrapperRPCS);

    int updateServices(List<ServiceWrapperRPC> serviceWrapperRPC);

    void deleteServices(List<ServiceWrapperRPC> serviceWrapperRPC);

    void deleteServices(String serviceId);

    void showdownServer(String serviceId);

    List<Map> queryServicesByServiceId(String serviceId);

    List<Map> queryServicesByServiceName(String serviceId);

    List<ServiceWrapperRPC> queryAllOuterServices();

    List<ServiceWrapperRPC> showCoreService();

    void setServiceVisible(String serviceId);

    void setServiceUnVisible(String serviceId);

    void setServiceAvailable(String serviceId);

    void setServiceUnavailable(String serviceId);
}
