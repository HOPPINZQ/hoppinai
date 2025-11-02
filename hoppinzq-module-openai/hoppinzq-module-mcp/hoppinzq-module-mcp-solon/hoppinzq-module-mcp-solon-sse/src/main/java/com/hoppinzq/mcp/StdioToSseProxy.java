package com.hoppinzq.mcp;

import org.noear.solon.ai.chat.tool.FunctionTool;
import org.noear.solon.ai.chat.tool.ToolProvider;
import org.noear.solon.ai.mcp.McpChannel;
import org.noear.solon.ai.mcp.client.McpClientProvider;
import org.noear.solon.ai.mcp.client.McpServerParameters;
import org.noear.solon.ai.mcp.server.annotation.McpServerEndpoint;

import java.util.Collection;

// 将STDIO服务代理为SSE服务
@McpServerEndpoint(sseEndpoint = "/proxy/sse")
public class StdioToSseProxy implements ToolProvider {
    private McpClientProvider stdioClient = McpClientProvider.builder()
        .channel(McpChannel.STDIO)
        .serverParameters(McpServerParameters.builder("java")
            .args("-jar", "path/to/stdio-service.jar")
            .build())
        .build();
    
    @Override
    public Collection<FunctionTool> getTools() {
        return stdioClient.getTools();
    }
}

