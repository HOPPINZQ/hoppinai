package com.hoppinzq.openai.service;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.kjetland.jackson.jsonSchema.JsonSchemaConfig;
import com.kjetland.jackson.jsonSchema.JsonSchemaGenerator;

import java.io.IOException;

public class ChatFunctionParametersSerializer extends JsonSerializer<Class<?>> {

    private final ObjectMapper mapper = new ObjectMapper();
    private final JsonSchemaConfig config = JsonSchemaConfig.vanillaJsonSchemaDraft4();
    private final JsonSchemaGenerator jsonSchemaGenerator = new JsonSchemaGenerator(mapper, config);

    /**
     * 序列化给定类的JSON Schema。
     *
     * @param value       要序列化为JSON Schema的类实例。如果为null，则输出null。
     * @param gen         JSON生成器，用于输出JSON数据。
     * @param serializers 序列化提供者，用于获取序列化器。
     * @throws IOException      如果在序列化过程中发生I/O错误。
     * @throws RuntimeException 如果生成JSON Schema失败。
     */
    @Override
    public void serialize(Class<?> value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        if (value == null) {
            gen.writeNull();
        } else {
            try {
                JsonNode schema = jsonSchemaGenerator.generateJsonSchema(value);
                gen.writeObject(schema);
            } catch (Exception e) {
                throw new RuntimeException("生成json失败", e);
            }
        }
    }
}





