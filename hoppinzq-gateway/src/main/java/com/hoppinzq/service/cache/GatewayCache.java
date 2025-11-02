package com.hoppinzq.service.cache;


import com.hoppinzq.service.bean.ServiceApiBean;
import com.hoppinzq.service.bean.ServletHandlerMapping;
import com.hoppinzq.service.core.ApiRunnable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * @author:ZhangQi 注册在网关的服务接口
 **/
public class GatewayCache {

    //供内部调用api缓存,在项目启动前会将api映射存入这里
    //用HashMap也是可以的，因为该Map只供查看，不允许任何操作修改内部细节
    public static Map<String, ApiRunnable> apiMap = new ConcurrentHashMap<String, ApiRunnable>();

    //供外部查看api缓存，在项目启动前会将暴露的服务跟api存入这里
    //同上
    public static List<ServiceApiBean> outApiList = Collections.synchronizedList(new ArrayList<ServiceApiBean>());

    //网关处理映射
    public static List<ServletHandlerMapping> servletHandlerMapList = new ArrayList<>();


}
