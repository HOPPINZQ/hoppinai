package com.hoppinzq.service.bean;

import com.hoppinzq.service.util.StringUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author:ZhangQi 外部注册服务实体类
 **/
public class ServiceRegisterBean implements Serializable {
    private static final long serialVersionUID = 2783377098145240357L;

    private String serviceName;
    private String serviceFullName;
    private List<String> serviceInterfaceFullName;// 实现的接口
    private List<String> serviceInterfaceName;// 实现的接口

    private List<ServiceMethodBean> serviceMethodBeanList;
    private Boolean available = Boolean.TRUE;
    private Boolean visible = Boolean.TRUE;
    private Class service;

    public ServiceRegisterBean() {
    }

    public ServiceRegisterBean(Boolean visible) {
        this.visible = visible;
    }

    public ServiceRegisterBean(Boolean visible, Class service) {
        this.visible = visible;
        this.service = service;
    }

    public Class getService() {
        return service;
    }

    public void setService(Class service) {
        this.service = service;
    }

    public Boolean isAvailable() {
        return available;
    }

    public Boolean isVisible() {
        return visible;
    }

    public List<String> getServiceInterfaceName() {
        return serviceInterfaceName;
    }

    public void setServiceInterfaceName(List<String> serviceInterfaceName) {
        this.serviceInterfaceName = serviceInterfaceName;
    }

    public void setVisible(Boolean visible) {
        this.visible = visible;
    }

    public void setAvailable(Boolean available) {
        this.available = available;
    }

    public String getServiceFullName() {
        return serviceFullName;
    }

    public void setServiceFullName(String serviceFullName) {
        this.serviceFullName = serviceFullName;
    }

    public List<String> getServiceInterfaceFullName() {
        return serviceInterfaceFullName;
    }

    public void setServiceInterfaceFullName(List<String> serviceInterfaceFullName) {
        this.serviceInterfaceFullName = serviceInterfaceFullName;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public List<ServiceMethodBean> getServiceMethodBeanList() {
        return serviceMethodBeanList;
    }

    public void setServiceMethodBeanList(List<ServiceMethodBean> serviceMethodBeanList) {
        this.serviceMethodBeanList = serviceMethodBeanList;
    }

    public void setServiceClass(Class serviceClass) {
        this.service = serviceClass;
    }

    public Map toJSON() {
        Map serviceRegisterBeanMap = new HashMap();
        serviceRegisterBeanMap.put("serviceName", StringUtil.notNull(this.serviceName));
        serviceRegisterBeanMap.put("serviceFullName", StringUtil.notNull(this.serviceFullName));
        serviceRegisterBeanMap.put("serviceMethodBeanList", this.serviceMethodBeanList == null ? new ArrayList<>() : this.serviceMethodBeanList.stream().map(i -> i.toJSON()).collect(Collectors.toList()));
        serviceRegisterBeanMap.put("class", this.service == null ? "" : this.service.toString());
        serviceRegisterBeanMap.put("available", this.available);
        serviceRegisterBeanMap.put("availableValue", this.available ? "可用" : "不可用");
        serviceRegisterBeanMap.put("serviceInterfaceName", this.serviceInterfaceName);
        serviceRegisterBeanMap.put("serviceInterfaceFullName", this.serviceInterfaceFullName);

        return serviceRegisterBeanMap;
    }
}
