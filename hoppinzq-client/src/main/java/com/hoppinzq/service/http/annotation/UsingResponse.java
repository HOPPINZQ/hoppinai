package com.hoppinzq.service.http.annotation;

import java.lang.annotation.*;

/**
 * @author:ZhangQi 响应头写入本次请求的HttpServletResponse
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface UsingResponse {
}
