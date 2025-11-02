package com.hoppinzq.service.bean;

import com.hoppinzq.service.annotation.GatewayServlet;
import com.hoppinzq.service.hander.ApiGatewayHandler;

/**
 * servlet映射
 *
 * @author:ZhangQi
 */
public class ServletHandlerMapping<T extends ApiGatewayHandler> {

    private T servletHandler;
    private String prefix;
    private String handlerName;
    private String description;

    public ServletHandlerMapping() {
    }

    public ServletHandlerMapping(T type, GatewayServlet gatewayServlet) {
        this.servletHandler = type;
        this.prefix = gatewayServlet.prefix();
        this.description = gatewayServlet.description();
        this.handlerName = type.getClass().getSimpleName();
    }

    public ServletHandlerMapping(T type, String prefix, String description) {
        this.servletHandler = type;
        this.prefix = prefix;
        this.description = description;
        this.handlerName = type.getClass().getSimpleName();
    }

    public String getHandlerName() {
        return handlerName;
    }

    public void setHandlerName(String handlerName) {
        this.handlerName = handlerName;
    }

    public T getServletHandler() {
        return servletHandler;
    }

    public void setServletHandler(T servletHandler) {
        this.servletHandler = servletHandler;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }
}

