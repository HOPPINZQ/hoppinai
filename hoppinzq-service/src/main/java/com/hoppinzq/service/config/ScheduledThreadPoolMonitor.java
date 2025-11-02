package com.hoppinzq.service.config;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ScheduledThreadPoolMonitor {
    public static void main(String[] args) {
        ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(5);
        // 执行定时任务
        executor.scheduleAtFixedRate(() -> {
            // 任务逻辑
            System.out.println("Executing task...");
        }, 0, 1, TimeUnit.SECONDS);
        // 监控线程池的运行情况

        Runnable monitor = () -> {
            int activeCount = executor.getActiveCount();
            int poolSize = executor.getPoolSize();
            int corePoolSize = executor.getCorePoolSize();
            int largestPoolSize = executor.getLargestPoolSize();
            long taskCount = executor.getTaskCount();
            long completedTaskCount = executor.getCompletedTaskCount();
            long scheduledTaskCount = executor.getTaskCount() - executor.getCompletedTaskCount();
            System.out.println("Active Threads: " + activeCount);
            System.out.println("Pool Size: " + poolSize);
            System.out.println("Core Pool Size: " + corePoolSize);
            System.out.println("Largest Pool Size: " + largestPoolSize);
            System.out.println("Task Count: " + taskCount);
            System.out.println("Completed Task Count: " + completedTaskCount);
            System.out.println("Scheduled Task Count: " + scheduledTaskCount);
        };
        executor.scheduleAtFixedRate(monitor, 0, 1, TimeUnit.SECONDS);
    }
}