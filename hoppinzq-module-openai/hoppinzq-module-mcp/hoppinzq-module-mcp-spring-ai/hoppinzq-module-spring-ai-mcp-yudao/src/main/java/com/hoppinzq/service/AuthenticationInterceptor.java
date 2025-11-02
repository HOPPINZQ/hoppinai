package com.hoppinzq.service;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.Objects;

public class AuthenticationInterceptor implements Interceptor {

    private final String token;

    AuthenticationInterceptor(String token) {
        Objects.requireNonNull(token, "token不为空");
        this.token = token;
    }

    /**
     * 拦截HTTP请求并添加认证头。
     * 此方法拦截HTTP请求，为请求添加"Authorization"头，用于携带访问令牌（token）。
     *
     * @param chain 请求链，用于构建和发送请求
     * @return Response 返回处理后的响应
     * @throws IOException 如果在处理请求过程中发生I/O错误
     */
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request()
                .newBuilder()
                .header("Authorization", "Bearer " + token)
                .header("tenant-id", "1")
                .build();
        return chain.proceed(request);
    }
}
