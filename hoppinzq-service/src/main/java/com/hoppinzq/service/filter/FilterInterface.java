package com.hoppinzq.service.filter;

/**
 * @author:ZhangQi 过滤器接口
 */
public interface FilterInterface {
    void doFilter(String param, RPCFilterChain chain);
}

