package com.hoppinzq.service.bean;

import com.alibaba.fastjson.JSON;
import com.hoppinzq.service.annotation.InterfaceImplName;
import com.hoppinzq.service.auth.AuthenticationProvider;
import com.hoppinzq.service.auth.AuthorizationProvider;
import com.hoppinzq.service.enums.ServerEnum;
import com.hoppinzq.service.enums.ServiceTypeEnum;
import com.hoppinzq.service.modification.ModificationManager;
import com.hoppinzq.service.serializer.CustomSerializer;
import com.hoppinzq.service.util.DateFormatUtil;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.*;

/**
 * @author:ZhangQi ServiceWrapper保存要通过远程处理公开的服务对象
 * 历史遗留服务包装类，新类已移至ServiceWrapperRPC
 */
public class ServiceWrapper implements Serializable {
    private static final long serialVersionUID = 2783377098145240357L;
    /**
     * 服务ID
     */
    private String id;
    /**
     * 服务名
     */
    private String name;
    /**
     * 服务注册日期
     */
    private Date date;
    /**
     * 服务描述（注解）
     */
    private String title;
    private String description;
    //服务持有者
    private String username;
    private String password;
    /**
     * 包装服务的持有者，实际的服务实现。
     */
    private Object service;
    /**
     * 身份验证
     */
    private AuthenticationProvider authenticationProvider;
    /**
     * 授权
     */
    private AuthorizationProvider authorizationProvider;
    /**
     * 修改管理器将跟踪对参数对象的更改
     */
    private ModificationManager modificationManager;
    /**
     * 序列化方式
     */
    private CustomSerializer customSerializer;
    private ServiceMessage serviceMessage;
    private ServiceRegisterBean serviceRegisterBean;
    private Boolean visible = Boolean.TRUE;//服务默认可见
    private Boolean available = Boolean.TRUE;//服务默认可用
    private ServiceTypeEnum serviceTypeEnum = ServiceTypeEnum.NORMAL;

    public ServiceWrapper() {
    }
    public ServiceWrapper(String id, Object service, AuthenticationProvider authenticationProvider, AuthorizationProvider authorizationProvider, ModificationManager modificationManager, CustomSerializer customSerializer, ServiceMessage serviceMessage, ServiceRegisterBean serviceRegisterBean, Boolean visible, Boolean available, ServiceTypeEnum serviceTypeEnum) {
        this.id = id;
        this.service = service;
        this.authenticationProvider = authenticationProvider;
        this.authorizationProvider = authorizationProvider;
        this.modificationManager = modificationManager;
        this.customSerializer = customSerializer;
        this.serviceMessage = serviceMessage;
        this.serviceRegisterBean = serviceRegisterBean;
        this.visible = visible;
        this.available = available;
        this.serviceTypeEnum = serviceTypeEnum;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
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

    public ServiceTypeEnum getServiceTypeEnum() {
        return serviceTypeEnum;
    }

    public void setServiceTypeEnum(ServiceTypeEnum serviceTypeEnum) {
        this.serviceTypeEnum = serviceTypeEnum;
    }

    public Boolean isAvailable() {
        return available;
    }

    public void setAvailable(Boolean available) {
        this.available = available;
        if (this.serviceRegisterBean != null) {
            this.serviceRegisterBean.setAvailable(available);
        }
    }

    public Boolean isVisible() {
        return visible;
    }

    public void setVisible(Boolean visible) {
        this.visible = visible;
        if (this.serviceRegisterBean != null) {
            this.serviceRegisterBean.setVisible(visible);
        }
    }

    public Boolean isInnerService() {
        return this.serviceMessage.getServiceType() == ServerEnum.INNER;
    }

    public ServiceRegisterBean getServiceRegisterBean() {
        return serviceRegisterBean;
    }

    public void setServiceRegisterBean(ServiceRegisterBean serviceRegisterBean) {
        this.serviceRegisterBean = serviceRegisterBean;
        this.visible = serviceRegisterBean.isVisible();
    }

    public ServiceMessage createServiceMessage() {
        return new ServiceMessage();
    }

    public ServiceMessage createInnerServiceMessage(ZqServerConfig zqServerConfig, String serviceTitle, String serviceDescription, int timeout) {
        ServiceMessage serviceMessage = this.createServiceMessage();
        serviceMessage.innerService(zqServerConfig.getIp(), zqServerConfig.getPort(), zqServerConfig.getPrefix(), serviceTitle, serviceDescription, timeout);
        return serviceMessage;
    }

    public Object getService() {
        return service;
    }

    public void setService(Object service) {
        this.service = service;
    }

    public ServiceMessage getServiceMessage() {
        return serviceMessage;
    }

    public void setServiceMessage(ServiceMessage serviceMessage) {
        this.serviceMessage = serviceMessage;
    }

    public AuthenticationProvider getAuthenticationProvider() {
        return authenticationProvider;
    }

    public void setAuthenticationProvider(AuthenticationProvider authenticationProvider) {
        this.authenticationProvider = authenticationProvider;
    }

    public AuthorizationProvider getAuthorizationProvider() {
        return authorizationProvider;
    }

    public void setAuthorizationProvider(AuthorizationProvider authorizationProvider) {
        this.authorizationProvider = authorizationProvider;
    }

    public ModificationManager getModificationManager() {
        return modificationManager;
    }

    public void setModificationManager(ModificationManager modificationManager) {
        this.modificationManager = modificationManager;
    }

    public CustomSerializer getSerializer() {
        return customSerializer;
    }

    public void setSerializer(CustomSerializer customSerializer) {
        this.customSerializer = customSerializer;
    }

    public ServiceWrapperRPC toRPCBean() {
        ServiceWrapperRPC serviceWrapperRPC = new ServiceWrapperRPC(id, name, DateFormatUtil.dateFormat(new Date()), title, description, visible, available
                , serviceTypeEnum, serviceMessage.toJSON(), serviceRegisterBean.toJSON());
        serviceWrapperRPC.setUsername(this.username);
        serviceWrapperRPC.setPassword(this.password);
        serviceWrapperRPC.setAuthenticationProvider(this.authenticationProvider.getClass().getName());
        serviceWrapperRPC.setAuthorizationProvider(this.authorizationProvider.getClass().getName());
        serviceWrapperRPC.setModificationManager(this.modificationManager.getClass().getName());
        //serviceWrapperRPC.setSerializer(this.serializer.getClass().getName());

        return serviceWrapperRPC;
    }

    @Override
    public String toString() {
        return JSON.toJSONString(toString());
    }

    /**
     * todo 历史遗留问题，格式解析最后解决
     *
     * @return
     */
    public Map toJSON() {
        Map map = new HashMap();
        Map serviceMap = new HashMap();
        if (this.service != null) {
            serviceMap.put("serviceName", this.service.getClass().getName());
            serviceMap.put("serviceSimpleName", this.service.getClass().getSimpleName());
            List list = new ArrayList();
            Method[] methods = this.service.getClass().getDeclaredMethods();
            for (Method method : methods) {
                Map methodMap = new HashMap();
                methodMap.put("returnType", method.getReturnType().getSimpleName());
                methodMap.put("methodName", method.getName());
                Class[] params = method.getParameterTypes();
                String[] paramsStr = new String[params.length];
                for (int i = 0; i < params.length; i++) {
                    paramsStr[i] = params[i].getSimpleName();
                }
                methodMap.put("methodParams", paramsStr);
                list.add(methodMap);
            }
            map.put("service", list);
        } else {
            map.put("service", "");
        }
        Map authenticationProviderMap = new HashMap();
        Map authorizationProviderMap = new HashMap();
        Map modificationManagerMap = new HashMap();
        Map serializerMap = new HashMap();
        map.put("serviceMessage", this.serviceMessage == null ? new HashMap<>() : this.serviceMessage.toJSON());
        InterfaceImplName interfaceImplName = this.authenticationProvider.getClass().getAnnotation(InterfaceImplName.class);
        if (interfaceImplName != null) {
            authenticationProviderMap.put("message", interfaceImplName.value());
        } else {
            authenticationProviderMap.put("message", "未知的用户认证方式");
        }
        authenticationProviderMap.put("type", this.authenticationProvider.getClass().getSimpleName());
        map.put("authenticationProvider", authenticationProviderMap);
        InterfaceImplName interfaceImplName1 = this.authorizationProvider.getClass().getAnnotation(InterfaceImplName.class);
        if (interfaceImplName1 != null) {
            authorizationProviderMap.put("message", interfaceImplName1.value());
        } else {
            authorizationProviderMap.put("message", "未知的用户认证方式");
        }
        authorizationProviderMap.put("type", this.authorizationProvider.getClass().getSimpleName());
        map.put("authorizationProvider", authorizationProviderMap);

        InterfaceImplName interfaceImplName2 = this.modificationManager.getClass().getAnnotation(InterfaceImplName.class);
        if (interfaceImplName2 != null) {
            modificationManagerMap.put("message", interfaceImplName2.value());
        } else {
            modificationManagerMap.put("message", "未知的参数跟踪方式");
        }
        modificationManagerMap.put("type", this.modificationManager.getClass().getSimpleName());
        InterfaceImplName interfaceImplName3 = this.customSerializer.getClass().getAnnotation(InterfaceImplName.class);
        if (interfaceImplName3 != null) {
            serializerMap.put("message", interfaceImplName3.value());
        } else {
            serializerMap.put("message", "未知的序列化方式");
        }
        serializerMap.put("type", this.customSerializer.getClass().getSimpleName());
        map.put("serializer", serializerMap);

        //map.put("serviceType",this.getServiceTypeEnum());
        map.put("modificationManager", modificationManagerMap);
        map.put("id", this.getId());
        map.put("date", DateFormatUtil.dateFormat(date));
        map.put("name", this.getName());
        map.put("title", this.getTitle());
        map.put("description", this.getDescription());
        map.put("serviceRegister", this.serviceRegisterBean == null ? new HashMap<>() : this.serviceRegisterBean.toJSON());
        return map;
    }
}
