### 基于MCPSDK的异步MCP Server的demo

### 传输方式使用stdio，因此需要将其打成jar包，放在一个文件里（如D盘），然后配置

```json
{
  "mcpServers": {
    "测试java自动化运维": {
      "command": "java",
      "args": [
        "-Dspring.ai.mcp.server.transport=STDIO",
        "-jar",
        "D:\\hoppinzq-module-mcp-async-server-1.0.jar"
      ]
    }
  }
}
```

### 这个示例展示了如何让AI去部署和管理Java项目

你需要在类MyAsyncServer中配置：

- wroot：java项目文件夹路径，你可以改造，目的是告诉AI关于pom文件的位置
- lroot：部署的Linux上的目标文件夹

你需要在类SSHBean中配置：

- ssh的连接信息

### 示例提示词如下：

- 1、AI，帮我部署一个Java服务
- 2、AI，帮我打包一个Java服务
- 3、AI，帮我部署一个Java服务到Linux服务器
- 4、AI，帮我部署一个Java服务到Linux服务器，并启动它
- 5、AI，部署的java服务的jvm参数是什么？
- 6、AI，关闭那个Java服务
- 7、AI，导出那个Java服务的堆栈文件
- 8、AI，启动远程服务器上的Java项目
- 9、AI，分析那个Java服务的堆内存摘要
- 10、AI，生成那个java服务的日志的html代码，要求在页面上可以实时打印日志
