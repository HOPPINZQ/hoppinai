package com.hoppinzq.mcp;

import com.hoppinzq.mcp.service.BpmnService;
import com.hoppinzq.mcp.service.ZQWebsiteService;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbacks;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.List;

@SpringBootApplication
public class McpServerSseApplication {

    public static void main(String[] args) {
        SpringApplication.run(McpServerSseApplication.class, args);
    }

    @Bean
    public List<ToolCallback> zqTools(ZQWebsiteService zqWebsiteService, BpmnService bpmnService) {
        return List.of(ToolCallbacks.from(zqWebsiteService, bpmnService));
    }

}
