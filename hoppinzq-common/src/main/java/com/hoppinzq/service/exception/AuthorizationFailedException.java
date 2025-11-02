package com.hoppinzq.service.exception;

/**
 * @author: ZhangQi
 * 授权异常，手动抛出此异常将被视为用户没有权限调用
 */
public class AuthorizationFailedException extends Exception {
    public AuthorizationFailedException(String message) {
        super(message);
    }
}
