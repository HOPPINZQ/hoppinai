package com.hoppinzq.service.config;

import com.hoppinzq.service.bean.ZqServerConfig;
import com.hoppinzq.service.cache.ServiceStore;
import com.hoppinzq.service.servlet.ProxyHoppinRPCServlet;
import com.hoppinzq.service.servlet.ProxyHoppinServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;

/**
 * @author:ZhangQi SpringHoppinzq核心配置
 */
@ConditionalOnWebApplication
public class SpringHoppinzqCoreConfig implements ApplicationContextAware {
    private static Logger logger = LoggerFactory.getLogger(SpringHoppinzqCoreConfig.class);

    @Autowired
    private ZqServerConfig zqServerConfig;

    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Bean
    public ServletRegistrationBean servletRegistrationBean() {
        //开启rpc服务
        ServiceStore.applicationContext = this.applicationContext;
        ProxyHoppinServlet proxyServlet = new ProxyHoppinRPCServlet();
        proxyServlet.setApplicationContext(this.applicationContext);
        String serviceAddress = "http://" + zqServerConfig.getIp() + ":" + zqServerConfig.getPort() + zqServerConfig.getPrefix();
        proxyServlet.setPropertyBean(zqServerConfig);
        ServletRegistrationBean registration = new ServletRegistrationBean(proxyServlet, zqServerConfig.getPrefix());
        logger.info("注册服务成功，服务路径：" + serviceAddress);
        return registration;
    }

}
