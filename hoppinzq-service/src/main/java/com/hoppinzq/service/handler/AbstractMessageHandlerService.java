package com.hoppinzq.service.handler;


public abstract class AbstractMessageHandlerService implements MessageHandlerService {
    @Override
    public void handlerEnum(MessageEnum messageEnum) {
        messageEnum = MessageEnum.DEFAULT;
    }

}

