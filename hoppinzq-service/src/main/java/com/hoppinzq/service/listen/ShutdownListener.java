package com.hoppinzq.service.listen;

import com.hoppinzq.service.bean.ZqServerConfig;
import com.hoppinzq.service.cache.ServiceStore;
import com.hoppinzq.service.common.UserPrincipal;
import com.hoppinzq.service.proxy.ServiceProxyFactory;
import com.hoppinzq.service.service.RegisterService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.stereotype.Component;

/**
 * @author:ZhangQi 应用意外终止时，调用注册中心delete方法移除服务
 **/
@Component
public class ShutdownListener implements ApplicationListener<ContextClosedEvent> {

    private static Logger logger = LoggerFactory.getLogger(ShutdownListener.class);
    @Autowired
    private ZqServerConfig zqServerConfig;

    @Override
    public void onApplicationEvent(ContextClosedEvent event) {
        logger.debug("服务关闭，删除注册中心的服务");
        UserPrincipal upp = new UserPrincipal(zqServerConfig.getUserName(), zqServerConfig.getPassword());
        RegisterService registerService = ServiceProxyFactory.createProxy(RegisterService.class, zqServerConfig.getServerCenter(), upp);
        registerService.deleteServices(ServiceStore.serviceId);
    }
}