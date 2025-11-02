package com.hoppinzq.service.httpclient;

import com.hoppinzq.service.handler.MethodInvocationHandler;
import com.hoppinzq.service.transport.HoppinRPCProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;

/**
 * @author:ZhangQi 基于http协议，rpc使用HttpClient模拟http请求
 */
public class HttpClientConnectionHoppinRPCProvider implements HoppinRPCProvider<HttpClientHoppinRPCSession> {
    @Override
    public HttpClientHoppinRPCSession createSession(MethodInvocationHandler invocationHandler) {
        HttpClient httpClient = getHttpClient();
        return new HttpClientHoppinRPCSession(httpClient, invocationHandler);
    }

    @Override
    public void loadBalance(MethodInvocationHandler invocationHandler) {
        //负载均衡
    }

    public HttpClient getHttpClient() {
        return new DefaultHttpClient();
    }

    @Override
    public void endSession(HttpClientHoppinRPCSession session, MethodInvocationHandler invocationHandler) {
        try {
            session.getHttpResponse().getEntity().getContent().close();
        } catch (IOException ignored) {
        }
    }
}
