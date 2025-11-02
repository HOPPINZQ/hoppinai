package com.hoppinzq.mcp;

import org.noear.solon.ai.annotation.ToolMapping;
import org.noear.solon.ai.mcp.server.annotation.McpServerEndpoint;
import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Header;
import org.noear.solon.annotation.Mapping;
import org.noear.solon.annotation.Param;

@Controller
@McpServerEndpoint(sseEndpoint = "/mcp/sse")
public class HybridService {
    // 同时作为Web API和MCP工具
    @ToolMapping(description = "查询库存")
    @Mapping("/api/inventory")
    public int getInventory(
            @Param(description = "产品ID") String productId,
            @Header("Authorization") String auth) {
        // 验证逻辑...
        return 100; // 示例库存
    }
    
    // 纯Web API
    @Mapping("/api/info")
    public String getInfo() {
        return "Service Info";
    }
    
    // 纯MCP工具
    @ToolMapping(description = "计算折扣")
    public double calculateDiscount(
            @Param(description = "原价") double price,
            @Param(description = "会员等级") String level) {
        if("VIP".equals(level)) {
            return price * 0.8;
        }
        return price;
    }
}
