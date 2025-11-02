package com.hoppinzq.mcp;

import org.noear.solon.ai.mcp.McpChannel;
import org.noear.solon.ai.mcp.client.McpClientProvider;
import org.noear.solon.ai.mcp.client.McpServerParameters;

import java.time.Duration;
import java.util.Map;

public class McpClientDemo {
    public static void main(String[] args) {
        // 连接SSE服务
        McpClientProvider sseClient = McpClientProvider.builder()
            .apiUrl("http://localhost:8080/mcp/sse")
            .requestTimeout(Duration.ofSeconds(20))
            .build();
        
        String greeting = sseClient.callToolAsText("hello", Map.of("name", "张三")).getContent();
        System.out.println(greeting);
        
        // 连接STDIO服务
        McpClientProvider stdioClient = McpClientProvider.builder()
            .channel(McpChannel.STDIO)
            .serverParameters(McpServerParameters.builder("java")
                .args("-jar", "path/to/stdio-service.jar")
                .build())
            .build();
            
        String sum = stdioClient.callToolAsText("add", Map.of("a", 5, "b", 3)).getContent();
        System.out.println("5 + 3 = " + sum);
    }
}
