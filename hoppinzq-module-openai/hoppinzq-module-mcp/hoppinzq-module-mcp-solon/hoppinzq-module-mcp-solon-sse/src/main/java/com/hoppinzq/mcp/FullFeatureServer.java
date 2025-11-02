package com.hoppinzq.mcp;

import org.noear.solon.ai.annotation.PromptMapping;
import org.noear.solon.ai.annotation.ResourceMapping;
import org.noear.solon.ai.annotation.ToolMapping;
import org.noear.solon.ai.chat.message.ChatMessage;
import org.noear.solon.ai.mcp.server.annotation.McpServerEndpoint;
import org.noear.solon.annotation.Param;

import java.util.Arrays;
import java.util.Collection;

@McpServerEndpoint(sseEndpoint = "/mcp/sse")
public class FullFeatureServer {
    // 工具服务
    @ToolMapping(description = "汇率转换")
    public double exchangeRate(
            @Param(description = "源货币") String from,
            @Param(description = "目标货币") String to) {
        // 实现汇率转换逻辑
        return 6.5;
    }
    
    // 资源服务
    @ResourceMapping(uri = "config://app-info",
                   description = "获取应用信息")
    public String getAppInfo() {
        return "AppName: WeatherService, Version: 1.0.0";
    }
    
    // 提示语服务
    @PromptMapping(description = "生成天气报告提示")
    public Collection<ChatMessage> weatherReportPrompt(
            @Param(description = "城市名称") String city) {
        return Arrays.asList(
            ChatMessage.ofSystem("你是一个天气报告助手"),
            ChatMessage.ofUser("请生成" + city + "的天气报告")
        );
    }
}
