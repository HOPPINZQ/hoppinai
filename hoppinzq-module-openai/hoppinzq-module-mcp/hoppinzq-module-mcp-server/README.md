### 基于MCPSDK的MCP Server的demo

### 传输方式使用stdio，因此需要将其打成jar包，放在一个文件里（如D盘），然后配置

```json
{
  "mcpServers": {
    "测试java自动化运维": {
      "command": "java",
      "args": [
        "-Dspring.ai.mcp.server.transport=STDIO",
        "-jar",
        "D:\\hoppinzq-module-mcp-server-1.0.jar"
      ]
    }
  }
}
```

### 这个示例展示了如何在MCP Server中声明工具、资源、提示词、根、采样、日志等代码的编写

### 示例提示词如下：

- 1、AI，帮我搜索周杰伦的音乐
- 2、AI，帮我搜索烟花易冷这首音乐
