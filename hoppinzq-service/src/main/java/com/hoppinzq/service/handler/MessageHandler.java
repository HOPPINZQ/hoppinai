package com.hoppinzq.service.handler;

import com.hoppinzq.service.message.MessageBean;

/**
 * @author:ZhangQi 消息处理类
 **/
public interface MessageHandler {
    void handle(MessageBean message);
}
