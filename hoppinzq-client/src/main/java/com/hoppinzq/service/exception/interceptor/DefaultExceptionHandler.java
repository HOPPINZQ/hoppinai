package com.hoppinzq.service.exception.interceptor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author:ZhangQi 默认的异常处理方式
 **/
public class DefaultExceptionHandler implements ExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(DefaultExceptionHandler.class);

    @Override
    public void handleException(Exception exception) {
        if (exception != null) {
            logger.error(exception.getMessage());
        }
    }
}