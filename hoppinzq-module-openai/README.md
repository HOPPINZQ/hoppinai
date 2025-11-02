### 架构

```
ai模块
│─hoppinzq-module-openai -------------------[AI模块，提供AI的能力]  
     │─hoppinzq-module-a2a -------------------[a2a模块]  
        │─hoppinzq-module-a2a-client -------------------[a2a的客户端模块]
        │─hoppinzq-module-a2a-model -------------------[a2a model模块]
        └─hoppinzq-module-a2a-server -------------------[a2a的服务端模块]
     │─hoppinzq-module-langchain -------------------[langchain模块，没有代码]  
     │─hoppinzq-module-openai-model -------------------[hoppinAI模块model模块]  
     │─hoppinzq-module-openai-api -------------------[hoppinAI模块api模块]  
     │─hoppinzq-module-openai-app -------------------[hoppinAI模块具体代码]  
     │─hoppinzq-module-openai-core -------------------[hoppinAI核心模块] 
     └─hoppinzq-module-mcp -------------------[mcp模块]  
         │─hoppinzq-module-mcp-async-server -------------------[MCP Server异步]  
         │─hoppinzq-module-mcp-client -------------------[MCP客户端模块]  
         │─hoppinzq-module-mcp-server -------------------[MCP Server标准输入输出模块]  
         │─hoppinzq-module-mcp-server-sse -------------------[MCP Server SSE模块]  
         └─hoppinzq-module-mcp-spring-ai -------------------[基于springAI的MCP Server模块]  
            │─mcp-demo -------------------[演示用demo]
            │─hoppinzq-module-spring-ai-mcp-client -------------------[springAI的MCP客户端模块]    
            │─hoppinzq-module-spring-ai-mcp-server -------------------[springAI的MCP Server 标准输入输出模块]  
            └─hoppinzq-module-spring-ai-mcp-server-sse -------------------[springAI的MCP Server 流式输出模块] 
```

- 其中 openai-app 是服务，启动该服务就行了
- openai-model openAI协议的模型层
- openai-core openAI的本质就是调接口，需要优雅
