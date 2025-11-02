package com.hoppinzq.service.interceptor.service;


import com.hoppinzq.service.bean.RPCBean;
import com.hoppinzq.service.bean.ServiceWrapper;
import com.hoppinzq.service.common.HoppinInvocationRequest;
import com.hoppinzq.service.common.HoppinInvocationResponse;
import com.hoppinzq.service.log.bean.ServiceLogBean;

/**
 * @author:ZhangQi 拦截器接口
 */
public interface RPCInterceptorInterface {
    /**
     * 在验证用户前执行自定义操作
     *
     * @param request        请求体
     * @param serviceWrapper 服务包装类
     */
    void beforeAuthentication(HoppinInvocationRequest request, ServiceWrapper serviceWrapper);

    /**
     * 在执行rpc调用的方法实现类前执行自定义操作
     *
     * @param request        请求体
     * @param serviceWrapper 服务包装类
     * @param rpcBean        rpc调用细节
     */
    void beforeMethod(HoppinInvocationRequest request, ServiceWrapper serviceWrapper, RPCBean rpcBean);

    /**
     * 在执行rpc调用的方法实现类后执行自定义操作
     *
     * @param response       响应体
     * @param serviceWrapper 服务包装类
     * @param rpcBean        rpc调用细节
     */
    void after(HoppinInvocationResponse response, ServiceWrapper serviceWrapper, RPCBean rpcBean);

    /**
     * 在执行rpc报错后（在处理完异常后）执行自定义操作
     *
     * @param response       响应体
     * @param serviceWrapper 服务包装类
     * @param rpcBean        rpc调用细节
     */
    void exception(HoppinInvocationResponse response, ServiceWrapper serviceWrapper, RPCBean rpcBean, Exception exception);

    /**
     * 在整个调用过程完毕后执行自定义操作
     *
     * @param request        请求体
     * @param response       响应体
     * @param serviceWrapper 服务包装类
     * @param rpcBean        rpc调用细节
     * @param logBean        服务调用日志
     */
    void end(HoppinInvocationRequest request, HoppinInvocationResponse response, ServiceWrapper serviceWrapper, RPCBean rpcBean, ServiceLogBean logBean);
}

