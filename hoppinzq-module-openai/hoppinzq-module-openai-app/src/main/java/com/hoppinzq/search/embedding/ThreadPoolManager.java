package com.hoppinzq.search.embedding;

import java.util.concurrent.*;

public class ThreadPoolManager {
    private static final int CORE_POOL_SIZE = 10; // 核心线程数
    private static final int MAXIMUM_POOL_SIZE = 10; // 最大线程数
    private static final long KEEP_ALIVE_TIME = 0L; // 空闲线程的存活时间
    private static ThreadPoolExecutor threadPoolExecutor = null; // 线程池实例
    private static volatile ThreadPoolExecutor threadPool;

    // 禁止直接创建对象
    private ThreadPoolManager() {
    }

    // 获取全局线程池对象
    public static synchronized ThreadPoolExecutor getThreadPool() {
        if (threadPool == null) {
            threadPool = createThreadPool();
        }
        return threadPool;
    }

    // 创建线程池方法
    public static ThreadPoolExecutor createThreadPool() {
        if (threadPoolExecutor == null) {
            synchronized (ThreadPoolManager.class) {
                if (threadPoolExecutor == null) {
                    // 队列容量
                    BlockingQueue<Runnable> queue = new ArrayBlockingQueue<>(100);
                    // 线程池
                    threadPoolExecutor = new ThreadPoolExecutor(CORE_POOL_SIZE,
                            MAXIMUM_POOL_SIZE,
                            KEEP_ALIVE_TIME,
                            TimeUnit.SECONDS,
                            queue);
                }
            }
        }
        return threadPoolExecutor;
    }

    // 提交任务
    public static void submitTask(Runnable task) {
        if (threadPoolExecutor == null) {
            createThreadPool();
        }
        threadPoolExecutor.submit(task);
    }

    public static Future submitTask(Callable task) {
        if (threadPoolExecutor == null) {
            createThreadPool();
        }
        return threadPoolExecutor.submit(task);
    }

    // 销毁线程池
    public static void destroyThreadPool() {
        if (threadPoolExecutor != null) {
            threadPoolExecutor.shutdown();
            threadPoolExecutor = null;
        }
    }
}