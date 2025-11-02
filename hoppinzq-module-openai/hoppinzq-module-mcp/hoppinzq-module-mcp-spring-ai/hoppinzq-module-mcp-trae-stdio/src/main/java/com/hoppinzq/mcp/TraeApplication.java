package com.hoppinzq.mcp;

import com.hoppinzq.mcp.service.APIService;
import com.hoppinzq.mcp.service.CDNService;
import com.hoppinzq.mcp.service.CodeService;
import com.hoppinzq.mcp.service.SSHService;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbacks;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.List;

@SpringBootApplication
public class TraeApplication {

    public static void main(String[] args) {
        SpringApplication.run(TraeApplication.class, args);
    }

    @Bean
    public List<ToolCallback> zqTools(SSHService sshService,
                                      CDNService cdnService,
                                      CodeService codeService,
                                      APIService apiService) {
        return List.of(ToolCallbacks.from(sshService, cdnService, codeService, apiService));
    }
}

