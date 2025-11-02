package com.hoppinzq.openai.service;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.hoppinzq.model.openai.completion.chat.ChatCompletionRequest;

import java.io.IOException;

public class ChatCompletionRequestSerializerAndDeserializer {

    public static class Serializer extends JsonSerializer<ChatCompletionRequest.ChatCompletionRequestFunctionCall> {
        /**
         * 序列化ChatCompletionRequest.ChatCompletionRequestFunctionCall对象。
         * 根据value对象的不同情况，将对象序列化为JSON格式。
         * 如果value为null或其name属性为null，则序列化为null。
         * 如果name属性为"none"或"auto"，则直接序列化该name属性值。
         * 否则，将对象序列化为包含name属性的JSON对象。
         *
         * @param value       要序列化的ChatCompletionRequest.ChatCompletionRequestFunctionCall对象。
         * @param gen         JsonGenerator对象，用于生成JSON数据。
         * @param serializers SerializerProvider对象，提供序列化相关的服务。
         * @throws IOException 如果在序列化过程中发生I/O错误。
         */
        @Override
        public void serialize(ChatCompletionRequest.ChatCompletionRequestFunctionCall value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
            if (value == null || value.getName() == null) {
                gen.writeNull();
            } else if ("none".equals(value.getName()) || "auto".equals(value.getName())) {
                gen.writeString(value.getName());
            } else {
                gen.writeStartObject();
                gen.writeFieldName("name");
                gen.writeString(value.getName());
                gen.writeEndObject();
            }
        }
    }

    public static class Deserializer extends JsonDeserializer<ChatCompletionRequest.ChatCompletionRequestFunctionCall> {
        /**
         * 反序列化函数调用请求。
         * 此方法用于将JSON解析为ChatCompletionRequest.ChatCompletionRequestFunctionCall对象。
         *
         * @param p    JSON解析器，用于解析JSON数据。
         * @param ctxt 反序列化上下文，提供反序列化过程中的额外信息。
         * @return ChatCompletionRequest.ChatCompletionRequestFunctionCall 反序列化后的函数调用请求对象。
         * @throws IOException 如果在解析过程中发生I/O错误。
         */
        @Override
        public ChatCompletionRequest.ChatCompletionRequestFunctionCall deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            if (p.getCurrentToken().isStructStart()) {
                p.nextToken();
                p.nextToken();
            }
            return new ChatCompletionRequest.ChatCompletionRequestFunctionCall(p.getValueAsString());
        }
    }
}
