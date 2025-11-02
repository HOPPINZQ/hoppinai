package com.hoppinzq.api;

import com.hoppinzq.service.annotation.ServiceRegister;
import com.hoppinzq.service.annotation.ServiceRegisterMethod;
import com.hoppinzq.utils.RedisUtils;
import org.springframework.beans.factory.annotation.Autowired;

@ServiceRegister(title = "用户认证服务", description = "用来做用户注册登录登出等功能")
public class AuthServiceImpl implements AuthService {

    @Autowired
    private RedisUtils redisUtils;

    @Override
    @ServiceRegisterMethod(title = "用户是否存在认证", description = "用户是否存在认证，返回true表示有效")
    public Boolean isToken(Long userId) {
        if (!redisUtils.hasKey("hoppinzq:user:token:" + userId)) {
            return false;
        }
        return true;
    }
}

