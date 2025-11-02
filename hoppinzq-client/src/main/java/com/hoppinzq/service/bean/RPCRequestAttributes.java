package com.hoppinzq.service.bean;

import com.hoppinzq.service.common.HoppinInvocationRequest;
import com.hoppinzq.service.common.HoppinInvocationResponse;
import com.hoppinzq.service.handler.MethodInvocationHandler;
import com.hoppinzq.service.transport.HoppinRPCSession;
import lombok.Data;

import java.io.Serializable;

/**
 * @author zhangqi
 * todo
 */
@Data
public class RPCRequestAttributes {
    public static Class<? extends MethodInvocationHandler> methodInvocationHandler;
    public static HoppinInvocationRequest request;
    public static HoppinRPCSession session;
    public static HoppinInvocationResponse response;
    public static String interfaceServiceName;
    public static String serviceURI;
    public static String serviceName;
    public static Serializable credentials;
}

