package com.hoppinzq.openai.service;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.fasterxml.jackson.databind.node.TextNode;

import java.io.IOException;

public class ChatFunctionCallArgumentsSerializerAndDeserializer {

    private final static ObjectMapper MAPPER = new ObjectMapper();

    private ChatFunctionCallArgumentsSerializerAndDeserializer() {
    }

    public static class Serializer extends JsonSerializer<JsonNode> {

        private Serializer() {
        }

        /**
         * 将JsonNode对象序列化为JSON字符串。
         *
         * @param value       要序列化的JsonNode对象
         * @param gen         JsonGenerator对象，用于生成JSON字符串
         * @param serializers SerializerProvider对象，提供序列化所需的上下文信息
         * @throws IOException 如果在序列化过程中发生I/O错误
         */
        @Override
        public void serialize(JsonNode value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
            if (value == null) {
                gen.writeNull();
            } else {
                gen.writeString(value instanceof TextNode ? value.asText() : value.toPrettyString());
            }
        }
    }

    public static class Deserializer extends JsonDeserializer<JsonNode> {

        private Deserializer() {
        }

        /**
         * 反序列化JSON数据。
         *
         * @param p    用于读取JSON数据的JsonParser对象。
         * @param ctxt 反序列化上下文，提供反序列化过程中的相关信息。
         * @return 反序列化后的JsonNode对象，如果输入为null或解析失败则返回null。
         * @throws IOException 如果在读取或解析JSON时发生I/O错误。
         */
        @Override
        public JsonNode deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            String json = p.getValueAsString();
            if (json == null || p.currentToken() == JsonToken.VALUE_NULL) {
                return null;
            }

            try {
                JsonNode node = null;
                try {
                    node = MAPPER.readTree(json);
                } catch (JsonParseException ignored) {
                }
                if (node == null || node.getNodeType() == JsonNodeType.MISSING) {
                    node = MAPPER.readTree(p);
                }
                return node;
            } catch (Exception ex) {
                ex.printStackTrace();
                return null;
            }
        }
    }

}
