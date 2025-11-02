package com.hoppinzq.exception;

/**
 * @author: ZhangQi
 * 自定义用户管理异常
 */
public class AuthException extends RuntimeException {
    public AuthException(String message) {
        super(message);
    }
}
