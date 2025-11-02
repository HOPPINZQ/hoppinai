package com.hoppinzq.mcp.service;

import com.hoppinzq.mcp.util.HoppinTool;
import com.hoppinzq.mcp.util.MCPUtil;
import jakarta.annotation.Resource;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.ToolResponseMessage;
import org.springframework.ai.mcp.SyncMcpToolCallbackProvider;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.definition.ToolDefinition;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@Service
public class MCPClientService implements ApplicationRunner {

    private static final String DEFAULT_PROMPT = "好啊，这就帮你查查";
    @Resource
    private ToolCallbackProvider tools;
    @Resource
    private HoppinAiChatModel chatModel;

    @Override
    public void run(ApplicationArguments args) {
        var tools = (SyncMcpToolCallbackProvider) this.tools;
        var toolCallbacks = tools.getToolCallbacks();
        List<HoppinTool> hoppinTools = new ArrayList<>();
        for (ToolCallback toolCallback : toolCallbacks) {
            ToolDefinition toolDefinition = toolCallback.getToolDefinition();
            hoppinTools.add(new HoppinTool(toolDefinition.name(), toolDefinition.description(), toolDefinition.inputSchema()));
        }
        String prompt = MCPUtil.getPrompt(hoppinTools);
        var chatClient = ChatClient.builder(chatModel)
                .defaultTools(tools)
                .build();
        String userInput = "帮我搜索10首APT这首音乐";
        String result = chatClient.prompt().system(prompt).user(userInput).call().content();
        String[] args1 = MCPUtil.getArgs(result);
        if (args1 != null && args1.length > 0) {
            for (ToolCallback toolCallback : toolCallbacks) {
                ToolDefinition toolDefinition = toolCallback.getToolDefinition();
                if (toolDefinition.name().equals(args1[0])) {
                    String call = toolCallback.call(args1[1]);
                    System.out.println("工具调用结果: " + call);
                    String uuid = UUID.randomUUID().toString(); // 工具调用ID
                    String rs = chatClient.prompt().system(prompt).user(userInput)
                            .tools(toolCallback)
                            .messages(
                                    List.of(new AssistantMessage(
                                            DEFAULT_PROMPT,
                                            new HashMap<>(),
                                            List.of(
                                                    new AssistantMessage.ToolCall(
                                                            uuid, "function", toolDefinition.name(),
                                                            args1[1]
                                                    )
                                            )
                                    ), new ToolResponseMessage(
                                            List.of(
                                                    new ToolResponseMessage.ToolResponse(
                                                            uuid, toolDefinition.name(), call
                                                    )))))
                            .call().content();
                    System.out.println("AI: " + rs);
                }
            }
        } else {
            System.out.println("AI: " + result);
        }
    }
}

