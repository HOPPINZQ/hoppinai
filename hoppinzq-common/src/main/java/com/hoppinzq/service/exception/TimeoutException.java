package com.hoppinzq.service.exception;

/**
 * @author: ZhangQi
 * 自定义超时异常
 */
public class TimeoutException extends RemotingException {
    public TimeoutException(Exception e) {
        super(e);
    }
}
