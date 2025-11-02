package com.hoppinzq.function.zq.express;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.hoppinzq.function.zq.constants.AiFunctionCallResponse;
import com.hoppinzq.service.util.StringUtil;
import lombok.AllArgsConstructor;
import lombok.Data;

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
import java.util.*;

@Data
@AllArgsConstructor
public class ExpressFunctionCall extends AiFunctionCallResponse {
    private List<Trace> traces;

    public ExpressFunctionCall(String expressId, ExpressCompanyCode expressCompanyCode) {
        System.out.println("快递查询请求已发送，快递单号：" + expressId + "，快递公司：" + expressCompanyCode);
        try {
            JSONObject jsonObject = getExpress(expressId, expressCompanyCode.name());
            if (jsonObject == null) {
                fail("快递查询失败，请检查快递单号和快递公司是否正确");
                return;
            }
            if (StringUtil.isNotEmpty(jsonObject.getString("Success")) && !jsonObject.getBoolean("Success")) {
                fail(jsonObject.getString("Reason"));
            } else {
                this.traces = new ArrayList<>();
                JSONArray traces = jsonObject.getJSONArray("Traces");
                for (JSONObject trace : traces.toJavaList(JSONObject.class)) {
                    Trace t = new Trace(trace.getString("AcceptTime"), trace.getString("Location"), trace.getString("AcceptStation"));
                    this.traces.add(t);
                }
                success("快递查询成功");
            }
        } catch (Exception e) {
            e.printStackTrace();
            fail("快递查询失败，请检查快递单号和快递公司是否正确");
        }
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
    private static JSONObject getExpress(String expressId, String code) {
        BufferedReader in = null;
        try {
            if ("315029360436187".equals(expressId)) {
                String jsonString = "{\"Location\":\"潍坊市\",\"LogisticCode\":\"315029360436187\",\"ShipperCode\":\"YD\",\"State\":\"2\",\"StateEx\":\"202\",\"Success\":true,\"Traces\":[{\"Action\":\"1\",\"AcceptStation\":\"【廊坊市】华北金韵腾飞公司捷通分部-刘德江（15231609829） 已揽收\",\"AcceptTime\":\"2025-03-18 10:32:59\",\"Location\":\"廊坊市\"},{\"Action\":\"2\",\"AcceptStation\":\"【廊坊市】已离开 华北金韵腾飞公司；发往 北京分拨交付中心\",\"AcceptTime\":\"2025-03-18 20:33:31\",\"Location\":\"廊坊市\"},{\"Action\":\"2\",\"AcceptStation\":\"【廊坊市】已到达 北京分拨交付中心\",\"AcceptTime\":\"2025-03-18 22:00:24\",\"Location\":\"廊坊市\"},{\"Action\":\"2\",\"AcceptStation\":\"【廊坊市】已离开 北京分拨交付中心；发往 山东潍坊分拨交付中心\",\"AcceptTime\":\"2025-03-18 22:04:41\",\"Location\":\"廊坊市\"},{\"Action\":\"2\",\"AcceptStation\":\"【潍坊市】已到达 山东潍坊分拨交付中心\",\"AcceptTime\":\"2025-03-19 08:50:56\",\"Location\":\"潍坊市\"},{\"Action\":\"2\",\"AcceptStation\":\"【潍坊市】已离开 山东潍坊分拨交付中心；发往 山东潍坊奎文高新北公司\",\"AcceptTime\":\"2025-03-19 09:17:45\",\"Location\":\"潍坊市\"},{\"Action\":\"2\",\"AcceptStation\":\"【潍坊市】已到达 山东潍坊奎文高新北公司\",\"AcceptTime\":\"2025-03-19 13:12:30\",\"Location\":\"潍坊市\"},{\"Action\":\"2\",\"AcceptStation\":\"【潍坊市】已到达 山东潍坊奎文高新北公司[05365010667]\",\"AcceptTime\":\"2025-03-19 13:12:40\",\"Location\":\"潍坊市\"},{\"Action\":\"202\",\"AcceptStation\":\"【潍坊市】山东潍坊奎文高新北公司[05365010667] 快递员 郑长江 (13430371220) 正在为您派件（有事呼叫我，勿找平台，少一次投诉，多一份感恩）【95126为韵达快递员外呼专属号码，请放心接听】\",\"AcceptTime\":\"2025-03-19 13:46:57\",\"Location\":\"潍坊市\"}]}";
                JSONObject response = JSON.parseObject(jsonString);
                response.put("now", new Date().toString());
                return response;
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
            return JSON.parseObject(result);
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


    private enum ExpressCompanyCode {
        SF, YTO, ZTO, STO, YD, BST;
    }

    public static class ExpressFunctionCallRequest {
        @JsonPropertyDescription("快递单号")
        public String expressId;

        @JsonPropertyDescription("快递公司编号，如顺丰SF, 圆通YTO, 中通ZTO, 申通STO, 韵达YD, 百世BST")
        @JsonProperty(required = true)
        public ExpressCompanyCode expressCompanyCode;
    }

    @Data
    @AllArgsConstructor
    public class Trace {
        public String acceptTime;
        public String location;
        public String acceptStation;
    }
}
