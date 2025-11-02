package com.hoppinzq.service.handler;

import com.hoppinzq.service.message.MessageBean;

/**
 * @author:ZhangQi 服务销毁时处理
 **/
public class ServiceDestroyHandler implements MessageHandler {
    private String serviceID;

    public ServiceDestroyHandler(String serviceID) {
        this.serviceID = serviceID;
    }

    @Override
    public void handle(MessageBean message) {
        System.err.println("服务：" + serviceID + "," + message.getMessageName());
    }
}