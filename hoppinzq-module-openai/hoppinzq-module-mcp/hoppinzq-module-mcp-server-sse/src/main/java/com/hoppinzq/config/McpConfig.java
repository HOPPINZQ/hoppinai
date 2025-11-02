package com.hoppinzq.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.modelcontextprotocol.server.transport.WebFluxSseServerTransportProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;

@Configuration
class McpConfig {
    @Bean
    WebFluxSseServerTransportProvider webFluxSseServerTransportProvider(ObjectMapper mapper) {
        return new WebFluxSseServerTransportProvider(mapper, "/mcp/message");
    }

    @Bean
    RouterFunction<?> mcpRouterFunction(WebFluxSseServerTransportProvider transportProvider) {
        return transportProvider.getRouterFunction();
    }

    //http://175.178.115.82:8099/openai/chatDeepSeek?message=%E4%BD%A0%E5%A5%BD%E5%95%8A&chatId=6f57c35137b240db9bb2b078aeef40c6&userId=274795671465279488&settingId=null
}
