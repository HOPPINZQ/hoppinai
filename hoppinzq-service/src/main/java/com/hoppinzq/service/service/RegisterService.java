package com.hoppinzq.service.service;

import com.hoppinzq.service.bean.ServiceWrapperRPC;

import java.util.List;
import java.util.Map;

public interface RegisterService {
    /**
     * 用以测试注册中心是否可用，若返回值表示可用
     *
     * @param name
     * @return
     */
    String sayHello(String name);

    /**
     * 新增一个服务
     *
     * @param serviceWrapperRPC
     */
    void insertService(ServiceWrapperRPC serviceWrapperRPC);

    /**
     * 新增一批服务
     *
     * @param serviceWrapperRPCS
     */
    void insertServices(List<ServiceWrapperRPC> serviceWrapperRPCS);

    /**
     * 更新一批服务，同一个服务会被覆盖
     *
     * @param serviceWrapperRPC
     * @return
     */
    int updateServices(List<ServiceWrapperRPC> serviceWrapperRPC);

    /**
     * 删除一批服务
     *
     * @param serviceWrapperRPC
     */
    void deleteServices(List<ServiceWrapperRPC> serviceWrapperRPC);

    /**
     * 删除一个服务通过服务ID
     *
     * @param serviceId
     */
    void deleteServices(String serviceId);

    /**
     * 服务下线
     *
     * @param serviceId
     */
    void shutdownServer(String serviceId);

    /**
     * 查询服务根据服务ID
     *
     * @param serviceId
     * @return
     */
    List<Map> queryServicesByServiceId(String serviceId);

    /**
     * 根据服务模块名称删除服务
     *
     * @param serviceId
     * @return
     */
    List<Map> queryServicesByServiceName(String serviceId);

    /**
     * 查询所有的外部服务
     *
     * @return
     */
    List<ServiceWrapperRPC> queryAllOuterServices();

    /**
     * 查询注册中心内部服务
     *
     * @return
     */
    List<ServiceWrapperRPC> showCoreService();

    /**
     * 设置服务状态为可见
     *
     * @param serviceId
     */
    void setServiceVisible(String serviceId);

    /**
     * 设置服务状态为不可见
     *
     * @param serviceId
     */
    void setServiceUnVisible(String serviceId);

    /**
     * 设置服务状态为可用
     *
     * @param serviceId
     */
    void setServiceAvailable(String serviceId);

    /**
     * 设置服务状态为不可用
     *
     * @param serviceId
     */
    void setServiceUnavailable(String serviceId);
}
