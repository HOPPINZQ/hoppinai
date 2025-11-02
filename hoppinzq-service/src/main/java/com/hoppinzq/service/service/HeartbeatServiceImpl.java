package com.hoppinzq.service.service;

import com.hoppinzq.service.annotation.ServiceRegister;
import com.hoppinzq.service.auth.AuthenticationNotCheckAuthenticator;
import com.hoppinzq.service.auth.AuthenticationNotCheckAuthorizer;
import com.hoppinzq.service.serializer.HessionSerializer;

import java.io.Serializable;

/**
 * @author:ZhangQi 心跳服务，心跳服务不会进行任何认证和权限校验
 * 注册中心将调用所有注册在注册中心的服务下的心跳服务，用来判断服务状态，心跳服务返回超时或者报错的，将服务状态置为不可用，
 * 并报告一个错误。心跳服务是集成在jar包里的，凡是要向注册中心注册服务的，必须注册心跳服务（自动注册）。心跳注册不成功，则其他服务不会被注册。
 **/
@ServiceRegister(title = "心跳服务",
        serializer = HessionSerializer.class,
        authorization = AuthenticationNotCheckAuthorizer.class,
        authentication = AuthenticationNotCheckAuthenticator.class)
public class HeartbeatServiceImpl implements HeartbeatService, Serializable {
    private static final long serialVersionUID = 2783377098145240357L;

    @Override
    public String areYouOk() {
        return "ok";
    }
}
