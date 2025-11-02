package com.hoppinzq.service.config;

import com.hoppinzq.service.bean.ApiPropertyBean;
import com.hoppinzq.service.bean.ServletHandlerMapping;
import com.hoppinzq.service.bean.ZqServerConfig;
import com.hoppinzq.service.cache.GatewayCache;
import com.hoppinzq.service.cache.GatewayConfigBean;
import com.hoppinzq.service.core.APIGatewayServlet;
import com.hoppinzq.service.core.ApiStore;
import com.hoppinzq.service.util.UUIDUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

/**
 * 注册网关服务，请求后缀需要携带/service/hoppinzq
 * 为什么由网关转发的请求的对象是服务类而不是一整个模块，这是因为考虑到某些方法的内部细节不同，如：有些方法需要校验用户权限，有些方法不需要。
 * 有些方法则有调用限制，那么为这些方法编写多个网关是有必要的
 * 当然你可以通过注解来实现上面的操作，你可以
 *
 * @author: ZhangQi
 */
@ConditionalOnWebApplication
@EnableConfigurationProperties(value = {ApiPropertyBean.class})
public class GatewayServletConfig {
    public static long startTime;
    public static String id = "";
    public static boolean isGateway = false;
    private static final Logger logger = LoggerFactory.getLogger(GatewayServletConfig.class);
    @Autowired
    private ApplicationContext applicationContext;
    @Autowired
    private ApiPropertyBean apiPropertyBean;
    @Autowired
    private ZqServerConfig zqServerConfig;

    @Bean
    public ServletRegistrationBean gatewayServletHandlerRegister() {
        isGateway = true;
        startTime = System.currentTimeMillis();
        id = UUIDUtil.getUUID();
        logger.debug("网关启动成功，网关ID：" + id);
        ApiStore apiStore = new ApiStore(applicationContext);
        apiStore.loadApiFromSpringBeans();
        ServletRegistrationBean<APIGatewayServlet> registrationBean = new ServletRegistrationBean<>();
        //手动注册，先写死，应该读取配置文件，但我懒
        GatewayCache.servletHandlerMapList = ApiStore.getRegisterServletHandler();
        for (ServletHandlerMapping servletHandlerMapping : GatewayCache.servletHandlerMapList) {
            APIGatewayServlet apiGatewayServlet = new APIGatewayServlet(servletHandlerMapping.getServletHandler(), servletHandlerMapping.getPrefix());
            apiGatewayServlet.setApiPropertyBean(apiPropertyBean);
            registrationBean.setServlet(apiGatewayServlet);
            registrationBean.addUrlMappings(servletHandlerMapping.getPrefix());
        }
        GatewayConfigBean.context = applicationContext;
        GatewayConfigBean.apiPropertyBean = apiPropertyBean;
        GatewayConfigBean.zqServerConfig = zqServerConfig;
        return registrationBean;
    }
}
