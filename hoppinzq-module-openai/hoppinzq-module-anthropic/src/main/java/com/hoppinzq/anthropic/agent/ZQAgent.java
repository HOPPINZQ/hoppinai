package com.hoppinzq.anthropic.agent;

import com.anthropic.client.AnthropicClient;
import com.anthropic.core.JsonValue;
import com.anthropic.models.messages.*;
import com.hoppinzq.anthropic.tool.ToolDefinition;
import lombok.Data;

import java.util.*;

import static com.hoppinzq.anthropic.constant.AIConstants.MAX_TOKENS;

/**
 * @author hoppinzq
 */
@Data
public class ZQAgent {
    private String systemPrompt;
    private final Scanner scanner;
    private final String model;
    private final AnthropicClient client;
    private final List<ToolDefinition> tools;
    private final List<MessageParam> conversation = new ArrayList<>();

    public ZQAgent(AnthropicClient client, String model, List<ToolDefinition> tools) {
        this.client = client;
        this.model = model;
        this.scanner = new Scanner(System.in);
        this.tools = tools;
    }

    public void run() {
        System.out.println("开始对话吧");
        while (true) {
            System.out.print("\u001b[94m你\u001b[0m: ");
            String userInput = scanner.nextLine();
            if (userInput.isEmpty()) {
                continue;
            }
            if(systemPrompt!=null && !systemPrompt.isEmpty()){
                MessageParam systemMessage = MessageParam.builder()
                        .role(MessageParam.Role.USER)
                        .content(systemPrompt)
                        .build();
                conversation.add(systemMessage);
            }
            MessageParam userMessage = MessageParam.builder()
                    .role(MessageParam.Role.USER)
                    .content(userInput)
                    .build();
            conversation.add(userMessage);

            Message message;
            try {
                message = runInference(conversation);
            } catch (Exception e) {
                System.out.println("错误: " + e.getMessage());
                continue;
            }
            conversation.add(message.toParam());

            while (true) {
                List<ContentBlockParam> toolResults = new ArrayList<>();
                boolean hasToolUse = false;

                for (ContentBlock content : message.content()) {
                    if (content.isText()) {
                        System.out.printf("\u001b[93mAI\u001b[0m: %s%n", content.text());
                    } else if (content.isToolUse()) {
                        hasToolUse = true;
                        ToolUseBlock toolUse = content.asToolUse();

                        System.out.printf("\u001b[96m工具\u001b[0m: %s(%s)%n", toolUse.name(), toolUse._input());

                        String toolResult = null;
                        Exception toolError = null;
                        boolean toolFound = false;

                        for (ToolDefinition tool : tools) {
                            if (tool.getName().equals(toolUse.name())) {
                                try {
                                    JsonValue input = toolUse._input();
                                    //todo : 注意下面一行代码这里！！！！待优化 ,处理JsonValue的逻辑是如此的丑陋
                                    toolResult = tool.getFunction().apply(Objects.requireNonNull(input.convert(tool.getType())).toString());
                                    System.out.printf("\u001b[92m结果\u001b[0m: %s%n", toolResult);
                                } catch (Exception e) {
                                    toolError = e;
                                    System.out.printf("\u001b[91m错误\u001b[0m: %s%n", e.getMessage());
                                }
                                toolFound = true;
                                break;
                            }
                        }

                        if (!toolFound) {
                            toolError = new Exception("工具 '" + toolUse.name() + "' 没有找到");
                            System.out.printf("\u001b[91m错误\u001b[0m: %s%n", toolError.getMessage());
                        }

                        if (toolError != null) {
                            toolResults.add(ContentBlockParam.ofToolResult(
                                    ToolResultBlockParam.builder()
                                            .toolUseId(toolUse.id())
                                            .content(toolError.getMessage())
                                            .isError(true)
                                            .build()
                            ));
                        } else {
                            toolResults.add(ContentBlockParam.ofToolResult(
                                    ToolResultBlockParam.builder()
                                            .toolUseId(toolUse.id())
                                            .content(toolResult)
                                            .isError(false)
                                            .build()
                            ));
                        }
                    }
                }

                if (!hasToolUse) {
                    break;
                }
                MessageParam toolResultMessage = MessageParam.builder()
                        .role(MessageParam.Role.USER)
                        .content(toolResults.toString())
                        .build();
                conversation.add(toolResultMessage);
                try {
                    message = runInference(conversation);
                } catch (Exception e) {

                    System.out.println("错误: " + e.getMessage());
                    break;
                }
                conversation.add(message.toParam());
            }
        }
    }

    private Message runInference(List<MessageParam> conversation){
        // 准备工具配置
        List<ToolUnion> anthropicTools = new ArrayList<>();
        for (ToolDefinition tool : tools) {
            anthropicTools.add(ToolUnion.ofTool(
                    Tool.builder()
                            .name(tool.getName())
                            .description(tool.getDescription())
                            .inputSchema(tool.getInputSchema())
                            .build()
            ));
        }
        MessageCreateParams.Builder messageBuilder = MessageCreateParams.builder()
                .model(model)
                .messages(conversation)
                .tools(anthropicTools);
        if(MAX_TOKENS > 0){
            messageBuilder.maxTokens(MAX_TOKENS);
        }
        MessageCreateParams params = messageBuilder.build();
        return client.messages().create(params);
    }
}