<p align='center'>
  <img src="https://hoppinzq.com/static/images/ChromeCore_74HJeQTQ1u.gif"  width="600"/>
  <br>  
  <img src="https://gitee.com/hoppin/chatgpt-java/widgets/widget_6.svg" width="200"/>
  <br>  
  <img src="https://gitee.com/hoppin/chatgpt-java/badge/star.svg?theme=dark" width="80"/>
  <img src="https://gitee.com/hoppin/chatgpt-java/badge/fork.svg?theme=dark" width="80"/>
</p>

## Hi 👋

这里是hoppinzq，和他的小AI，然后这是一个演示项目，演示一些AI

## ✍️ 语言

![Java](https://img.shields.io/badge/Java-JDK8-blue?style=flat&logo=openjdk&logoColor=white)
![Java](https://img.shields.io/badge/Java-JDK17-green?style=flat&logo=openjdk&logoColor=white)

## 🛠 技术栈和工具

|                                                                      | 技术栈&中间件&工具                                                                                                                                                                                                                                                                                                                       |
| -------------------------------------------------------------------- | --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| ![后端](https://img.shields.io/badge/-后端-black?style=flat) | ![Spring](https://img.shields.io/badge/-Spring-green?style=flat&logo=Spring&logoColor=white) ![SpringBoot](https://img.shields.io/badge/-SpringBoot-0170fe?style=flat&logo=SpringBoot) ![OpenAI](https://img.shields.io/badge/-OpenAI-1d365d?style=flat&logo=OpenAI) ![Mysql](https://img.shields.io/badge/-Mysql-CEF1D1?style=flat&logo=mysql) ![Redis](https://img.shields.io/badge/-Redis-red?style=flat&logo=redis)|
| ![前端](https://img.shields.io/badge/-前端-black?style=flat)   | ![HTML](https://img.shields.io/badge/-HTML-E34F26?style=flat&logo=html5&logoColor=white) ![JavaScript](https://img.shields.io/badge/-JavaScript-C69D00?style=flat&logo=javascript&logoColor=white) ![jquery](https://img.shields.io/badge/-Alpine-2f74c0?style=flat&logo=jquery&logoColor=white)                                                                                                                                                                   |
| ![Devops](https://img.shields.io/badge/-Devops-black?style=flat)     | ![Nginx](https://img.shields.io/badge/-Nginx-CEF1D1?style=flat&logo=nginx)  ![Maven](https://img.shields.io/badge/-Maven-cbe3f2?style=flat&logo=gitee)                                                                                                                                                                    |
| ![IDE](https://img.shields.io/badge/-IDE-black?style=flat)           | ![IDEA](https://img.shields.io/badge/-IDEA-3a3a3a?style=flat&logo=intellijidea)  ![Cursor](https://img.shields.io/badge/-Cursor-3a3a3a?style=flat&logo=claude)                                                |
| ![其他](https://img.shields.io/badge/-其他-black?style=flat)           | ![Gitee](https://img.shields.io/badge/-Gitee-black?style=flat&logo=gitee&logoColor=red) ![apifox](https://img.shields.io/badge/-apifox-black?style=flat&logo=apifox)  ![Centos](https://img.shields.io/badge/-Centos-black?style=flat&logo=centos)                                                                                            |

## 🎤介绍

本项目是很多项目通过SpringHoppin框架集成的微服务版，算是一个Demo性质的项目，融合了注册中心、统一认证、网关、AI、博客、短视频、爬虫等一系列项目。  
当然你可能只对AI感兴趣，我的意向也是这样。所以目前网站上只有关于AI的内容，AI的代码也是最完善的，其他待迁移。另外，你无需了解SpringHoppin是如何工作的。  
前端框架使用Alpine和JQuery

> 👉 [Alpine的官方文档](https://www.alpinejs.cn/)
> <br>
> 👉 [SpringHoppin文档](https://hoppinzq.com/springhoppinzq/doc.html)

## 📗目录结构

部分代码未迁移或者部分迁移，如需完整源代码（在我的私有仓库），请联系微信：zhangqiff19  
项目有57个模块，AI是其中一个模块，如果你只想要AI模块，可联系我或者自行拆分（应尽可能保留注册中心模块、auth认证模块、file文件模块、gateway网关模块）
，本项目尽可能完善AI模块，但是AI模块对我来说并不是重点。目前我已经仅最大可能拆分了，想要让openai-app服务正常工作的话，需要的模块还是很多~

```text


/hoppin-ai [项目目录]  
│  
├─hoppinzq-center ------------------[注册中心模块，可以不启动]  
├─hoppinzq-client ------------------[核心模块，SpringHoppin客户端依赖]  
├─hoppinzq-common ------------------[核心模块，公共模块]  
│─hoppinzq-gateway -------------------[核心模块，网关模块]  
│─hoppinzq-html -------------------[前端页面，前后分离的]  
│─hoppinzq-lucene -------------------[非核心模块，提供搜索引擎的能力]  
│─hoppinzq-module-auth -------------------[核心模块，认证模块]  
     │─hoppinzq-module-auth-api -------------------[供外部服务调用认证模块的服务进行认证]  
     └─hoppinzq-module-auth-app -------------------[认证模块的具体实现代码]  
│─hoppinzq-module-file -------------------[核心模块，文件模块]  
     │─hoppinzq-module-file-api -------------------[供外部服务调用]  
     └─hoppinzq-module-file-app -------------------[文件模块，集成了OSS、minio、ftp、ssh等文件操作能力]  
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
│─hoppinzq-mybatis -------------------[非核心模块，mybatis和拓展]  
│─hoppinzq-redis -------------------[非核心模块，提供缓存的能力]  
└─hoppinzq-service -------------------[核心模块，SpringHoppin服务端依赖]  
```

## 💡如何启动

要启动ai模块，你最好先启动注册中心和认证中心，然后再启动ai模块。当然不启动会一直在报错，但不影响使用。

#### 1、执行sql文件里的脚本

#### 2、启动注册中心模块hoppinzq-center

#### 3、配置认证中心的数据源，启动认证中心hoppinzq-module-auth模块（可选）

#### 4、配置AI模块数据源，启动hoppinzq-module-openai-app模块

#### 5、打开zq-apps-chat.html即可

### 调用示例

```java
        OpenAiService service=new OpenAiService("api-key",
        Duration.ofSeconds(60),"OpenAI代理地址");
final List<ChatMessage> messages=new ArrayList<>();
final ChatMessage userMessage=new ChatMessage(ChatMessageRole.USER.value(),"你好AI");
        messages.add(userMessage);
        ChatCompletionRequest chatCompletionRequest=ChatCompletionRequest
        .builder()
        .model("gpt-4o-mini")
        .messages(messages)
        .n(1)
        .build();
        ChatCompletionResult chatCompletion=service.createChatCompletion(chatCompletionRequest);
```

## 💡如何部署

我们有一套自动的CI/CD流程，考虑到对你们并没有什么用，就都去掉了，反正把注册中心、认证中心、openai-app打这三个jar包就行了。配置文件你得配好。

## ✏️hoppinAI更新内容

- 添加了对functionCall的支持，支持动态调用Java代码
- 添加了对MCP Client的支持，支持调用注册的MCP Server
- 添加了ZSON，用于处理大模型返回不规范的json，不过有了FunctionCall后，并没什么用。适用于以下场景：
  ```text
  1、非json数据：{"a":"acv","dd":"qwe"}希望对你有帮助
  2、未闭合的json：{"a":{"b":[1,2,
  3、多余的json：{{"name":"zhangsan"}}
  4、单引号json：{'a':1,"v":"qwe"}
  5、遗漏,json：{"name":"zhangsan""v":"qwe"}
  6、多余,json：{"name":"zhangsan","v":"qwe",}
  7、markdown的json：```json {"name":"zhangsan","v":"qwe"}```
  8、用=代替冒号的json：{"name"="zhangsan","v"="qwe"}
  9、丢失冒号的json：{"name" "zhangsan","v" "qwe"}
  10、未加引号的json：{"name":zhangsan,"v":1}
  11、中文符号的json：{“name”：“张三”}
  12、带注释的json：{“name”：“张三”//这是name}
  13、值缺失：[1,2,,3]
  14、带有未转义的控制字符的json：{"name":"zhangsan","v":"qwe\n"}
  ```
- 所有openAI协议接口支持
- Deepseek 推理能力支持
- 增加了RAG知识库的支持
- 聊天页面重写
- 添加了计费接口和token工具类
- 封装hoppinai client端mcp调用的提示词
  ```text
  你是一个有用的助手，可以使用一些工具来回答用户的问题，现在有这些工具：
  
  工具1：
  
  - 工具名称：search
  - 工具描述：搜索相关内容
  - 工具参数：
    - search_content:
      - 类型：string
      - 描述：搜索的内容
      - 是否必填：是
    - search_type:
      - 类型：string
      - 描述：搜索内容的类型
      - 默认值：music
      - 是否必填：否
      - 可选值：[music, website, other]
    - search_num:
      - 类型：integer
      - 描述：搜索数目
      - 默认值：10
      - 最小值：1
      - 最大值：30
      - 是否必填：否
  - 参数必填：[search_content]
  - 参数示例：{"search_content":"周杰伦","search_type":"music"}
  
  根据用户的问题选择合适的工具。如果不需要工具，请直接回复。
  
  重要提示：当您需要使用工具时，您必须只使用以下确切的JSON对象格式进行响应，而不能使用其他格式：
  
  {
    "tool":"tool_name",
    "arguments":{
      "arguments_name"："value"
    }
  }
  
  收到工具的响应后：
    - 1、将原始数据转换为自然的对话式响应。
    - 2、保持回答简洁但信息丰富
    - 3、关注最相关的信息
    - 4、使用用户问题中的适当上下文
    - 5、永远不要用完全相同的参数重新进行之前的工具调用
  
  请仅使用上面明确定义的工具。
  
  ```  
- 其他更新，我忘了

## 📗文档

- [✖️你可能想了解✖️]()
    - [1️⃣OpenAI文档](https://openai.apifox.cn/)
    - [2️⃣MCP](https://hoppinzq.com/mcp/mcp.html)
    - [3️⃣Ollama](https://ollama.com/)
- [😜其他]()
    - [📗Embeddings：](https://hoppinzq.com/embedding/embedding.html)
    - [🌹A2A](https://hoppinzq.com/a2a/a2a.html)


## 🎬反馈

> 🌷微信：zhangqiff19

## 😘感谢

[OpenAI](http://openai.com/)  
[MCP](https://modelcontextprotocol.io/)  
[Trae](https://www.trae.cn/)  
[deepseek](https://chat.deepseek.com/)  
[Cursor](https://modelcontextprotocol.io/)  
[Cherry Studio](https://modelcontextprotocol.io/)  
[腾讯云](https://cloud.tencent.com/)

## 📄License

MIT

### ❤️ GitHub Stats

[![Fork me on Gitee](https://gitee.com/hoppin/chatgpt-java/widgets/widget_3.svg)](https://gitee.com/hoppin/chatgpt-java)

[![hoppinzq/chatgpt-java](https://gitee.com/hoppin/chatgpt-java/widgets/widget_card.svg?colors=eae9d7,2e2f29,272822,484a45,eae9d7,747571)](https://gitee.com/hoppin/chatgpt-java)
