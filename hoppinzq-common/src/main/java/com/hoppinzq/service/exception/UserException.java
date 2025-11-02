package com.hoppinzq.service.exception;

/**
 * @author: ZhangQi
 * 自定义用户管理异常
 */
public class UserException extends RemotingException {
    public UserException(String message) {
        super(message);
    }
}
