package com.hoppinzq;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.modelcontextprotocol.server.McpServer;
import io.modelcontextprotocol.server.McpServerFeatures;
import io.modelcontextprotocol.server.McpSyncServer;
import io.modelcontextprotocol.server.transport.StdioServerTransportProvider;
import io.modelcontextprotocol.spec.McpSchema.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MyServer {
    public static void main(String[] args) throws JsonProcessingException {
        System.out.println(createSchema());
        // 设置系统默认编码为UTF-8，确保正确处理中文字符
        System.setProperty("file.encoding", "UTF-8");
        // 创建基于标准输入输出的传输提供者，用于服务器通信
        StdioServerTransportProvider transportProvider = new StdioServerTransportProvider(new ObjectMapper());
        // 创建并配置MCP同步服务器
        McpSyncServer syncServer = McpServer.sync(transportProvider)
                .serverInfo("my-server", "1.0.0") // 设置服务器标识和版本
                .capabilities(ServerCapabilities.builder()
                        .resources(true, true) // 启用资源读写功能
                        .tools(true)          // 启用工具功能
                        .prompts(true)        // 启用提示功能
                        .logging()            // 启用日志功能
                        .build())
                .build();
        try {

            // 添加工具、资源和提示
            addSearchTool(syncServer);
            addExampleResource(syncServer);
            addGreetingPrompt(syncServer);

            // 关闭服务器
            // syncServer.close();
        } catch (JsonProcessingException e) {
            // 处理异常，例如打印错误信息
            System.err.println("创建JSON Schema时发生错误: " + e.getMessage());
        }
    }

    /**
     * 添加搜索工具到服务器
     *
     * @param syncServer MCP同步服务器实例
     * @throws JsonProcessingException JSON处理异常
     */
    private static void addSearchTool(McpSyncServer syncServer) throws JsonProcessingException {
        // 创建工具的JSON Schema定义
        String schema = createSchema();

        // 创建工具规范，包含工具定义和处理逻辑
        McpServerFeatures.SyncToolSpecification syncToolSpecification = new McpServerFeatures.SyncToolSpecification(
                new Tool("search", "搜索相关内容", schema),
                (exchange, arguments) -> {
                    List<Content> result = new ArrayList<>();
                    try {
                        String searchType = (String) arguments.get("searchType");
                        String searchContent = (String) arguments.get("searchContent");
                        // 发送日志通知
                        syncServer.loggingNotification(LoggingMessageNotification.builder()
                                .level(LoggingLevel.INFO)
                                .logger("custom-logger")
                                .data("searchType:" + searchType + ",searchContent:" + searchContent)
                                .build());
                        String searchResult = null;
                        switch (searchType) {
                            case "music":
                                String musicRes = searchMusic(searchContent);
                                ObjectMapper objectMapper = new ObjectMapper();
                                ArrayNode arrayNode = objectMapper.createArrayNode();
                                JsonNode musicResJson = objectMapper.readTree(musicRes);
                                JsonNode musicResult = musicResJson.get("result");
                                JsonNode songs = musicResult.get("songs");
                                if (songs != null) {
                                    songs.forEach(song -> {
                                        ObjectNode objectNode = objectMapper.createObjectNode();
                                        String title = song.get("name").asText();
                                        JsonNode artists = song.get("artists");
                                        if (artists.isArray() && artists.size() > 0) {
                                            String singer = "";
                                            for (int i = 0; i < artists.size(); i++) {
                                                singer += artists.get(i).get("name").asText();
                                                if (i < artists.size() - 1) {
                                                    singer += ",";
                                                }
                                            }
                                            objectNode.put("artist", singer);
                                        } else {
                                            String artist = artists.get(0).get("name").asText();
                                            objectNode.put("artist", artist);
                                        }
                                        JsonNode albumNode = song.get("album");
                                        if (albumNode != null) {
                                            String album = albumNode.get("name").asText();
                                            objectNode.put("album", album);
                                        }
                                        Integer id = song.get("id").asInt();
                                        objectNode.put("id", id);
                                        objectNode.put("url", "http://music.163.com/song/media/outer/url?id=" + id + ".mp3");
                                        objectNode.put("name", title);
                                        arrayNode.add(objectNode);
                                    });
                                }
                                searchResult = arrayNode.toString();
                                break;
                            case "website":
                                // 搜索网页
                                searchResult = "现在不支持搜索网页，目前只支持搜索音乐";
                                break;
                            case "other":
                                searchResult = "目前只支持搜索音乐";
                                break;
                            default:
                                result.add(new TextContent("不支持的搜索类型: " + searchType));
                                return new CallToolResult(result, true);
                        }

                        result.add(new TextContent(searchResult));
                    } catch (Exception e) {
                        result.add(new TextContent("搜索错误: " + e.getMessage()));
                        return new CallToolResult(result, true);
                    }

                    return new CallToolResult(result, false);
                });
        syncServer.addTool(syncToolSpecification);
    }

    private static String searchMusic(String searchContent) throws Exception {
        URL url = new URL("http://hoppin.cn:3000/search?keywords=" + URLEncoder.encode(searchContent, StandardCharsets.UTF_8));
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setConnectTimeout(5000);
        connection.setReadTimeout(5000);
        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String inputLine;
        StringBuffer content = new StringBuffer();
        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine);
        }
        in.close();
        return content.toString();
    }

    /**
     * 创建工具的JSON Schema
     *
     * @return JSON Schema字符串
     * @throws JsonProcessingException JSON处理异常
     */
    private static String createSchema() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();

        ObjectNode propertiesNode = mapper.createObjectNode();
        ObjectNode rootNode = mapper.createObjectNode();
        rootNode.put("type", "object");
        rootNode.put("id", "urn:jsonschema:search");
        // 定义operation属性：指定可用的计算操作
        ObjectNode searchNode = mapper.createObjectNode();
        searchNode.put("type", "string");
        searchNode.put("description", "搜索内容");
        ObjectNode searchTypeNode = mapper.createObjectNode();
        searchTypeNode.put("type", "string");
        searchTypeNode.put("description", "搜索类型，可以是音乐，网站和其他");
        ArrayNode typeNode = mapper.createArrayNode();
        typeNode.add("music").add("website").add("other");
        searchTypeNode.set("enum", typeNode);
        // 添加所有属性到properties节点
        propertiesNode.set("searchContent", searchNode);
        propertiesNode.set("searchType", searchTypeNode);

        // 添加properties到根节点
        rootNode.set("properties", propertiesNode);
        // 将JSON对象转换为字符串
        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(rootNode);
    }

    /**
     * 添加示例资源到服务器
     *
     * @param syncServer MCP同步服务器实例
     */
    private static void addExampleResource(McpSyncServer syncServer) {
        // 创建资源规范，包含资源定义和访问处理逻辑
        McpServerFeatures.SyncResourceSpecification syncResourceSpecification = new McpServerFeatures.SyncResourceSpecification(
                // 定义资源的基本信息：URI、名称、描述和内容类型
                new Resource("custom://resource", "示例资源", "这是一个示例资源", "text/plain", null),
                (exchange, request) -> {
                    // 处理资源读取请求
                    List<ResourceContents> contents = new ArrayList<>();
                    String content = "这是资源内容示例";
                    contents.add(new TextResourceContents("custom://resource/content", "text/plain", content));
                    return new ReadResourceResult(contents);
                });
        syncServer.addResource(syncResourceSpecification);
    }

    /**
     * 添加问候语生成提示到服务器
     *
     * @param syncServer MCP同步服务器实例
     */
    private static void addGreetingPrompt(McpSyncServer syncServer) {
        // 创建提示规范，包含提示定义和处理逻辑
        McpServerFeatures.SyncPromptSpecification syncPromptSpecification = new McpServerFeatures.SyncPromptSpecification(
                // 定义提示的基本信息和参数
                new Prompt("greeting", "生成问候语", new ArrayList<>(Arrays.asList(
                        new PromptArgument("name", "用户名称", true)))),
                (exchange, request) -> {
                    // 处理提示请求
                    List<PromptMessage> messages = new ArrayList<>();

                    // 处理用户名称，确保正确处理UTF-8编码
                    String name;
                    try {
                        name = new String(((String) request.arguments().get("name")).getBytes("UTF-8"), StandardCharsets.UTF_8);
                    } catch (UnsupportedEncodingException e) {
                        name = (String) request.arguments().get("name");
                    }
                    if (name == null || name.isEmpty()) {
                        name = "访客";
                    }

                    // 创建对话消息序列
                    PromptMessage userMessage = new PromptMessage(
                            Role.USER,
                            new TextContent("你好，请给我一个友好的问候"));

                    PromptMessage systemMessage = new PromptMessage(
                            Role.ASSISTANT,
                            new TextContent("你是一个友好的助手，请为用户生成问候语"));

                    PromptMessage assistantMessage = new PromptMessage(
                            Role.ASSISTANT,
                            new TextContent("你好，" + name + "！很高兴见到你。今天过得怎么样？"));

                    messages.add(systemMessage);
                    messages.add(userMessage);
                    messages.add(assistantMessage);

                    return new GetPromptResult("为用户" + name + "生成的问候语", messages);
                });
        syncServer.addPrompt(syncPromptSpecification);
    }
}
