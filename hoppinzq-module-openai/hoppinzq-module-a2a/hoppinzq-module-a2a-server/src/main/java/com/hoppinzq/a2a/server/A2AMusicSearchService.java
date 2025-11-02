package com.hoppinzq.a2a.server;

import com.hoppinzq.a2a.model.*;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class A2AMusicSearchService {
    /**
     * Create translation agent card
     */
    public AgentCard createSearchAgentCard() {
        AgentProvider provider = new AgentProvider(
                "hoppinai",
                "https://hoppinzq.com"
        );

        AgentCapabilities capabilities = new AgentCapabilities(
                true,  // streaming
                true,  // pushNotifications
                true   // stateTransitionHistory
        );

        AgentAuthentication authentication = new AgentAuthentication(
                List.of("bearer"),
                null
        );

        AgentSkill skill = new AgentSkill(
                "ai-music-saerch",
                "AI音乐搜索服务",
                "AI帮助你搜索音乐",
                List.of("音乐", "ai"),
                List.of(
                        "例子: 搜索APT这首音乐",
                        "例子: 帮我搜索一首音乐，名字是烟花易冷"
                ),
                List.of("text"),
                List.of("text")
        );

        return new AgentCard(
                "AI音乐搜索机器人",
                "专业的搜索AI，帮你搜索音乐",
                "http://localhost:9003/a2a",
                provider,
                "1.0.0",
                "http://localhost:8080/docs",
                capabilities,
                authentication,
                List.of("text"),
                List.of("text"),
                List.of(skill)
        );
    }


    /**
     * Create translation task handler using ChatClient
     */
    public TaskHandler createSearchTaskHandler(ChatModel chatModel) {
        ChatClient chatClient = ChatClient.create(chatModel);

        return (task, message) -> {
            try {
                // Extract text content from message parts
                String userMessage = extractTextFromMessage(message);

                if (userMessage == null || userMessage.trim().isEmpty()) {
                    return createErrorTask(task, "No text content found in the message");
                }
                System.out.println("userMessage: " + userMessage);
                // Create translation prompt
                String translationPrompt = createMusicPrompt(userMessage);

                // Call ChatClient for translation
                String translatedText = chatClient
                        .prompt(translationPrompt)
                        .call()
                        .content();

                // Create response message with translation
                TextPart responsePart = new TextPart(translatedText, null);
                Message responseMessage = new Message(
                        UUID.randomUUID().toString(),
                        "message",
                        "assistant",
                        List.of(responsePart),
                        message.contextId(),
                        task.id(),
                        List.of(message.messageId()),
                        null
                );

                // Create completed status
                TaskStatus completedStatus = new TaskStatus(
                        TaskState.COMPLETED,
                        null,  // No status message
                        Instant.now().toString()
                );

                // Add response to history
                List<Message> updatedHistory = task.history() != null ?
                        List.of(task.history().toArray(new Message[0])) :
                        List.of();
                updatedHistory = List.of(
                        java.util.stream.Stream.concat(
                                updatedHistory.stream(),
                                java.util.stream.Stream.of(message, responseMessage)
                        ).toArray(Message[]::new)
                );

                return new Task(
                        task.id(),
                        task.contextId(),
                        task.kind(),
                        completedStatus,
                        task.artifacts(),
                        updatedHistory,
                        task.metadata()
                );

            } catch (Exception e) {
                return createErrorTask(task, "Translation failed: " + e.getMessage());
            }
        };
    }

    /**
     * Create translation prompt for ChatClient
     */
    private String createMusicPrompt(String text) {
        return String.format("""
                        你是一个有用的助手，可以使用一些工具来回答用户的问题，现在有这些工具：
                                 
                                 工具1:
                                 	- 工具名称: search_music
                                 	- 工具描述: 搜索音乐
                                 	- 工具参数：
                                 	    - searchContent:
                                 	        - 类型：string
                                 	        - 描述：搜索内容
                                 	        - 是否必填：是
                                 	- 必填项: ["searchContent"]
                                 根据用户的问题选择合适的工具。如果不需要工具，请直接回复。
                                 
                                 重要提示：当您需要使用工具时，您必须只使用以下确切的JSON对象格式进行响应，而不能使用其他格式，也不能额外输出其他内容：
                                 
                                 {
                                     "tool":"工具名称",
                                     "arguments":{
                                         "arguments_name"："value"
                                     }
                                 }
                                 
                                 收到工具的响应后：
                                     - 1、将原始数据转换为自然的对话式响应
                                     - 2、保持回答简洁但信息丰富
                                     - 3、关注最相关的信息
                                     - 4、使用用户问题中的适当上下文
                                     - 5、永远不要用完全相同的参数重新进行之前的工具调用
                                 
                     现在用户询问: %s
                """, text);
    }

    /**
     * Extract text content from message parts
     */
    private String extractTextFromMessage(Message message) {
        if (message.parts() == null || message.parts().isEmpty()) {
            return null;
        }

        StringBuilder textBuilder = new StringBuilder();
        for (Part part : message.parts()) {
            if (part instanceof TextPart textPart) {
                if (textBuilder.length() > 0) {
                    textBuilder.append("\n");
                }
                textBuilder.append(textPart.text());
            }
        }

        return textBuilder.toString();
    }


    /**
     * Create error task for translation failures
     */
    private Task createErrorTask(Task originalTask, String errorMessage) {
        TaskStatus errorStatus = new TaskStatus(
                TaskState.FAILED,
                null,  // No status message
                Instant.now().toString()
        );

        return new Task(
                originalTask.id(),
                originalTask.contextId(),
                originalTask.kind(),
                errorStatus,
                originalTask.artifacts(),
                originalTask.history(),
                originalTask.metadata()
        );
    }
}

