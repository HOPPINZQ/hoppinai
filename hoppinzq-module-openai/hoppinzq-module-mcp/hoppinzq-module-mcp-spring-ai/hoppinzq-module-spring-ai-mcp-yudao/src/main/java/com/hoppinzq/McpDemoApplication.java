package com.hoppinzq;

import com.hoppinzq.service.YudaoDeptTools;
import com.hoppinzq.service.YudaoRoleTools;
import com.hoppinzq.service.YudaoService;
import com.hoppinzq.service.YudaoUserTools;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbacks;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.List;

@SpringBootApplication
public class McpDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(McpDemoApplication.class, args);
    }

    @Bean
    public YudaoService yudaoService() {
        return new YudaoService("test1");
    }

    @Bean
    public List<ToolCallback> zqTools(YudaoUserTools yudaoUserTools,
                                      YudaoDeptTools yudaoDeptTools,
                                      YudaoRoleTools yudaoRoleTools) {
        return List.of(ToolCallbacks.from(yudaoUserTools, yudaoDeptTools, yudaoRoleTools));
    }

}
