### 基于MCP Server的trae拓展

### 如何使用

#### 1、在SSHConnection里配置ssh连接信息

#### 2、使用指令`go install github.com/Done-0/fuck-u-code/cmd/fuck-u-code@latest`安装代码分析组件（需要go和git环境）

#### 3、打包该项目，将jar包放在随便一个位置（如D盘里）。

#### 4、在trae里配置自己的MCP Server，配置内容如下：

```json
{
  "mcpServers": {
    "我的工具": {
      "command": "java",
      "args": [
        "-jar",
        "D:\\hoppinzq-module-mcp-trae-stdio-0.0.3-SNAPSHOT.jar"
      ]
    }
  }
}
```

#### 5、开始对话，让trae生成前端工程。示例提示词：

- AI，帮我部署一下前端项目
- AI，构建前端项目，将打包后的文件部署到服务器上
- AI，用我的工具分析一下你写的js代码
- AI，我提供了几个接口，根据接口的功能将页面绘制出来
