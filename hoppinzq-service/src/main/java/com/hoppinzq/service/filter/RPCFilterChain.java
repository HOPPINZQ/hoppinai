package com.hoppinzq.service.filter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author:ZhangQi 过滤器接口
 */
public class RPCFilterChain {
    private List<FilterInterface> FilterInterfaces;

    public RPCFilterChain() {
        FilterInterfaces = new ArrayList<>();
    }

    public RPCFilterChain addFilter(FilterInterface FilterInterface) {
        FilterInterfaces.add(FilterInterface);
        return this;
    }

    public void doFilter(String param) {
        if (FilterInterfaces.isEmpty()) {
            return;
        }
        FilterInterface currentFilterInterface = FilterInterfaces.get(0);
        FilterInterfaces.remove(0);
        currentFilterInterface.doFilter(param, this);
    }
}