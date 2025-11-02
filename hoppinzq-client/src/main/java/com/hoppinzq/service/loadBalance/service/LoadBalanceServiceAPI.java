package com.hoppinzq.service.loadBalance.service;

import com.hoppinzq.service.annotation.ApiMapping;
import com.hoppinzq.service.annotation.ApiServiceMapping;
import com.hoppinzq.service.loadBalance.LoadBalanceCache;
import com.hoppinzq.service.loadBalance.LoadBalanceConfig;

import java.util.Map;

/**
 * @author:ZhangQi 暴露负载均衡接口（动态配置）
 */
@ApiServiceMapping(title = "负载均衡", description = "负载均衡内容")
public class LoadBalanceServiceAPI {

    @ApiMapping("/isLoadBalance")
    public boolean isLoadBalance() {
        return LoadBalanceConfig.isLoadBalance;
    }

    @ApiMapping("/getLoadBalance")
    public Map getLoadBalanceMap() {
        if (!LoadBalanceConfig.isLoadBalance) {
            throw new RuntimeException("未开启负载均衡");
        }
        return LoadBalanceCache.loadBalanceMap;
    }

    @ApiMapping("/getIndexMap")
    public Map getIndexMap() {
        if (!LoadBalanceConfig.isLoadBalance) {
            throw new RuntimeException("未开启负载均衡");
        }
        return LoadBalanceCache.indexMap;
    }

}
