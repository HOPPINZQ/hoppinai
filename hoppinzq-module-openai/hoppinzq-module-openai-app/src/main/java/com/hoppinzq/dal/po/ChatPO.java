package com.hoppinzq.dal.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@TableName("chat")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ChatPO {

    @TableId(type = IdType.INPUT, value = "chat_id")
    private String chat_id;
    @TableField("chat_user_id")
    private String chat_user_id;
    @TableField("chat_createDate")
    private String chat_createDate;
    @TableField("chat_title")
    private String chat_title;
    @TableField("chat_answer")
    private String chat_answer;
    @TableField("chat_state")
    private String chat_state;
    @TableField("chat_modal")
    private String chat_modal;
    @TableField("chat_context")
    private int chat_context;
    @TableField("chat_system")
    private String chat_system;
    @TableField("chat_image")
    private String chat_image;

    @TableField(exist = false)
    private List<ChatMessagePO> chatMessageList;

}
