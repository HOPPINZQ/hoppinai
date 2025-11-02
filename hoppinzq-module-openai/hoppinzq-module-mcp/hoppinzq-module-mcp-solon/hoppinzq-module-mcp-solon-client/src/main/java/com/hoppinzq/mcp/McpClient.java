package com.hoppinzq.mcp;

import org.noear.solon.ai.mcp.client.McpClientProvider;

import java.util.Map;

public class McpClient {
    public void test() {
        McpClientProvider clientProvider = McpClientProvider.builder()
                .apiUrl("http://localhost:8080/mcp/sse")
                .build();

        String rst1 = clientProvider.callToolAsText("get_weather", Map.of("city", "杭州"))
                .getContent();

        String rst2 = clientProvider.readResourceAsText("weather://cities")
                .getContent();
    }
}
