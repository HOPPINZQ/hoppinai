package com.hoppinzq.service.circuitBreaker.service;

import com.hoppinzq.service.annotation.ApiMapping;
import com.hoppinzq.service.annotation.ApiServiceMapping;
import com.hoppinzq.service.circuitBreaker.bean.CircuitBreakerSetting;
import com.hoppinzq.service.circuitBreaker.cache.CircuitBreakerCache;
import com.hoppinzq.service.circuitBreaker.cache.CircuitBreakerStore;
import com.hoppinzq.service.circuitBreaker.config.BreakerAutoConfig;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 暴露内部数据
 */
@ApiServiceMapping(title = "熔断器接口", description = "展示熔断器相关细节")
public class BreakerService {

    @ApiMapping(value = "isBreaker", title = "服务是否开启熔断器", description = "返回true表示开启")
    public boolean isBreaker() {
        return BreakerAutoConfig.isBreaker;
    }

    @ApiMapping(value = "getBreakerLogs", title = "获取熔断器日志")
    public Map getBreakerLogs() {
        if (!BreakerAutoConfig.isBreaker) {
            throw new RuntimeException("熔断器未开启");
        }
        return CircuitBreakerCache.stateMap;
    }

    @ApiMapping(value = "getBreakerSettings", title = "获取熔断器配置", description = "这个配置是可以动态修改的")
    public List<CircuitBreakerSetting> getBreakerSettings() {
        if (!BreakerAutoConfig.isBreaker) {
            throw new RuntimeException("熔断器未开启");
        }
        return CircuitBreakerStore.circuitBreakerSettings.stream()
                .map(setting -> {
                    CircuitBreakerSetting filteredSetting = new CircuitBreakerSetting();
                    filteredSetting.setTimeout(setting.getTimeout());
                    filteredSetting.setThreshold(setting.getThreshold());
                    filteredSetting.setFallback(setting.getFallback());
                    filteredSetting.setClassName(setting.getClassName());
                    filteredSetting.setMethodName(setting.getMethodName());
                    return filteredSetting;
                })
                .collect(Collectors.toList());
    }
}
