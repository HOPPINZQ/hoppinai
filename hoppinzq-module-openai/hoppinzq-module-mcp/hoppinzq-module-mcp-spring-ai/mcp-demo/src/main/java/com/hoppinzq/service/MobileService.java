package com.hoppinzq.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hoppinzq.service.mail.SimpleMailSender;
import com.hoppinzq.util.HoppinzqHttpUtils;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;

import java.util.HashMap;


/**
 * 需要使用HAMIBOT才能使用手机，有疑问或者不会使用，请联系我
 * 邮件需要你的邮箱开启SMTP服务
 */
@Service
public class MobileService {
    private static final String HAMIBOT_TOKEN = "hmp_16fb073978fbf6f976851e3ce9735b144cf3ce1d51229f971668c4c84919f74f";
    private static final String HAMIBOT_URL = "https://api.hamibot.com/v1/devices";
    private static final String HAMIBOT_MOBILE_TITLE = "张祺的小AI";

    @Tool(name = "send_message_to_mobile", description = "给手机发送通知")
    public String sendMessageToMobile(@ToolParam(required = true, description = "发送内容") String text) {
        try {
            String deviceResponse = HoppinzqHttpUtils.getString(HAMIBOT_URL, new HashMap<String, String>(1) {{
                put("Authorization", HAMIBOT_TOKEN);
            }});
            if ("Unauthorized".equals(deviceResponse)) {
                return "Token无效，请检查配置";
            } else {
                JSONObject deviceJson = JSON.parseObject(deviceResponse);
                JSONArray devices = deviceJson.getJSONArray("items");
                if (devices.isEmpty()) {
                    return "未找到设备";
                } else {
                    Boolean isOnline = false;
                    for (int i = 0; i < devices.size(); i++) {
                        JSONObject device = devices.getJSONObject(i);
                        if (!device.getBoolean("online")) {
                            continue;
                        } else {
                            isOnline = true;
                            String deviceId = device.getString("_id");
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("title", HAMIBOT_MOBILE_TITLE);
                            jsonObject.put("text", text);
                            String string = HoppinzqHttpUtils.postString(HAMIBOT_URL + "/" + deviceId + "/messages",
                                    jsonObject.toJSONString(),
                                    new HashMap<String, String>(1) {{
                                        put("Authorization", HAMIBOT_TOKEN);
                                    }});
                            if (!"".equals(string.trim())) {
                                JSONObject result = JSON.parseObject(string);
                                return "向手机发送消息失败！原因是：" + result.getString("message");
                            } else {
                                return "向手机发送消息成功！发送内容是：" + text;
                            }
                        }
                    }
                    if (!isOnline) {
                        return "设备不在线";
                    }
                }
            }
            return "未找到设备";
        } catch (Exception e) {
            e.printStackTrace();
            return "向手机发送消息失败！原因是：" + e.getMessage();
        }
    }

    @Tool(name = "send_message_to_email", description = "给邮箱发送通知")
    public String sendMessageToEmail(@ToolParam(required = true, description = "邮箱地址") String email,
                                     @ToolParam(required = true, description = "发送内容") String text) {
        return SimpleMailSender.sendEmail(email, text);
    }
}

