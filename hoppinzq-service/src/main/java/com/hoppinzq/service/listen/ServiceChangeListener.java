package com.hoppinzq.service.listen;

import com.hoppinzq.service.bean.ServiceWrapperRPC;
import com.hoppinzq.service.cache.RPCServiceStore;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author:ZhangQi 管理服务变化线程
 **/
public class ServiceChangeListener implements Runnable {
    private List<ServiceWrapperRPC> list;
    private EventServiceListener eventListener;

    /**
     * @param eventListener 要监听的List和事件监听器
     */
    public ServiceChangeListener(EventServiceListener eventListener) {
        this.list = RPCServiceStore.serviceWrapperRPCList;
        this.eventListener = eventListener;
    }

    @Override
    public void run() {
        // 创建一个临时的List，用于比较和检测变化
        List<ServiceWrapperRPC> tempList = new CopyOnWriteArrayList<>(list);
        while (true) {
            if (tempList.size() != list.size()) {
                tempList.clear();
                //将原List的元素添加到临时List中
                tempList.addAll(list);
                // 调用事件监听器的方法，触发事件
                eventListener.onServiceChange(list);
            }
        }
    }
}