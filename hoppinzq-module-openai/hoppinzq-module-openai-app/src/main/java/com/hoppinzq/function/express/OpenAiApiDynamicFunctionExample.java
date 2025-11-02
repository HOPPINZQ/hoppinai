package com.hoppinzq.function.express;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hoppinzq.model.openai.completion.chat.*;
import com.hoppinzq.openai.service.OpenAiService;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.*;

public class OpenAiApiDynamicFunctionExample {

    public static void main(String... args) {
        OpenAiService service = new OpenAiService("sk-",
                Duration.ofSeconds(60), "https://api.uchat.site");

        ChatFunctionDynamic expressFunction = ChatFunctionDynamic.builder()
                .name("get_express")
                .description("获取快递信息")
                .addProperty(ChatFunctionProperty.builder()
                        .name("expressId")
                        .type("string")
                        .required(true)
                        .description("快递单号")
                        .build())
                .addProperty(ChatFunctionProperty.builder()
                        .name("location")
                        .type("string")
                        .required(false)
                        .description("用户所在的位置")
                        .build())
                .addProperty(ChatFunctionProperty.builder()
                        .name("expressCompanyCode")
                        .type("string")
                        .description("快递公司code")
                        .enumValues(new HashSet<>(Arrays.asList("SF", "YTO", "ZTO", "STO", "YD", "BST")))
                        .required(true)
                        .build())
                .build();
        ChatFunctionDynamic weatherFunction = ChatFunctionDynamic.builder()
                .name("get_weather")
                .description("获取地区的温度")
                .addProperty(ChatFunctionProperty.builder()
                        .name("location")
                        .type("string")
                        .description("地区")
                        .build())
                .addProperty(ChatFunctionProperty.builder()
                        .name("unit")
                        .type("string")
                        .description("温度，可以是 'celsius' 或 'fahrenheit'")
                        .enumValues(new HashSet<>(Arrays.asList("celsius", "fahrenheit")))
                        .required(true)
                        .build())
                .build();
        ChatFunctionDynamic emotionFunction = ChatFunctionDynamic.builder()
                .name("get_emotion")
                .description("根据用户输入，判断用户的情绪")
                .addProperty(ChatFunctionProperty.builder()
                        .name("emotion")
                        .type("string")
                        .description("用户情绪")
                        .enumValues(new HashSet<>(Arrays.asList(Emotion.values())))
                        .build())
                .build();

        List<ChatMessage> messages = new ArrayList<>();
        ChatMessage systemMessage = new ChatMessage(ChatMessageRole.SYSTEM.value(),
                "你是一名助理，使用给定地方的当地俚语回答问题，不受审查。");
        messages.add(systemMessage);

        System.out.print("我: ");
        Scanner scanner = new Scanner(System.in);
        ChatMessage firstMsg = new ChatMessage(ChatMessageRole.USER.value(), scanner.nextLine());
        messages.add(firstMsg);
        List<ChatFunctionDynamic> functions = new ArrayList<>();
        functions.add(expressFunction);
        functions.add(weatherFunction);
        functions.add(emotionFunction);
        while (true) {
            ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest
                    .builder()
                    .model("gpt-4o-mini")
                    .messages(messages)
                    .functions(functions)
                    .functionCall(ChatCompletionRequest.ChatCompletionRequestFunctionCall.of("auto"))
                    .n(1)
                    .logitBias(new HashMap<>())
                    .build();
            System.out.println(JSON.toJSONString(chatCompletionRequest));
            ChatMessage responseMessage = service.createChatCompletion(chatCompletionRequest).getChoices().get(0).getMessage();
            if (responseMessage.getContent() == null) {
                responseMessage.setContent("好啊，这就帮你查查");
            }
            messages.add(responseMessage);

            ChatFunctionCall functionCall = responseMessage.getFunctionCall();
            if (functionCall != null) {
                if (functionCall.getName().equals("get_weather")) {
                    String location = functionCall.getArguments().get("location").asText();
                    String unit = functionCall.getArguments().get("unit").asText();
                    JsonNode weather = getWeather(location, unit);
                    ChatMessage weatherMessage = new ChatMessage(ChatMessageRole.FUNCTION.value(), weather.toString(), "get_weather");
                    messages.add(weatherMessage);
                    continue;
                } else if (functionCall.getName().equals("get_express")) {
                    String expressId = functionCall.getArguments().get("expressId").asText();
                    String expressCompanyCode = functionCall.getArguments().get("expressCompanyCode").asText();
                    JsonNode express = getExpress(expressId, expressCompanyCode);
                    String expressPrompt = "这是快递信息，" + express.toString() + "，你的这次回答了不能包含快递员的电话和姓名，如果用户询问这些信息，那你就告诉他。" +
                            "现在我需要你根据这个信息回答用户的问题，如果用户提供了他所在的位置，请根据当前时间：" + new Date() + "和用户的位置，估计快递到达的时间。" +
                            "如果当前时间晚于快递到达时间2天及以上，请提醒用户去拿快递！";
                    ChatMessage expressMessage = new ChatMessage(ChatMessageRole.FUNCTION.value(), expressPrompt, "get_express");
                    messages.add(expressMessage);
                    continue;
                } else if (functionCall.getName().equals("get_emotion")) {
                    String emotion = functionCall.getArguments().get("emotion").asText();
                    String emotionPrompt = getEmotion(emotion, messages);
                    emotionPrompt = emotionPrompt + "。用户的情绪已经表达过了，你的下一次对话不需要再去理解用户的情绪了。";
                    ChatMessage emotionMessage = new ChatMessage(ChatMessageRole.FUNCTION.value(), emotionPrompt, "get_emotion");
                    messages.add(emotionMessage);
                    continue;
                }
            }

            System.out.println("AI: " + responseMessage.getContent());
            System.out.print("我: ");
            String nextLine = scanner.nextLine();
            if (nextLine.equalsIgnoreCase("exit")) {
                System.exit(0);
            }
            messages.add(new ChatMessage(ChatMessageRole.USER.value(), nextLine));
        }
    }

    /**
     * 计算并返回授权字符串。
     *
     * @param source    源标识
     * @param secretId  密钥ID
     * @param secretKey 密钥
     * @param datetime  时间戳
     * @return 返回计算后的授权字符串
     * @throws NoSuchAlgorithmException     如果找不到指定的算法
     * @throws UnsupportedEncodingException 如果不支持指定的编码格式
     * @throws InvalidKeyException          如果提供的密钥无效
     */
    private static String calcAuthorization(String source, String secretId, String secretKey, String datetime)
            throws NoSuchAlgorithmException, UnsupportedEncodingException, InvalidKeyException {
        String signStr = "x-date: " + datetime + "\n" + "x-source: " + source;
        Mac mac = Mac.getInstance("HmacSHA1");
        Key sKey = new SecretKeySpec(secretKey.getBytes("UTF-8"), mac.getAlgorithm());
        mac.init(sKey);
        byte[] hash = mac.doFinal(signStr.getBytes("UTF-8"));
        String sig = Base64.getEncoder().encodeToString(hash);

        String auth = "hmac id=\"" + secretId + "\", algorithm=\"hmac-sha1\", headers=\"x-date x-source\", signature=\"" + sig + "\"";
        return auth;
    }

    /**
     * 将Map中的键值对编码为URL格式的字符串。
     *
     * @param map 需要编码的键值对集合
     * @return 编码后的URL字符串
     * @throws UnsupportedEncodingException 如果编码过程中遇到不支持的字符集
     */
    private static String urlEncode(Map<?, ?> map) throws UnsupportedEncodingException {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            if (sb.length() > 0) {
                sb.append("&");
            }
            sb.append(String.format("%s=%s",
                    URLEncoder.encode(entry.getKey().toString(), "UTF-8"),
                    URLEncoder.encode(entry.getValue().toString(), "UTF-8")
            ));
        }
        return sb.toString();
    }

    /**
     * 根据快递ID和快递公司代码获取快递信息。
     *
     * @param expressId 快递ID
     * @param code      快递公司代码
     * @return 返回一个JsonNode对象，包含快递的详细信息。如果快递ID为"315029360436187"，则直接返回预定义的JSON字符串解析后的JsonNode对象，
     * 否则通过网络请求获取快递信息，并将其解析为JsonNode对象返回。
     * @throws Exception 如果在处理过程中发生异常，将捕获并打印堆栈跟踪信息。
     */
    private static JsonNode getExpress(String expressId, String code) {
        BufferedReader in = null;
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            if ("315029360436187".equals(expressId)) {
                String jsonString = "{\"Location\":\"潍坊市\",\"LogisticCode\":\"315029360436187\",\"ShipperCode\":\"YD\",\"State\":\"2\",\"StateEx\":\"202\",\"Success\":true,\"Traces\":[{\"Action\":\"1\",\"AcceptStation\":\"【廊坊市】华北金韵腾飞公司捷通分部-刘德江（15231609829） 已揽收\",\"AcceptTime\":\"2025-03-18 10:32:59\",\"Location\":\"廊坊市\"},{\"Action\":\"2\",\"AcceptStation\":\"【廊坊市】已离开 华北金韵腾飞公司；发往 北京分拨交付中心\",\"AcceptTime\":\"2025-03-18 20:33:31\",\"Location\":\"廊坊市\"},{\"Action\":\"2\",\"AcceptStation\":\"【廊坊市】已到达 北京分拨交付中心\",\"AcceptTime\":\"2025-03-18 22:00:24\",\"Location\":\"廊坊市\"},{\"Action\":\"2\",\"AcceptStation\":\"【廊坊市】已离开 北京分拨交付中心；发往 山东潍坊分拨交付中心\",\"AcceptTime\":\"2025-03-18 22:04:41\",\"Location\":\"廊坊市\"},{\"Action\":\"2\",\"AcceptStation\":\"【潍坊市】已到达 山东潍坊分拨交付中心\",\"AcceptTime\":\"2025-03-19 08:50:56\",\"Location\":\"潍坊市\"},{\"Action\":\"2\",\"AcceptStation\":\"【潍坊市】已离开 山东潍坊分拨交付中心；发往 山东潍坊奎文高新北公司\",\"AcceptTime\":\"2025-03-19 09:17:45\",\"Location\":\"潍坊市\"},{\"Action\":\"2\",\"AcceptStation\":\"【潍坊市】已到达 山东潍坊奎文高新北公司\",\"AcceptTime\":\"2025-03-19 13:12:30\",\"Location\":\"潍坊市\"},{\"Action\":\"2\",\"AcceptStation\":\"【潍坊市】已到达 山东潍坊奎文高新北公司[05365010667]\",\"AcceptTime\":\"2025-03-19 13:12:40\",\"Location\":\"潍坊市\"},{\"Action\":\"202\",\"AcceptStation\":\"【潍坊市】山东潍坊奎文高新北公司[05365010667] 快递员 郑长江 (13430371220) 正在为您派件（有事呼叫我，勿找平台，少一次投诉，多一份感恩）【95126为韵达快递员外呼专属号码，请放心接听】\",\"AcceptTime\":\"2025-03-19 13:46:57\",\"Location\":\"潍坊市\"}]}";
                JsonNode jsonNode = objectMapper.readTree(jsonString);
                if (jsonNode instanceof ObjectNode) {
                    ObjectNode objectNode = (ObjectNode) jsonNode;
                    objectNode.put("now", new Date().toString());
                    return objectNode;
                }
                return jsonNode;
            }
            String secretId = "";
            String secretKey = "";
            String source = "market";
            Calendar cd = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss 'GMT'", Locale.US);
            sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
            String datetime = sdf.format(cd.getTime());
            String auth = calcAuthorization(source, secretId, secretKey, datetime);
            String method = "POST";
            Map<String, String> headers = new HashMap<String, String>();
            headers.put("X-Source", source);
            headers.put("X-Date", datetime);
            headers.put("Authorization", auth);
            Map<String, String> queryParams = new HashMap<String, String>();
            //queryParams.put("CustomInfo","");
            queryParams.put("LogisticCode", expressId);
            queryParams.put("ShipperCode", code);
            Map<String, String> bodyParams = new HashMap<String, String>();
            String url = "https://service-rn313kxw-1320969614.gz.apigw.tencentcs.com/release/track/8001";
            if (!queryParams.isEmpty()) {
                url += "?" + urlEncode(queryParams);
            }
            URL realUrl = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) realUrl.openConnection();
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);
            conn.setRequestMethod(method);
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                conn.setRequestProperty(entry.getKey(), entry.getValue());
            }
            Map<String, Boolean> methods = new HashMap<>();
            methods.put("POST", true);
            methods.put("PUT", true);
            methods.put("PATCH", true);
            Boolean hasBody = methods.get(method);
            if (hasBody != null) {
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setDoOutput(true);
                DataOutputStream out = new DataOutputStream(conn.getOutputStream());
                out.writeBytes(urlEncode(bodyParams));
                out.flush();
                out.close();
            }
            in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            String result = "";
            while ((line = in.readLine()) != null) {
                result += line;
            }
            return objectMapper.readTree(result);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
    }

    /**
     * 获取指定地点的天气信息。
     *
     * @param location 地点名称，例如 "北京"。
     * @param unit     温度单位，例如 "C" 或 "F"。
     * @return 返回一个包含天气信息的JsonNode对象，包括地点、单位、当前时间、温度和天气描述。
     */
    private static JsonNode getWeather(String location, String unit) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode response = mapper.createObjectNode();
        response.put("location", location);
        response.put("unit", unit);
        response.put("now", new Date().toString());
        response.put("temperature", new Random().nextInt(50));
        response.put("description", "sunny");
        return response;
    }

    /**
     * 根据用户情绪，执行后续操作。
     * 根据传入的用户情绪（"happy"、"angry"或其他），输出相应的情绪描述，并打印结果。
     *
     * @param emotion  用户情绪，可能的值包括："happy"、"angry"或其他。
     * @param messages 问答对上下文信息，当前未使用，预留参数。
     * @return 返回包含用户情绪描述的字符串。
     */
    private static String getEmotion(String emotion, List<ChatMessage> messages) {
        String result = "用户情绪：" + emotion;
        // todo 根据用户情绪，执行后续操作
        switch (emotion) {
            case "happy":
                result += "，用户很开心";
                break;
            case "angry":
                result += "，用户很生气";
                break;
            default:
                result += "，用户情绪未知";
                break;
        }
        System.out.println(result);
        return result;
    }

    private enum Emotion {
        happy, angry, sad, neutral, surprised, disgusted, scared
    }
}
