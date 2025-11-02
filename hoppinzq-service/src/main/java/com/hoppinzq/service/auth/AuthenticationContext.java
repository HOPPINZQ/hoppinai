package com.hoppinzq.service.auth;

import java.io.Serializable;

/**
 * 保存已验证成功的用户在其调用服务的一个独立线程内，
 * 在该线程内的操作随时可以通过AuthenticationContext.getPrincipal取出调用方信息以存根
 */
public class AuthenticationContext {
    private static ThreadLocal<Serializable> principalHolder = new ThreadLocal<Serializable>();

    public static final Serializable getPrincipal() {
        return principalHolder.get();
    }

    public static final void setPrincipal(Serializable principal) {
        principalHolder.set(principal);
    }

    public static void exit() {
        principalHolder.set(null);
    }

    public static void enter() {
        principalHolder.set(null);
    }
}
