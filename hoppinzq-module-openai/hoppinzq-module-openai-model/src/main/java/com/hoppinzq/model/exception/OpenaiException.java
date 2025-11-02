package com.hoppinzq.model.exception;

/**
 * @author: ZhangQi
 */
public class OpenaiException extends RuntimeException {
    public OpenaiException(String message) {
        super(message);
    }

    public OpenaiException(String message, Throwable cause) {
        super(message, cause);
    }
}
