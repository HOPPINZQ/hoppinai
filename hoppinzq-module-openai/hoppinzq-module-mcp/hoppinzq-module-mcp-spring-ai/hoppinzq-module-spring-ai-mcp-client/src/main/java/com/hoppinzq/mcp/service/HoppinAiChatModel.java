package com.hoppinzq.mcp.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hoppinzq.model.openai.completion.chat.*;
import com.hoppinzq.openai.service.OpenAiService;
import lombok.SneakyThrows;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.ToolResponseMessage;
import org.springframework.ai.chat.metadata.ChatResponseMetadata;
import org.springframework.ai.chat.metadata.DefaultUsage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Component
public class HoppinAiChatModel implements ChatModel {

    @Value("${spring.ai.openai.api-key}")
    private String apiKey;
    @Value("${spring.ai.openai.base-url}")
    private String baseUrl;
    @Value("${spring.ai.openai.chat.model}")
    private String model;

    @SneakyThrows
    @Override
    public ChatResponse call(Prompt prompt) {
        List<Message> instructions = prompt.getInstructions();
        List<ChatMessage> chatMessageList = new ArrayList<>();
        for (Message instruction : instructions) {
            ChatMessage chatMessage = new ChatMessage();
            String role = instruction.getMessageType().name().toLowerCase(Locale.ROOT);
            chatMessage.setRole(role);
            if ("tool".equals(role)) {
                var toolResponseMessage = (ToolResponseMessage) instruction;
                List<ToolResponseMessage.ToolResponse> responses = toolResponseMessage.getResponses();
                chatMessage.setTool_call_id(responses.get(0).id());
                chatMessage.setContent(responses.get(0).responseData());
            } else if ("assistant".equals(role)) {
                var assistantMessage = (AssistantMessage) instruction;
                if (assistantMessage.hasToolCalls()) {
                    List<AssistantMessage.ToolCall> toolCalls = assistantMessage.getToolCalls();
                    if (toolCalls.size() > 0) {
                        AssistantMessage.ToolCall toolCall = toolCalls.get(0);
                        ObjectMapper objectMapper = new ObjectMapper();
                        chatMessage.setToolCalls(
                                List.of(new ChatToolCall(toolCall.id(), toolCall.type(),
                                        new ChatFunctionCall(toolCall.name(), objectMapper.readTree(toolCall.arguments()))
                                )));
                    }
                } else {
                    chatMessage.setContent(instruction.getText());
                }
            } else {
                chatMessage.setContent(instruction.getText());
            }
            chatMessageList.add(chatMessage);
        }
        // chatMessageList最后两个放在最前面
        ChatMessage chatMessage = chatMessageList.get(chatMessageList.size() - 1);
        chatMessageList.remove(chatMessageList.size() - 1);
        chatMessageList.add(0, chatMessage);
        chatMessage = chatMessageList.get(chatMessageList.size() - 1);
        chatMessageList.remove(chatMessageList.size() - 1);
        chatMessageList.add(0, chatMessage);

        var options = prompt.getOptions();
        OpenAiService openAiService = new OpenAiService(apiKey, Duration.ofSeconds(60), baseUrl);
        ChatCompletionResult chatCompletion = openAiService.createChatCompletion(ChatCompletionRequest.builder()
                .model(model)
                .messages(chatMessageList)
                .stream(false)
                .build());
        ChatResponseMetadata chatResponseMetadata = ChatResponseMetadata.builder()
                .id(chatCompletion.getId())
                .model(chatCompletion.getModel())
                .usage(new DefaultUsage((int) chatCompletion.getUsage().getPromptTokens(),
                        (int) chatCompletion.getUsage().getCompletionTokens(),
                        (int) chatCompletion.getUsage().getTotalTokens()))
                .build();
        List<Generation> generations = new ArrayList<>();
        for (ChatCompletionChoice choice : chatCompletion.getChoices()) {
            ChatMessage message = choice.getMessage();
            AssistantMessage assistantMessage = new AssistantMessage(message.getContent());
            Generation generation = new Generation(assistantMessage);
            generations.add(generation);
        }
        return new ChatResponse(generations, chatResponseMetadata);
    }
}

