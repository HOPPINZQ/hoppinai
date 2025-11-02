package com.hoppinzq;

import com.hoppinzq.service.*;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbacks;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.List;

/**
 * 使用请参考readme.md，有问题请微信：zhangqiff19
 */
@SpringBootApplication
@MapperScan("com.hoppinzq.dao")
public class McpDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(McpDemoApplication.class, args);
    }

    @Bean
    public List<ToolCallback> zqTools(CSGOService csgoService, BitcoinService bitcoinService,
                                      ExpressService expressService, RAGService ragService,
                                      WindowService windowService, SpiderService spiderService,
                                      MobileService mobileService, CodeService codeService, DBService dbService, BpmnService bpmnService) {
        ToolCallback[] from = ToolCallbacks.from(csgoService, bitcoinService,
                expressService, ragService, windowService, spiderService, mobileService, codeService, dbService, bpmnService);
        return List.of(from);
    }
}
