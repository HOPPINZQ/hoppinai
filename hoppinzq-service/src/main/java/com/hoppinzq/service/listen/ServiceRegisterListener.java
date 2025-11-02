package com.hoppinzq.service.listen;

import com.hoppinzq.service.bean.*;
import com.hoppinzq.service.cache.ServiceStore;
import com.hoppinzq.service.common.UserPrincipal;
import com.hoppinzq.service.enums.ServerEnum;
import com.hoppinzq.service.proxy.ServiceProxyFactory;
import com.hoppinzq.service.service.HeartbeatService;
import com.hoppinzq.service.service.RegisterService;
import com.hoppinzq.service.task.TaskStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.availability.AvailabilityChangeEvent;
import org.springframework.boot.availability.ReadinessState;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author:ZhangQi 用于监听SpringBoot项目是否启动成功，启动成功将任务队列里的方法按序执行
 **/
@Component
public class ServiceRegisterListener implements ApplicationListener<AvailabilityChangeEvent> {

    private static Logger logger = LoggerFactory.getLogger(ServiceRegisterListener.class);
    Boolean isCoreServerAvailable = true;
    @Autowired
    private ZqServerConfig zqServerConfig;

    @Override
    public void onApplicationEvent(AvailabilityChangeEvent event) {
        if (ReadinessState.ACCEPTING_TRAFFIC == event.getState()) {
            if (!TaskStore.taskQueue.isEmpty()) {
                logger.info("应用启动完成，开始向注册中心注册服务！");
                try {
                    //一开始是想一个服务作为一个任务，放在循环队列里，注册成功的服务从循序队列移除
                    //不成功的服务继续等待注册，后来注意到：要么全部注册不成功，要么全部注册成功，不存在部分的情况，就弃用了该队列的设计
                    //所以现在该队列只有一个任务，即一个注册所有服务的任务
                    //返回true表示全部服务注册成功
                    Object o = TaskStore.taskQueue.pop().execute();
                    if (Boolean.parseBoolean(String.valueOf(o))) {
                        //如果注册成功，开辟一个线程去轮询注册中心的心跳服务，当注册中心挂掉，重新等待注册。
                        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);
                        executorService.scheduleAtFixedRate(() -> {
                            try {
                                HeartbeatService service = ServiceProxyFactory.createProxy(HeartbeatService.class, zqServerConfig.getServerCenter());
                                service.areYouOk();
                                if (!isCoreServerAvailable) {
                                    //重新注册！
                                    //todo modWrapper 的方法已被修改
                                    logger.info("注册中心已恢复服务，重新注册！");
                                    UserPrincipal upp = new UserPrincipal(zqServerConfig.getUserName(), zqServerConfig.getPassword());
                                    RegisterService registerService = ServiceProxyFactory.createProxy(RegisterService.class, zqServerConfig.getServerCenter(), upp);
                                    List<ServiceWrapper> serviceWrappers = modWrapper();
                                    List<ServiceWrapperRPC> serviceWrapperRPCS = new ArrayList<>();
                                    for (ServiceWrapper serviceWrapper : serviceWrappers) {
                                        ServiceWrapperRPC serviceWrapperRPC = serviceWrapper.toRPCBean();
                                        serviceWrapperRPCS.add(serviceWrapperRPC);
                                    }
                                    registerService.insertServices(serviceWrapperRPCS);
                                    logger.info("向注册中心注册服务成功！");
                                }
                                isCoreServerAvailable = true;
                            } catch (Exception e) {
                                logger.error("注册中心可能已停止服务，将尝试重新连接注册中心！");
                                isCoreServerAvailable = false;
                            }
                        }, 0, 10, TimeUnit.SECONDS);
//                        // 等待一段时间后关闭线程池
//                        try {
//                            Thread.sleep(5000);
//                            executorService.shutdown();
//                        } catch (InterruptedException e) {
//
//                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    /**
     * 为每一个模块注册一个心跳服务
     */
    private void registerHeartbeatService() {

    }

    public List<ServiceWrapper> modWrapper() {
        List<ServiceWrapper> serviceWrappers = ServiceStore.serviceWrapperList;
        List<ServiceWrapper> serviceWrappersCopyList = new ArrayList<>();
        for (ServiceWrapper serviceWrapper : serviceWrappers) {
            ServiceRegisterBean serviceRegisterBean = new ServiceRegisterBean();
            serviceRegisterBean.setVisible(serviceWrapper.isVisible());
            serviceRegisterBean.setServiceClass(serviceWrapper.getService().getClass().getInterfaces()[0]);
            ZqServerConfig zqServerConfig = this.zqServerConfig;
            ServiceMessage serviceMessage = new ServiceMessage(zqServerConfig.getIp(), zqServerConfig.getPort(), zqServerConfig.getPrefix(), ServerEnum.OUTER);
            ServiceWrapper serviceWrapperCopy = new ServiceWrapper(serviceWrapper.getId(),
                    null, serviceWrapper.getAuthenticationProvider(), serviceWrapper.getAuthorizationProvider(),
                    serviceWrapper.getModificationManager(), serviceWrapper.getSerializer(), serviceMessage, serviceRegisterBean,
                    serviceWrapper.isVisible(), serviceWrapper.isAvailable(), serviceWrapper.getServiceTypeEnum());
            serviceWrappersCopyList.add(serviceWrapperCopy);
        }
        return ServiceStore.serviceWrapperList;
    }
}
