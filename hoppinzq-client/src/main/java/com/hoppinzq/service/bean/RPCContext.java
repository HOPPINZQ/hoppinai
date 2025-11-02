package com.hoppinzq.service.bean;


import java.io.Serializable;

/**
 * @author zhangqi
 * 一次rpc调用的上下文
 */
public class RPCContext {
    private static final ThreadLocal<Serializable> principalHolder = new ThreadLocal<Serializable>();

    public static final Serializable getPrincipal() {
        return principalHolder.get();
    }

    public static final void setPrincipal(Serializable principal) {
        principalHolder.set(principal);
    }

    public static void exit() {
        principalHolder.set(null);
    }

    public static void enter(Serializable principal) {
        principalHolder.set(principal);
    }
}
