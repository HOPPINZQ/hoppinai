package com.hoppinzq.mcp;

import org.noear.solon.ai.chat.tool.FunctionToolDesc;
import org.noear.solon.ai.mcp.server.McpServerEndpointProvider;
import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Inject;
import org.noear.solon.annotation.Mapping;

@Controller
public class ToolManager {
    @Inject("finance-tools")
    McpServerEndpointProvider financeEndpoint;
    
    @Mapping("/tool/add")
    public void addTool() {
        financeEndpoint.addTool(new FunctionToolDesc("calculateTax")
            .doHandle(params -> {
                double income = (double)params.get("income");
                return String.valueOf(income * 0.2); // 简单计算20%税
            }));
    }
    
    @Mapping("/tool/remove")
    public void removeTool() {
        financeEndpoint.removeTool("calculateTax");
    }
}

