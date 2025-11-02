package com.hoppinzq.service.annotation;

import java.lang.annotation.*;

/**
 * @author zhangqi
 * rpc响应体封装注解，todo
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RPCResponse {
}
