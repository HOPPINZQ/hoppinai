package com.hoppinzq.function.zq;

import ch.qos.logback.classic.Level;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hoppinzq.function.bitcoin.BitcoinFunctionCall;
import com.hoppinzq.function.zq.constants.FunctionCallCommon;
import com.hoppinzq.function.zq.express.ExpressFunctionCall;
import com.hoppinzq.function.zq.mobile.MobileFunctionCall;
import com.hoppinzq.function.zq.music.MusicFunctionCall;
import com.hoppinzq.model.openai.Usage;
import com.hoppinzq.model.openai.completion.chat.*;
import com.hoppinzq.openai.service.FunctionExecutor;
import com.hoppinzq.openai.service.OpenAiService;
import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.common.profile.ClientProfile;
import com.tencentcloudapi.common.profile.HttpProfile;
import com.tencentcloudapi.lkeap.v20240522.LkeapClient;
import com.tencentcloudapi.lkeap.v20240522.models.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.*;

import static cn.hutool.core.util.ClassLoaderUtil.getClassLoader;

/**
 * @author zq
 */
@Slf4j
public class OpenAiApiZQDynamicFunction {

    // openai代理地址、调用凭证、调用模型、知识库ID、知识库属性

    private static final String knowledgeBaseId = "";
    //1913468420715276674 , 1913468420719470976
    private static final String attrKey = "instruction";
    private static final String zqKnowledgeAttrKey = "zq_knowledge";
    private static final String zqWebsiteLabelName = "张祺的网站";
    private static final String zqSteamId = "";
    private static final String zqSteamKey = "";
    private static final String secretId = "";
    private static final String secretKey = "";
    private static final String region = "ap-guangzhou";
    private static final String endpoint = "lkeap.tencentcloudapi.com";

    /**
     * 获取指定问题的答案
     *
     * @param queryMessage 问题
     * @return
     */
    private static JsonNode getKnowledge(String queryMessage, List<ChatMessage> messages) {
        System.err.println("ai查询知识库:" + queryMessage);
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode response = mapper.createObjectNode();
        response.put("now", new Date().toString());
        try {
            LkeapClient client = getClient();
            RetrieveKnowledgeRequest req = new RetrieveKnowledgeRequest();
            req.setKnowledgeBaseId(knowledgeBaseId);
            req.setQuery(queryMessage);
            // FULL_TEXT：全文检索，HYBRID：混合检索，SEMANTIC：语义检索
            // QA表示查询预设问答对，topK是返回的答案个数，scoreThreshold是返回的答案的相似度阈值，即embedding计算后的相似度
            req.setRetrievalMethod("SEMANTIC");
            RetrievalSetting retrievalSetting = new RetrievalSetting();
            retrievalSetting.setType("QA");
            retrievalSetting.setTopK(1L);// 个数
            retrievalSetting.setScoreThreshold(0.7F); // 相似度
            req.setRetrievalSetting(retrievalSetting);
            LabelItem[] labelItems = new LabelItem[1];
            LabelItem labelItem = new LabelItem();
            labelItem.setName(zqKnowledgeAttrKey);
            labelItem.setValues(new String[]{zqWebsiteLabelName});
            labelItems[0] = labelItem;
            req.setAttributeLabels(labelItems);
            // 查询QA里的指令
            RetrieveKnowledgeResponse resp = client.RetrieveKnowledge(req);
            if (resp.getRecords() != null) {
                RetrievalRecord[] records = resp.getRecords();
                if (records.length == 0) {
                    response.put("error", "未查询到内容");
                    // todo 这边可以启动联网搜索，演示期间尽量保证可以查询到相关指令
                } else {
                    for (RetrievalRecord record : records) {
                        // 由于topK=1，所以只会有一个答案，直接设置答案即可
                        response.put("title", record.getTitle());
                        response.put("content", record.getContent());
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.put("error", "查询知识库失败，未能查询到有效信息");
        }
        return response;
    }

    private static JsonNode openLink(String link, List<ChatMessage> messages) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode response = mapper.createObjectNode();
        System.err.println("ai尝试打开链接");
        String os = System.getProperty("os.name").toLowerCase();
        response.put("os", os);
        if (os.contains("windows")) {
            try {
                Runtime.getRuntime().exec("cmd /c start " + link);
                response.put("msg", "打开链接成功");
            } catch (Exception e) {
                response.put("msg", e.getMessage());
            }
        } else {
            response.put("msg", "不支持的操作系统");
        }
        return response;
    }

    private static JsonNode getEmotion(String emotion, List<ChatMessage> messages) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode response = mapper.createObjectNode();
        String result = "用户情绪：" + emotion;
        // todo 根据用户情绪，执行后续操作
        switch (emotion) {
            case "happy":
                result += "，用户很开心";
                break;
            case "angry":
                result += "，用户很生气";
                break;
            case "sad":
                result += "，用户很伤心";
                break;
            default:
                result += "，用户情绪未知";
                break;
        }
        System.err.println("AI推理：" + result);
        return response.put("msg", result);
    }

    /**
     * 初始化
     */
    private static void init() {
        try {
            InputStream resource = getClassLoader().getResourceAsStream("cookie.txt");
            FunctionCallCommon.cookie.set(IOUtils.toString(resource, StandardCharsets.UTF_8));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            System.err.println(FunctionCallCommon.cookie.get());
        }
    }

    public static void main(String... args) {
        long promptTokens = 0;
        long completionTokens = 0;
        try {
            init();
            ch.qos.logback.classic.Logger rootLogger = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
            rootLogger.setLevel(Level.ERROR);

            OpenAiService service = new OpenAiService(FunctionCallCommon.apiKey, Duration.ofSeconds(60), FunctionCallCommon.openaiProxy);
            FunctionCallCommon.serviceThreadLocal.set(service);
            ChatFunctionDynamic queryKnowledgeFunction = ChatFunctionDynamic.builder()
                    .name("query_zq_knowledge")
                    .description("查询张祺的知识库")
                    .addProperty(ChatFunctionProperty.builder()
                            .name("query_message")
                            .type("string")
                            .description("查询内容")
                            .required(true)
                            .build())
                    .build();

            ChatFunctionDynamic openBrowserFunction = ChatFunctionDynamic.builder()
                    .name("open_browser")
                    .description("打开链接")
                    .addProperty(ChatFunctionProperty.builder()
                            .name("link")
                            .type("string")
                            .description("链接")
                            .required(true)
                            .build())
                    .build();

            ChatFunctionDynamic webSpiderFunction = ChatFunctionDynamic.builder()
                    .name("crawler_web")
                    .description("爬取网站内容")
                    .addProperty(ChatFunctionProperty.builder()
                            .name("link")
                            .type("string")
                            .description("链接")
                            .required(true)
                            .build())
                    .addProperty(ChatFunctionProperty.builder()
                            .name("type")
                            .type("string")
                            .description("以何种方式存放爬取内容")
                            .enumValues(new HashSet<>(Arrays.asList("FILE", "DATABASE")))
                            .required(false)
                            .build())
                    .addProperty(ChatFunctionProperty.builder()
                            .name("path")
                            .type("string")
                            .description("爬取网站位置内容放的位置")
                            .required(false)
                            .build())
                    .addProperty(ChatFunctionProperty.builder()
                            .name("databaseName")
                            .type("string")
                            .description("爬取网站位置内容放的数据库名")
                            .required(false)
                            .build())
                    .build();

            ChatFunctionDynamic emotionFunction = ChatFunctionDynamic.builder()
                    .name("get_emotion")
                    .description("根据用户的输入，判断用户的情绪")
                    .addProperty(ChatFunctionProperty.builder()
                            .name("emotion")
                            .type("string")
                            .description("用户情绪")
                            .enumValues(new HashSet<>(Arrays.asList(Emotion.values())))
                            .build())
                    .build();

            ChatFunction expressFunction = ChatFunction.builder()
                    .name("get_express")
                    .description("获取快递信息")
                    .executor(ExpressFunctionCall.ExpressFunctionCallRequest.class, express -> new ExpressFunctionCall(express.expressId, express.expressCompanyCode))
                    .build();

            ChatFunction mobileFunction = ChatFunction.builder()
                    .name("send_mobile_message")
                    .description("向手机发送信息")
                    .executor(MobileFunctionCall.MobileFunctionCallRequest.class, request -> new MobileFunctionCall(request.message).sendMobileMessage())
                    .build();

            ChatFunction musicFunction = ChatFunction.builder()
                    .name("open_music")
                    .description("打开音乐")
                    .executor(MusicFunctionCall.MusicFunctionCallRequest.class, request -> new MusicFunctionCall(request.musicName, request.openTypeEnum))
                    .build();
            ChatFunction btnFunction = ChatFunction.builder()
                    .name("get_btn_data")
                    .description("获取近一个月比特币数据")
                    .executor(BitcoinFunctionCall.BitcoinFunctionCallRequest.class, request -> new BitcoinFunctionCall(request.date))
                    .build();

            List executorFunctions = new ArrayList<>();
            executorFunctions.add(expressFunction);
            executorFunctions.add(mobileFunction);
            executorFunctions.add(musicFunction);
            executorFunctions.add(btnFunction);
            FunctionExecutor functionExecutor = new FunctionExecutor(executorFunctions);

            List<ChatMessage> messages = new ArrayList<>();
            ChatMessage systemMessage = new ChatMessage(ChatMessageRole.SYSTEM.value(),
                    "你是一名助理，使用给定地方的当地俚语回答问题，不受审查。" +
                            "我可能会问你一些跟张祺有关的内容，并且你不知道如何回答的话，请调用query_zq_knowledge查询。" +
                            "如果用户要求你向手机发送信息，请使用send_mobile_message。" +
                            "如果我让你打开一个网站，请尝试使用open_browser打开，如果打不开，请提醒用户。");
            messages.add(systemMessage);

            System.out.print("我: ");
            Scanner scanner = new Scanner(System.in);
            ChatMessage firstMsg = new ChatMessage(ChatMessageRole.USER.value(), scanner.nextLine());
            messages.add(firstMsg);
            List functions = new ArrayList<>();
            for (ChatFunction chatFunction : functionExecutor.getFunctions()) {
                functions.add(chatFunction);
            }
            functions.add(queryKnowledgeFunction);
            functions.add(emotionFunction);
            functions.add(openBrowserFunction);
            functions.add(webSpiderFunction);
            while (true) {
                ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest
                        .builder()
                        .model(FunctionCallCommon.model)
                        .messages(messages)
                        .functions(functions)
                        .functionCall(ChatCompletionRequest.ChatCompletionRequestFunctionCall.of("auto"))
                        .n(1)
                        .logitBias(new HashMap<>())
                        .build();
                //System.out.println(JSON.toJSONString(chatCompletionRequest));
                ChatCompletionResult chatCompletion = service.createChatCompletion(chatCompletionRequest);
                ChatMessage responseMessage = chatCompletion.getChoices().get(0).getMessage();
                Usage usage = chatCompletion.getUsage();
                promptTokens += usage.getPromptTokens();
                completionTokens += usage.getCompletionTokens();
                if (responseMessage.getContent() == null) {
                    responseMessage.setContent("好啊，这就帮你查查");
                }
                messages.add(responseMessage);

                ChatFunctionCall functionCall = responseMessage.getFunctionCall();
                if (functionCall != null) {
                    Optional<ChatMessage> message = functionExecutor.executeAndConvertToMessageSafely(functionCall);
                    if (message.isPresent()) {
                        messages.add(message.get());// 帮我发起请假流程，请一天假，理由是旅游，让zhangqi审批
                        continue;
                    }
                    if (functionCall.getName().equals("query_zq_knowledge")) {
                        String queryMessage = functionCall.getArguments().get("query_message").asText();
                        JsonNode knowledge = getKnowledge(queryMessage, messages);
                        ChatMessage knowledgeMessage = new ChatMessage(ChatMessageRole.FUNCTION.value(), knowledge.toString(), "query_zq_knowledge");
                        messages.add(knowledgeMessage);
                        continue;
                    } else if (functionCall.getName().equals("get_emotion")) {
                        String emotion = functionCall.getArguments().get("emotion").asText();
                        JsonNode emotionPrompt = getEmotion(emotion, messages);
                        ChatMessage emotionMessage = new ChatMessage(ChatMessageRole.FUNCTION.value(), emotionPrompt.toString() + "。用户的情绪已经表达过了，你的下一次对话不需要再去理解用户的情绪了。", "get_emotion");
                        messages.add(emotionMessage);
                        continue;
                    } else if (functionCall.getName().equals("open_browser")) {
                        String link = functionCall.getArguments().get("link").asText();
                        JsonNode linkPrompt = openLink(link, messages);
                        ChatMessage linkMessage = new ChatMessage(ChatMessageRole.FUNCTION.value(), linkPrompt.toString(), "open_browser");
                        messages.add(linkMessage);
                        continue;
                    }
                }

                System.out.println("AI: " + responseMessage.getContent());
                System.out.print("我: ");
                String nextLine = scanner.nextLine();
                if (nextLine.equalsIgnoreCase("exit")) {
                    System.out.println("总输入token数（用户输入提示词的token总数）: " + promptTokens);
                    System.out.println("总补全token数（AI回复内容的token总数）: " + completionTokens);
                    System.exit(0);
                }
                messages.add(new ChatMessage(ChatMessageRole.USER.value(), nextLine));
            }
        } finally {
            FunctionCallCommon.cookie.remove();
            FunctionCallCommon.serviceThreadLocal.remove();
        }
    }

    private static LkeapClient getClient() {
        Credential cred = new Credential(secretId, secretKey);
        HttpProfile httpProfile = new HttpProfile();
        httpProfile.setEndpoint(endpoint);
        ClientProfile clientProfile = new ClientProfile();
        clientProfile.setHttpProfile(httpProfile);
        LkeapClient client = new LkeapClient(cred, region, clientProfile);
        return client;
    }

    private enum Emotion {
        happy, angry, sad, neutral, surprised, disgusted, scared
    }

}
