package com.hoppinzq.service.bean;

import com.hoppinzq.service.common.HoppinInvocationRequest;
import com.hoppinzq.service.common.HoppinInvocationResponse;
import com.hoppinzq.service.serializer.CustomSerializer;
import com.hoppinzq.service.transport.HoppinRPCProvider;
import com.hoppinzq.service.transport.HoppinRPCSession;
import lombok.Data;

import java.io.Serializable;
import java.util.Map;

/**
 * @author zhangqi
 * 一次rpc调用的细节的封装
 */
@Data
public class RPCBean implements Serializable {
    private static final long serialVersionUID = 1L;

    private HoppinInvocationRequest hoppinInvocationRequest;
    private HoppinInvocationResponse hoppinInvocationResponse;
    private Map headers;
    private HoppinRPCProvider provider;
    private HoppinRPCSession session;
    private Class<? extends CustomSerializer> serializable;
    private Object proxy;
    private String method;
    private Object[] args;
    private String exception;
}
