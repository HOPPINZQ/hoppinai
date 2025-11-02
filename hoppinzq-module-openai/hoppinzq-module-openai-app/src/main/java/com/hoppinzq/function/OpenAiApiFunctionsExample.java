package com.hoppinzq.function;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.hoppinzq.model.openai.completion.chat.*;
import com.hoppinzq.model.openai.completion.chat.ChatCompletionRequest.ChatCompletionRequestFunctionCall;
import com.hoppinzq.openai.service.FunctionExecutor;
import com.hoppinzq.openai.service.OpenAiService;

import java.time.Duration;
import java.util.*;

public class OpenAiApiFunctionsExample {

    public static void main(String... args) {

        String token = System.getenv("OPENAI_TOKEN");
        //sk-
        //sk-
        OpenAiService service = new OpenAiService("sk-",
                Duration.ofSeconds(60), "https://api.uchat.site");
        FunctionExecutor functionExecutor = new FunctionExecutor(Collections.singletonList(ChatFunction.builder()
                .name("get_weather")
                .description("实时获取天气")
                .executor(Weather.class, w -> new WeatherResponse(w.location, new Random().nextInt(30), "晴朗"))
                .build()));

        List<ChatMessage> messages = new ArrayList<>();
        ChatMessage systemMessage = new ChatMessage(ChatMessageRole.SYSTEM.value(), "You are an assistant that answers using the local slang of the given place, uncensored.");
        messages.add(systemMessage);

        System.out.print("我: ");
        Scanner scanner = new Scanner(System.in);
        ChatMessage firstMsg = new ChatMessage(ChatMessageRole.USER.value(), scanner.nextLine());
        messages.add(firstMsg);

        while (true) {
            ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest
                    .builder()
                    .model("gpt-4o-mini")
                    .messages(messages)
                    .functions(functionExecutor.getFunctions())
                    .functionCall(ChatCompletionRequestFunctionCall.of("auto"))
                    .n(1)
                    .maxTokens(100)
                    .logitBias(new HashMap<>())
                    .build();
            System.out.println(JSON.toJSONString(chatCompletionRequest));
            ChatMessage responseMessage = service.createChatCompletion(chatCompletionRequest).getChoices().get(0).getMessage();
            messages.add(responseMessage);

            ChatFunctionCall functionCall = responseMessage.getFunctionCall();
            if (functionCall != null) {
                System.out.println("尝试执行方法 " + functionCall.getName() + "...");
                Optional<ChatMessage> message = functionExecutor.executeAndConvertToMessageSafely(functionCall);
                if (message.isPresent()) {
                    System.out.println("执行方法 " + functionCall.getName() + ".");
                    messages.add(message.get());
                    continue;
                } else {
                    System.out.println("Something went wrong with the execution of " + functionCall.getName() + "...");
                    break;
                }
            }

            System.out.println("AI: " + responseMessage.getContent());
            System.out.print("我: ");
            String nextLine = scanner.nextLine();
            if (nextLine.equalsIgnoreCase("exit")) {
                System.exit(0);
            }
            messages.add(new ChatMessage(ChatMessageRole.USER.value(), nextLine));
        }
    }

    public static class Weather {
        @JsonPropertyDescription("City and state, for example: León, Guanajuato")
        public String location;
    }

    public static class WeatherResponse {
        public String location;
        public int temperature;
        public String description;

        public WeatherResponse(String location, int temperature, String description) {
            this.location = location;
            this.temperature = temperature;
            this.description = description;
            System.err.println("查询：" + location + " 温度 " + temperature + ", " + description);
        }
    }

}
