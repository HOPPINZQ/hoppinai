package com.hoppinzq.service.cache;

import com.hoppinzq.service.bean.ServiceWrapperRPC;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author:ZhangQi 注册中心服务存放
 **/
public class OuterRPCServiceStore {
    //注册中心服务备份
    public static List<ServiceWrapperRPC> coreServiceOuterServiceList = Collections.synchronizedList(new ArrayList<ServiceWrapperRPC>());
}
