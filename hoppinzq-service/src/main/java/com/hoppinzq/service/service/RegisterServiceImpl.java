package com.hoppinzq.service.service;

import com.hoppinzq.service.annotation.ServiceRegister;
import com.hoppinzq.service.annotation.ServiceRegisterMethod;
import com.hoppinzq.service.auth.AuthenticationNotCheckAuthenticator;
import com.hoppinzq.service.auth.AuthenticationNotCheckAuthorizer;
import com.hoppinzq.service.bean.ServiceMessage;
import com.hoppinzq.service.bean.ServiceRegisterBean;
import com.hoppinzq.service.bean.ServiceWrapper;
import com.hoppinzq.service.bean.ServiceWrapperRPC;
import com.hoppinzq.service.cache.RPCServiceStore;
import com.hoppinzq.service.cache.ServiceStore;
import com.hoppinzq.service.enums.ServerEnum;
import com.hoppinzq.service.enums.ServiceTypeEnum;
import com.hoppinzq.service.modification.NotModificationManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;
import java.util.Map;

/**
 *
 */
@ServiceRegister(title = "外部服务的注册服务", description = "通过该服务提供的方法，对外部服务进行管理")
public class RegisterServiceImpl implements RegisterService {

    private static Logger logger = LoggerFactory.getLogger(RegisterService.class);
    List<ServiceWrapperRPC> serviceWrapperList = RPCServiceStore.serviceWrapperRPCList;
    @Value("${zqServer.isStrict:false}")
    private Boolean isStrict;

    /**
     * 注册单个服务
     *
     * @param serviceWrapperRPC
     */
    @Override
    @ServiceRegisterMethod(title = "新增服务", description = "新增仅一个服务")
    public void insertService(ServiceWrapperRPC serviceWrapperRPC) {
        ServiceWrapperRPC wrapper = checkOuterService(serviceWrapperRPC);
        if (wrapper != null) {
            if (isStrict) {
                throw new RuntimeException("该服务已注册，必须通过updateServices更新注册！");
            } else {
                serviceWrapperList.remove(wrapper);
            }
        }
        if (serviceWrapperRPC.getServiceTypeEnum() == ServiceTypeEnum.HEARTBEAT) {
            synchronized (ServiceStore.heartbeatService) {
                ServiceStore.heartbeatService.add(changeHB(serviceWrapperRPC));
            }
            logger.debug("心跳服务注册成功！服务来源：" + serviceWrapperRPC.getServiceMessage().get("serviceIP").toString()
                    + ":" + serviceWrapperRPC.getServiceMessage().get("servicePort").toString());
        } else {
            //这个是线程安全的
            serviceWrapperList.add(serviceWrapperRPC);
        }
    }

    /**
     * 注册一批服务
     *
     * @param serviceWrapperRPCS
     */
    @Override
    @ServiceRegisterMethod(title = "新增服务", description = "可以新增多个服务")
    public void insertServices(List<ServiceWrapperRPC> serviceWrapperRPCS) {
        for (ServiceWrapperRPC serviceWrapperRPC : serviceWrapperRPCS) {
            insertService(serviceWrapperRPC);
        }
    }

    @Override
    @ServiceRegisterMethod(title = "测试", description = "返回数据表示服务可用")
    public String sayHello(String name) {
        return "hello :" + name;
    }


    @Override
    @ServiceRegisterMethod(title = "修改服务", description = "未实现，可用修改多个服务")
    public int updateServices(List<ServiceWrapperRPC> serviceWrappers) {
        int index = 0;
//        for(ServiceWrapperRPC serviceWrapper:serviceWrappers){
//            Map serviceRegisterBean=serviceWrapper.getServiceRegisterBean();
//            for(ServiceWrapperRPC serviceWrapper1:serviceWrapperList){
//                Map serviceMessage1=serviceWrapper1.getServiceMessage();
//                if(serviceMessage1.getServiceType()==ServerEnum.OUTER){
//                    ServiceRegisterBean serviceRegisterBean1=serviceWrapper1.getServiceRegisterBean();
//                    if(serviceRegisterBean1.getServiceName().equals(serviceRegisterBean.getServiceName())){
//                        serviceWrapper1=serviceWrapper;
//                        index++;
//                    }
//                }
//            }
//        }
        return index;
    }

    @Override
    @ServiceRegisterMethod(title = "删除服务", description = "未实现，删除多个服务")
    public void deleteServices(List<ServiceWrapperRPC> serviceWrappers) {
//        List<ServiceWrapperRPC> rpcs=RPCServiceStore.serviceWrapperRPCList;
//        for(int i=0;i<rpcs.size();i++){
//            ServiceWrapperRPC core=rpcs.get(i);
//            for(ServiceWrapperRPC serviceWrapperRPC:serviceWrappers){
//                if(serviceWrapperRPC.getId().equals(core.getId())){
//                    rpcs.remove(i);
//                }
//            }
//        }
    }

    @Override
    @ServiceRegisterMethod(title = "删除服务", description = "未实现，根据服务ID删除服务")
    public void deleteServices(String serviceId) {

//        List<ServiceWrapperRPC> rpcs=RPCServiceStore.serviceWrapperRPCList;
//        List<ServiceWrapper> heartbeatServices=ServiceStore.heartbeatService;
//        for(int i=0;i<rpcs.size();i++){
//            ServiceWrapperRPC core=rpcs.get(i);
//            if(serviceId.equals(core.getId())){
//                rpcs.remove(i);i--;
//            }
//        }
//        for(int j=0;j<heartbeatServices.size();j++){
//            ServiceWrapper core=heartbeatServices.get(j);
//            if(serviceId.equals(core.getId())){
//                heartbeatServices.remove(j);j--;
//            }
//        }
    }

    @Override
    @ServiceRegisterMethod(title = "删除服务", description = "未实现，在项目关闭后删除所有该项目的服务")
    public void shutdownServer(String serviceId) {
        logger.debug("服务：" + serviceId + " 已下线，将删除所有注册的服务");
        this.deleteServices(serviceId);
    }


    @Override
    @ServiceRegisterMethod(title = "查询服务", description = "未实现，根据服务ID查询服务")
    public List<Map> queryServicesByServiceId(String serviceId) {
        return null;
    }

    @Override
    @ServiceRegisterMethod(title = "查询服务", description = "未实现，根据服务模块名称删除服务")
    public List<Map> queryServicesByServiceName(String serviceId) {
        return null;
    }

    @Override
    @ServiceRegisterMethod(title = "查询服务", description = "查询所有外部服务")
    public List<ServiceWrapperRPC> queryAllOuterServices() {
        return RPCServiceStore.serviceWrapperRPCList;
    }

    @Override
    @ServiceRegisterMethod(title = "查询服务", description = "查询注册中心内部")
    public List<ServiceWrapperRPC> showCoreService() {
        return null;
    }

    @Override
    @ServiceRegisterMethod(title = "服务状态修改", description = "未实现，将指定服务ID的服务设置为：可见")
    public void setServiceVisible(String serviceId) {

    }

    @Override
    @ServiceRegisterMethod(title = "服务状态修改", description = "未实现，将指定服务ID的服务设置为：不可见")
    public void setServiceUnVisible(String serviceId) {

    }

    @Override
    @ServiceRegisterMethod(title = "服务状态修改", description = "未实现，将指定服务ID的服务设置为：可用")
    public void setServiceAvailable(String serviceId) {

    }

    @Override
    @ServiceRegisterMethod(title = "服务状态修改", description = "未实现，将指定服务ID的服务设置为：不可用")
    public void setServiceUnavailable(String serviceId) {

    }

    /**
     * 判断服务是否已经存在
     *
     * @param serviceWrapperRPC
     * @return
     */
    private ServiceWrapperRPC checkOuterService(ServiceWrapperRPC serviceWrapperRPC) {
//        if(serviceWrapperList.size()==0)
//            return null;
//        Map serviceMessageRPC=serviceWrapperRPC.getServiceMessage();
//        for(ServiceWrapperRPC wrapper:serviceWrapperList){
//            Map serviceMessage=wrapper.getServiceMessage();
//            String s1=serviceMessageRPC.get("serviceIP")+""+serviceMessageRPC.get("servicePort");
//            String s2=serviceMessage.get("serviceIP")+""+serviceMessage.get("servicePort");
//            if(s1.equals(s2)){
//                return wrapper;
//            }
//        }
        return null;
    }

    /**
     * 转心跳服务格式
     *
     * @param serviceWrapperRPC
     * @return
     */
    private ServiceWrapper changeHB(ServiceWrapperRPC serviceWrapperRPC) {
        ServiceWrapper serviceWrapper = new ServiceWrapper();
        serviceWrapper.setId(serviceWrapperRPC.getId());
        serviceWrapper.setService(null);
        serviceWrapper.setServiceTypeEnum(ServiceTypeEnum.HEARTBEAT);
        serviceWrapper.setAuthenticationProvider(new AuthenticationNotCheckAuthenticator());
        serviceWrapper.setAuthorizationProvider(new AuthenticationNotCheckAuthorizer());
        serviceWrapper.setModificationManager(new NotModificationManager());
        serviceWrapper.setServiceRegisterBean(new ServiceRegisterBean(Boolean.FALSE));
        serviceWrapper.setServiceMessage(new ServiceMessage(
                String.valueOf(serviceWrapperRPC.getServiceMessage().get("serviceIP")),
                String.valueOf(serviceWrapperRPC.getServiceMessage().get("servicePort")),
                String.valueOf(serviceWrapperRPC.getServiceMessage().get("servicePrefix")),
                ServerEnum.INNER));
        return serviceWrapper;
    }
}
