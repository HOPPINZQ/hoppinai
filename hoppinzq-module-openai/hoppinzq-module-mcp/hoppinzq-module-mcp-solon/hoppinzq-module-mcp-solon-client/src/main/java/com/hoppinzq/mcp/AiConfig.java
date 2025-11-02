package com.hoppinzq.mcp;

import org.noear.solon.ai.chat.ChatConfig;
import org.noear.solon.ai.chat.ChatModel;
import org.noear.solon.ai.chat.ChatResponse;
import org.noear.solon.ai.mcp.client.McpClientProvider;
import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.annotation.Inject;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Configuration
public class AiConfig {
    @Bean
    public ChatModel chatModel(
            @Inject("${solon.ai.chat.config}") ChatConfig chatConfig,
            @Inject("mcp-weather") McpClientProvider toolProvider) {
        
        return ChatModel.of(chatConfig)
            .defaultToolsAdd(toolProvider)
            .build();
    }
    
    @Bean("mcp-weather")
    public McpClientProvider weatherToolProvider() {
        return McpClientProvider.builder()
            .apiUrl("http://weather-service/mcp/sse")
            .build();
    }
}


