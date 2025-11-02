package com.hoppinzq.service.url;

import com.hoppinzq.service.handler.MethodInvocationHandler;
import com.hoppinzq.service.loadBalance.LoadBalanceCache;
import com.hoppinzq.service.transport.HoppinRPCProvider;

import java.io.IOException;

/**
 * @author:ZhangQi url请求方式
 */
public class HttpURLConnectionHoppinRPCProvider implements HoppinRPCProvider<HttpURLHoppinRPCSession> {

    @Override
    public HttpURLHoppinRPCSession createSession(MethodInvocationHandler invocationHandler) {
        return new HttpURLHoppinRPCSession(invocationHandler);
    }

    @Override
    public void loadBalance(MethodInvocationHandler invocationHandler) {
        if (invocationHandler.getServiceURI() == null || invocationHandler.getServiceName() != null) {
            String interfaceServiceName = invocationHandler.getInterfaceServiceName();
            String uri = LoadBalanceCache.getNextService(invocationHandler.getServiceName(), interfaceServiceName);
            invocationHandler.setServiceURI(uri);
        }
    }

    @Override
    public void endSession(HttpURLHoppinRPCSession session, MethodInvocationHandler invocationHandler) {
        try {
            session.getConn().getInputStream().close();
        } catch (IOException ignored) {
        }
    }
}
