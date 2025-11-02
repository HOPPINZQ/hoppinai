//package com.hoppinzq.a2a.server;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.hoppinzq.a2a.model.*;
//import org.springframework.ai.chat.client.ChatClient;
//import org.springframework.ai.chat.model.ChatModel;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//import java.time.Instant;
//import java.util.List;
//import java.util.UUID;
//
///**
// * A2A server configuration class - AI Translation Bot
// */
//@Configuration
//public class A2ATranslationServerConfiguration {
//
//    /**
//     * Configure A2AServer bean
//     */
//    @Bean
//    public A2AServer a2aServer(ObjectMapper objectMapper, ChatModel chatModel) {
//        // Create translation agent card
//        AgentCard agentCard = createTranslationAgentCard();
//
//        // Create translation task handler
//        TaskHandler taskHandler = createTranslationTaskHandler(chatModel);
//
//        return new A2AServer(agentCard, taskHandler, objectMapper);
//    }
//
//    /**
//     * Create translation agent card
//     */
//    private AgentCard createTranslationAgentCard() {
//        AgentProvider provider = new AgentProvider(
//            "hoppinai",
//            "https://hoppinzq.com"
//        );
//
//        AgentCapabilities capabilities = new AgentCapabilities(
//            true,  // streaming
//            true,  // pushNotifications
//            true   // stateTransitionHistory
//        );
//
//        AgentAuthentication authentication = new AgentAuthentication(
//            List.of("bearer"),
//            null
//        );
//
//        AgentSkill skill = new AgentSkill(
//            "ai-translator",
//            "AI翻译服务",
//            "专业的AI翻译官，能翻译多国语音. 包括英语，汉语，日语，法语，西班牙语，德语等等",
//            List.of("翻译", "语音", "ai", "说多种语言的"),
//            List.of(
//                "例子: 将 'Hello World' 翻译为 汉语",
//                "例子: 请把这句话翻译成英文: '你好'",
//                "例子: 将下面的法语翻译为汉语: 'Bonjour le monde'"
//            ),
//            List.of("text"),
//            List.of("text")
//        );
//
//        return new AgentCard(
//            "AI翻译机器人",
//            "专业的翻译AI，能准确地翻译多国语言，包括英语，汉语，日语，法语，西班牙语，德语等等.",
//            "http://localhost:8080/a2a",
//            provider,
//            "1.0.0",
//            "http://localhost:8080/docs",
//            capabilities,
//            authentication,
//            List.of("text"),
//            List.of("text"),
//            List.of(skill)
//        );
//    }
//
//    /**
//     * Create translation task handler using ChatClient
//     */
//    private TaskHandler createTranslationTaskHandler(ChatModel chatModel) {
//        ChatClient chatClient = ChatClient.create(chatModel);
//
//        return (task, message) -> {
//            try {
//                // Extract text content from message parts
//                String textToTranslate = extractTextFromMessage(message);
//
//                if (textToTranslate == null || textToTranslate.trim().isEmpty()) {
//                    return createErrorTask(task, "No text content found in the message");
//                }
//
//                // Create translation prompt
//                String translationPrompt = createTranslationPrompt(textToTranslate);
//
//                // Call ChatClient for translation
//                String translatedText = chatClient
//                    .prompt(translationPrompt)
//                    .call()
//                    .content();
//
//                // Create response message with translation
//                TextPart responsePart = new TextPart(translatedText, null);
//                Message responseMessage = new Message(
//                    UUID.randomUUID().toString(),
//                    "message",
//                    "assistant",
//                    List.of(responsePart),
//                    message.contextId(),
//                    task.id(),
//                    List.of(message.messageId()),
//                    null
//                );
//
//                // Create completed status
//                TaskStatus completedStatus = new TaskStatus(
//                    TaskState.COMPLETED,
//                    null,  // No status message
//                    Instant.now().toString()
//                );
//
//                // Add response to history
//                List<Message> updatedHistory = task.history() != null ?
//                    List.of(task.history().toArray(new Message[0])) :
//                    List.of();
//                updatedHistory = List.of(
//                    java.util.stream.Stream.concat(
//                        updatedHistory.stream(),
//                        java.util.stream.Stream.of(message, responseMessage)
//                    ).toArray(Message[]::new)
//                );
//
//                return new Task(
//                    task.id(),
//                    task.contextId(),
//                    task.kind(),
//                    completedStatus,
//                    task.artifacts(),
//                    updatedHistory,
//                    task.metadata()
//                );
//
//            } catch (Exception e) {
//                return createErrorTask(task, "Translation failed: " + e.getMessage());
//            }
//        };
//    }
//
//    /**
//     * Extract text content from message parts
//     */
//    private String extractTextFromMessage(Message message) {
//        if (message.parts() == null || message.parts().isEmpty()) {
//            return null;
//        }
//
//        StringBuilder textBuilder = new StringBuilder();
//        for (Part part : message.parts()) {
//            if (part instanceof TextPart textPart) {
//                if (textBuilder.length() > 0) {
//                    textBuilder.append("\n");
//                }
//                textBuilder.append(textPart.text());
//            }
//        }
//
//        return textBuilder.toString();
//    }
//
//    /**
//     * Create translation prompt for ChatClient
//     */
//    private String createTranslationPrompt(String text) {
//        return String.format("""
//            You are a professional translator. Please translate the following text to the most appropriate target language.
//
//            Instructions:
//            1. If the text is in Chinese, translate to English
//            2. If the text is in English, translate to Chinese
//            3. If the text is in other languages, translate to Chinese
//            4. Maintain the original meaning and context
//            5. Provide natural, fluent translations
//            6. Only return the translated text, no explanations
//
//            Text to translate: %s
//            """, text);
//    }
//
//    /**
//     * Create error task for translation failures
//     */
//    private Task createErrorTask(Task originalTask, String errorMessage) {
//        TaskStatus errorStatus = new TaskStatus(
//            TaskState.FAILED,
//            null,  // No status message
//            Instant.now().toString()
//        );
//
//        return new Task(
//            originalTask.id(),
//            originalTask.contextId(),
//            originalTask.kind(),
//            errorStatus,
//            originalTask.artifacts(),
//            originalTask.history(),
//            originalTask.metadata()
//        );
//    }
//}
