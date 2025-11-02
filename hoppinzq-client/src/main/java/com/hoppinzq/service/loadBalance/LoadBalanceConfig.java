package com.hoppinzq.service.loadBalance;

import com.hoppinzq.service.bean.ZqServerConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;

import javax.annotation.PostConstruct;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author:ZhangQi 负载均衡配置
 */
@ConditionalOnWebApplication
public class LoadBalanceConfig {
    public static boolean isLoadBalance = false;
    private static final Logger logger = LoggerFactory.getLogger(LoadBalanceConfig.class);
    @Autowired
    private ZqServerConfig zqServerConfig;

    @PostConstruct
    public void init() {
        logger.debug("负载均衡模块加载");
        isLoadBalance = true;
        LoadBalanceCache loadBalanceCache = new LoadBalanceCache(zqServerConfig);
        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        Runnable task = new Runnable() {
            @Override
            public void run() {
                loadBalanceCache.loadService();
                logger.debug("负载均衡模块加载缓存服务");
            }
        };
        executorService.scheduleAtFixedRate(task, 0, 30, TimeUnit.SECONDS);
    }
}