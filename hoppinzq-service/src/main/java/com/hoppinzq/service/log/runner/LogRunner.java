package com.hoppinzq.service.log.runner;

import com.hoppinzq.service.constant.Constant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.File;

/**
 * @author:ZhangQi todo 日志行为控制台可控
 **/
@Component
public class LogRunner implements CommandLineRunner {

    private static Logger logger = LoggerFactory.getLogger(LogRunner.class);

    @Override
    public void run(String... args) {
        try {
            String os = System.getProperty("os.name").toLowerCase();
            if (os.contains("windows")) {
                Constant.LOG_PATH = "D:" + File.separator + "log" + File.separator;
            } else {
                Constant.LOG_PATH = File.separator + "home" +
                        File.separator + "hoppinzq" + File.separator + "log" +
                        File.separator;
            }
            logger.debug("日志文件将上传到：" + Constant.LOG_PATH);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        // --force 强制覆盖
        //项目启动，日志刷入缓存，todo 应该从数据库来

    }
}
