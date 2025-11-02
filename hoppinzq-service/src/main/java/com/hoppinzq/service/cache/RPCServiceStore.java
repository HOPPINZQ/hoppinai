package com.hoppinzq.service.cache;

import com.hoppinzq.service.bean.ServiceWrapperRPC;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author:ZhangQi 注册中心服务存放
 **/
public class RPCServiceStore {
    //注册在注册中心的服务
    public static List<ServiceWrapperRPC> serviceWrapperRPCList = Collections.synchronizedList(new ArrayList<ServiceWrapperRPC>());
}
