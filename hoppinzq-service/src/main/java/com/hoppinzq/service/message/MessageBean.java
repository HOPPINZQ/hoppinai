package com.hoppinzq.service.message;

import com.hoppinzq.service.handler.MessageEnum;
import com.hoppinzq.service.handler.MessageHandler;

/**
 * @author:ZhangQi 消息实体类
 **/
public class MessageBean {
    private String messageName;
    private Object message;
    private MessageEnum messageType;// 1 记录日志 2 记录服务操作 3 记录网关配置
    private MessageHandler messageHandler;

    public MessageBean() {
    }

    public MessageBean(String messageName, Object message, MessageEnum messageType) {
        this.messageName = messageName;
        this.message = message;
        this.messageType = messageType;
    }

    public MessageBean(String messageName, Object message, MessageEnum messageType, MessageHandler messageHandler) {
        this.messageName = messageName;
        this.message = message;
        this.messageType = messageType;
        this.messageHandler = messageHandler;
    }

    public String getMessageName() {
        return messageName;
    }

    public void setMessageName(String messageName) {
        this.messageName = messageName;
    }

    public Object getMessage() {
        return message;
    }

    public void setMessage(Object message) {
        this.message = message;
    }

    public MessageEnum getMessageType() {
        return messageType;
    }

    public void setMessageType(MessageEnum messageType) {
        this.messageType = messageType;
    }

    public MessageHandler getMessageHandler() {
        return messageHandler;
    }

    public void setMessageHandler(MessageHandler messageHandler) {
        this.messageHandler = messageHandler;
    }
}

