package com.hoppinzq.service.filter;

import com.hoppinzq.service.filter.aop.RPCFilter;

/**
 * @author:ZhangQi 默认的rpc过滤器
 */
@RPCFilter
class DefaultFilterInterface implements FilterInterface {
    @Override
    public void doFilter(String param, RPCFilterChain chain) {
        System.err.println(param);
        param += "，zq";
        chain.doFilter(param);
    }
}