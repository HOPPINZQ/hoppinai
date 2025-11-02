package com.hoppinzq.test;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import lombok.Builder;
import lombok.Data;
import okhttp3.*;

import java.text.MessageFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class ReActAgent {
    private static final String AGENT_ACTION_TEMPLATE = "工具名称，必须是[{0}]中的一个";
    // 在AI平台（如百炼、coze等）上，需要先注册工具，然后创建agent时关联工具，这样系统会自动帮你拼接到Prompt中了
    // 此次作为demo，手写写死
    private static final List<Tool> TOOLS = Arrays.asList(
            // 工具1：查询补货计划单详情
            Tool.builder()
                    .name("查询补货计划单详情")
                    .desc("根据补货计划单号查询补货计划单详情")
                    .parameters(Collections.singletonList(
                            Tool.Parameter.builder()
                                    .name("orderCode")
                                    .desc("补货计划单号")
                                    .type("string")
                                    .required(true)
                                    .build()))
                    .build(),
            // 工具2：审批补货计划单
            Tool.builder().name("审批补货计划单").desc("根据补货计划单号审批补货计划单")
                    .parameters(Collections.singletonList(
                            Tool.Parameter.builder().name("orderCode").desc("补货计划单号").type("string").required(true).build())).build());
    private static final String USER_PROMPT = "# 角色设定\n"
            + "你是一位经验丰富的供应链智能助理，专注于补货计划单的审批，具备如下技能：\n"
            + "\n"
            + "## 技能1：从上下文提取补货计划单号\n"
            + "1. 识别用户提供的文本中的补货计划单号；\n"
            + "\n"
            + "## 技能2：查询补货计划单详情并审批\n"
            + "1. 根据提取到的单号查询补货计划单的状态；\n"
            + "2. 如果是待审批状态，则调用审批工具进行审批；\n"
            + "3. 如果不是待审批状态，则告知用户该状态无法进行审批。\n"
            + "\n"
            + "## 行为准则\n"
            + "- 只讨论与供应链管理和补货计划单审批相关的话题；\n"
            + "- 回复时根据内容选择最合适的展现方式；";

    public static void main(String[] args) {
        // LLM的AK，注意不要泄露，不要泄露，不要泄露        String ak = args[0];
        // 创建 Scanner 对象用于接收用户输入
        Scanner scanner = new Scanner(System.in);                // 多轮会话的记忆
        Memory memory = new Memory();
        System.out.println("请提问，开始和AI的对话吧");                // 循环逻辑
        while (true) {
            // 接收用户输入
            String input = scanner.nextLine();
            System.out.println("用户输入：" + input);
            // 判断是否满足退出条件
            if ("exit".equalsIgnoreCase(input)) {
                System.out.println("检测到退出指令，对话结束！");
                break; // 满足条件时退出循环
            }
            // reAct
            StringBuilder latestInput = new StringBuilder(input);
            String output = reAct("sk-", memory, latestInput);
            System.out.println("AI输出: " + output);
            memory.add(new Memory.ChatMsg(Memory.ChatMsg.USER, input));
            memory.add(new Memory.ChatMsg(Memory.ChatMsg.AI, output));
            // 开始下一轮对话
        }
        // 关闭 Scanner
        scanner.close();
    }

    private static String reAct(String ak, Memory memory, StringBuilder latestInput) {
        while (true) {
            String prompt = prompt(USER_PROMPT, TOOLS, memory, latestInput);
            String llmResult = LLM.llm(prompt, ak);
            /**
             * 可能的结果
             * Thought: 已收到补货计划单号，现需查询补货计划单详情。
             * Action: 查询补货计划单详情
             * Action Input: {"orderCode": "BH002"}
             *
             * Thought: 当前补货计划单处于待审批状态，可以进行审批操作。
             * Action: 审批补货计划单
             * Action Input: {"orderCode": "BH002"}
             *
             * 1、通过Action，可以判断要调用哪个工具
             * 2、通过Action Input，知道工具入参是什么
             *
             * 此处仅做示例，因此：
             * 1、大模型返回比较玄学，正确解析是个麻烦事，demo中Action解析逻辑写的比较简单，Action Input就不解析了
             * 2、工具调用用本地函数做示例，真实场景会调用RPC接口（如Dubbo等）、HTTP等
             */
            // llm判断要调用工具：查询补货计划单详情
            if (isQueryPlanOrderAction(llmResult)) {
                String toolResult = JSON.toJSONString(queryPlanOrder(null));
                // 所谓的observation就是观察工具执行的结果，最终会追加到下一次llm调用的Prompt中
                String observataion = toolResult;
                latestInput.append("\n").append(llmResult)
                        .append("\n").append("Observation: ").append(observataion);
            }            // llm判断要调用工具：审批补货计划单
            else if (isAuditPlanOrderAction(llmResult)) {
                String toolResult = JSON.toJSONString(auditPlanOrder(null));
                // 所谓的observation就是观察工具执行的结果，最终会追加到下一次llm调用的Prompt中
                String observataion = toolResult;
                latestInput.append("\n").append(llmResult)
                        .append("\n").append("Observation: ").append(observataion);
            }            // 无需工具调用，本轮对话结束，回复用户
            else {
                return llmResult;
            }
        }
    }

    private static String prompt(String userPrompt, List<Tool> tools, Memory memory, StringBuilder latestInput) {
        String prompt = "${{user_prompt}}\n"
                + "---------------------\n"
                + "# 工具列表\n"
                + "${{tool_definitions}}\n"
                + "\n"
                + "使用如下格式：\n"
                + "Thought: 思考并确定下一步的最佳行动方案\n"
                + "Action: ${{agent_action}}\n"
                + "Action Input: 工具参数，必须是 JSON 对象\n"
                + "Observation: 工具执行结果\n"
                + "... (Thought/Action/Action Input/Observation 可以重复N次)\n"
                + "\n"
                + "注意：\n"
                + "- 不使用工具时，回复中不要出现 Thought、Action、Action Input；\n"
                + "- 使用工具前，先检查是否缺少必要参数，缺少必要参数时直接向用户提问，不要出现 Thought、Action、Action Input；\n"
                + "- 工具执行遇到问题时，向用户寻求帮助；\n"
                + "- 需要执行同一个工具多次时，Action Input 可以出现多次；\n"
                + "\n" + "---------------------\n"
                + "# 对话记录\n"
                + "${{history_record}}\n"
                + "\n"
                + "# 最新输入\n"
                + "${{latest_input}}";
        prompt = prompt.replace("${{user_prompt}}", userPrompt);
        prompt = prompt.replace("${{tool_definitions}}", JSON.toJSONString(tools));
        prompt = prompt.replace("${{agent_action}}", MessageFormat.format(AGENT_ACTION_TEMPLATE, TOOLS.stream().map(Tool::getName).collect(Collectors.joining(","))));
        prompt = prompt.replace("${{history_record}}", memory.getAll());
        prompt = prompt.replace("${{latest_input}}", latestInput.toString());
        return prompt;
    }

    /**
     * 工具1：查询补货计划单详情      * 一般对应我们的RPC接口（如Dubbo等），需要提前在AI平台上注册成工具。会通过泛化调用过去
     */
    private static PlanOrder queryPlanOrder(String orderCode) {
        PlanOrder order = new PlanOrder();
        order.setOrderCode(orderCode);        // 可审批的状态
        order.setStatus("待审批");
        return order;
    }

    /**
     * 工具2：审批补货计划单      * 一般对应我们的RPC接口（如Dubbo等），需要提前在AI平台上注册成工具。会通过泛化调用过去
     */
    private static AuditResult auditPlanOrder(String orderCode) {
        return new AuditResult("审批成功", true);
    }

    private static boolean isQueryPlanOrderAction(String llmResult) {
        return llmResult.contains("Action") && llmResult.contains("查询补货计划单详情");
    }

    private static boolean isAuditPlanOrderAction(String llmResult) {
        return llmResult.contains("Action") && llmResult.contains("审批补货计划单");
    }
}

@Data
@Builder
class Tool {
    private String name;
    private String desc;
    private List<Parameter> parameters;

    @Data
    @Builder
    public static class Parameter {
        private String name;
        private String desc;
        private String type;
        private Boolean required;
        private List<Parameter> properties;
    }
}

@Data
class Memory {
    private List<ChatMsg> memories = new ArrayList<>();

    public void add(ChatMsg chatMsg) {
        memories.add(chatMsg);
    }

    public String getAll() {
        StringBuilder sb = new StringBuilder();
        for (ChatMsg chatMsg : memories) {
            sb.append(chatMsg.getRole()).append(": \n").append(chatMsg.getMsg()).append("\n");
        }
        return sb.toString();
    }

    @Data
    public static class ChatMsg {
        public static final String USER = "用户";
        public static final String AI = "AI";
        public String role;
        public String msg;

        public ChatMsg(String role, String msg) {
            this.role = role;
            this.msg = msg;
        }
    }
}

// 大模型调用，这里可替换成千问、OpenAI等的调用
class LLM {
    private static final String model = "gpt-4o-mini";

    public static String llm(String prompt, String ak) {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(60, TimeUnit.SECONDS);   // 设置连接超时时间为60秒
        builder.readTimeout(120, TimeUnit.SECONDS);     // 设置读取超时时间为120秒
        builder.writeTimeout(60, TimeUnit.SECONDS);     // 设置写入超时时间为60秒
        OkHttpClient client = builder.build();
        MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
        JSONObject reqData = new JSONObject();
        reqData.put("model", "deepseek-r1:1.5b");
        JSONObject reqBody = new JSONObject();
        reqBody.put("role", "user");
        reqBody.put("content", prompt);
        reqData.put("messages", Collections.singletonList(reqBody));
        RequestBody body = RequestBody.create(mediaType, reqData.toJSONString());
        // 创建请求
//        Request request = new Request.Builder()
//                .url("https://api.uchat.site/v1/chat/completions")
//                .header("Authorization", "Bearer " + ak) // 增加请求头属性
//                .post(body) // 添加请求体
//                .build();                // 同步请求，demo没用流式

        Request request = new Request.Builder()
                .url("http://localhost:11434/api/chat")
                .post(body) // 添加请求体
                .build();                // 同步请求，demo没用流式
        try {
            Response response = client.newCall(request).execute();
            if (!response.isSuccessful()) {
                throw new RuntimeException("req llm exception, res : " + response.toString());
            }
            String res = response.body().string();
            GearsAIResult gearsAIResult = JSON.parseObject(res, GearsAIResult.class);
            return Optional.ofNullable(gearsAIResult).map(GearsAIResult::getChoices).map(e -> e.get(0)).map(GearsAIChoice::getMessage).map(GearsAIMessage::getContent).orElseThrow(() -> new RuntimeException("调用llm异常"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Data
    static class GearsAIResult {
        private List<GearsAIChoice> choices;        // 省略usage
    }

    @Data
    static class GearsAIChoice {
        private GearsAIMessage message;
        private Integer index;
        @JSONField(name = "finish_reason")
        private String finish_reason;
    }

    @Data
    static class GearsAIMessage {
        private String role;
        private String content;
    }
}

// 补货计划单模型
@Data
class PlanOrder {
    private String orderCode;
    private String status;
}

// 审批结果
@Data
class AuditResult {
    private String data;
    private Boolean success;

    public AuditResult(String data, Boolean success) {
        this.data = data;
        this.success = success;
    }
}
