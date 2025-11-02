package com.hoppinzq.service.http.annotation;

import java.lang.annotation.*;

/**
 * @author:ZhangQi 请求查询字符串注解，声明方法的一个字段是查询字符串
 * 如：
 * @HttpApi(url="http://127.0.0.1:8899/getQrState",method="get") String test(@GetParam String code_id);
 * 在执行test("123")方法时，将会返回接口： http://127.0.0.1:8899/getQrState?code_id=123 的预期值
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface GetParam {
}
