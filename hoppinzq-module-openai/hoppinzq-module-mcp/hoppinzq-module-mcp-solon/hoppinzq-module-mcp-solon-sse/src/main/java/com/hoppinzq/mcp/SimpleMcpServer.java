package com.hoppinzq.mcp;

import org.noear.solon.Solon;
import org.noear.solon.ai.annotation.ToolMapping;
import org.noear.solon.ai.mcp.server.annotation.McpServerEndpoint;
import org.noear.solon.annotation.Param;

@McpServerEndpoint(sseEndpoint = "/mcp/sse")
public class SimpleMcpServer {

    @ToolMapping(description = "问候服务")
    public String hello(@Param(name="name", description = "用户名") String name) {
        return "你好, " + name;
    }
}


