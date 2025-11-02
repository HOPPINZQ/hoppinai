package com.hoppinzq.service.transport;

import com.hoppinzq.service.common.HoppinInvocationRequest;

import java.io.InputStream;
import java.lang.reflect.Method;

/**
 * @author:ZhangQi rpc会话接口
 */
public interface HoppinRPCSession {
    InputStream sendInvocationRequest(Method method, HoppinInvocationRequest request, InputStream streamArgument) throws Exception;
}
