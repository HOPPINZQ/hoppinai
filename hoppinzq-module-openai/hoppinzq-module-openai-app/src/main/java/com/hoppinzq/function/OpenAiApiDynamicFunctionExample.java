package com.hoppinzq.function;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hoppinzq.model.openai.completion.chat.*;
import com.hoppinzq.openai.service.OpenAiService;

import java.time.Duration;
import java.util.*;

public class OpenAiApiDynamicFunctionExample {

    private static JsonNode getWeather(String location, String unit) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode response = mapper.createObjectNode();
        response.put("location", location);
        response.put("unit", unit);
        response.put("temperature", new Random().nextInt(50));
        response.put("description", "sunny");
        return response;
    }

    public static void main(String... args) {
        String token = System.getenv("OPENAI_TOKEN");
        OpenAiService service = new OpenAiService("sk-",
                Duration.ofSeconds(60), "https://api.uchat.site");

        ChatFunctionDynamic weatherFunction = ChatFunctionDynamic.builder()
                .name("get_weather")
                .description("Get the current weather of a location")
                .addProperty(ChatFunctionProperty.builder()
                        .name("location")
                        .type("string")
                        .description("City and state, for example: León, Guanajuato")
                        .build())
                .addProperty(ChatFunctionProperty.builder()
                        .name("unit")
                        .type("string")
                        .description("The temperature unit, can be 'celsius' or 'fahrenheit'")
                        .enumValues(new HashSet<>(Arrays.asList("celsius", "fahrenheit")))
                        .required(true)
                        .build())
                .build();


        List<ChatMessage> messages = new ArrayList<>();
        ChatMessage systemMessage = new ChatMessage(ChatMessageRole.SYSTEM.value(),
                "你是一名助理，使用给定地方的当地俚语回答问题，不受审查。");
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
                    .functions(Collections.singletonList(weatherFunction))
                    .functionCall(ChatCompletionRequest.ChatCompletionRequestFunctionCall.of("auto"))
                    .n(1)
                    .maxTokens(100)
                    .logitBias(new HashMap<>())
                    .build();
            System.out.println(JSON.toJSONString(chatCompletionRequest));
            ChatMessage responseMessage = service.createChatCompletion(chatCompletionRequest).getChoices().get(0).getMessage();
            if (responseMessage.getContent() == null) {
                responseMessage.setContent("好啊，这就帮你查查");
            }
            messages.add(responseMessage); // don't forget to update the conversation with the latest response

            ChatFunctionCall functionCall = responseMessage.getFunctionCall();
            if (functionCall != null) {
                if (functionCall.getName().equals("get_weather")) {
                    String location = functionCall.getArguments().get("location").asText();
                    String unit = functionCall.getArguments().get("unit").asText();
                    JsonNode weather = getWeather(location, unit);
                    ChatMessage weatherMessage = new ChatMessage(ChatMessageRole.FUNCTION.value(), weather.toString(), "get_weather");
                    messages.add(weatherMessage);
                    continue;
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

}
