package com.hoppinzq.service.log.bean;

import java.io.Serializable;

/**
 * @author:ZhangQi 日志上下文
 **/
public class LogContext {
    private static ThreadLocal<Serializable> principalHolder = new ThreadLocal<Serializable>();

    public static final Serializable getPrincipal() {
        if (principalHolder.get() == null) {
            principalHolder.set(new ServiceLogBean());
        }
        return principalHolder.get();
    }

    public static final void setPrincipal(Serializable principal) {
        principalHolder.set(principal);
    }

    public static void exit() {
        principalHolder.set(null);
    }

    public static final Serializable enter(Serializable principal) {
        if (principalHolder.get() == null) {
            principalHolder.set(principal);
        }
        return principalHolder.get();
    }
}
