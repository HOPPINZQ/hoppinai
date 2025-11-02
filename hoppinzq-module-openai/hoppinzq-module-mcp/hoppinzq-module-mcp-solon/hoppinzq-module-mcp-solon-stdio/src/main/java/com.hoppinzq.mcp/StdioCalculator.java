package com.hoppinzq.mcp;

import org.noear.solon.ai.annotation.ToolMapping;
import org.noear.solon.ai.mcp.McpChannel;
import org.noear.solon.ai.mcp.server.annotation.McpServerEndpoint;
import org.noear.solon.annotation.Param;

@McpServerEndpoint(channel = McpChannel.STDIO)
public class StdioCalculator {
    @ToolMapping(description = "加法计算")
    public int add(@Param int a, @Param int b) {
        return a + b;
    }
    
    @ToolMapping(description = "减法计算")
    public int subtract(@Param int a, @Param int b) {
        return a - b;
    }
}

