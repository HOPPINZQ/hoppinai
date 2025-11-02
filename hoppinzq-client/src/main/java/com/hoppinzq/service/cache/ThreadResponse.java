package com.hoppinzq.service.cache;

import com.hoppinzq.service.common.HoppinInvocationResponse;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author:ZhangQi 保存rpc线程的response
 **/
public class ThreadResponse {
    public static Map<Thread, HoppinInvocationResponse> responseMap = new ConcurrentHashMap<>();

    public static synchronized HoppinInvocationResponse get(Thread thread) {
        HoppinInvocationResponse result = responseMap.get(thread);
        return result;
    }

    public static synchronized void set(Thread thread, HoppinInvocationResponse response) {
        responseMap.put(thread, response);
    }
}

