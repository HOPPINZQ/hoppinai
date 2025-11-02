### 基于SpringAI的MCP的demo

> 须知：  
> 1、SpringAI要求JDK版本为17+，SpringBoot版本为3.4.0+ 。本项目使用3.4.4版本的SpringBoot。    
> 2、SpringAI在1.0.0-M7版本后跟1.0.0-M6版本前的starters的命名全部不一样。本项目使用1.0.0-M7版本的starter，如果
> 你的工程是1.0.0-M6版本及以下版本的starter，请注意修改pom.xml中的依赖名。

#### 1、pom引入springboot依赖：

```xml
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.4.4</version>
        <relativePath/>
    </parent>
```   

#### 2、引入SpringAI的依赖：

```xml
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>org.springframework.ai</groupId>
            <artifactId>spring-ai-bom</artifactId>
            <version>1.0.0-M7</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>
```

#### 3、引入MCP的依赖：

```xml
<dependency>
    <groupId>org.springframework.ai</groupId>
    <artifactId>下面的starter</artifactId>
</dependency>
```

|          | 1.0.0-M7 及以上| 1.0.0-M6 及以下 |
| -------- | -------- | -------- |
| mcp server stdio |`spring-ai-starter-mcp-server` | `spring-ai-mcp-server-spring-boot-starter` |
| mcp server sse |`spring-ai-starter-mcp-server-webflux` | `spring-ai-mcp-server-webflux-spring-boot-starter` |
| mcp client |`spring-ai-starter-mcp-client` | `spring-ai-mcp-client-spring-boot-starter` |
| mcp client sse |`spring-ai-starter-mcp-client-webflux` | `spring-ai-mcp-client-webflux-spring-boot-starter` |
