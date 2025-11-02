package com.hoppinzq.service.auth;

import com.hoppinzq.service.annotation.InterfaceImplName;
import com.hoppinzq.service.common.HoppinInvocationRequest;
import com.hoppinzq.service.common.UserPrincipal;
import com.hoppinzq.service.exception.AuthorizationFailedException;

import java.io.Serializable;

/**
 * @author:ZhangQi 使用凭证校验DEMO, 通过secretId和secretKey来替代用户名密码，需要连接数据库或统一权限系统使用。
 */
@InterfaceImplName("通过secretId和secretKey")
public class AuthenticationCheckAuthorizerSecret implements AuthorizationProvider, Serializable {
    private static final long serialVersionUID = 2783377098145240357L;

    @Override
    public void authorize(HoppinInvocationRequest hoppinInvocationRequest) throws AuthorizationFailedException {
        UserPrincipal userPrincipal = (UserPrincipal) AuthenticationContext.getPrincipal();
        if (userPrincipal == null) {
            throw new AuthorizationFailedException("请提供有效凭据，然后重试");
        }
        if (userPrincipal.getSecretId() == null || userPrincipal.getSecretKey() == null) {
            throw new AuthorizationFailedException("请提供可用凭据，然后重试");
        }
    }
}
