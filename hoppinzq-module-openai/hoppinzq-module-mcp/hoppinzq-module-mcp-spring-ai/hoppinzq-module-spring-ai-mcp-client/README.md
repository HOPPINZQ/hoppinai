### 与SpringAI集成的MCP Client

### 用前须知

#### SpringAI的`ChatModel`被我实现并重写了，你需要用`HoppinAiChatModel`对话

#### 我的MCP Client采用提示词的方式，使用MCP Server

#### 配置的MCP Server里不能使用根和采样

### 如何使用

#### 1、在配置文件里配置MCP Server的地址（你应该会配），必须配置`hoppinzq-module-mcp-server`模块的jar包，因为源代码我写死了获取音乐。你可以尝试改造，这是十分容易的。

#### 2、启动该项目即可。
