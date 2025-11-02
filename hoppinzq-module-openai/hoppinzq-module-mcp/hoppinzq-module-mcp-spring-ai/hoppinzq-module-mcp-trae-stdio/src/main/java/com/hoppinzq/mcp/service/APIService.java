package com.hoppinzq.mcp.service;

import com.hoppinzq.mcp.utils.ClassLoaderUtils;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Service;

@Service
public class APIService {

    @Tool(name = "get_api_info", description = "获取接口的openAPI格式的信息")
    public String getApiInfo() {
        return ClassLoaderUtils.getResource("openAPI.json");
    }
}
