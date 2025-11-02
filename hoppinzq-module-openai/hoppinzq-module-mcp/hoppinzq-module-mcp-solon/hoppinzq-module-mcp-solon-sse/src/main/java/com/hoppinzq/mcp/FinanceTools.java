package com.hoppinzq.mcp;

import org.noear.solon.ai.annotation.ToolMapping;
import org.noear.solon.ai.mcp.server.annotation.McpServerEndpoint;
import org.noear.solon.annotation.Param;

// 金融工具服务
@McpServerEndpoint(name="finance-tools", sseEndpoint = "/finance/sse")
public class FinanceTools {
    @ToolMapping(description = "计算复利")
    public double compoundInterest(
            @Param(description = "本金") double principal,
            @Param(description = "年利率") double rate,
            @Param(description = "年数") int years) {
        return principal * Math.pow(1 + rate, years);
    }
}

