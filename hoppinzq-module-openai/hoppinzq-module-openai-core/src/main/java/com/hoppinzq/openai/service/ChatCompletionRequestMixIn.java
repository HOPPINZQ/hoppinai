package com.hoppinzq.openai.service;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.hoppinzq.model.openai.completion.chat.ChatCompletionRequest;

public abstract class ChatCompletionRequestMixIn {

    /**
     * 获取ChatCompletion请求中的函数调用信息。
     * 该方法用于获取与ChatCompletion请求相关的函数调用信息，通过序列化和反序列化处理。
     *
     * @return ChatCompletionRequest.ChatCompletionRequestFunctionCall 返回ChatCompletion请求的函数调用信息。
     */
    @JsonSerialize(using = ChatCompletionRequestSerializerAndDeserializer.Serializer.class)
    @JsonDeserialize(using = ChatCompletionRequestSerializerAndDeserializer.Deserializer.class)
    abstract ChatCompletionRequest.ChatCompletionRequestFunctionCall getFunctionCall();

}
