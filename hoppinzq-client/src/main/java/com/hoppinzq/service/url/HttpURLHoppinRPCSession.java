package com.hoppinzq.service.url;

import com.hoppinzq.service.bean.RPCBean;
import com.hoppinzq.service.bean.RPCContext;
import com.hoppinzq.service.common.HoppinInvocationRequest;
import com.hoppinzq.service.handler.MethodInvocationHandler;
import com.hoppinzq.service.proxy.ServiceProxyFactory;
import com.hoppinzq.service.serializer.HessionSerializer;
import com.hoppinzq.service.transport.HoppinRPCSession;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;

/**
 * @author:ZhangQi url请求方式
 */
public class HttpURLHoppinRPCSession implements HoppinRPCSession {
    int connectTimeout = 5000; // 连接超时时间
    int readTimeout = 10000; // 读取超时时间
    private final MethodInvocationHandler invocationHandler;
    private HttpURLConnection conn;

    public HttpURLHoppinRPCSession(MethodInvocationHandler invocationHandler) {
        this.invocationHandler = invocationHandler;
    }

    @Override
    public InputStream sendInvocationRequest(Method method, HoppinInvocationRequest request, InputStream streamArgument) throws IOException {
        //conn = (HttpURLConnection) new URL(invocationHandler.getServiceURI()).openConnection();
        URL url = new URL(invocationHandler.getServiceURI());

        conn = (HttpURLConnection) url.openConnection();
        conn.setConnectTimeout(connectTimeout);
        conn.setReadTimeout(readTimeout);
        conn.setDoOutput(true);

        RPCBean rpcBean = (RPCBean) RPCContext.getPrincipal();
        Map<String, String> headers = rpcBean.getHoppinInvocationRequest().getHeaders();
        if (headers != null) {
            for (String key : headers.keySet()) {
                conn.setRequestProperty(key, headers.get(key));
            }
        }

        if (streamArgument != null) {
            conn.setChunkedStreamingMode(ServiceProxyFactory.streamBufferSize);
        }

        //  序列化开始
        HessionSerializer hessionSerializer = new HessionSerializer();
        hessionSerializer.serialize(request, conn.getOutputStream());
        //  序列化结束

        if (streamArgument != null) {
            sendStreamArgumentToHttpOutputStream(streamArgument, conn.getOutputStream());
        }
        //设置响应头
        Map<String, List<String>> headerFields = conn.getHeaderFields();

        rpcBean.setHeaders(headerFields);
        //((RPCBean) RPCContext.getPrincipal()).getInvocationResponse().setHeaders(conn.getHeaderFields());

        return conn.getInputStream();
    }

    private void sendStreamArgumentToHttpOutputStream(InputStream streamArgument, OutputStream outputStream) throws IOException {
        byte[] buf = new byte[ServiceProxyFactory.streamBufferSize];
        int len;
        while ((len = streamArgument.read(buf)) > -1) {
            outputStream.write(buf, 0, len);
        }
    }

    public HttpURLConnection getConn() {
        return conn;
    }
}
