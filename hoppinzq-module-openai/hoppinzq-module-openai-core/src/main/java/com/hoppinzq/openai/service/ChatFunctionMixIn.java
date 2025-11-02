package com.hoppinzq.openai.service;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

public abstract class ChatFunctionMixIn {

    /**
     * 获取函数参数的类类型。
     * 该方法用于抽象地获取当前函数参数的类类型，通常用于序列化处理。
     *
     * @return 返回参数的类类型。
     */
    @JsonSerialize(using = ChatFunctionParametersSerializer.class)
    abstract Class<?> getParametersClass();

}
