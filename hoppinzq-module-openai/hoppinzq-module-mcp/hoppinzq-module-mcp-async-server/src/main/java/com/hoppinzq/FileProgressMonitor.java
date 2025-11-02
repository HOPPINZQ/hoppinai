package com.hoppinzq;

import com.jcraft.jsch.SftpProgressMonitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DecimalFormat;
import java.util.Timer;
import java.util.TimerTask;

public class FileProgressMonitor extends TimerTask implements SftpProgressMonitor {

    private static final Logger logger = LoggerFactory.getLogger(MyAsyncServer.class);

    private long progressInterval = 500; // 默认间隔时间为0.5秒

    private boolean isEnd = false; // 记录传输是否结束

    private long transfered; // 记录已传输的数据总大小

    private long fileSize; // 记录文件总大小

    private Timer timer; // 定时器对象

    private boolean isScheduled = false; // 记录是否已启动timer记时器

    public FileProgressMonitor(long fileSize) {
        this.fileSize = fileSize;
    }

    @Override
    public void run() {
        if (!isEnd()) { // 判断传输是否已结束
            logger.info("正在传输中");
            long transfered = getTransfered();
            if (transfered != fileSize) { // 判断当前已传输数据大小是否等于文件总大小
                logger.info("当前传输了" + transfered + " bytes");
                sendProgressMessage(transfered);
            } else {
                logger.info("文件传输完毕!");
                setEnd(true); // 如果当前已传输数据大小等于文件总大小，说明已完成，设置end
            }
        } else {
            logger.info("文件传输完毕!正在停止文件记时器!");
            stop(); // 如果传输结束，停止timer记时器
            return;
        }
    }

    public void stop() {
        logger.info("尝试停止文件计时器!");
        if (timer != null) {
            timer.cancel();
            timer.purge();
            timer = null;
            isScheduled = false;
        }
        logger.info("文件计时器停止成功!");
    }

    public void start() {
        logger.info("尝试开启文件计时器!");
        if (timer == null) {
            timer = new Timer();
        }
        timer.schedule(this, 1000, progressInterval);
        isScheduled = true;
        logger.info("开启文件计时器成功!");
    }

    /**
     * 打印progress信息
     *
     * @param transfered
     */
    private void sendProgressMessage(long transfered) {
        if (fileSize != 0) {
            double d = ((double) transfered * 100) / (double) fileSize;
            DecimalFormat df = new DecimalFormat("#.##");
            logger.info("传输进度: " + df.format(d) + "%");
        } else {
            logger.info("传输进度: " + transfered);
        }
    }

    /**
     * 实现了SftpProgressMonitor接口的count方法
     */
    public boolean count(long count) {
        if (isEnd()) return false;
        if (!isScheduled) {
            start();
        }
        add(count);
        return true;
    }

    /**
     * 实现了SftpProgressMonitor接口的end方法
     */
    public void end() {
        setEnd(true);
        logger.info("传输完毕!");
    }

    private synchronized void add(long count) {
        transfered = transfered + count;
    }

    private synchronized long getTransfered() {
        return transfered;
    }

    public synchronized void setTransfered(long transfered) {
        this.transfered = transfered;
    }

    private synchronized boolean isEnd() {
        return isEnd;
    }

    private synchronized void setEnd(boolean isEnd) {
        this.isEnd = isEnd;
    }

    public void init(int op, String src, String dest, long max) {
        //
    }
}
