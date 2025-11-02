package com.hoppinzq.a2a.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.List;

/**
 * A2A Server Spring Boot Application
 */
@SpringBootApplication
public class A2AServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(A2AServerApplication.class, args);
    }

    @Bean
    public List<A2AServer> a2aServer(ObjectMapper objectMapper, ChatModel chatModel,
                                     A2AMusicSearchService a2aMusicSearchService, A2ATranslationService a2ATranslationService) {
        return List.of(
                new A2AServer(a2aMusicSearchService.createSearchAgentCard(), a2aMusicSearchService.createSearchTaskHandler(chatModel), objectMapper),
                new A2AServer(a2ATranslationService.createTranslationAgentCard(), a2ATranslationService.createTranslationTaskHandler(chatModel), objectMapper)
        );
    }
}
