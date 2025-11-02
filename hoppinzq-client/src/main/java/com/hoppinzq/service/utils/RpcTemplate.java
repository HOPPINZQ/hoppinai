package com.hoppinzq.service.utils;

import com.hoppinzq.service.bean.RPCBean;
import com.hoppinzq.service.bean.RPCContext;
import com.hoppinzq.service.common.HoppinInvocationRequest;
import com.hoppinzq.service.common.HoppinInvocationResponse;

import java.util.Map;

/**
 * @author:ZhangQi 提供rpc主体操作
 */
public class RpcTemplate {

    private static HoppinInvocationResponse hoppinInvocationResponse;

    /**
     * 设置rpc的请求头
     *
     * @param key
     * @param params
     */
    public static void setRequestHeader(String key, String params) {
        HoppinInvocationRequest request = getRequest();
        request.getHeaders().put(key, params);
    }

    /**
     * 设置一批请求头
     *
     * @param headers
     */
    public static void setRequestHeader(Map<String, Object> headers) {
        HoppinInvocationRequest request = getRequest();
        for (String key : headers.keySet()) {
            request.getHeaders().put(key, headers.get(key));
        }

    }

    /**
     * 获取请求头
     *
     * @return
     */
    public static Map<String, Object> getRequestRequest() {
        HoppinInvocationRequest request = getRequest();
        if (request == null) {
            return null;
        }
        return request.getHeaders();
    }

    /**
     * 获取rpc请求体
     *
     * @return
     */
    public static HoppinInvocationRequest getRequest() {
        RPCBean rpcBean = getRpcBean();
        if (rpcBean.getHoppinInvocationRequest() == null) {
            setRequest();
        }
        return rpcBean.getHoppinInvocationRequest();
    }

    /**
     * 设置请求头
     *
     * @param request
     */
    public static void setRequest(HoppinInvocationRequest request) {
        RPCBean rpcBean = getRpcBean();
        if (request == null) {
            rpcBean.setHoppinInvocationRequest(new HoppinInvocationRequest());
        } else {
            rpcBean.setHoppinInvocationRequest(request);
        }
    }

    /**
     * 设置空请求头
     */
    public static void setRequest() {
        setRequest(null);
    }

    /**
     * 获取rpc的响应体
     *
     * @return
     */
    public static HoppinInvocationResponse getResponse() {
        return hoppinInvocationResponse;
    }

    /**
     * 设置响应头
     *
     * @param response
     */
    public static void setResponse(HoppinInvocationResponse response) {
        RPCBean rpcBean = getRpcBean();
        if (response == null) {
            rpcBean.setHoppinInvocationResponse(new HoppinInvocationResponse());
        } else {
            rpcBean.setHoppinInvocationResponse(response);
        }
    }

    public static HoppinInvocationResponse getLocalResponse() {
        RPCBean rpcBean = getRpcBean();
        return rpcBean.getHoppinInvocationResponse();
    }

    /**
     * 获取rpc的响应头
     *
     * @return
     */
    public static Map<String, Object> getResponseHeader() {
        RPCBean rpcBean = getRpcBean();
        if (rpcBean == null) {
            return null;
        }
        HoppinInvocationResponse response = rpcBean.getHoppinInvocationResponse();
        return response.getHeaders();
    }

    /**
     * 设置空响应头
     */
    public static void setResponse() {
        setResponse(null);
    }

    /**
     * 设置rpc主体
     */
    public static void setRpcBean() {
        setRpcBean(null);
    }

    /**
     * 获取当前线程的rpc主体
     *
     * @return
     */
    public static RPCBean getRpcBean() {
        if (RPCContext.getPrincipal() == null) {
            RPCContext.enter(new RPCBean());
        }
        return (RPCBean) RPCContext.getPrincipal();
    }

    /**
     * 设置roc主体
     *
     * @param rpcBean
     */
    public static void setRpcBean(RPCBean rpcBean) {
        if (rpcBean == null) {
            RPCContext.enter(new RPCBean());
        } else {
            RPCContext.enter(rpcBean);
        }
    }

    /**
     * 清理
     */
    public static void remove() {
        hoppinInvocationResponse = getLocalResponse();
        RPCContext.exit();
    }

}

