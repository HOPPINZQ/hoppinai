package com.hoppinzq.function.zq.mobile;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.hoppinzq.function.zq.constants.AiFunctionCallResponse;
import com.hoppinzq.service.util.http.HoppinzqHttpUtils;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.HashMap;

import static com.hoppinzq.function.zq.constants.FunctionCallCommon.HAMIBOT_MOBILE_TITLE;
import static com.hoppinzq.function.zq.constants.FunctionCallCommon.HAMIBOT_TOKEN;

@Data
@EqualsAndHashCode(callSuper = true)
public class MobileFunctionCall extends AiFunctionCallResponse {
    private String text;

    public MobileFunctionCall(String text) {
        this.text = text;
        sendMobileMessage();
    }

    public MobileFunctionCall sendMobileMessage() {
        try {
            String deviceResponse = HoppinzqHttpUtils.getString("https://api.hamibot.com/v1/devices", new HashMap<String, String>(1) {{
                put("Authorization", HAMIBOT_TOKEN);
            }});
            if ("Unauthorized".equals(deviceResponse)) {
                fail("Token无效，请检查配置");
            } else {
                JSONObject deviceJson = JSON.parseObject(deviceResponse);
                JSONArray devices = deviceJson.getJSONArray("items");
                if (devices.isEmpty()) {
                    fail("暂无设备");
                } else {
                    for (int i = 0; i < devices.size(); i++) {
                        JSONObject device = devices.getJSONObject(i);
                        if (!device.getBoolean("online")) {
                            fail("设备不在线");
                        } else {
                            String deviceId = device.getString("_id");
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("title", HAMIBOT_MOBILE_TITLE);
                            jsonObject.put("text", this.text);
                            String string = HoppinzqHttpUtils.postString("https://api.hamibot.com/v1/devices/" + deviceId + "/messages",
                                    jsonObject.toJSONString(),
                                    new HashMap<String, String>(1) {{
                                        put("Authorization", HAMIBOT_TOKEN);
                                    }});
                            if (!"".equals(string.trim())) {
                                JSONObject result = JSON.parseObject(string);
                                fail(result.getString("message"));
                            } else {
                                success("向手机发送消息成功！发送内容是：" + this.text);
                            }
                        }
                    }
                }
                return this;
            }
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
            return this;
        }
        return this;
    }

    public static class MobileFunctionCallRequest {
        @JsonPropertyDescription("消息内容")
        @JsonProperty(required = true)
        public String message;
    }
}


