package com.hoppinzq.mcp.service;

import com.hoppinzq.mcp.utils.CmdUtils;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;

@Service
public class CodeService {

    // go install github.com/Done-0/fuck-u-code/cmd/fuck-u-code@latest
    @Tool(name = "analyze_code", description = "分析指定文件的代码，如果用户要求你分析代码，请调用analyze_code工具, 并传入本地文件路径")
    public String analyzeCode(@ToolParam(required = true, description = "本地文件路径") String filePath) {
        String cmd = "fuck-u-code analyze " + filePath + " -m";
        return CmdUtils.startProcess(cmd);
    }
}

