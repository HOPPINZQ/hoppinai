package com.hoppinzq;

import io.modelcontextprotocol.client.McpSyncClient;
import io.modelcontextprotocol.client.transport.ServerParameters;
import io.modelcontextprotocol.client.transport.StdioClientTransport;
import io.modelcontextprotocol.spec.McpSchema.*;

import java.time.Duration;
import java.util.List;
import java.util.Map;

public class MyClient {
    public static void main(String[] args) {
        // 设置控制台输出编码
        System.setProperty("file.encoding", "UTF-8");
        try {
            // 初始化客户端
            McpSyncClient client = initializeClient();
            // 列出可用工具
            listTools(client);
            callZQSearch(client);
            listResources(client);
            readResource(client);
            listPrompts(client);
            // 关闭客户端
            closeClient(client);
        } catch (Exception e) {
            System.err.println("发生错误: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // 初始化客户端
    private static McpSyncClient initializeClient() {
        // 创建客户端传输层
        ServerParameters jar = ServerParameters.builder("java")
                .args("-Dfile.encoding=UTF-8", "-jar", "D:\\hoppinzq-module-mcp-async-server-1.0.jar")
                .env(Map.of("ZQ_TOKEN", "hoppinzq"))
                .build();
        StdioClientTransport stdioClientTransport = new StdioClientTransport(jar);
        // 创建同步客户端
        McpSyncClient client = io.modelcontextprotocol.client.McpClient.sync(stdioClientTransport)
                .requestTimeout(Duration.ofSeconds(10))
                .capabilities(ClientCapabilities.builder()
                        .roots(true)      // 启用根目录支持
                        .sampling()       // 启用采样支持
                        .build())
                .roots(List.of(new Root("file:///D:/music", "音乐文件在这里面")))
                .sampling(request -> {
                    // 简单的采样处理器实现
                    System.out.println("收到采样请求: " + request);
                    return CreateMessageResult.builder()
                            .role(Role.ASSISTANT)
                            .message("这是一个示例响应")
                            .build();
                }).loggingConsumer(notification -> {
                    System.out.println("接收的日志: " + notification.data());
                })
                .build();

        // 初始化连接
        System.out.println("正在初始化MCP客户端...");
        client.initialize();
        System.out.println("MCP客户端初始化完成");
        return client;
    }

    // 列出可用工具
    private static void listTools(McpSyncClient client) {
        System.out.println("\n获取可用工具列表:");
        ListToolsResult tools = client.listTools();
        tools.tools().forEach(tool -> {
            System.out.println("工具名称: " + tool.name());
            System.out.println("工具描述: " + tool.description());
            System.out.println("工具Schema: " + tool.inputSchema());
            System.out.println("-------------------");
        });
    }

    private static void callZQSearch(McpSyncClient client) {
        System.out.println("\n调用搜索工具:");
        CallToolResult calcResult = client.callTool(
                new CallToolRequest("search",
                        Map.of("search_content", "APT", "search_type", "music"))
        );
        System.out.println("结果: ");
        calcResult.content().forEach(content -> {
            if (content instanceof TextContent) {
                System.out.println(((TextContent) content).text());
            }
        });
    }

    // 列出可用资源
    private static void listResources(McpSyncClient client) {
        System.out.println("\n获取可用资源列表:");
        ListResourcesResult resources = client.listResources();
        resources.resources().forEach(resource -> {
            System.out.println("资源URI: " + resource.uri());
            System.out.println("资源名称: " + resource.name());
            System.out.println("资源描述: " + resource.description());
            System.out.println("资源类型: " + resource.mimeType());
            System.out.println("-------------------");
        });
    }

    // 读取资源示例
    private static void readResource(McpSyncClient client) {
        ListResourcesResult resources = client.listResources();
        if (!resources.resources().isEmpty()) {
            Resource firstResource = resources.resources().get(0);
            System.out.println("\n读取资源: " + firstResource.uri());
            ReadResourceResult resourceResult = client.readResource(
                    new ReadResourceRequest(firstResource.uri())
            );
            System.out.println("资源内容: ");
            resourceResult.contents().forEach(content -> {
                if (content instanceof TextResourceContents) {
                    System.out.println(((TextResourceContents) content).text());
                }
            });
        }
    }

    // 列出可用提示
    private static void listPrompts(McpSyncClient client) {
        System.out.println("\n获取可用提示列表:");
        ListPromptsResult prompts = client.listPrompts();
        prompts.prompts().forEach(prompt -> {
            System.out.println("提示名称: " + prompt.name());
            System.out.println("提示描述: " + prompt.description());
            System.out.println("提示参数: ");
            prompt.arguments().forEach(arg -> {
                System.out.println("  - " + arg.name() + (arg.required() ? " (必需)" : " (可选)") + ": " + arg.description());
            });
            System.out.println("-------------------");
        });
    }

    // 使用提示示例
    private static void useGreetingPrompt(McpSyncClient client) {
        if (client.listPrompts().prompts().stream().anyMatch(prompt -> prompt.name().equals("greeting"))) {
            System.out.println("\n使用问候提示:");
            GetPromptResult promptResult = client.getPrompt(
                    new GetPromptRequest("greeting", Map.of("name", "张三"))
            );
            System.out.println("提示结果: " + promptResult.description());
            System.out.println("消息: ");
            promptResult.messages().forEach(message -> {
                System.out.println("角色: " + message.role());
                if (message.content() instanceof TextContent) {
                    System.out.println("内容: " + ((TextContent) message.content()).text());
                }
                System.out.println("-------------------");
            });
        }
    }

    // 添加根目录
    private static void addRootDirectory(McpSyncClient client) {
        System.out.println("\n添加根目录:");
        client.addRoot(new Root("file:///tmp", "临时目录"));
        System.out.println("根目录已添加");

        // 通知服务器根目录列表已更改
        client.rootsListChangedNotification();
        System.out.println("已通知服务器根目录列表变更");
    }

    // 关闭客户端
    private static void closeClient(McpSyncClient client) {
        System.out.println("\n正在关闭客户端...");
        client.closeGracefully();
        System.out.println("客户端已关闭");
    }
}
