package com.hoppinzq.service;

import com.hoppinzq.service.cmd.CmdUtils;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;

/**
 * 需要go环境和安装fuck-u-code
 * 在go环境下执行：go install github.com/Done-0/fuck-u-code/cmd/fuck-u-code@latest
 * 可能下不下来，配置go国内镜像仓库：go env -w GOPROXY=https://mirrors.aliyun.com/goproxy,direct
 * 有疑问请联系我微信：zhangqiff19
 */
@Service
public class CodeService {

    @Tool(name = "analyze_code", description = "分析指定文件的代码，如果用户要求你分析代码，请调用analyze_code工具, 并传入本地文件路径")
    public String analyzeCode(@ToolParam(required = true, description = "本地文件路径") String filePath) {
        String cmd = "fuck-u-code analyze " + filePath + " -m";
        return CmdUtils.startProcess(cmd);
    }
}

