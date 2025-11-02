package com.hoppinzq.service.handler;

import com.hoppinzq.service.log.bean.ServiceLogBean;
import com.hoppinzq.service.message.MessageBean;
import com.hoppinzq.service.util.DateFormatUtil;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author:ZhangQi 日志处理
 **/
public class FileLogHandler implements MessageHandler {

    private String logFile;

    public FileLogHandler(String logFile) {
        this.logFile = logFile;
    }

    /**
     * 重写该方法已实现自己的日志入库
     *
     * @param message
     */
    @Override
    public void handle(MessageBean message) {
        try {
            String nowDay = DateFormatUtil.stampToDateDayNow();
            //if(FileUtil.)
            // 将日志消息写入日志文件
            FileWriter fileWriterLog = new FileWriter(logFile, true);
            FileWriter fileWriterLogDay = new FileWriter(logFile.replaceAll(".txt", "-" + nowDay + ".txt"), true);
            PrintWriter writer = new PrintWriter(fileWriterLog);
            PrintWriter writerDay = new PrintWriter(fileWriterLogDay);
            ServiceLogBean serviceLogBean = (ServiceLogBean) message.getMessage();
            writer.println(serviceLogBean.toString());
            writerDay.println(serviceLogBean.toString());
            writer.flush();
            writerDay.flush();
            writer.close();
            writerDay.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}