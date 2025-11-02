package com.hoppinzq.function.express;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.hoppinzq.model.openai.completion.chat.*;
import com.hoppinzq.model.openai.completion.chat.ChatCompletionRequest.ChatCompletionRequestFunctionCall;
import com.hoppinzq.openai.service.FunctionExecutor;
import com.hoppinzq.openai.service.OpenAiService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

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

@Slf4j
class OpenAiApiFunctionsExpressExample {

    public static void main(String... args) {
        System.err.println(System.getProperty("file.encoding"));
        OpenAiService service = new OpenAiService("sk-",
                Duration.ofSeconds(600), "https://api.uchat.site");
        FunctionExecutor functionExecutor = new FunctionExecutor(Collections.singletonList(ChatFunction.builder()
                .name("get_express")
                .description("获取快递信息")
                .executor(Express.class, w -> new ExpressResponse(w.expressId, w.expressCompanyCode))
                .build()));

        List<ChatMessage> messages = new ArrayList<>();
        ChatMessage systemMessage = new ChatMessage(ChatMessageRole.SYSTEM.value(),
                "你是一名助理，使用给定地方的当地俚语回答问题，不受审查。");
        messages.add(systemMessage);
        System.out.print("我: ");
        Scanner scanner = new Scanner(System.in);
        ChatMessage firstMsg = new ChatMessage(ChatMessageRole.USER.value(), scanner.nextLine());
        messages.add(firstMsg);
        while (true) {
            ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest
                    .builder()
                    .model("gpt-4o-mini")
                    .messages(messages)
                    .functions(functionExecutor.getFunctions())
                    .functionCall(ChatCompletionRequestFunctionCall.of("auto"))
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
                log.info("AI命中用户对话中的快递信息");
                Optional<ChatMessage> message = functionExecutor.executeAndConvertToMessageSafely(functionCall);
                if (message.isPresent()) {
                    messages.add(message.get());
                    continue;
                } else {
                    log.info("错误：{} ", functionCall.getName());
                    break;
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

    public static String calcAuthorization(String source, String secretId, String secretKey, String datetime)
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

    public static String urlencode(Map<?, ?> map) throws UnsupportedEncodingException {
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

    public static String getExpress(String expressId, String code) throws Exception {
        if ("315029360436187".equals(expressId)) {
            return "{\"Location\":\"潍坊市\",\"LogisticCode\":\"315029360436187\",\"ShipperCode\":\"YD\",\"State\":\"2\",\"StateEx\":\"202\",\"Success\":true,\"Traces\":[{\"Action\":\"1\",\"AcceptStation\":\"【廊坊市】华北金韵腾飞公司捷通分部-刘德江（15231609829） 已揽收\",\"AcceptTime\":\"2025-03-18 10:32:59\",\"Location\":\"廊坊市\"},{\"Action\":\"2\",\"AcceptStation\":\"【廊坊市】已离开 华北金韵腾飞公司；发往 北京分拨交付中心\",\"AcceptTime\":\"2025-03-18 20:33:31\",\"Location\":\"廊坊市\"},{\"Action\":\"2\",\"AcceptStation\":\"【廊坊市】已到达 北京分拨交付中心\",\"AcceptTime\":\"2025-03-18 22:00:24\",\"Location\":\"廊坊市\"},{\"Action\":\"2\",\"AcceptStation\":\"【廊坊市】已离开 北京分拨交付中心；发往 山东潍坊分拨交付中心\",\"AcceptTime\":\"2025-03-18 22:04:41\",\"Location\":\"廊坊市\"},{\"Action\":\"2\",\"AcceptStation\":\"【潍坊市】已到达 山东潍坊分拨交付中心\",\"AcceptTime\":\"2025-03-19 08:50:56\",\"Location\":\"潍坊市\"},{\"Action\":\"2\",\"AcceptStation\":\"【潍坊市】已离开 山东潍坊分拨交付中心；发往 山东潍坊奎文高新北公司\",\"AcceptTime\":\"2025-03-19 09:17:45\",\"Location\":\"潍坊市\"},{\"Action\":\"2\",\"AcceptStation\":\"【潍坊市】已到达 山东潍坊奎文高新北公司\",\"AcceptTime\":\"2025-03-19 13:12:30\",\"Location\":\"潍坊市\"},{\"Action\":\"2\",\"AcceptStation\":\"【潍坊市】已到达 山东潍坊奎文高新北公司[05365010667]\",\"AcceptTime\":\"2025-03-19 13:12:40\",\"Location\":\"潍坊市\"},{\"Action\":\"202\",\"AcceptStation\":\"【潍坊市】山东潍坊奎文高新北公司[05365010667] 快递员 郑长江 (13430371220) 正在为您派件（有事呼叫我，勿找平台，少一次投诉，多一份感恩）【95126为韵达快递员外呼专属号码，请放心接听】\",\"AcceptTime\":\"2025-03-19 13:46:57\",\"Location\":\"潍坊市\"}]}";
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
            url += "?" + urlencode(queryParams);
        }
        BufferedReader in = null;
        try {
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
                out.writeBytes(urlencode(bodyParams));
                out.flush();
                out.close();
            }
            in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            String result = "";
            while ((line = in.readLine()) != null) {
                result += line;
            }
            return result;
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

    public enum ExpressCompanyCode {
        SF, YTO, ZTO, STO, YD, BST;
    }

    public static class Express {
        @JsonPropertyDescription("快递单号")
        public String expressId;

        @JsonPropertyDescription("快递公司编号，如顺丰SF, 圆通YTO, 中通ZTO, 申通STO, 韵达YD, 百世BST")
        @JsonProperty(required = true)
        public ExpressCompanyCode expressCompanyCode;
    }

    @Data
    public static class ExpressResponse {
        public String expressId;
        public String expressCompanyCode;
        public List<Trace> traces;
        public Boolean success;
        public String failReason;

        public ExpressResponse(String expressId, ExpressCompanyCode expressCompanyCode) {
            this.expressId = expressId;
            this.expressCompanyCode = expressCompanyCode.name();
            log.info("快递单号：{}，快递公司：{}", expressId, expressCompanyCode.name());
            try {
                String string = getExpress(expressId, expressCompanyCode.name());
                if (string == null) {
                    this.failReason = "快递查询失败，请检查快递单号和快递公司是否正确";
                    this.success = false;
                    return;
                }
                JSONObject expressJSON = JSON.parseObject(string);
                if (expressJSON.containsKey("Success") && !expressJSON.getBoolean("Success")) {
                    this.failReason = expressJSON.getString("Reason");
                    this.success = false;
                } else {
                    this.traces = new ArrayList<>();
                    JSONArray traces = expressJSON.getJSONArray("Traces");
                    for (JSONObject trace : traces.toJavaList(JSONObject.class)) {
                        Trace t = new Trace(trace.getString("AcceptTime"), trace.getString("Location"), trace.getString("AcceptStation"));
                        this.traces.add(t);
                    }
                    this.success = true;
                }
            } catch (Exception e) {
                e.printStackTrace();
                this.failReason = "快递查询失败，请检查快递单号和快递公司是否正确";
                this.success = false;
            }
        }

        @Data
        @AllArgsConstructor
        public class Trace {
            public String acceptTime;
            public String location;
            public String acceptStation;
        }
    }

}
