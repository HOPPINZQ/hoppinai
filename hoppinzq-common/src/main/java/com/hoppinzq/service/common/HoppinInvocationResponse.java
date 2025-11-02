package com.hoppinzq.service.common;

import java.io.Serializable;
import java.util.Map;

/**
 * @author:ZhangQi 调用服务请求的响应
 */
public class HoppinInvocationResponse implements Serializable {

    /**
     * 状态码
     */
    private int status = 200;

    /**
     * 远程服务方法本身引发的异常
     */
    private Throwable exception;

    /**
     * 如果没有引发异常，则方法调用的结果
     */
    private Serializable result;

    /**
     * 执行远程操作时服务器上发生的修改列表
     */
    private ModificationList[] modifications;

    private Map headers;

    public HoppinInvocationResponse() {
    }

    public void setSelf(Throwable exception, Serializable result, ModificationList[] modifications, Map headers) {
        this.exception = exception;
        this.result = result;
        this.modifications = modifications;
        this.headers = headers;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Map getHeaders() {
        return headers;
    }

    public void setHeaders(Map headers) {
        this.headers = headers;
    }

    public Throwable getException() {
        return exception;
    }

    public void setException(Throwable exception) {
        this.exception = exception;
    }

    public Serializable getResult() {
        return result;
    }

    public void setResult(Serializable result) {
        this.result = result;
    }

    public ModificationList[] getModifications() {
        return modifications;
    }

    public void setModifications(ModificationList[] modifications) {
        this.modifications = modifications;
    }
}
