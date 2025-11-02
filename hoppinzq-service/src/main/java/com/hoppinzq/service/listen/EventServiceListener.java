package com.hoppinzq.service.listen;

import com.hoppinzq.service.bean.ServiceWrapperRPC;

import java.util.List;

/**
 * @author:ZhangQi 服务监听接口
 **/
public interface EventServiceListener {

    /**
     * 服务列表内容改变时执行方法
     *
     * @param list
     */
    void onServiceChange(List<ServiceWrapperRPC> list);
}