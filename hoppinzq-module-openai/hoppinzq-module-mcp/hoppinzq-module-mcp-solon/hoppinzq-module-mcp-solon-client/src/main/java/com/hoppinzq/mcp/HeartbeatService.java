package com.hoppinzq.mcp;

import org.noear.solon.ai.mcp.server.annotation.McpServerEndpoint;

@McpServerEndpoint(sseEndpoint = "/mcp/sse", heartbeatInterval = "60s")
public class HeartbeatService {
    // ...
}
