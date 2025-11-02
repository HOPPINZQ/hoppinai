package com.hoppinzq.mcp;

import org.noear.solon.ai.annotation.ToolMapping;
import org.noear.solon.ai.mcp.server.annotation.McpServerEndpoint;
import org.noear.solon.annotation.Param;

// 教育工具服务
@McpServerEndpoint(name="edu-tools", sseEndpoint = "/edu/sse")
public class EducationTools {
    @ToolMapping(description = "生成数学题")
    public String generateMathProblem(@Param(description = "难度级别") String level) {
        if("easy".equals(level)) {
            return "3 + 5 = ?";
        } else {
            return "∫(x^2)dx from 0 to 1 = ?";
        }
    }
}