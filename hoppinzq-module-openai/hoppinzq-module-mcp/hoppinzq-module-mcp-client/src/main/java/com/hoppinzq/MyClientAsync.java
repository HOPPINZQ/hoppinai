package com.hoppinzq;

import io.modelcontextprotocol.client.McpAsyncClient;
import io.modelcontextprotocol.client.McpClient;
import io.modelcontextprotocol.client.transport.ServerParameters;
import io.modelcontextprotocol.client.transport.StdioClientTransport;
import io.modelcontextprotocol.spec.McpSchema.CallToolRequest;
import io.modelcontextprotocol.spec.McpSchema.CallToolResult;
import io.modelcontextprotocol.spec.McpSchema.ListToolsResult;
import io.modelcontextprotocol.spec.McpSchema.TextContent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Map;

public class MyClientAsync {

    private static final Logger logger = LoggerFactory.getLogger(MyClientAsync.class);

    public static void main(String[] args) {
        // 设置控制台输出编码
        System.setProperty("file.encoding", "UTF-8");

        try {
            // 初始化客户端
            McpAsyncClient client = initializeClient();
            // 列出可用工具
            listTools(client);
            callZQWeb(client);
            closeClient(client);
        } catch (Exception e) {
            System.err.println("发生错误: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // 初始化客户端
    private static McpAsyncClient initializeClient() {
        // 创建客户端传输层
        ServerParameters jar = ServerParameters.builder("java")
                .args("-Dfile.encoding=UTF-8", "-jar", "D:\\hoppinzq-module-mcp-async-server-1.0.jar")
                .build();
        ServerParameters npx = ServerParameters.builder("D:\\nodejs\\npx.cmd")
                .args("-y", "apifox-mcp-server@latest", "--project=1582737")
                .env(Map.of("APIFOX_ACCESS_TOKEN", "APS-mckpBZ37t77lF1hvID7mvV8NIMFfW63r"))
                .build();
        ServerParameters uvx = ServerParameters.builder("C:\\Users\\hoppin\\.cherrystudio\\bin\\uvx.exe")
                .args("mcp-server-fetch")
                .build();
        StdioClientTransport stdioClientTransport = new StdioClientTransport(jar);
        McpAsyncClient client = McpClient.async(stdioClientTransport)
                .requestTimeout(Duration.ofSeconds(10))
                .loggingConsumer(notification -> {
                    System.out.println("接收的日志: " + notification.data());
                    return Mono.empty();
                })
                .toolsChangeConsumer(tools -> Mono.fromRunnable(() -> {
                    logger.info("Tools updated: {}", tools);
                }))
                .build();

        client.initialize()
                .flatMap(initResult -> client.listTools())
                .flatMap(tools -> {
                    return client.callTool(new CallToolRequest("search",
                            Map.of("searchContent", "烟花易冷", "searchType", "music")
                    ));
                })
                .doFinally(signalType -> {
                    client.closeGracefully().subscribe();
                })
                .subscribe();
        return client;
    }

    // 列出可用工具
    private static void listTools(McpAsyncClient client) {
        System.out.println("\n获取可用工具列表:");
        Mono<ListToolsResult> tools = client.listTools();
        tools.doOnSuccess(listToolsResult -> listToolsResult.tools().forEach(tool -> {
            System.out.println("工具名称: " + tool.name());
            System.out.println("工具描述: " + tool.description());
            System.out.println("工具Schema: " + tool.inputSchema());
            System.out.println("-------------------");
        })).subscribe();
    }

    private static void callZQWeb(McpAsyncClient client) {
        System.out.println("\n调用工具:");
        Mono<CallToolResult> callToolResultMono = client.callTool(
                new CallToolRequest("search",
                        Map.of("searchContent", "烟花易冷", "searchType", "music")));
        System.out.println("结果: ");
        callToolResultMono.doOnSuccess(callToolResult -> {
            callToolResult.content().forEach(content -> {
                if (content instanceof TextContent) {
                    System.out.println(((TextContent) content).text());
                }
            });
        }).subscribe();
    }

    // 关闭客户端
    private static void closeClient(McpAsyncClient client) {
        System.out.println("\n正在关闭客户端...");
        client.closeGracefully();
        System.out.println("客户端已关闭");
    }
}
