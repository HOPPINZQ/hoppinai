package com.hoppinzq.service.interceptor.service;

import com.hoppinzq.service.annotation.ServiceOrder;
import com.hoppinzq.service.bean.RPCBean;
import com.hoppinzq.service.bean.ServiceWrapper;
import com.hoppinzq.service.common.HoppinInvocationRequest;
import com.hoppinzq.service.common.HoppinInvocationResponse;
import com.hoppinzq.service.interceptor.aop.RPCInterceptor;
import com.hoppinzq.service.log.bean.ServiceLogBean;

/**
 * @author:ZhangQi 默认拦截器，不做任何处理
 **/
@ServiceOrder(1)
@RPCInterceptor(10)
public class DefaultRPCInterceptorInterface implements RPCInterceptorInterface {

    @Override
    public void beforeAuthentication(HoppinInvocationRequest request, ServiceWrapper serviceWrapper) {

    }

    @Override
    public void beforeMethod(HoppinInvocationRequest request, ServiceWrapper serviceWrapper, RPCBean rpcBean) {

    }

    @Override
    public void after(HoppinInvocationResponse response, ServiceWrapper serviceWrapper, RPCBean rpcBean) {

    }

    @Override
    public void exception(HoppinInvocationResponse response, ServiceWrapper serviceWrapper, RPCBean rpcBean, Exception exception) {

    }

    @Override
    public void end(HoppinInvocationRequest request, HoppinInvocationResponse response, ServiceWrapper serviceWrapper, RPCBean rpcBean, ServiceLogBean logBean) {

    }

}

