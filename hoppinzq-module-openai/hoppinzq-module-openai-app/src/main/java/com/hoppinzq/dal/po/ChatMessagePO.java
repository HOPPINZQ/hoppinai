package com.hoppinzq.dal.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.hoppinzq.service.bean.RequestContext;
import com.hoppinzq.service.bean.RequestParam;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@TableName("chatmessage")
public class ChatMessagePO {

    @TableId(type = IdType.ASSIGN_UUID, value = "message_id")
    private String message_id;
    @TableField("message_createDate")
    private String message_createDate;
    @TableField("message")
    private String message;
    @TableField("message_role")
    private String message_role;
    @TableField("message_index")
    private String message_index;
    @TableField("reason_message")
    private String reason_message;
    @TableField("chat_id")
    private String chat_id;

    public ChatMessagePO toMap(Map map) {
        if (map.containsKey("messageId")) {
            this.message_id = map.get("messageId").toString();
        }
        if (map.containsKey("date")) {
            this.message_createDate = map.get("date").toString();
        }
        if (map.containsKey("message")) {
            this.message = map.get("message").toString();
        }
        if (map.containsKey("role")) {
            this.message_role = map.get("role").toString();
        }
        if (map.containsKey("chatId")) {
            this.chat_id = map.get("chatId").toString();
        }
        if (map.containsKey("index")) {
            this.message_index = map.get("index").toString();
        }

        try {
            RequestParam requestParam = (RequestParam) RequestContext.getPrincipal();
            String remoteAddr = requestParam.getRequest().getRemoteAddr();
        } catch (Exception ex) {

        }
        return this;
    }

}
