package com.hoppinzq.service.filter;

public class Main {
    public static void main(String[] args) {
        RPCFilterChain chain = new RPCFilterChain();
        chain.addFilter(new DefaultFilterInterface());
        chain.addFilter(new DefaultFilterInterface());
        chain.doFilter("param1");
    }
}