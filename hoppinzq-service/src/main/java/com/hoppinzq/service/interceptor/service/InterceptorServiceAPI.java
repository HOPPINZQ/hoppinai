package com.hoppinzq.service.interceptor.service;

import com.hoppinzq.service.annotation.ApiMapping;
import com.hoppinzq.service.annotation.ApiServiceMapping;
import com.hoppinzq.service.bean.OrderServiceClazz;
import com.hoppinzq.service.cache.ServiceStore;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author:ZhangQi 暴露拦截器接口（动态配置）
 */
@ApiServiceMapping(title = "拦截器", description = "暴露拦截器内容")
public class InterceptorServiceAPI {

    @ApiMapping(value = "getInterceptor", title = "获取拦截器")
    public List<OrderServiceClazz> getInterceptor() {
        return ServiceStore.orderServiceClazzes.stream()
                .filter((item -> {
                    return item.getType() == OrderServiceClazz.OrderServiceTypeEnum.INTERCEPTOR.getState();
                })).collect(Collectors.toList());
    }
}
