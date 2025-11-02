package com.hoppinzq.service.handler;

import com.hoppinzq.service.log.bean.ServiceLogBean;
import com.hoppinzq.service.message.MessageBean;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class SaveGatewaySettingHandler implements MessageHandler {

    @Override
    public void handle(MessageBean message) {
        try {
            String gatewaySettings = null;
            FileWriter fileWriterLog = new FileWriter(gatewaySettings, true);
            PrintWriter writer = new PrintWriter(fileWriterLog);
            ServiceLogBean serviceLogBean = (ServiceLogBean) message.getMessage();
            writer.println(serviceLogBean.toString());
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}


