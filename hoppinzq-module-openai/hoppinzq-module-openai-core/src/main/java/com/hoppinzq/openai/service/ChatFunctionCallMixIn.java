package com.hoppinzq.openai.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

public abstract class ChatFunctionCallMixIn {

    /**
     * 获取函数调用的参数。
     * 此抽象方法用于获取ChatFunctionCallArgumentsSerializerAndDeserializer序列化和反序列化时使用的参数。
     * 使用@JsonSerialize和@JsonDeserialize注解指定序列化和反序列化时使用的自定义类。
     *
     * @return JsonNode 返回一个JsonNode对象，包含函数调用的参数。
     */
    @JsonSerialize(using = ChatFunctionCallArgumentsSerializerAndDeserializer.Serializer.class)
    @JsonDeserialize(using = ChatFunctionCallArgumentsSerializerAndDeserializer.Deserializer.class)
    abstract JsonNode getArguments();

}
