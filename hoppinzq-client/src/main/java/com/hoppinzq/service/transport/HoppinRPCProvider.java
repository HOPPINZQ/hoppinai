package com.hoppinzq.service.transport;

import com.hoppinzq.service.handler.MethodInvocationHandler;

/**
 * @author:ZhangQi rpc的请求管理接口，管理一次请求会话
 * 先把负载均衡放在这里，接口太多我也会头晕的 ~
 */
public interface HoppinRPCProvider<T extends HoppinRPCSession> {

    T createSession(MethodInvocationHandler invocationHandler);

    void loadBalance(MethodInvocationHandler invocationHandler);

    void endSession(T session, MethodInvocationHandler invocationHandler);
}
