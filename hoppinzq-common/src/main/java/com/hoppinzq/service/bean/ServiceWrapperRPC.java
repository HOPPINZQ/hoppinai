package com.hoppinzq.service.bean;

import com.hoppinzq.service.enums.ServiceTypeEnum;

import java.io.Serializable;
import java.util.Map;

/**
 * @author:ZhangQi 可被序列化的rpc服务包装类
 */
public class ServiceWrapperRPC implements Serializable {
    private static final long serialVersionUID = 123451234L;

    //服务ID
    private String id;
    //服务名
    private String name;
    private String date;
    //服务标题，描述
    private String title;
    private String description;
    //服务提供者
    private String username;
    private String password;
    //身份验证方式，授权方式。服务跟踪参数修改方式
    private String authenticationProvider;
    private String authorizationProvider;
    private String modificationManager;
    private String serializer;

    //服务状态，类型
    private Boolean visible = Boolean.TRUE;//服务默认可见
    private Boolean available = Boolean.TRUE;//服务默认可用
    private ServiceTypeEnum serviceTypeEnum = ServiceTypeEnum.NORMAL;

    private Map serviceMessage;
    private Map serviceRegisterBean;

    public ServiceWrapperRPC() {
    }

    public ServiceWrapperRPC(String id, String name, String date, String title, String description, Boolean visible, Boolean available, ServiceTypeEnum serviceTypeEnum, Map serviceMessage, Map serviceRegisterBean) {
        this.id = id;
        this.name = name;
        this.date = date;
        this.title = title;
        this.description = description;
        this.visible = visible;
        this.available = available;
        this.serviceTypeEnum = serviceTypeEnum;
        this.serviceMessage = serviceMessage;
        this.serviceRegisterBean = serviceRegisterBean;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAuthenticationProvider() {
        return authenticationProvider;
    }

    public void setAuthenticationProvider(String authenticationProvider) {
        this.authenticationProvider = authenticationProvider;
    }

    public String getAuthorizationProvider() {
        return authorizationProvider;
    }

    public void setAuthorizationProvider(String authorizationProvider) {
        this.authorizationProvider = authorizationProvider;
    }

    public String getModificationManager() {
        return modificationManager;
    }

    public void setModificationManager(String modificationManager) {
        this.modificationManager = modificationManager;
    }

    public Boolean getVisible() {
        return visible;
    }

    public void setVisible(Boolean visible) {
        this.visible = visible;
    }

    public Boolean getAvailable() {
        return available;
    }

    public void setAvailable(Boolean available) {
        this.available = available;
    }

    public ServiceTypeEnum getServiceTypeEnum() {
        return serviceTypeEnum;
    }

    public void setServiceTypeEnum(ServiceTypeEnum serviceTypeEnum) {
        this.serviceTypeEnum = serviceTypeEnum;
    }

    public Map getServiceMessage() {
        return serviceMessage;
    }

    public void setServiceMessage(Map serviceMessage) {
        this.serviceMessage = serviceMessage;
    }

    public Map getServiceRegisterBean() {
        return serviceRegisterBean;
    }

    public void setServiceRegisterBean(Map serviceRegisterBean) {
        this.serviceRegisterBean = serviceRegisterBean;
    }

    public String getSerializer() {
        return serializer;
    }

    public void setSerializer(String serializer) {
        this.serializer = serializer;
    }
}
