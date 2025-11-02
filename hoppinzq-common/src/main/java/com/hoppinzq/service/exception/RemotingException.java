package com.hoppinzq.service.exception;

/**
 * @author: ZhangQi
 * 远程服务调用自定义异常
 */
public class RemotingException extends RuntimeException {
    public RemotingException(Exception e) {
        super(e);
    }

    public RemotingException(String msg) {
        super(msg);
    }

    public RemotingException(Throwable throwable) {
        super(throwable);
    }
}
