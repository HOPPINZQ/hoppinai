package com.hoppinzq.service.exception;

/**
 * @author: ZhangQi
 * 熔断器异常
 */
public class CircuitBreakerException extends RemotingException {
    public CircuitBreakerException(Exception e) {
        super(e);
    }
}
