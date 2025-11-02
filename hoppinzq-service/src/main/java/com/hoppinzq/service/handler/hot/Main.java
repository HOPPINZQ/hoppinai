package com.hoppinzq.service.handler.hot;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        ConfigurationManager configManager = new ConfigurationManager("D:\\settings\\gateway.properties");
        while (true) {
            Thread.sleep(1000);
            String configValue = configManager.getProperty("log4j.rootLogger");
            System.out.println("Config value: " + configValue);
        }
    }
}