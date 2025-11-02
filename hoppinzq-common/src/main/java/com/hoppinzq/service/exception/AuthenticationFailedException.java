package com.hoppinzq.service.exception;

/**
 * @author: ZhangQi
 * 身份验证异常，手动抛出此异常将被视为用户校验失败
 */
public class AuthenticationFailedException extends Exception {
    public AuthenticationFailedException(String message) {
        super(message);
    }

    public AuthenticationFailedException(Throwable cause) {
        super(cause);
    }
}
