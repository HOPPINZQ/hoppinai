package com.hoppinzq.service.auth;

import com.hoppinzq.service.annotation.InterfaceImplName;
import com.hoppinzq.service.common.HoppinInvocationRequest;
import com.hoppinzq.service.exception.AuthorizationFailedException;

import java.io.Serializable;

/**
 * @author:ZhangQi 授权身份验证的请求
 */
@InterfaceImplName("授权身份验证的请求")
public class AuthenticationCheckAuthorizer implements AuthorizationProvider, Serializable {
    private static final long serialVersionUID = 2783377098145240357L;

    /**
     * 我这里这个授权的方法很简单，通过身份验证就通过授权
     *
     * @param hoppinInvocationRequest
     * @throws AuthorizationFailedException
     */
    @Override
    public void authorize(HoppinInvocationRequest hoppinInvocationRequest) throws AuthorizationFailedException {
        if (AuthenticationContext.getPrincipal() == null) {
            throw new AuthorizationFailedException("用户未认证，请输入正确用户名密码，然后重试");
        }
    }
}
