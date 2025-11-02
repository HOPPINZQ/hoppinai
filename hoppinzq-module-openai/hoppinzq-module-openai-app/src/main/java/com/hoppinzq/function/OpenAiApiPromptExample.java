package com.hoppinzq.function;

import com.alibaba.fastjson.JSONObject;
import com.hoppinzq.model.openai.completion.chat.ChatCompletionRequest;
import com.hoppinzq.model.openai.completion.chat.ChatCompletionResult;
import com.hoppinzq.model.openai.completion.chat.ChatMessage;
import com.hoppinzq.model.openai.completion.chat.ChatMessageRole;
import com.hoppinzq.openai.service.OpenAiService;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

class OpenAiApiPromptExample {
    public static void main(String... args) {
        OpenAiService service = new OpenAiService("sk-",
                Duration.ofSeconds(60), "https://api.uchat.site");
        final List<ChatMessage> messages = new ArrayList<>();
        final ChatMessage systemMessage = new ChatMessage(ChatMessageRole.SYSTEM.value(), "你是一个有用的助手，可以使用一些工具来回答用户的问题，现在有这些工具：\n" +
                "\n" +
                "工具1:\n" +
                "\t- 工具名称:搜索工具\n" +
                "\t- 工具描述: 可以搜索互联网上的信息\n" +
                "\t- 工具参数：\n" +
                "\t    - searchType:\n" +
                "\t        - 类型：string\n" +
                "\t        - 描述：搜索类型，可以是音乐，网站和其他\n" +
                "\t        - 可选值：[\"music\",\"website\",\"other\"]\n" +
                "\t        - 是否必填：否\n" +
                "\t    - searchContent:\n" +
                "\t        - 类型：string\n" +
                "\t        - 描述：搜索内容\n" +
                "\t        - 是否必填：是\n" +
                "\t\n" +
                "根据用户的问题选择合适的工具。如果不需要工具，请直接回复。\n" +
                "\n" +
                "重要提示：当您需要使用工具时，您必须只使用以下确切的JSON对象格式进行响应，而不能使用其他格式，也不能额外输出其他内容：\n" +
                "\n" +
                "{\n" +
                "    \"tool\":\"tool_name\",\n" +
                "    \"arguments\":{\n" +
                "        \"arguments_name\"：\"value\"\n" +
                "    }\n" +
                "}\n" +
                "\n" +
                "收到工具的响应后：\n" +
                "    - 1、将原始数据转换为自然的对话式响应\n" +
                "    - 2、保持回答简洁但信息丰富，而且只要json格式\n" +
                "    - 3、关注最相关的信息\n" +
                "    - 4、使用用户问题中的适当上下文\n" +
                "    - 5、永远不要用完全相同的参数重新进行之前的工具调用\n" +
                "    - 6、与用户交流时切勿提及工具名称。例如，不要说\"我需要使用edit_file工具来编辑你的文件\"，只需说\"我将编辑你的文件\"\n" +
                "    - 7、只在必要时调用工具。如果用户的任务较为宽泛或你已经知道答案，直接回应而不调用工具\n" +
                "    - 8、在调用每个工具之前，先向用户解释为什么要调用它\n" +
                "\n" +
                "请仅使用上面明确定义的工具。");
        final ChatMessage userMessage = new ChatMessage(ChatMessageRole.USER.value(), "搜索烟花易冷这首音乐");
        messages.add(systemMessage);
        messages.add(userMessage);
        ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest
                .builder()
                .model("gpt-4o-mini")
                .messages(messages)
                .build();
        System.err.println(JSONObject.toJSONString(chatCompletionRequest));
        ChatCompletionResult chatCompletion = service.createChatCompletion(chatCompletionRequest);
        System.err.println(JSONObject.toJSONString(chatCompletion));
        service.shutdownExecutor();
    }
}
