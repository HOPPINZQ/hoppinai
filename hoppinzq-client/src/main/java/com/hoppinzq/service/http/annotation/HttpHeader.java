package com.hoppinzq.service.http.annotation;

import java.lang.annotation.*;

/**
 * @author:ZhangQi 声明请求需要携带的（一批）请求头，或者声明字段作为本次请求的请求头
 * 如：
 * @HttpHeaders({
 * @HttpHeader(key = "x-zhang0",value = "qi0"),
 * @HttpHeader(key = "x-zhang1",value = "qi1"),
 * @HttpHeader(key = "x-zhang2",value = "qi2")
 * })
 * @HttpHeader(key = "x-zhang",value = "getToken")
 * @HttpApi(url = "http://127.0.0.1:8801/service/hoppinzq?method=getGatewayMapping&params={}")
 * String getData(@HttpHeader(key = "token") String header);
 * <p>
 * 则该请求会携带x-zhang0，x-zhang1，x-zhang2，x-zhang这四个已经赋值的请求头和一个通过传参设置的请求头token。
 */
@Target({ElementType.METHOD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Repeatable(HttpHeaders.class)
public @interface HttpHeader {
    String key();

    String value() default "";
}
