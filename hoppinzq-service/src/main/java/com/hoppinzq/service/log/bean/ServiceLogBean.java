package com.hoppinzq.service.log.bean;

import com.hoppinzq.service.common.UserPrincipal;
import com.hoppinzq.service.util.UUIDUtil;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Map;

/**
 * @author:ZhangQi 服务调用日志
 */
public class ServiceLogBean implements Serializable {
    private static final long serialVersionUID = 1222222L;

    private String id;
    private String ip;
    private String name;
    private String type;
    private String method;
    private Object[] params;
    private String requestParams;
    private String exception;
    private String traceId;
    private long time;
    private String date;
    private Map headers;
    private UserPrincipal userPrincipal;
    private long startTime;
    private long methodStartTime;
    private long endTime;
    private boolean isError = false;
    private int logType = 0;//0表示普通日志，1表示熔断器

    public ServiceLogBean() {
        this.id = UUIDUtil.getUUID();
        this.startTime = System.currentTimeMillis();
    }

    public int getLogType() {
        return logType;
    }

    public void setLogType(int logType) {
        this.logType = logType;
    }

    @Override
    public String toString() {
        return "ServiceLogBean{" +
                "id='" + id + '\'' +
                ", ip='" + ip + '\'' +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", method='" + method + '\'' +
                ", params=" + Arrays.toString(params) +
                ", requestParams='" + requestParams + '\'' +
                ", exception='" + exception + '\'' +
                ", traceId='" + traceId + '\'' +
                ", time=" + time +
                ", date='" + date + '\'' +
                ", headers=" + headers +
                ", userPrincipal=" + userPrincipal +
                ", startTime=" + startTime +
                ", methodStartTime=" + methodStartTime +
                ", endTime=" + endTime +
                ", isError=" + isError +
                '}';
    }

    public boolean isError() {
        return isError;
    }

    public void setError(boolean error) {
        isError = error;
    }

    public String getRequestParams() {
        return requestParams;
    }

    public void setRequestParams(String requestParams) {
        this.requestParams = requestParams;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getTraceId() {
        return traceId;
    }

    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public Object[] getParams() {
        return params;
    }

    public void setParams(Object[] params) {
        this.params = params;
    }

    public String getException() {
        return exception;
    }

    public void setException(String exception) {
        this.exception = exception;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Map getHeaders() {
        return headers;
    }

    public void setHeaders(Map headers) {
        this.headers = headers;
        if (this.headers != null && this.headers.get("traceId") != null) {
            this.traceId = String.valueOf(this.headers.get("traceId"));
        }
    }

    public UserPrincipal getUserPrincipal() {
        return userPrincipal;
    }

    public void setUserPrincipal(UserPrincipal userPrincipal) {
        this.userPrincipal = userPrincipal;
    }

    public long getMethodStartTime() {
        return methodStartTime;
    }

    public void setMethodStartTime(long methodStartTime) {
        this.methodStartTime = methodStartTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }
}
