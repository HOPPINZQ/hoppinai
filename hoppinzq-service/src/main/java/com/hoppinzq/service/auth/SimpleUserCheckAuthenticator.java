package com.hoppinzq.service.auth;

import com.hoppinzq.service.annotation.InterfaceImplName;
import com.hoppinzq.service.common.HoppinInvocationRequest;
import com.hoppinzq.service.common.UserPrincipal;
import com.hoppinzq.service.exception.AuthenticationFailedException;

import java.io.Serializable;

/**
 * @author:ZhangQi 简单的身份验证提供类，在调用服务需要传入，为用户名/密码组合的方法进行身份验证。
 * 仅仅用户名为“zhangqi”，密码为“123456”的用户调用，这只是一个演示，通常需要对接数据库或者统一权限认证系统
 * @see HoppinInvocationRequest#getCredentials() 通过该方法可以获取到调用方身份信息
 */
@InterfaceImplName("用户名是zhangqi，密码是123456的用户验证通过")
public class SimpleUserCheckAuthenticator implements AuthenticationProvider, Serializable {
    private static final long serialVersionUID = 2783377098145240357L;

    //暂时写死，这两个是服务提供方的用户名密码
    private String username = "zhangqi";
    private String password = "123456";

    public SimpleUserCheckAuthenticator() {
    }

    public SimpleUserCheckAuthenticator(String username, String password) {
        this.username = username;
        this.password = password;
    }

    @Override
    public void authenticate(HoppinInvocationRequest hoppinInvocationRequest) throws AuthenticationFailedException {
        if (hoppinInvocationRequest.getCredentials() != null && hoppinInvocationRequest.getCredentials() instanceof UserPrincipal) {
            UserPrincipal upp = (UserPrincipal) hoppinInvocationRequest.getCredentials();//调用方

            //调用方是否是服务提供者？
            if (username.equals(upp.getUsername()) && password.equals(upp.getPassword())) {
                AuthenticationContext.setPrincipal(upp);
            } else {
                throw new AuthenticationFailedException("身份验证失败");
            }
        } else {
            throw new AuthenticationFailedException("缺少用户信息");
        }
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
