package com.hoppinzq.service.trace;

import com.hoppinzq.service.trace.bean.TraceBean;

import java.util.List;

/**
 * @author:ZhangQi 链路跟踪上下文
 */
public class TraceContext {
    private static ThreadLocal<List<TraceBean>> principalHolder = new ThreadLocal<List<TraceBean>>();

    public static final List<TraceBean> getPrincipal() {
        return principalHolder.get();
    }

    public static final void setPrincipal(List<TraceBean> traceBeanList) {
        principalHolder.set(traceBeanList);
    }

    public static void exit() {
        principalHolder.set(null);
    }

    public static void enter() {
        principalHolder.set(null);
    }
}

