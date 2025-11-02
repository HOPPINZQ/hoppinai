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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class MyServer {

    private static final Logger logger = LoggerFactory.getLogger(MyServer.class);

    public static void main(String[] args) {
        // 设置系统默认编码为UTF-8，确保正确处理中文字符
        System.setProperty("file.encoding", "UTF-8");
        logger.info("服务器启动");
        // 创建基于标准输入输出的传输提供者，用于服务器通信
        StdioServerTransportProvider transportProvider = new StdioServerTransportProvider(new ObjectMapper());
        // 创建并配置MCP同步服务器
        McpSyncServer syncServer = McpServer.sync(transportProvider)
                .serverInfo("my-server", "1.0.0") // 设置服务器标识和版本
                .resourceTemplates(new ResourceTemplate("mysql:///{database}/{table}", "数据库资源", "这是张祺的自定义数据库资源", "text/plain", null))
                .capabilities(ServerCapabilities.builder()
                        .resources(true, true) // 启用资源读写功能
                        .tools(true)          // 启用工具功能
                        .prompts(true)        // 启用提示功能
                        .logging()            // 启用日志功能
                        .build())
                .build();
        try {

            // 添加工具、资源和提示
            addTool(syncServer);
            addResource(syncServer);
            addPrompt(syncServer);

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
    private static void addTool(McpSyncServer syncServer) throws JsonProcessingException {
        // 创建工具的JSON Schema定义
        String schema = createSchema();

        // 创建工具规范，包含工具定义和处理逻辑
        McpServerFeatures.SyncToolSpecification syncToolSpecification = new McpServerFeatures.SyncToolSpecification(
                new Tool("search", "搜索相关内容", schema),
                (exchange, arguments) -> {
                    Implementation clientInfo = exchange.getClientInfo();
                    ClientCapabilities clientCapabilities = exchange.getClientCapabilities();
                    ListRootsResult listRootsResult = exchange.listRoots();
                    // 这里你可以用客户端的能力，去处理这次请求，如：用户说我要搜索APT，你可以向客户端发起一个请求补全的调用，让客户端的大模型
                    // 去决定搜索APT网页还是APT音乐，或者让大模型问用户，你要搜索APT音乐还是APT网页？
                    logger.info("客户端信息：{},客户端能力：{}，根：{}", clientInfo, clientCapabilities, listRootsResult);
                    syncServer.loggingNotification(LoggingMessageNotification.builder()
                            .level(LoggingLevel.INFO)
                            .logger("custom-logger")
                            .data("采集到的根：" + listRootsResult.toString())
                            .build());
                    List<Content> result = new ArrayList<>();
                    logger.info("搜索参数：{}", arguments);
                    try {
                        String searchType = (String) arguments.get("search_type");
                        String searchContent = (String) arguments.get("search_content");
                        // 发送日志通知
                        syncServer.loggingNotification(LoggingMessageNotification.builder()
                                .level(LoggingLevel.INFO)
                                .logger("custom-logger")
                                .data("search_type:" + searchType + ",search_content:" + searchContent)
                                .build());
                        String searchResult = null;
                        switch (searchType) {
                            case "music":
                                String musicRes = searchMusic(searchContent);
                                logger.info("音乐查询：{}", musicRes);
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
                        HashMap metaData = new HashMap<>();
                        metaData.put("search_type", arguments.get("search_type"));
                        metaData.put("search_content", arguments.get("search_content"));
                        exchange.createMessage(CreateMessageRequest.builder()
                                .temperature(0.5)
                                .maxTokens(1000)
                                .systemPrompt("你现在是一个音乐助手，你需要根据用户输入的搜索内容，返回音乐搜索结果，结果格式为json数组，数组中每个元素为一个")
                                .modelPreferences(ModelPreferences.builder()
                                        .hints(Arrays.asList(
                                                new ModelHint("gpt-3.5-turbo"),
                                                new ModelHint("gpt-4o-mini")
                                        ))
                                        .costPriority(0.5).speedPriority(0.5).intelligencePriority(0.5)
                                        .build())
                                .messages(Arrays.asList(
                                        new SamplingMessage(Role.USER, new TextContent("搜索音乐")),
                                        new SamplingMessage(Role.ASSISTANT, new TextContent(searchResult))
                                ))
                                .metadata(metaData)
                                .includeContext(CreateMessageRequest.ContextInclusionStrategy.THIS_SERVER)
                                .build());
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
        searchNode.put("required", true);
        ObjectNode searchTypeNode = mapper.createObjectNode();
        searchTypeNode.put("type", "string");
        searchTypeNode.put("description", "搜索类型，可以是音乐，网站和其他");
        ArrayNode typeNode = mapper.createArrayNode();
        typeNode.add("music").add("website").add("other");
        searchTypeNode.set("enum", typeNode);
        ObjectNode searchNumNode = mapper.createObjectNode();
        searchNumNode.put("type", "integer");
        searchNumNode.put("description", "搜索数量");
        searchNumNode.put("default", 10);
        searchNumNode.put("minimum", 1);
        searchNumNode.put("maximum", 30);

        // 添加所有属性到properties节点
        propertiesNode.set("search_content", searchNode);
        propertiesNode.set("search_type", searchTypeNode);
        propertiesNode.set("search_num", searchNumNode);

        // 添加properties到根节点
        rootNode.set("properties", propertiesNode);
        rootNode.set("required", mapper.createArrayNode().add("search_content"));
        // 将JSON对象转换为字符串
        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(rootNode);
    }

    /**
     * 添加示例资源到服务器
     *
     * @param syncServer MCP同步服务器实例
     */
    private static void addResource(McpSyncServer syncServer) {
        // 创建资源规范，包含资源定义和访问处理逻辑
        McpServerFeatures.SyncResourceSpecification syncResourceSpecification = new McpServerFeatures.SyncResourceSpecification(
                // 定义资源的基本信息：URI、名称、描述和内容类型
                new Resource("mysql:///blog/blog", "数据库资源", "这是张祺的自定义数据库资源", "text/plain", null),
                (exchange, request) -> {
                    String uri = request.uri();
                    // 解析URI
                    String uriTemp = uri.replaceAll("mysql://", "");
                    String databaseURI = URLDecoder.decode(uriTemp, StandardCharsets.UTF_8);
                    if (!databaseURI.startsWith("/blog/")) {
                        throw new RuntimeException("无效的数据库");
                    }
                    String table = databaseURI.substring(databaseURI.lastIndexOf("/") + 1);
                    List<ResourceContents> contents = new ArrayList<>();
                    switch (table) {
                        case "blog":
                            String blogContent = "这是博客数据";
                            contents.add(new TextResourceContents(uri, "text/plain", blogContent));
                            contents.add(new TextResourceContents(uri, "text/plain", "MCP的简介，作者zq"));
                            contents.add(new TextResourceContents(uri, "text/plain", "embedding的简介，作者zq"));
                            contents.add(new TextResourceContents(uri, "text/plain", "RAG的简介，作者zq"));
                            break;
                        case "video":
                            String videoContent = "这是视频数据";
                            contents.add(new TextResourceContents(uri, "text/plain", videoContent));
                            break;
                        default:
                            String defaultContent = "没有数据";
                            contents.add(new TextResourceContents(uri, "text/plain", defaultContent));
                    }
                    return new ReadResourceResult(contents);
                });
        syncServer.addResource(syncResourceSpecification);
    }

    /**
     * 添加生成提示到服务器
     *
     * @param syncServer MCP同步服务器实例
     */
    private static void addPrompt(McpSyncServer syncServer) {
        // 创建提示规范，包含提示定义和处理逻辑
        McpServerFeatures.SyncPromptSpecification syncPromptSpecification = new McpServerFeatures.SyncPromptSpecification(
                // 定义提示的基本信息和参数
                new Prompt("search", "搜索", new ArrayList<>(Arrays.asList(
                        new PromptArgument("content", "搜索内容", true),
                        new PromptArgument("type", "搜索类型，可以是音乐，网站或者其他", true)))),
                (exchange, request) -> {
                    // 处理提示请求
                    List<PromptMessage> messages = new ArrayList<>();
                    // 处理内容，确保正确处理UTF-8编码
                    String content, type;
                    try {
                        content = new String(((String) request.arguments().get("content")).getBytes("UTF-8"), StandardCharsets.UTF_8);
                        type = new String(((String) request.arguments().get("type")).getBytes("UTF-8"), StandardCharsets.UTF_8);
                    } catch (UnsupportedEncodingException e) {
                        content = (String) request.arguments().get("content");
                        type = (String) request.arguments().get("type");
                    }
                    if (content == null || content.isEmpty()) {
                        content = "爱的供养";
                    }
                    if (type == null || type.isEmpty()) {
                        type = "其他";
                    }
                    // 创建对话消息序列
                    PromptMessage userMessage = new PromptMessage(
                            Role.USER,
                            new TextContent("你好，我想搜索关于" + content + "的信息，搜索类型是：" + type + "。"));

                    PromptMessage systemMessage = new PromptMessage(
                            Role.ASSISTANT,
                            new TextContent("你是一个友好的助手，请为用户搜索" + content + "的相关信息。"));

                    PromptMessage assistantMessage = new PromptMessage(
                            Role.ASSISTANT,
                            new TextContent("你好，关于" + content + "的搜索内容是。我为你找到了一些相关信息。"));

                    messages.add(systemMessage);
                    messages.add(userMessage);
                    messages.add(assistantMessage);

                    return new GetPromptResult("搜索 " + content + "内容的示例提示词", messages);
                });
        syncServer.addPrompt(syncPromptSpecification);
    }
}
