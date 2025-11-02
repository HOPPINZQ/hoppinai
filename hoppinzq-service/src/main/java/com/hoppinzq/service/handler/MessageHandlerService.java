package com.hoppinzq.service.handler;

import com.hoppinzq.service.message.MessageBean;


public interface MessageHandlerService extends MessageHandler {

    void handlerEnum(MessageEnum messageEnum);

    void clear(MessageBean messageBean);
}

