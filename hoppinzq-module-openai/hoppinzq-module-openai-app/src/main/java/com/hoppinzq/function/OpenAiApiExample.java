package com.hoppinzq.function;

import com.alibaba.fastjson.JSONObject;
import com.hoppinzq.model.openai.completion.chat.ChatCompletionRequest;
import com.hoppinzq.model.openai.completion.chat.ChatCompletionResult;
import com.hoppinzq.model.openai.completion.chat.ChatMessage;
import com.hoppinzq.model.openai.completion.chat.ChatMessageRole;
import com.hoppinzq.openai.service.OpenAiService;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

class OpenAiApiExample {
    public static void main(String... args) {
        //String token = System.getenv("OPENAI_TOKEN");

        OpenAiService service = new OpenAiService("sk-",
                Duration.ofSeconds(60), "https://api.uchat.site");

        System.out.println("\nCreating completion...");
//        CompletionRequest completionRequest = CompletionRequest.builder()
//                .model("gpt-3.5-turbo")
//                .prompt("Somebody once told me the world is gonna roll me")
//                .echo(true)
//                .user("testing")
//                .n(3)
//                .build();
//        service.createCompletion(completionRequest).getChoices().forEach(System.out::println);
//
//        System.out.println("\nCreating Image...");
//        CreateImageRequest request = CreateImageRequest.builder()
//                .prompt("A cow breakdancing with a turtle")
//                .build();
//
//        System.out.println("\nImage is located at:");
//        System.out.println(service.createImage(request).getData().get(0).getUrl());

        System.out.println("Streaming chat completion...");
        final List<ChatMessage> messages = new ArrayList<>();
        final ChatMessage userMessage = new ChatMessage(ChatMessageRole.USER.value(), "搜索烟花易冷这首音乐");
        messages.add(userMessage);

        List<JSONObject> tools = new ArrayList<>();
        JSONObject tool = new JSONObject();
        tool.put("type", "function");
        tool.put("function", new JSONObject().fluentPut("name", "search")
                .fluentPut("description", "搜索相关内容")
                .fluentPut("parameters", new JSONObject()
                        .fluentPut("type", "object")
                        .fluentPut("properties", new JSONObject()
                                .fluentPut("content", new JSONObject()
                                        .fluentPut("type", "string")
                                        .fluentPut("description", "搜索内容"))
                                .fluentPut("type", new JSONObject()
                                        .fluentPut("type", "string")
                                        .fluentPut("description", "搜索类型，可以是音乐，网站和其他，默认是音乐")
                                        .fluentPut("enum", new String[]{"music", "website", "other"})))
                        .fluentPut("required", new String[]{"content"})));
        tools.add(tool);
        ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest
                .builder()
                .model("gpt-4o-mini")
                .messages(messages)
                .toolChoice("auto")
                .tools(tools)
                .n(1)
                .logitBias(new HashMap<>())
                .build();
        System.err.println(JSONObject.toJSONString(chatCompletionRequest));
        ChatCompletionResult chatCompletion = service.createChatCompletion(chatCompletionRequest);
        System.err.println(JSONObject.toJSONString(chatCompletion));
        service.shutdownExecutor();
    }
}
