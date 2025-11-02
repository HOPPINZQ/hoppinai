package com.hoppinzq.service.message;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author:ZhangQi 本地服务的消息缓冲
 **/
public class MessageBuffer {
    //缓冲，先放1024个消息
    public static MessageBean[] items = new MessageBean[1024];
    public static int putptr/*写索引*/,
            takeptr/*读索引*/,
            count/*队列中存在的数据个数*/;
    private static Lock lock = new ReentrantLock();
    private static Condition notFull = lock.newCondition();
    private static Condition notEmpty = lock.newCondition();

    /**
     * 向队列内写入消息
     *
     * @param x
     * @throws InterruptedException
     */
    public static void put(MessageBean x) throws InterruptedException {
        lock.lock();
        try {
            while (count == items.length) {
                notFull.await();
            }
            items[putptr] = x;
            if (++putptr == items.length) {
                putptr = 0;
            }
            ++count;
            notEmpty.signal();
        } finally {
            lock.unlock();
        }
    }

    /**
     * 从队列获取消息
     *
     * @return
     * @throws InterruptedException
     */
    public static MessageBean take() throws InterruptedException {
        lock.lock();
        try {
            while (count == 0) {
                notEmpty.await();
            }
            MessageBean x = items[takeptr];
            if (++takeptr == items.length) {
                takeptr = 0;
            }
            --count;
            notFull.signal();
            return x;
        } finally {
            lock.unlock();
        }
    }
}

