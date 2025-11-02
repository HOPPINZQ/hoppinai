package com.hoppinzq.mcp.service;

import jakarta.annotation.PostConstruct;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.ai.tool.execution.DefaultToolCallResultConverter;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ZQWebsiteService {

    private List<ZQWebsite> websites = new ArrayList<>();

    @Tool(name = "zq_get_webs", description = "获取所有网站")
    public List<ZQWebsite> getAllWebs() {
        return websites;
    }

    @Tool(name = "zq_get_web", description = "通过title获取网站", returnDirect = false, resultConverter = DefaultToolCallResultConverter.class)
    public ZQWebsite getWebByTitle(@ToolParam(required = true, description = "网站标题") String title) {
        return websites.stream().filter(website -> website.title().equals(title)).findFirst().orElse(null);
    }

    @PostConstruct
    public void init() {
        websites.addAll(List.of(
                new ZQWebsite("张祺的网站", "https://hoppinzq.com"),
                new ZQWebsite("张祺的音乐网站", "https://hoppinzq.com/wukong/")
        ));
    }
}

record ZQWebsite(String title, String url) {
}
