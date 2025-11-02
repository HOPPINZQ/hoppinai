package com.hoppinzq.service.auth;


import com.hoppinzq.service.annotation.InterfaceImplName;
import com.hoppinzq.service.common.HoppinInvocationRequest;

import java.io.Serializable;

/**
 * @author:ZhangQi 不需要身份验证就可以调用服务
 */
@InterfaceImplName("不需要身份验证就可以调用服务")
public class AuthenticationNotCheckAuthenticator implements AuthenticationProvider, Serializable {
    private static final long serialVersionUID = 2783377098145240357L;

    @Override
    public void authenticate(HoppinInvocationRequest hoppinInvocationRequest) {
    }
}
