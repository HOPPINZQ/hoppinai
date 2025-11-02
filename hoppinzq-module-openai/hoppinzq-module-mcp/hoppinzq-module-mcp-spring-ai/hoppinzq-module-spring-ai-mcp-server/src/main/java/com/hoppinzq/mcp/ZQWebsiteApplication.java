package com.hoppinzq.mcp;

import com.hoppinzq.mcp.service.ZQWebsiteService;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbacks;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.List;

@SpringBootApplication
public class ZQWebsiteApplication {

    public static void main(String[] args) {
        SpringApplication.run(ZQWebsiteApplication.class, args);
    }

    @Bean
    public List<ToolCallback> zqTools(ZQWebsiteService zqWebsiteService) {
        return List.of(ToolCallbacks.from(zqWebsiteService));
    }


}
