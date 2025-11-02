package com.hoppinzq.service.filter.service;

import com.hoppinzq.service.annotation.ApiMapping;
import com.hoppinzq.service.annotation.ApiServiceMapping;
import com.hoppinzq.service.bean.OrderServiceClazz;
import com.hoppinzq.service.cache.ServiceStore;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author:ZhangQi 暴露过滤器接口（动态配置）
 */
@ApiServiceMapping(title = "过滤器", description = "暴露过滤器内容")
public class FilterServiceAPI {

    @ApiMapping(value = "getFilter", title = "获取过滤器")
    public List<OrderServiceClazz> getFilter() {
        return ServiceStore.orderServiceClazzes.stream()
                .filter((item -> {
                    return item.getType() == OrderServiceClazz.OrderServiceTypeEnum.FILTER.getState();
                })).collect(Collectors.toList());
    }

}
