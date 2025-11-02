package com.hoppinzq.service.auth;

import com.hoppinzq.service.annotation.InterfaceImplName;
import com.hoppinzq.service.common.HoppinInvocationRequest;
import com.hoppinzq.service.exception.AuthenticationFailedException;

import java.io.Serializable;

@InterfaceImplName("匿名验证")
public class EnableAnonymousAuthenticator implements AuthenticationProvider, Serializable {
    private static final long serialVersionUID = 2783377098145240357L;

    @Override
    public void authenticate(HoppinInvocationRequest hoppinInvocationRequest) throws AuthenticationFailedException {
        if (hoppinInvocationRequest.getCredentials() != null && hoppinInvocationRequest.getCredentials() instanceof Anonymous) {
            Anonymous anonymous = (Anonymous) hoppinInvocationRequest.getCredentials();//调用方
            if (anonymous != null && anonymous.getName() != null) {
                AuthenticationContext.setPrincipal(anonymous);
            } else {
                throw new AuthenticationFailedException("匿名身份验证失败");
            }
        } else {
            throw new AuthenticationFailedException("缺少匿名凭据");
        }
    }
}
