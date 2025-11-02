package com.hoppinzq.mcp;

import org.noear.solon.ai.chat.tool.FunctionTool;
import org.noear.solon.ai.chat.tool.ToolProvider;
import org.noear.solon.ai.mcp.McpChannel;
import org.noear.solon.ai.mcp.client.McpClientProvider;
import org.noear.solon.ai.mcp.server.annotation.McpServerEndpoint;

import java.util.Collection;

// 将SSE服务代理为STDIO服务
@McpServerEndpoint(channel = McpChannel.STDIO)
public class SseToStdioProxy implements ToolProvider {
    private McpClientProvider sseClient = McpClientProvider.builder()
        .apiUrl("http://remote-service/mcp/sse")
        .build();
    
    @Override
    public Collection<FunctionTool> getTools() {
        return sseClient.getTools();
    }
}